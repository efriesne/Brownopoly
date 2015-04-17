package edu.brown.cs.cmen.brownopoly.game;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import spark.QueryParamsMap;
import spark.Request;
import spark.Response;
import spark.Route;

import com.google.common.collect.ImmutableMap;

import edu.brown.cs.cmen.brownopoly.board.Board;
import edu.brown.cs.cmen.brownopoly.board.BoardSquare;
import edu.brown.cs.cmen.brownopoly.player.Player;
import edu.brown.cs.cmen.brownopoly_util.Dice;

/**
 * Class that deals with interplayer things. Handles turns.
 * 
 * @author mprafson
 *
 */
public class Referee {
  private Queue<Player> q;
  private Dice dice;
  private Board board;
  private Player currplayer;
  private boolean isFastPlay;
  private BoardSquare currSquare;

  

  public Referee(Board board, Collection<Player> players, boolean isFastPlay) {
    this.board = board;
    q = new LinkedList<>(players);
    this.isFastPlay = isFastPlay;
  }
  
  private class StartTurnHandler implements Route {

    @Override
    public Object handle(Request req, Response res) {
      nextTurn();      
      Map<String, Object> variables = ImmutableMap.of("player", currplayer);
      return GSON.toJson(variables);
    }
  }
  
  private class RollHandler implements Route {

    @Override
    public Object handle(Request req, Response res) {
      Dice dice = rollDice();
      boolean goToJail = false;
      if (dice.numDoubles() == 3) {
        currplayer.moveToJail();
        goToJail = true;
      }
      Map<String, Object> variables = ImmutableMap.of("dice", dice, "jail", goToJail);
      return GSON.toJson(variables);
    }
  }
  
  private class InJailRollHandler implements Route {
    //when the user is in jail, a use jail free card/pay to get out of jail/roll button
    //maps to this handler
    @Override
    public Object handle(Request req, Response res) {
      boolean paidBail = mustPayBail();
      boolean canMove = paidBail;
      Dice dice = rollDice();
      if (!paidBail) { //can try to roll doubles
        if (dice.isDoubles()) {
          canMove = true;
        }
      }
      Map<String, Object> variables = ImmutableMap.of("paid", paidBail, "dice", dice,
          "move", canMove);
      return GSON.toJson(variables);
    }
  }
  
  private class MoveHandler implements Route {
  
    @Override
    public Object handle(Request req, Response res) {
      move();
      String square = currSquare.getName();
      int needsInput = currSquare.setupEffect();
      Map<String, Object> variables = ImmutableMap.of("player", currplayer,
          "square", square, "input", needsInput);
      return GSON.toJson(variables);
    }
  }
  
  private class SquareEffectHandler implements Route {
    
    @Override
    public Object handle(Request req, Response res) {
      QueryParamsMap qm = req.queryMap();
      int input = Integer.parseInt(qm.value("input"));
      String turnInfo = play(input);      
      Map<String, Object> variables = ImmutableMap.of("info", turnInfo, "player", currplayer);
      return GSON.toJson(variables);
    }
  }


  
  public void nextTurn() {
    if (!dice.isDoubles() || currplayer.isInJail()) {
      currplayer = q.remove();
      dice = new Dice();
      q.add(currplayer);
    }
    //curr.startTurn(); 
  }
  
  /**
   * If true, player can roll the dice, otherwise stays in jail
   * @return
   */
  public boolean mustPayBail() {
    if (currplayer.getTurnsInJail() == 2) {
      currplayer.payBail();
      return true;
    }
    return false;
  }
  
  public Dice rollDice() {
    dice.roll();
    return dice;
  }
  
  public void move() {
    int pos = currplayer.move(dice.getRollSum());
    currSquare = board.getSquare(pos);
  }
  
  public String play(int input) {
    return currSquare.executeEffect(currplayer, input);
    if (currplayer.isBankrupt()) {
      q.remove();
    }
  }

  public void trade(Player p) {
    new Trader(currplayer, p);
  }
}
