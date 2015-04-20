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
  private Player currplayer;
  private boolean isFastPlay;
  private BoardSquare currSquare;

  public Referee(Board board, Collection<Player> players, boolean isFastPlay) {
    this.board = board;
    q = new LinkedList<>(players);
    this.isFastPlay = isFastPlay;
    currplayer = q.remove();
    dice = new Dice();
    q.add(currplayer);
  }

  public Player nextTurn() {
    if (!dice.isDoubles() || currplayer.isInJail()) {
      currplayer = q.remove();
      q.add(currplayer);
    }
    dice = new Dice();
    return currplayer;
    // Trade trade = currplayer.startTurn();
  }

  public void rollInJail() {
    boolean paidBail = mustPayBail();
    Dice dice = rollDice();
    if (!paidBail) { // can try to roll doubles
      if (dice.isDoubles()) {
        currplayer.exitJail();
      }
    }
  }

  //returns a boolean to see if you can move
  public boolean roll() {
    dice.roll();
    if (dice.numDoubles() == 3) {
      currplayer.moveToJail();
      return false;
    }
    return true;
  }

  /**
   * If true, player can roll the dice, otherwise stays in jail
   * 
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

  public Dice getDice() {
    return dice;
  }

  //return boolean indicating if more input is needed
  public boolean move() {
    int pos = currplayer.move(dice.getRollSum());
    currSquare = board.getSquare(pos);
    return OwnableManager.isOwned(pos);
  }

  public String play(int input) {
    String msg = currSquare.executeEffect(currplayer, input);
    // edge case: what if player changed positions after executeEffect?
    if (currplayer.isBankrupt()) {
      q.remove();
    }
    return msg;
  }

  public void trade(Player p) {
    new Trader(currplayer, p);
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
}
