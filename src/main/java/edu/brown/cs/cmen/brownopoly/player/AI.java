package edu.brown.cs.cmen.brownopoly.player;

import java.util.List;

import edu.brown.cs.cmen.brownopoly.game.Game;
import edu.brown.cs.cmen.brownopoly.ownable.Property;

public class AI extends Player {
  Game game;
  public AI(int numAI, List<Property> startingProperties, Game game) { //or game state
    super("AI " + numAI, startingProperties);
    this.game = game;
  }

  @Override
  public boolean makeBuyingDecision(Property p) {
    return true;
  }
  
  @Override
  public void startTurn() {
    /**
     * determine if it should build using makeBuildingDecision()
     * Determine whether it should make a trade using makeTradeDecision()
     */
  }
  
  public void makeTradeDecision() {
    
  }
  
  public void makeBuildingDecision() {
    
  }



}
