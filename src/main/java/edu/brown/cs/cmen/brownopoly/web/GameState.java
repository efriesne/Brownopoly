package edu.brown.cs.cmen.brownopoly.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.brown.cs.cmen.brownopoly.player.Player;

public class GameState {

  private List<PlayerJSON> players;

  public GameState(Collection<Player> players) {
    this.players = new ArrayList<>();
    for (Player p : players) {
      this.players.add(new PlayerJSON(p));
    }
  }

}
