package edu.brown.cs.cmen.brownopoly.player;

import edu.brown.cs.cmen.brownopoly.game.GameSettings;
import edu.brown.cs.cmen.brownopoly.ownable.Property;

public class PlayerBuilder {
  public PlayerBuilder() {

  }

  public Human createHuman(String name, GameSettings settings) {
    Human h = new Human(name);

    h.setBalance(settings.getStartCash());
    Property[] starting = new Property[settings.getStartProperties()];
    for (int i = 0; i < starting.length; i++) {
      starting[i] = PropertyManager.getRandomProperty();
    }

    return new Human(name, starting);
  }
  // public AIPlayer createAI() {}
}
