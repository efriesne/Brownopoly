package edu.brown.cs.cmen.brownopoly.player;

import edu.brown.cs.cmen.brownopoly.game.GameSettings;

public class PlayerBuilder {
  public PlayerBuilder() {

  }

  public Human createHuman(String name, GameSettings settings) {
    Human h = new Human(name);

    // h.setBalance(settings.getStartCash());

    return new Human(name);
  }

  // public AIPlayer createAI() {}
}
