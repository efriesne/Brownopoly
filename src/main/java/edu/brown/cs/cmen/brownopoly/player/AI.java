package edu.brown.cs.cmen.brownopoly.player;

import java.util.*;

import edu.brown.cs.cmen.brownopoly.board.*;
import edu.brown.cs.cmen.brownopoly.game.MonopolyConstants;
import edu.brown.cs.cmen.brownopoly.game.TradeProposal;
import edu.brown.cs.cmen.brownopoly.ownable.*;

public class AI extends Player {
  //the number of standard deviations of cost the AI requires to feel "safe"
  int riskAversionLevel = MonopolyConstants.DEFAULT_RISK_AVERSION_LEVEL;
  Board board;
  Set<TradeProposal> proposalCache = new HashSet<>();

  public AI(String numAI, List<Property> startingProperties, boolean isAI, Board board, String id) {
    super("AI " + numAI, startingProperties, isAI, id);
    this.board = board;
  }

  
  @Override
  public void startTurn(boolean isFastPlay) {
    //rationale here is that the AI wants to stay in jail unless many more properties to buy or freeparking is high
    //when you're in jail, you have 0 expected cost, but miss out on opportunities to buy and opportunity to collect
    //money from both free parking and GO square.
    if (inJail) {
      if(isFastPlay) {
        payBail();
      } else {
        if (hasJailFree()) {
          useJailFree();
        } else {
          double[] predictionArray = safeToPay();
          boolean safeToPay = predictionArray[0] - MonopolyConstants.JAIL_BAIL >= 0;
          boolean canPay = getBalance() - MonopolyConstants.JAIL_BAIL >= MonopolyConstants.AI_MINIMUM_SAFE_BALANCE;
          boolean highExpectedEarnings = predictionArray[1] > 0;
          boolean manyPropertiesAvailable = (OwnableManager.numOwned() / MonopolyConstants.NUM_OWNABLES) <=
                  MonopolyConstants.OWNED_CAPACITY_THRESHOLD;
          if ((getBalance() >= MonopolyConstants.JAIL_BAIL && safeToPay && canPay &&
                  (highExpectedEarnings || manyPropertiesAvailable)) || getTurnsInJail() == 2) {
            payBail();
          }
        }
      }
    }
  }

  public String makePayOffMortgageDecision() {
    List<Ownable> mortgagedProperties = getBank().getMortgaged();
    List<Ownable> demortgagedProperties = new ArrayList<>();
    double predictedBalance = safeToPay()[0];
    for(Ownable ownable : mortgagedProperties) {
      assert ownable.isMortgaged();
      if(predictedBalance - getDemortgageOwnablePrice(ownable) >= MonopolyConstants.AI_MINIMUM_SAFE_BALANCE &&
              getBalance() - getDemortgageOwnablePrice(ownable) >= 2 * MonopolyConstants.AI_MINIMUM_SAFE_BALANCE) {
        ownable.demortgage();
        demortgagedProperties.add(ownable);
      }
    }
    if(demortgagedProperties.isEmpty()) {
      return "";
    } else {
      StringBuilder toReturn = new StringBuilder();
      toReturn.append(getName() + " unmortgaged ");
      if(demortgagedProperties.size() == 1) {
        toReturn.append(demortgagedProperties.get(0).getName());
        return toReturn.toString();
      } else {
        for(int i = 0; i < demortgagedProperties.size(); i++) {
          if(i == demortgagedProperties.size() - 1) {
            toReturn.append("and " + demortgagedProperties.get(i).getName());
          } else {
            toReturn.append(demortgagedProperties.get(i).getName() + ", ");
          }
        }
        return toReturn.toString();
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

  public boolean makeTradeDecision(TradeProposal proposal) {

    if(proposal != null) {

      int opponentBenefitMultiplier = findBenefitMultiplier(proposal.getInitializer(), proposal.getRecipProps());
      int personalBenefitMultiplier = findBenefitMultiplier(proposal.getRecipient(), proposal.getInitProps());

      double[] costEarningsBefore = costEarningsPerRound();
      double costPerRoundBefore = costEarningsBefore[0];
      double earningsPerRoundBefore = costEarningsBefore[1];
      double netIncomeBefore = earningsPerRoundBefore - costPerRoundBefore;
      double wealthBefore = wealth();

      double costDeviationBefore = findStandardDeviation(costPerRoundBefore);
      double roundsPerRevolutionBefore = MonopolyConstants.NUM_BOARDSQUARES / MonopolyConstants.EXPECTED_DICE_ROLL;
      riskAversionLevel = MonopolyConstants.DEFAULT_RISK_AVERSION_LEVEL;
      double deviantBoardCostBefore = (costPerRoundBefore + costDeviationBefore * riskAversionLevel) * roundsPerRevolutionBefore;
      double expectedEarningsBefore = earningsPerRoundBefore * roundsPerRevolutionBefore + MonopolyConstants.GO_CASH;
      double predictedBalanceBefore = getBalance() + expectedEarningsBefore - deviantBoardCostBefore;

      simulate(proposal);

      double[] costEarningsAfter = costEarningsPerRound();
      double costPerRoundAfter = costEarningsAfter[0];
      double earningsPerRoundAfter = costEarningsAfter[1];
      double netIncomeAfter = earningsPerRoundAfter - costPerRoundAfter;
      double wealthAfter = wealth();

      double costDeviationAfter = findStandardDeviation(costPerRoundAfter);
      double roundsPerRevolutionAfter = MonopolyConstants.NUM_BOARDSQUARES / MonopolyConstants.EXPECTED_DICE_ROLL;
      riskAversionLevel = MonopolyConstants.DEFAULT_RISK_AVERSION_LEVEL;
      double deviantBoardCostAfter = (costPerRoundAfter + costDeviationAfter * riskAversionLevel) * roundsPerRevolutionAfter;
      double expectedEarningsAfter = earningsPerRoundAfter * roundsPerRevolutionAfter + MonopolyConstants.GO_CASH;
      double predictedBalanceAfter = getBalance() + expectedEarningsAfter - deviantBoardCostAfter;

      normalize(proposal);

      double wealthChange = wealthAfter - wealthBefore;
      double revolutionIncomeChange = (netIncomeAfter - netIncomeBefore) * roundsPerRevolutionAfter;

      boolean isSafe = predictedBalanceAfter >= 0;
      boolean notSafeBefore = predictedBalanceBefore < 0;
      boolean canAfford = (getBalance() - proposal.getRecipMoney() >= MonopolyConstants.AI_MINIMUM_SAFE_BALANCE);
      boolean receivingMoney = (proposal.getInitMoney() - proposal.getRecipMoney()) >= 0;
      boolean highEnoughWealth = (wealthChange > 0 &&
              wealthChange >= (-1 * (revolutionIncomeChange * MonopolyConstants.AI_DESIRED_ROUNDS_COMPENSATION * opponentBenefitMultiplier)));
      boolean highEnoughProperty = (revolutionIncomeChange > 0 &&
              (revolutionIncomeChange * MonopolyConstants.AI_DESIRED_ROUNDS_COMPENSATION * personalBenefitMultiplier / 2) >= (-1 * wealthChange));

      System.out.println("Opponent Multiplier: " + opponentBenefitMultiplier);
      System.out.println("Personal Multiplier: " + personalBenefitMultiplier);

      return (isSafe || notSafeBefore) && (canAfford || receivingMoney) && (highEnoughProperty || highEnoughWealth);
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
          if(safeToPay()[0] - property.price() * 1.5 >= 0 &&
                  getBalance() - property.price() * 1.5 >= MonopolyConstants.AI_MINIMUM_SAFE_BALANCE) {
            String[] requesting = new String[1];
            requesting[0] = "" + property.getId();
            int moneyOffering = (int) (property.price() * 1.5);
            proposals.add(new TradeProposal(this, opponent, new String[0], moneyOffering, requesting, 0));
          }
        }
      }
    }
    for(TradeProposal proposal : proposals) {
      if(!proposalCache.contains(proposal)) {
        proposalCache.add(proposal);
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
      boolean builtSomething = true;
      while(!feelingUnsafe && builtSomething) {
        builtSomething = false;
        for (Monopoly monopoly : getBank().getMonopolies()) {
          System.out.println("can build houses before");
          for (Property property : monopoly.canBuildHouses()) {
            System.out.println("can build houses after1");
            System.out.println("get house cost before");
            int cost = MonopolyConstants.getHouseCost(property.getId());
            System.out.println("get house cost after");
            System.out.println("safe to pay before");
            if (safeToPay()[0] - cost >= 0 &&
                    getBalance() - cost >= MonopolyConstants.AI_MINIMUM_SAFE_BALANCE) {
              System.out.println("Safe to pay after1");
              buyHouse(property);
              properties.add(property);
              builtSomething = true;
            } else {
              feelingUnsafe = true;
            }
            System.out.println("Safe to pay after2");
          }
          System.out.println("can build houses after2");
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
  public String makeMortgageDecision(String message) {
    if(isBroke()) {
      Ownable mortgaged = null;
      int houses = 0;
      boolean mortgagedSomething = false;
      while(!mortgagedSomething) {
        for(Property property : getProperties()) {
          if(!property.isMortgaged()) {
            mortgageOwnable(property);
            mortgagedSomething = true;
            mortgaged = property;
            break;
          }
        }
        if(!mortgagedSomething) {
          for(Railroad railroad : getRailroads()) {
            if(!railroad.isMortgaged()) {
              mortgageOwnable(railroad);
              mortgagedSomething = true;
              mortgaged = railroad;
              break;
            }
          }
        }
        if(!mortgagedSomething) {
          for(Utility utility : getUtilities()) {
            if(!utility.isMortgaged()) {
              mortgageOwnable(utility);
              mortgagedSomething = true;
              mortgaged = utility;
              break;
            }
          }
        }
        if(!mortgagedSomething) {
          for(Monopoly monopoly : getMonopolies()) {
            for(Property property : monopoly.canSellHouses()) {
              if (property.getNumHouses() > 0) {
                sellHouse(property);
                mortgagedSomething = true;
                houses++;
                mortgaged = property;
                break;
              }
            }
            if (!mortgagedSomething) {
              for (Property property : monopoly.getProperties()) {
                if (!property.isMortgaged()) {
                  mortgageOwnable(property);
                  mortgagedSomething = true;
                  mortgaged = property;
                  break;
                }
              }
            }
            if(mortgagedSomething) {
              break;
            }
          }
        }
      }
      if(houses != 0) {
        message += "house_" + mortgaged.getName() + " ";
      } else {
        message += mortgaged.getName() + " ";
      }
      return makeMortgageDecision(message);
    }
    return message;
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
    return (predictedBalance - ownable.price()) >= 0 &&
            currentBalance - ownable.price() >= MonopolyConstants.AI_MINIMUM_SAFE_BALANCE;
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
