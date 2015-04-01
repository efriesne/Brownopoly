package edu.brown.cs.cmen.brownopoly.player;

import java.util.ArrayList;
import java.util.List;

import edu.brown.cs.cmen.brownopoly.game.GameSettings;
import edu.brown.cs.cmen.brownopoly.ownable.OwnableManager;
import edu.brown.cs.cmen.brownopoly.ownable.Property;

public class PlayerBuilder {
  public PlayerBuilder() {

  }

  public Player createPlayer(String name, GameSettings settings, int type) {
    List<Property> starting = new ArrayList<>();
    for (int i = 0; i < settings.getStartProperties(); i++) {
      starting.add(OwnableManager.getRandomProperty());
    }
    Player player = null;
    switch (type) {
      case 0:
        player = new Human(name, starting);
      case 1:
        // player = new AI(name, starting);
      default:
        // error
        break;
    }

    player.setBalance(settings.getStartCash());

    return player;
  }
  // public AIPlayer createAI() {}
}
