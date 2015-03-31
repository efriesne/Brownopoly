package edu.brown.cs.cmen.brownopoly.player;

import edu.brown.cs.cmen.brownopoly.game.GameSettings;
import edu.brown.cs.cmen.brownopoly.ownable.OwnableManager;
import edu.brown.cs.cmen.brownopoly.ownable.Property;

public class PlayerBuilder {
  public PlayerBuilder() {

  }

  public Human createHuman(String name, GameSettings settings) {

    Property[] starting = new Property[settings.getStartProperties()];
    for (int i = 0; i < starting.length; i++) {
      starting[i] = OwnableManager.getRandomProperty();
    }
    Human h = new Human(name, starting);
    h.setBalance(settings.getStartCash());

    return h;
  }
  // public AIPlayer createAI() {}
}
