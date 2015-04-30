package edu.brown.cs.cmen.brownopoly.player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.brown.cs.cmen.brownopoly.board.*;
import edu.brown.cs.cmen.brownopoly.game.Game;
import edu.brown.cs.cmen.brownopoly.game.MonopolyConstants;
import edu.brown.cs.cmen.brownopoly.game.TradeProposal;
import edu.brown.cs.cmen.brownopoly.ownable.*;

import javax.rmi.CORBA.Util;

public class AI extends Player {
  //the number of standard deviations of cost the AI requires to feel "safe"
  int riskAversionLevel = MonopolyConstants.DEFAULT_RISK_AVERSION_LEVEL;
  Board board;

  public AI(String numAI, List<Property> startingProperties, boolean isAI, Board board, String id) {
    super("AI " + numAI, startingProperties, isAI, id);
    this.board = board;
  }

  
  @Override
  public void startTurn() {
    //rationale here is that the AI wants to stay in jail unless many more properties to buy or freeparking is high
    //when you're in jail, you have 0 expected cost, but miss out on opportunities to buy and opportunity to collect
    //money from both free parking and GO square.
    if (inJail) {
      double[] predictionArray = safeToPay();
      boolean safeToPay = predictionArray[0] - MonopolyConstants.JAIL_BAIL >= MonopolyConstants.AI_MINIMUM_SAFE_BALANCE;
      boolean highExpectedEarnings = predictionArray[1] > 0;
      boolean manyPropertiesAvailable = (OwnableManager.numOwned() / MonopolyConstants.NUM_OWNABLES) <=
              MonopolyConstants.OWNED_CAPACITY_THRESHOLD;
      if (getBalance() >= MonopolyConstants.JAIL_BAIL && safeToPay &&
              (highExpectedEarnings || manyPropertiesAvailable)) {
        payBail();
      }
    }
  }

  public double[] safeToPay() {
    int currentBalance = getBalance();
    double[] costEarnings = costEarningsPerRound();
    double costPerRound = costEarnings[0];
    double earningsPerRound = costEarnings[1];
    double costDeviation = findStandardDeviation(costPerRound);
    double roundsPerRevolution = MonopolyConstants.NUM_BOARDSQUARES / MonopolyConstants.EXPECTED_DICE_ROLL;
    riskAversionLevel = MonopolyConstants.DEFAULT_RISK_AVERSION_LEVEL;
    double deviantBoardCost = (costPerRound + costDeviation * riskAversionLevel) * roundsPerRevolution;
    double expectedCost = costPerRound * roundsPerRevolution;
    double expectedEarnings = earningsPerRound * roundsPerRevolution + MonopolyConstants.GO_CASH;
    double predictedBalance = currentBalance + expectedEarnings - deviantBoardCost;
    double[] toReturn = new double[2];
    toReturn[0] = predictedBalance;
    toReturn[1] = expectedEarnings - expectedCost;
    return toReturn;
  }

  //first array is ownables
  public boolean makeTradeDecision(TradeProposal proposal) {

    if(proposal != null) {

      int opponentBenefitMultiplier = findBenefitMultiplier(proposal.getInitializer(), proposal.getRecipProps());
      int personalBenefitMultiplier = findBenefitMultiplier(proposal.getRecipient(), proposal.getInitProps());

      double[] costEarningsBefore = costEarningsPerRound();
      double costPerRoundBefore = costEarningsBefore[0];
      double earningsPerRoundBefore = costEarningsBefore[1];
      double netIncomeBefore = earningsPerRoundBefore - costPerRoundBefore;
      double wealthBefore = wealth();

      simulate(proposal);

      double[] costEarningsAfter = costEarningsPerRound();
      double costPerRoundAfter = costEarningsAfter[0];
      double earningsPerRoundAfter = costEarningsAfter[1];
      double netIncomeAfter = earningsPerRoundAfter - costPerRoundAfter;
      double wealthAfter = wealth();

      double costDeviation = findStandardDeviation(costPerRoundAfter);
      double roundsPerRevolution = MonopolyConstants.NUM_BOARDSQUARES / MonopolyConstants.EXPECTED_DICE_ROLL;
      riskAversionLevel = MonopolyConstants.DEFAULT_RISK_AVERSION_LEVEL;
      double deviantBoardCost = (costPerRoundAfter + costDeviation * riskAversionLevel) * roundsPerRevolution;
      double expectedEarnings = earningsPerRoundAfter * roundsPerRevolution + MonopolyConstants.GO_CASH;
      double predictedBalance = getBalance() + expectedEarnings - deviantBoardCost;

      normalize(proposal);

      double wealthChange = wealthAfter - wealthBefore;
      double revolutionIncomeChange = (netIncomeAfter - netIncomeBefore) * roundsPerRevolution;

      boolean isSafe = predictedBalance >= MonopolyConstants.AI_MINIMUM_SAFE_BALANCE;
      boolean canAfford = (getBalance() - proposal.getRecipMoney() >= 0);
      boolean highEnoughWealth = (wealthChange > 0 &&
              wealthChange >= (-1 * (revolutionIncomeChange * MonopolyConstants.AI_DESIRED_ROUNDS_COMPENSATION * opponentBenefitMultiplier)));
      boolean highEnoughProperty = (revolutionIncomeChange > 0 &&
              (revolutionIncomeChange * MonopolyConstants.AI_DESIRED_ROUNDS_COMPENSATION * personalBenefitMultiplier / 2) >= (-1 * wealthChange));

      System.out.println("Opponent Multiplier: " + opponentBenefitMultiplier);
      System.out.println("Personal Multiplier: " + personalBenefitMultiplier);

      return isSafe && canAfford && (highEnoughProperty || highEnoughWealth);
    } else {
      return false;
    }
  }

  public int findBenefitMultiplier(Player recipient, String[] propertyRequested) {
    int multiplier = 1;
    for(String s : propertyRequested) {
      Ownable ownable = OwnableManager.getOwnable(Integer.parseInt(s));
      String type = ownable.getType();
      if (type.equals("railroad")) {
        multiplier += recipient.getRailroads().size();
      } else if (type.equals("utility")) {
        multiplier += 2 * (recipient.getUtilities().size());
      } else if (type.equals("property")) {
        Property property = (Property) ownable;
        for(Property member : property.getPropertiesInMonopoly()) {
          if(recipient.getBank().checkMonopoly(member)) {
            multiplier += 4;
          } else if(recipient.getProperties().contains(member)) {
            multiplier += 2;
          }
        }
      }
    }
    return multiplier;
  }

  public void simulate(TradeProposal proposal) {
    Player initializer = proposal.getInitializer();
    String[] propertyOffering = proposal.getInitProps();
    String[] propertyRequested = proposal.getRecipProps();
    int moneyOffering = proposal.getInitMoney();
    int moneyRequested = proposal.getRecipMoney();
    initializer.addToBalance(moneyRequested + (-1 * moneyOffering));
    addToBalance(moneyOffering + (-1 * moneyRequested));
    initializer.removeOwnables(propertyOffering);
    removeOwnables(propertyRequested);
    initializer.addOwnables(propertyRequested);
    addOwnables(propertyOffering);
  }

  public void normalize(TradeProposal proposal) {
    Player initializer = proposal.getInitializer();
    String[] propertyOffering = proposal.getInitProps();
    String[] propertyRequested = proposal.getRecipProps();
    int moneyOffering = proposal.getInitMoney();
    int moneyRequested = proposal.getRecipMoney();
    addToBalance(moneyRequested + (-1 * moneyOffering));
    initializer.addToBalance(moneyOffering + (-1 * moneyRequested));
    initializer.removeOwnables(propertyRequested);
    removeOwnables(propertyOffering);
    initializer.addOwnables(propertyOffering);
    addOwnables(propertyRequested);
  }

  /**
   * AI first looks through opponent properties to see if it wants anything
   * Then makes offer based on how much it values it.
   * @return
   */
  @Override
  public TradeProposal makeTradeProposal() {
    List<TradeProposal> proposals = new ArrayList<>();
    Bank myBank = getBank();
    List<Player> opponents = getOpponents();
    for(Player opponent : opponents) {
      for(Property property : opponent.getProperties()) {
        if(myBank.checkMonopoly(property)) {
          if(safeToPay()[0] - property.price() * 1.5 >= MonopolyConstants.AI_MINIMUM_SAFE_BALANCE) {
            String[] requesting = new String[1];
            requesting[0] = "" + property.getId();
            String[] offering = new String[1];
            int moneyOffering = (int) (property.price() * 1.5);
            proposals.add(new TradeProposal(this, opponent, requesting, 0, offering, moneyOffering));
          }
        }
      }
    }
    System.out.println(proposals.size());
    for(TradeProposal proposal : proposals) {
      if (makeTradeDecision(proposal)) {
        System.out.println("Proposal Shipped");
        return proposal;
      }
    }
    return null;
  }

  @Override
  public String makeBuildDecision() {
    Set<Property> properties = new HashSet<>();
    boolean feelingUnsafe = false;
    if(!getBank().getMonopolies().isEmpty()) {
      while(!feelingUnsafe) {
        for (Monopoly monopoly : getBank().getMonopolies()) {
          for (Property property : monopoly.canBuildHouses()) {
            int cost = MonopolyConstants.getHouseCost(property.getId());
            if (safeToPay()[0] - cost >= MonopolyConstants.AI_MINIMUM_SAFE_BALANCE &&
                    getBalance() >= cost) {
              buyHouse(property);
              properties.add(property);
            } else {
              feelingUnsafe = true;
            }
          }
        }
      }
      String toReturn = getName() + " bought houses on ";
      if(properties.size() == 0) {
        return "";
      } else if (properties.size() == 1) {
        for(Property property : properties) {
          toReturn += property.getName();
        }
        return toReturn;
      } else {
        int i = 1;
        for (Property property : properties) {
          if(i == properties.size()) {
            toReturn += "and " + property.getName() + ".";
          } else {
            toReturn += property.getName() + ", ";
          }
          i++;
        }
        return toReturn;
      }
    }
    return "";
  }

  //mortgages properties
  public void mortgageOwnable() {
    if(isBroke()) {
      boolean mortgagedSomething = false;
      while(!mortgagedSomething) {
        for(Property property : getBank().getProperties()) {
          if(!property.isMortgaged()) {
            property.mortgage();
            mortgagedSomething = true;
            break;
          }
        }
        if(!mortgagedSomething) {
          for(Railroad railroad : getBank().getRailroads()) {
            if(!railroad.isMortgaged()) {
              railroad.mortgage();
              mortgagedSomething = true;
              break;
            }
          }
        }
        if(!mortgagedSomething) {
          for(Utility utility : getBank().getUtilities()) {
            if(!utility.isMortgaged()) {
              utility.mortgage();
              mortgagedSomething = true;
              break;
            }
          }
        }
        if(!mortgagedSomething) {
          for(Monopoly monopoly : getBank().getMonopolies()) {
            for(Property property : monopoly.canSellHouses()) {
              sellHouse(property);
              mortgagedSomething = true;
              break;
            }
            if(mortgagedSomething) {
              break;
            }
          }
        }
      }
      mortgageOwnable();
    }
  }

  @Override
  public boolean makeBuyingDecision(Ownable ownable) {
    int currentBalance = getBalance();
    double[] costEarnings = costEarningsPerRound();
    double costPerRound = costEarnings[0];
    double earningsPerRound = costEarnings[1];
    double costDeviation = findStandardDeviation(costPerRound);
    double roundsPerRevolution = MonopolyConstants.NUM_BOARDSQUARES / MonopolyConstants.EXPECTED_DICE_ROLL;
    setRiskAversionLevel(ownable);
    double deviantBoardCost = (costPerRound + costDeviation * riskAversionLevel) * roundsPerRevolution;
    double expectedEarnings = earningsPerRound * roundsPerRevolution + MonopolyConstants.GO_CASH;
    double predictedBalance = currentBalance + expectedEarnings - deviantBoardCost;
    return (predictedBalance - ownable.price()) >= MonopolyConstants.AI_MINIMUM_SAFE_BALANCE;
  }

  public void setRiskAversionLevel(Ownable ownable) {
    int id = ownable.getId();
    if(id == 12 || id == 28) {
      if(getUtilities().size() == 1) {
        riskAversionLevel = 0;
      } else {
        riskAversionLevel = MonopolyConstants.DEFAULT_RISK_AVERSION_LEVEL;
      }
    } else if(id == 5 || id == 15 || id == 25 || id == 35) {
      int numRailroads = getRailroads().size();
      if(numRailroads > 2) {
        riskAversionLevel = -1;
      } else if(numRailroads > 1) {
        riskAversionLevel = 0;
      } else if(numRailroads > 0) {
        riskAversionLevel = 1;
      } else {
        riskAversionLevel = MonopolyConstants.DEFAULT_RISK_AVERSION_LEVEL;
      }
    } else {
      if(getBank().checkMonopoly((Property)ownable)) {
        riskAversionLevel = -2;
      } else {
        riskAversionLevel = MonopolyConstants.DEFAULT_RISK_AVERSION_LEVEL;
      }
    }
  }

  public double findStandardDeviation(double mean) {
    double squaredDifferencesSum = 0.0;
    for(int i = 0; i < MonopolyConstants.NUM_BOARDSQUARES; i++) {
      if(OwnableManager.getOwnable(i) != null && OwnableManager.isOwned(i)) {
        double currValue = 0.0;
        if(i == 12 || i == 28) {
          Utility util = OwnableManager.getUtility(i);
          boolean amOwner = util.owner().getId().equals(getId());
          if(!amOwner) {
            currValue = MonopolyConstants.EXPECTED_DICE_ROLL * util.rent();
          }
        } else if(i == 5 || i == 15 || i == 25 || i == 35) {
          Railroad railroad = OwnableManager.getRailroad(i);
          boolean amOwner = railroad.owner().getId().equals(getId());
          if(!amOwner) {
            currValue = railroad.rent();
          }
        } else {
          Property property = OwnableManager.getProperty(i);
          boolean amOwner = property.owner().getId().equals(getId());
          if(!amOwner) {
            currValue = property.rent();
          }
        }
        squaredDifferencesSum += Math.pow((currValue - mean), 2);
      } else {
        squaredDifferencesSum += Math.pow(mean, 2);
      }
    }
    return Math.sqrt(squaredDifferencesSum / MonopolyConstants.NUM_BOARDSQUARES);
  }

  public double[] costEarningsPerRound() {
    double cost = 0.0;
    double earnings = 0.0;
    for(int i = 0; i < MonopolyConstants.NUM_BOARDSQUARES; i++) {
      if(OwnableManager.getOwnable(i) != null && OwnableManager.getOwnable(i).isOwned()) {
        if(i == 12 || i == 28) {
          Utility util = OwnableManager.getUtility(i);
          boolean amOwner = util.owner().getId().equals(getId());
          if(amOwner) {
            earnings += MonopolyConstants.EXPECTED_DICE_ROLL * util.rent();
          } else {
            cost += MonopolyConstants.EXPECTED_DICE_ROLL * util.rent();
          }
        } else if(i == 5 || i == 15 || i == 25 || i == 35) {
          Railroad railroad = OwnableManager.getRailroad(i);
          boolean amOwner = railroad.owner().getId().equals(getId());
          if(amOwner) {
            earnings += railroad.rent();
          } else {
            cost += railroad.rent();
          }
        } else {
          Property property = OwnableManager.getProperty(i);
          boolean amOwner = property.owner().getId().equals(getId());
          if(amOwner) {
            earnings += property.rent();
          } else {
            cost += property.rent();
          }
        }
      }
    }
    int numOpponents = getOpponents().size();
    double[] costEarnings = new double[2];
    costEarnings[0] = (cost + MonopolyConstants.INCOME_TAX + MonopolyConstants.LUXURY_TAX) / MonopolyConstants.NUM_BOARDSQUARES;
    costEarnings[1] = ((numOpponents * earnings) + Board.freeParking) / MonopolyConstants.NUM_BOARDSQUARES;
    return costEarnings;
  }
}
