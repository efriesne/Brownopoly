package edu.brown.cs.cmen.brownopoly.game;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;

import edu.brown.cs.cmen.brownopoly.board.Board;
import edu.brown.cs.cmen.brownopoly.board.BoardSquare;
import edu.brown.cs.cmen.brownopoly.ownable.OwnableManager;
import edu.brown.cs.cmen.brownopoly.player.Player;
import edu.brown.cs.cmen.brownopoly.web.GameState;
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
  private Player currPlayer;
  private boolean isFastPlay;
  private BoardSquare currSquare;

  public Referee(Board board, Collection<Player> players, boolean isFastPlay) {
    this.board = board;
    q = new LinkedList<>(players);
    this.isFastPlay = isFastPlay;
    currPlayer = q.peek();
    dice = new Dice();
  }

  public Player nextTurn() {
    if (!dice.isDoubles() || currPlayer.isInJail()) {
      currPlayer = q.remove();
      q.add(currPlayer);
      dice = new Dice();
    }
    currPlayer.startTurn();
    return currPlayer;
  }

  
  /**
   *  //can try to roll doubles
   * @return
   */

  //returns a boolean to see if you can move
  public boolean roll() {
    dice.roll();
    if (currPlayer.isInJail()) {
      if (isFastPlay || (currPlayer.getTurnsInJail() == 2)) {
        currPlayer.payBail();
        return true;
      } else {
        if (dice.isDoubles()) {
          currPlayer.exitJail();
          dice.resetDoubles();
          return true;
        }
      }
      currPlayer.addTurnsInJail();
      return false;
    } else {
      if (dice.numDoubles() == 3) {
        currPlayer.moveToJail();
        return false;
      }
      return true;
    }
  }


  public Dice getDice() {
    return dice;
  }

  //return boolean indicating if more input is needed
  public boolean move(int dist) {
    int pos = currPlayer.move(dist);
    currSquare = board.getSquare(pos);
    return OwnableManager.isOwned(pos);
  }

  public String play(int input) {
    String msg = currSquare.executeEffect(currPlayer, input);
    // edge case: what if player changed positions after executeEffect?
    if (currPlayer.isBankrupt()) {
      q.remove();
    }
    return msg;
  }
  
  public void trade(Player p) {
    new Trader(currPlayer, p);
  }

  public GameState getCurrGameState() {
    return new GameState(Collections.unmodifiableCollection(q));
  }

  public boolean mortgageRequired() {
    for (Player p : q) {
      if (!p.isBankrupt() && p.hasNegativeBalance()) {
        return true;
      }
    }
    return false;
  }
  public BoardSquare getCurrSquare() {
    return currSquare;
  }
  public Player getCurrPlayer() { return currPlayer; }
}
