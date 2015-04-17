package edu.brown.cs.cmen.brownopoly.player;

import java.util.List;

import edu.brown.cs.cmen.brownopoly.board.Board;
import edu.brown.cs.cmen.brownopoly.game.Game;
import edu.brown.cs.cmen.brownopoly.game.Trader;
import edu.brown.cs.cmen.brownopoly.ownable.Ownable;
import edu.brown.cs.cmen.brownopoly.ownable.Property;

public class AI extends Player {
  Board board;
  public AI(String numAI, List<Property> startingProperties, boolean isAI, Board board) {
    super("AI " + numAI, startingProperties, isAI);
    this.board = board;
  }

  
  @Override
  public void startTurn() {
    Trader trader = makeTradeDecision();
    makeBuildingDecision();
    /**
     * determine if it should build using makeBuildingDecision()
      makeTradeDecision()
     */
  }
  
  public Trader makeTradeDecision() {
    if (true) {
      //Trader trader = new Trader(this);
      //return trader;
    } 
    return null;
  }
  
  public void makeBuildingDecision() {
    
  }

  @Override
  public boolean makeBuyingDecision(Ownable prop) {
    return true;
  }


  @Override
  public boolean isAI() {
    return true;
  }



}
