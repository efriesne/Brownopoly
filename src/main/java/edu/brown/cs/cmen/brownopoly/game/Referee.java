package edu.brown.cs.cmen.brownopoly.game;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import edu.brown.cs.cmen.brownopoly.board.Board;
import edu.brown.cs.cmen.brownopoly.board.BoardSquare;
import edu.brown.cs.cmen.brownopoly.ownable.Ownable;
import edu.brown.cs.cmen.brownopoly.ownable.OwnableManager;
import edu.brown.cs.cmen.brownopoly.ownable.Property;
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

  // for testing, used in GUIRunner.DummyHandler
  public void fillDummyPlayer() {
    Player player1 = q.peek();
    if (player1 == null) {
      return;
    }

    player1.buyProperty(OwnableManager.getProperty(1));
    player1.buyProperty(OwnableManager.getProperty(3));
    player1.buyProperty(OwnableManager.getProperty(6));
    player1.buyProperty(OwnableManager.getProperty(8));
    player1.buyProperty(OwnableManager.getProperty(9));
    player1.buyProperty(OwnableManager.getProperty(11));
    player1.mortgageOwnable(OwnableManager.getProperty(11));
    player1.buyRailroad(OwnableManager.getRailroad(15));
    player1.buyUtility(OwnableManager.getUtility(28));
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
   * //can try to roll doubles
   * 
   * @return
   */

  // returns a boolean to see if you can move
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

  // return boolean indicating if more input is needed
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

  public Player getCurrPlayer() {
    return currPlayer;
  }

  public void handleMortgage(int ownableId, boolean mortgaging) {
    Ownable curr = null;
    try {
      curr = OwnableManager.getProperty(ownableId);
    } catch (IllegalArgumentException e) {
    }
    try {
      curr = OwnableManager.getUtility(ownableId);
    } catch (IllegalArgumentException e) {
    }
    try {
      curr = OwnableManager.getRailroad(ownableId);
    } catch (IllegalArgumentException e) {
    }
    assert curr != null;
    assert curr.owner() == currPlayer;

    if (mortgaging) {
      currPlayer.mortgageOwnable(curr);
    } else {
      currPlayer.demortgageOwnable(curr);
    }
  }

  public void handleHouseTransactions(int id, boolean isBuying, int numHouses) {
    if (numHouses < 0) {
      numHouses *= -1;
    }
    for (int i = 0; i < numHouses; i++) {
      handleHouse(id, isBuying);
    }
  }

  private void handleHouse(int id, boolean isBuying) {
    Property p = OwnableManager.getProperty(id);
    assert p.owner() != null;
    if (isBuying) {
      p.owner().buyHouse(p);
    } else {
      p.owner().sellHouse(p);
    }
  }

  public int[] findValidBuilds() {
    List<Property> valids = currPlayer.getValidBuildProps();
    int[] validIds = new int[valids.size()];
    for (int i = 0; i < validIds.length; i++) {
      validIds[i] = valids.get(i).getId();
    }
    return validIds;
  }

  public int[] findValidSells() {
    List<Property> valids = currPlayer.getValidSellProps();
    int[] validIds = new int[valids.size()];
    for (int i = 0; i < validIds.length; i++) {
      validIds[i] = valids.get(i).getId();
    }
    return validIds;
  }

}
