package edu.brown.cs.cmen.brownopoly.game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import edu.brown.cs.cmen.brownopoly.board.Board;
import edu.brown.cs.cmen.brownopoly.board.BoardSquare;
import edu.brown.cs.cmen.brownopoly.ownable.Ownable;
import edu.brown.cs.cmen.brownopoly.ownable.OwnableManager;
import edu.brown.cs.cmen.brownopoly.ownable.Property;
import edu.brown.cs.cmen.brownopoly.player.Player;
import edu.brown.cs.cmen.brownopoly.web.GameState;
import edu.brown.cs.cmen.brownopoly.web.PlayerJSON;
import edu.brown.cs.cmen.brownopoly.web.TradeProposalJSON;
import edu.brown.cs.cmen.brownopoly_util.Dice;

/**
 * Class that deals with interplayer things. Handles turns.
 * 
 * @author mprafson
 *
 */
public class Referee implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = -6887490899466474541L;
  private Queue<Player> q;
  private Dice dice;
  private Board board;
  private Player currPlayer;
  private boolean isFastPlay;
  private BoardSquare currSquare;

  public Referee(Board board, List<Player> players, boolean isFastPlay) {
    this.board = board;
    q = randomizeOrder(players);
    this.isFastPlay = isFastPlay;
    currPlayer = q.peek();
    dice = new Dice();
    // fillDummyPlayer();
  }

  private Queue<Player> randomizeOrder(List<Player> players) {
    Queue<Player> list = new LinkedList<>();
    while (!players.isEmpty()) {
      list.add(randomRemove(players));
    }
    return list;
  }

  private Player randomRemove(List<Player> players) {
    assert players.size() > 0;
    int ind = new Random().nextInt(players.size());
    return players.remove(ind);
  }

  // for testing, used in GUIRunner.DummyHandler
  void fillDummyPlayer() {
    Player player1 = q.poll();
    Player player2 = q.poll();
    if (player1 == null || player2 == null) {
      return;
    }
    player1.addToBalance(1000);
    player1.buyOwnable(OwnableManager.getOwnable(1));
    player1.buyOwnable(OwnableManager.getOwnable(3));
    player1.buyOwnable(OwnableManager.getOwnable(11));
    player1.buyOwnable(OwnableManager.getOwnable(13));
    player1.buyOwnable(OwnableManager.getOwnable(14));
    player2.addToBalance(4000);
    player2.buyOwnable(OwnableManager.getOwnable(6));
    player2.buyOwnable(OwnableManager.getOwnable(8));
    player2.buyOwnable(OwnableManager.getOwnable(9));
    player2.buyOwnable(OwnableManager.getOwnable(31));
    player2.buyOwnable(OwnableManager.getOwnable(32));
    player2.buyOwnable(OwnableManager.getOwnable(34));
    player2.buyOwnable(OwnableManager.getOwnable(37));
    player2.buyOwnable(OwnableManager.getOwnable(39));
    player1.buyOwnable(OwnableManager.getOwnable(15));
    player1.buyOwnable(OwnableManager.getOwnable(28));

    q.add(player1);
    q.add(player2);
  }

  public int getNumPlayers() {
    return q.size();
  }

  public Player nextTurn() {
    if (!dice.isDoubles() || !q.contains(currPlayer) || currPlayer.isInJail()) {
      currPlayer = q.remove();
      q.add(currPlayer);
      dice = new Dice();
    }
    currPlayer.startTurn(isFastPlay);
    return currPlayer;
  }

  void pushCurrPlayer() {
    while (q.peek() != currPlayer) {
      q.add(q.remove());
    }
  }

  /**
   * //can try to roll doubles
   * 
   * @return
   */
  public void releaseFromJail(int usedJailCard) {
    if (usedJailCard == 1) {
      currPlayer.payBail();
    } else {
      currPlayer.useJailFree();
    }
  }

  // returns a boolean to see if you can move
  public boolean roll() {
    dice.roll();
    if (currPlayer.isInJail()) {
      if (dice.isDoubles()) {
        currPlayer.exitJail();
        dice.resetDoubles();
        return true;
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
    return !OwnableManager.isOwned(pos);
  }

  public boolean playerCanBuy() {
    return currPlayer.canBuyOwnable(OwnableManager.getOwnable(currSquare
        .getId()));
  }

  public void removeBankruptPlayers() {
    List<Player> bankrupts = new ArrayList<Player>();
    for (Player p : q) {
      if (p.isBankrupt()) {
        bankrupts.add(p);
        p.clear();
      }
    }
    q.removeAll(bankrupts);
  }

  public String play(int input) {
    return currSquare.executeEffect(currPlayer, input);
  }

  public PlayerJSON getPlayerJSON(String playerID) {
    return getCurrGameState().getPlayerByID(playerID);
  }

  public Player getPlayerByID(String id) {
    for (Player p : q) {
      if (p.getId().equals(id)) {
        return p;
      }
    }

    return null;
  }

  public int checkTradeMoney(String playerID, int money) {
    Player player = getPlayerByID(playerID);
    if (money < 0) {
      return 0;
    } else if (money > player.getBalance()) {
      return 1;
    }
    return 2;
  }

  public boolean trade(String recipientID, String[] initProps, int initMoney,
      String[] recipProps, int recipMoney) {
    Player recipient = getPlayerByID(recipientID);
    return currPlayer.trade(recipient, initProps, initMoney, recipProps,
        recipMoney);
  }

  public GameState getCurrGameState() {
    return new GameState(Collections.unmodifiableCollection(q), isFastPlay);
  }

  public BoardSquare getCurrSquare() {
    return currSquare;
  }

  public PlayerJSON getCurrPlayer() {
    return getCurrGameState().getPlayerByID(currPlayer.getId());
  }

  public TradeProposalJSON getAITrade() {
    return new TradeProposalJSON(currPlayer.makeTradeProposal());
  }

  public String getAIBuild() {
    return currPlayer.makeBuildDecision();
  }

  public String getAIPayOff() {
    return currPlayer.makePayOffMortgageDecision();
  }

  public String mortgageAI(String playerID) {
    Player p = getPlayerByID(playerID);
    return p.makeMortgageDecision("");
  }

  public void handleMortgage(int ownableId, boolean mortgaging, String playerID) {
    Player player = getPlayerByID(playerID);
    Ownable curr = OwnableManager.getOwnable(ownableId);
    assert curr != null;
    assert curr.owner() == player;

    if (mortgaging) {
      player.mortgageOwnable(curr);
    } else {
      player.demortgageOwnable(curr);
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

  public int[] findValidBuilds(String playerID) {
    Player player = getPlayerByID(playerID);
    List<Property> valids = player.getValidHouseProps(true);
    int[] validIds = new int[valids.size()];
    for (int i = 0; i < validIds.length; i++) {
      validIds[i] = valids.get(i).getId();
    }
    return validIds;
  }

  public int[] findValidSells(String playerID) {
    Player player = getPlayerByID(playerID);
    List<Property> valids = player.getValidHouseProps(false);
    int[] validIds = new int[valids.size()];
    for (int i = 0; i < validIds.length; i++) {
      validIds[i] = valids.get(i).getId();
    }
    return validIds;
  }

  public int[] findValidMortgages(boolean mortgaging, String playerID) {
    Player player = getPlayerByID(playerID);
    List<Ownable> valids = player.getValidMortgageProps(mortgaging);
    int[] validIds = new int[valids.size()];
    for (int i = 0; i < validIds.length; i++) {
      validIds[i] = valids.get(i).getId();
    }
    return validIds;
  }
}
