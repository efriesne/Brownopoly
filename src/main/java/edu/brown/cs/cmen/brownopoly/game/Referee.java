package edu.brown.cs.cmen.brownopoly.game;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

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
  

  
  public Player nextTurn() {
    if (!dice.isDoubles() || currplayer.isInJail()) {
      currplayer = q.remove();
      dice = new Dice();
      q.add(currplayer);
    }
    return currplayer;
      //Trade trade = currplayer.startTurn();
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

  public void roll() {
    rollDice();
    if (dice.numDoubles() == 3) {
      currplayer.moveToJail();
    }
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

  public void move() {
    int pos = currplayer.move(dice.getRollSum());
    currSquare = board.getSquare(pos);
  }

  public String play(int input) {
    String msg = currSquare.executeEffect(currplayer, input);
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
}
