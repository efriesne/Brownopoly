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
        if(hasJailFree()) {
          useJailFree();
        } else {
          payBail();
        }
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

  /**
   * AI checks if there's a mortgaged property and then decides if it wants to pay it off.
   * @return a message indicating what was unmortgaged
   */
  public String makePayOffMortgageDecision() {
    List<Ownable> mortgagedProperties = getBank().getMortgaged();
    List<Ownable> demortgagedProperties = new ArrayList<>();

    //uses expected earnings/cost and standard deviation to see if it's "safe"
    double predictedBalance = safeToPay()[0];
    for(Ownable ownable : mortgagedProperties) {
      assert ownable.isMortgaged();
      if(predictedBalance - getDemortgageOwnablePrice(ownable) >= MonopolyConstants.AI_MINIMUM_SAFE_BALANCE &&
              getBalance() - getDemortgageOwnablePrice(ownable) >= MonopolyConstants.DEFAULT_RISK_AVERSION_LEVEL * MonopolyConstants.AI_MINIMUM_SAFE_BALANCE) {
        ownable.demortgage();
        demortgagedProperties.add(ownable);
      }
    }

    //builds message of what it unmortgaged
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

  /**
   * uses expected cost/earnings of the board, finds standard deviation, and applies risk aversion level
   * to calculate if the AI feels "safe"
   * @return the AI's prediction of its balance after one revolution around the board, which takes into
   * account a cost deviation, and the expected net income of one revolution which does not.
   */
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

  /**
   * Receives a Trade proposal and decides if it's receiving more "value" than it's giving away.
   * Uses multipliers to help calculate what actual value to each player.
   * @param proposal
   * @return it's decision
   */
  public boolean makeTradeDecision(TradeProposal proposal) {

    if(proposal != null) {

      int opponentBenefitMultiplier = findBenefitMultiplier(proposal.getInitializer(), proposal.getRecipProps());
      int personalBenefitMultiplier = findBenefitMultiplier(proposal.getRecipient(), proposal.getInitProps());

      int personalDetrimentMultiplier = findDetrimentMultiplier(proposal.getRecipProps());

      double proposalValue = calculateValue(proposal.getInitProps());
      double requestValue = calculateValue(proposal.getRecipProps());

      int moneyDifference = proposal.getRecipMoney() - proposal.getInitMoney();
      boolean receivingMoney = moneyDifference >= 0;
      boolean canAfford = getBalance() - moneyDifference >= MonopolyConstants.AI_MINIMUM_SAFE_BALANCE;

      //this value prevents someone from being able to pick up any property not triggering a multiplier for $1 over sticker price
      double heuristic = proposalValue == 0 ? 0.6 : 1;

      boolean betterForMe = (personalBenefitMultiplier * proposalValue + proposal.getInitMoney()) * heuristic >
                            opponentBenefitMultiplier * requestValue * personalDetrimentMultiplier + proposal.getRecipMoney() ;

      return (canAfford || receivingMoney) && betterForMe;
    } else {
      return false;
    }
  }

  /**
   * Determines the value (by retail price) of the array of ownables it's given
   * @param ownables
   * @return
   */
  public double calculateValue(String[] ownables) {
    double totalValue = 0.0;
    for(String s : ownables) {
      Ownable ownable = OwnableManager.getOwnable(Integer.parseInt(s));
      totalValue += ownable.price();
    }
    return totalValue;
  }

  /**
   * Determines how "valuable" the list of properties is to the input player
   * @param recipient
   * @param propertyRequested
   * @return
   */
  public int findBenefitMultiplier(Player recipient, String[] propertyRequested) {
    Set<Property> used = new HashSet<>();
    int multiplier = 1;
    int numRailroads = 0;
    int numUtilities = 0;
    boolean addedRailroads = false;
    boolean addedUtilities = false;
    List<Property> proposedProps = new ArrayList<>();
    for(String s : propertyRequested) {
      Ownable ownable = OwnableManager.getOwnable(Integer.parseInt(s));
      String type = ownable.getType();
      if(type.equals("property")) {
        proposedProps.add((Property)ownable);
      } else if(type.equals("railroad")) {
        numRailroads++;
      } else if(type.equals("utility")) {
        numUtilities++;
      }
    }
    for(String s : propertyRequested) {
      Ownable ownable = OwnableManager.getOwnable(Integer.parseInt(s));
      String type = ownable.getType();
      if (type.equals("railroad")) {
        if(!addedRailroads) {
          multiplier += recipient.getRailroads().size() + (numRailroads - 1);
          addedRailroads = true;
        }
      } else if (type.equals("utility")) {
        if(!addedUtilities) {
          multiplier += 2 * (recipient.getUtilities().size() + (numUtilities - 1));
          addedUtilities = true;
        }
      } else if (type.equals("property")) {
        Property property = (Property) ownable;
        if(!used.contains(property)) {
          Set<Property> members = property.getPropertiesInMonopoly();
          if(members.size() == 1) {
            for(Property member : members) {
              if(recipient.getProperties().contains(member)) {
                multiplier += 4;
              } else if(proposedProps.contains(member)) {
                multiplier += 4;
                proposedProps.remove(member);
              }
            }
          } else if(members.size() == 2) {
            int numMonopolySiblings = 0;
            for(Property property1 : proposedProps) {
              if(members.contains(property1)) {
                numMonopolySiblings++;
                used.add(property1);
              }
            }
            for(Property property2 : recipient.getProperties()) {
              if(members.contains(property2)) {
                numMonopolySiblings++;
              }
            }
            if(numMonopolySiblings == 2) {
              multiplier += 4;
            } else if(numMonopolySiblings == 1) {
              multiplier += 2;
            }
          }
        }
      }
    }
    return multiplier;
  }

  /**
   * Determines how detrimental it is for this AI player to give away the input properties
   * @param propertyRequested
   * @return
   */
  public int findDetrimentMultiplier(String[] propertyRequested) {
    Set<Property> used = new HashSet<>();
    int multiplier = 1;
    int numRailroads = 0;
    int numUtilities = 0;
    List<Property> proposedProps = new ArrayList<>();
    for(String s : propertyRequested) {
      Ownable ownable = OwnableManager.getOwnable(Integer.parseInt(s));
      String type = ownable.getType();
      if(type.equals("property")) {
        proposedProps.add((Property)ownable);
      } else if(type.equals("railroad")) {
        numRailroads++;
      } else if(type.equals("utility")) {
        numUtilities++;
      }
    }
    if(numRailroads == getRailroads().size() && numRailroads > 0) {
      multiplier += numRailroads - 1;
    } else {
      multiplier += numRailroads;
    }
    if(numUtilities == getUtilities().size() && numUtilities > 0) {
      multiplier += (numUtilities - 1) * 2;
    } else {
      multiplier += numUtilities * 2;
    }

    for(String s : propertyRequested) {
      Ownable ownable = OwnableManager.getOwnable(Integer.parseInt(s));
      String type = ownable.getType();
      if (type.equals("property")) {
        Property property = (Property) ownable;
        if(!used.contains(property)) {
          Set<Property> members = property.getPropertiesInMonopoly();
          if(members.size() == 1) {
            for(Property member : members) {
              if(proposedProps.contains(member)) {
                multiplier += 4;
                proposedProps.remove(member);
              } else {
                for(Monopoly monopoly : getMonopolies()) {
                  for(Property property1 : monopoly.getProperties()) {
                    if(property1.equals(member)) {
                      multiplier += 4;
                    }
                  }
                }
              }
            }
          } else if(members.size() == 2) {
            int numMonopolySiblings = 0;
            for(Property property2 : getProperties()) {
              if(members.contains(property2) && !proposedProps.contains(property2)) {
                numMonopolySiblings++;
              }
            }
            for(Monopoly monopoly : getMonopolies()) {
              for(Property property3 : monopoly.getProperties()) {
                if(members.contains(property3) && !proposedProps.contains(property3)) {
                  numMonopolySiblings++;
                }
              }
            }
            for(Property property1 : proposedProps) {
              if(members.contains(property1)) {
                numMonopolySiblings++;
                used.add(property1);
              }
            }
            if(numMonopolySiblings == 2) {
              multiplier += 4;
            } else if(numMonopolySiblings == 1) {
              multiplier += 2;
            }
          }
        }
      }
    }
    return multiplier;
  }

  /**
   * AI searches for a property that would give it a monopoly, makes offer of 1.5X retail price
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
          for (Property property : monopoly.canBuildHouses()) {
            int cost = MonopolyConstants.getHouseCost(property.getId());
            if (safeToPay()[0] - cost >= 0 &&
                    getBalance() - cost >= MonopolyConstants.AI_MINIMUM_SAFE_BALANCE) {
              buyHouse(property);
              properties.add(property);
              builtSomething = true;
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

  /**
   * Mortgages properties in order of importance, starting with the least valuable
   * @param message
   * @return
   */
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
        message += "house_" + mortgaged.getName() + ",";
      } else {
        message += mortgaged.getName() + ",";
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
    //will buy a property if it feels safe in the given board and can afford to do so.
    return (predictedBalance - ownable.price()) >= 0 &&
            currentBalance - ownable.price() >= MonopolyConstants.AI_MINIMUM_SAFE_BALANCE;
  }

  /**
   * Depending on what the ownable is, will be more risky or not.
   * @param ownable
   */
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

  /**
   * Calculates standard deviation of expected cost per round
   * @param mean
   * @return
   */
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

  /**
   * Find the expected cost/earnings per round of the current board
   * @return
   */
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
