package edu.brown.cs.cmen.brownopoly.player;

import java.util.ArrayList;
import java.util.List;

import edu.brown.cs.cmen.brownopoly.board.Board;
import edu.brown.cs.cmen.brownopoly.ownable.OwnableManager;
import edu.brown.cs.cmen.brownopoly.ownable.Property;

public class PlayerBuilder {

  private boolean isHuman;
  private String name;
  private int numStartingProperties;
  private Board board;
  private String id;

  public PlayerBuilder(int playerNumber) {
    isHuman = true;
    name = "Player " + (playerNumber + 1);
    numStartingProperties = 0;
  }

  public PlayerBuilder isAI() {
    isHuman = false;
    return this;
  }

  public PlayerBuilder withName(String name) {
    if (!name.trim().equals("")) {
      this.name = name;
    }
    return this;
  }

  public PlayerBuilder withStartingProperties(int numStarting) {
    numStartingProperties = numStarting;
    return this;
  }

  public PlayerBuilder withBoard(Board board) {
    this.board = board;
    return this;
  }

  public PlayerBuilder withID(String id) {
    this.id = id;
    return this;
  }

  public Player build() {
    List<Property> starting = new ArrayList<>();
    for (int i = 0; i < numStartingProperties; i++) {
      Property prop = OwnableManager.getRandomProperty();
      OwnableManager.addOwned(prop);
      starting.add(prop);
    }
    Player player;
    if (isHuman) {
      player = new Human(name, starting, false, id);
    } else {
      player = new AI(name, starting, true, board, id);
    }
    return player;
  }
}
