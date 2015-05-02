package edu.brown.cs.cmen.brownopoly.player;

import java.util.List;

import edu.brown.cs.cmen.brownopoly.game.TradeProposal;
import edu.brown.cs.cmen.brownopoly.ownable.Ownable;
import edu.brown.cs.cmen.brownopoly.ownable.Property;

public class Human extends Player {
  // TODO I changed the variable name, sorry -marley
  private StringBuilder builder_name;
  public Human(String name, List<Property> startingProperties, boolean isAI, String id) {
    super(name, startingProperties, isAI, id);
  }

  @Override
  public boolean makeBuyingDecision(Ownable ownable) {return false;}

  @Override
  public void startTurn(boolean isFastPlay) {
    // doesn't do much for human
  }

  @Override
  public String makePayOffMortgageDecision() {
    return "";
  }

  @Override
  public boolean makeTradeDecision(TradeProposal proposal) {return true;}

  @Override
  public TradeProposal makeTradeProposal() {return null;}

  @Override
  public String makeBuildDecision() {return "";}

  @Override
  public String makeMortgageDecision(String message){return "";}




}
