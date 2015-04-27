package edu.brown.cs.cmen.brownopoly.player;

import java.util.List;

import edu.brown.cs.cmen.brownopoly.board.*;
import edu.brown.cs.cmen.brownopoly.game.MonopolyConstants;
import edu.brown.cs.cmen.brownopoly.ownable.*;

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
    if (inJail) {
      boolean canPay = safeToPayBail();
      boolean wantToPay = wantsToPayBail();
      if(canPay && wantToPay) {
        payBail();
      }
    }
  }

  public boolean wantsToPayBail() {
    //rationale here is that the AI wants to stay in jail unless many more properties to buy or freeparking is high
    boolean manyPropertiesAvailable = (OwnableManager.numOwned() / MonopolyConstants.NUM_OWNABLES) <= MonopolyConstants.
  }

  public boolean safeToPayBail() {
    int currentBalance = getBalance();
    double[] costEarnings = costEarningsPerRound();
    double costPerRound = costEarnings[0];
    double earningsPerRound = costEarnings[1];
    double costDeviation = findStandardDeviation(costPerRound);
    double roundsPerRevolution = MonopolyConstants.NUM_BOARDSQUARES / MonopolyConstants.EXPECTED_DICE_ROLL;
    riskAversionLevel = MonopolyConstants.DEFAULT_RISK_AVERSION_LEVEL;
    double deviantBoardCost = (costPerRound + costDeviation * riskAversionLevel) * roundsPerRevolution;
    double expectedEarnings = earningsPerRound * roundsPerRevolution + MonopolyConstants.GO_CASH;
    double predictedBalance = currentBalance + expectedEarnings - deviantBoardCost;
    return (predictedBalance - MonopolyConstants.JAIL_BAIL) >= 0;
  }
  
  public boolean makeTradeDecision(String[][] initProps, int initMoney, String[][] recipProps, int recipMoney) {
    return false;
  }
  
  public String buildHouses() {
    return "";
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
    BoardSquare[] array = board.getBoard();
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
    BoardSquare[] array = board.getBoard();
    for(int i = 0; i < MonopolyConstants.NUM_BOARDSQUARES; i++) {
      if(OwnableManager.getOwnable(i) != null && OwnableManager.isOwned(i)) {
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
