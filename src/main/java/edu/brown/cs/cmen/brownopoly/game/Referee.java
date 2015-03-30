package edu.brown.cs.cmen.brownopoly.game;

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

  public Referee(Board board, Player... players) {
    this.board = board;
    q = new LinkedList<>();
    for (Player p : players) {
      q.add(p);
    }
  }

  public void play() {
    Player p = q.remove();
    q.add(p);

    dice = new Dice();
    do {
      int roll = dice.roll();

      if (dice.numDoubles() == 3) {
        // TODO Go to jail
      }

    } while (dice.isDoubles());

  }
}
