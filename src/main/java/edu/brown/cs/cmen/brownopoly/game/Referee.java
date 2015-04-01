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

  public Referee(Board board, Collection<Player> players) {
    this.board = board;
    q = new LinkedList<>(players);
  }
  
  public void nextTurn() {
    curr = q.remove();
    q.add(curr);
    curr.startTurn(); //how to get info about what AI decides to do
  }
  
  public int[] rollDice() {
    if (curr.isInJail()) {
      if (curr.hasJailFree()) {
        
      } else if (curr.getTurnsInJail() == 2) {
        curr.payBail();
      } else {
        
      }
    }
    dice = new Dice();
    dice.roll();
    return new int[]{dice.getFirstRoll(), dice.getSecondRoll()};
  }
  
  //in javascript, while play is true, call play again
  public boolean play() {
      if (curr.isInJail()) {
        if (dice.isDoubles()) {
          curr.exitJail();
        } else {
          if (curr.getTurnsInJail() == 2) {
          }
        }
      }
      if (curr.isInJail() && dice.isDoubles()) {
        curr.exitJail();
      }

      if (dice.numDoubles() == 3) {
        // TODO go to jail
      }

      int pos = curr.move(dice.getRollSum());
      board.getSquare(pos).executeEffect(curr);
  }

  public void trade(Player p) {
    new Trader(curr, p);
  }
}
