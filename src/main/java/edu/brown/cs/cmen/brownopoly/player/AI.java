package edu.brown.cs.cmen.brownopoly.player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.brown.cs.cmen.brownopoly.board.*;
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
      boolean safeToPay = predictionArray[0] - MonopolyConstants.JAIL_BAIL >= 0;
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

    normalize(proposal);

    return (wealthAfter >= wealthBefore) && (netIncomeAfter >= netIncomeBefore);

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

  @Override
  public TradeProposal makeTradeProposal() {
    return null;
  }

  @Override
  public String makeBuildDecision() {
    Set<Property> properties = new HashSet<>();
    if(!getBank().getMonopolies().isEmpty()) {
      for(Monopoly monopoly : getBank().getMonopolies()) {
        for(Property property : monopoly.canBuildHouses()) {
          if(safeToPay()[0] - MonopolyConstants.getHouseCost(property.getId()) >= 0 &&
                  getBalance() >= MonopolyConstants.getHouseCost(property.getId())) {
            buyHouse(property);
            properties.add(property);
          }
        }
      }
      String toReturn = getName() + " bought houses on ";
      if(properties.size() == 0) {
        return null;
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
    return null;
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
    System.out.println(getName() + " : " + ownable.getName());
    System.out.println("COST PER ROUND: " + costPerRound);
    System.out.println("Earnings per round: " + earningsPerRound);
    System.out.println("Cost Deviation: " + costDeviation);
    System.out.println("Deviant Board cost: " + deviantBoardCost);
    System.out.println("Expected Earnings: " + expectedEarnings);
    System.out.println("predicted balance: " + predictedBalance);
    System.out.println((predictedBalance - ownable.price()) >= 0);
    return (predictedBalance - ownable.price()) >= 0;
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
      System.out.println(OwnableManager.getOwnable(i));
      if(OwnableManager.getOwnable(i) != null && OwnableManager.isOwned(i)) {
        if(i == 12 || i == 28) {
          Utility util = OwnableManager.getUtility(i);
          System.out.println(util);
          boolean amOwner = util.owner().getId().equals(getId());
          if(amOwner) {
            earnings += MonopolyConstants.EXPECTED_DICE_ROLL * util.rent();
          } else {
            cost += MonopolyConstants.EXPECTED_DICE_ROLL * util.rent();
          }
        } else if(i == 5 || i == 15 || i == 25 || i == 35) {
          Railroad railroad = OwnableManager.getRailroad(i);
          System.out.println(railroad);
          boolean amOwner = railroad.owner().getId().equals(getId());
          if(amOwner) {
            earnings += railroad.rent();
          } else {
            cost += railroad.rent();
          }
        } else {
          Property property = OwnableManager.getProperty(i);
          System.out.println(property);
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
