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

  public void play() {
    curr = q.remove();
    q.add(curr);

    dice = new Dice();

    do {
      int roll = dice.roll();
      if (curr.isInJail() && dice.isDoubles()) {
        // TODO exit jail
      }

      if (dice.numDoubles() == 3) {
        // TODO go to jail
        break;
      }

      int pos = curr.move(roll);
      board.getSquare(pos).executeEffect(curr);

    } while (dice.isDoubles());
  }

  public void trade(Player p) {
    new Trader(curr, p);
  }
}
