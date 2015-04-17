package edu.brown.cs.cmen.brownopoly.game;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

import edu.brown.cs.cmen.brownopoly.board.Board;
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
  private Player curr;
  private boolean isFastPlay;

  /*
   * private class StartTurnHandler implements Route {
   * 
   * @Override public Object handle(Request req, Response res) { Player p =
   * nextTurn(); Map<String, Object> variables = ImmutableMap.of("player", p);
   * return GSON.toJson(variables);
   * 
   * } }
   * 
   * private class RollHandler implements Route {
   * 
   * @Override public Object handle(Request req, Response res) { Dice dice =
   * rollDice(); boolean goToJail = false; if (dice.numDoubles() == 3) {
   * curr.moveToJail(); goToJail = true; } Map<String, Object> variables =
   * ImmutableMap.of("dice", dice, "jail", goToJail); return
   * GSON.toJson(variables); } }
   * 
   * private class InJailRollHandler implements Route { //when the user is in
   * jail, a use jail free card/pay to get out of jail/roll button //maps to
   * this handler
   * 
   * @Override public Object handle(Request req, Response res) { boolean
   * paidBail = mustPayBail(); boolean canMove = false; Dice dice = null; if
   * (!paidBail) { //can try to roll doubles dice = rollDice(); if
   * (dice.isDoubles()) { canMove = true; } } Map<String, Object> variables =
   * ImmutableMap.of("paid", paidBail, "dice", dice, "move", canMove); return
   * GSON.toJson(variables); } }
   */

  public Referee(Board board, Collection<Player> players, boolean isFastPlay) {
    this.board = board;
    q = new LinkedList<>(players);
    this.isFastPlay = isFastPlay;
  }

  public Player nextTurn() {
    curr = q.remove();
    dice = new Dice();
    q.add(curr);
    curr.startTurn(); // how to get info about what AI decides to do, this needs
                      // to call roll
    return curr;
  }

  /**
   * If true, player can roll the dice, otherwise stays in jail
   * 
   * @return
   */
  public boolean mustPayBail() {
    if (curr.getTurnsInJail() == 2) {
      curr.payBail();
      return true;
    }
    return false;
  }

  public Dice rollDice() {
    dice.roll();
    return dice;
  }

  public String play() {
    int pos = curr.move(dice.getRollSum());
    return board.getSquare(pos).executeEffect(curr);
  }

  public void trade(Player p) {
    new Trader(curr, p);
  }
}
