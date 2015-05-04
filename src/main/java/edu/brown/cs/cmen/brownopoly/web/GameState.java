package edu.brown.cs.cmen.brownopoly.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.brown.cs.cmen.brownopoly.game.Game;
import edu.brown.cs.cmen.brownopoly.player.Player;

public class GameState {

  private List<PlayerJSON> players;
  private int numHousesForHotel;
  private boolean fastPlay;

  public GameState(Collection<Player> players, boolean fastPlay) {
    this.players = new ArrayList<>();
    for (Player p : players) {
      this.players.add(new PlayerJSON(p));
    }
    numHousesForHotel = Game.numHousesForHotel();
    this.fastPlay = fastPlay;
  }

  public PlayerJSON getPlayerByID(String id) {
    for (PlayerJSON p : players) {
      if (p.getID().equals(id)) {
        return p;
      }
    }
    return null;
  }

}
