package edu.brown.cs.cmen.brownopoly.player;

import java.util.ArrayList;
import java.util.List;

import edu.brown.cs.cmen.brownopoly.board.Board;
import edu.brown.cs.cmen.brownopoly.ownable.OwnableManager;
import edu.brown.cs.cmen.brownopoly.ownable.Property;

public class PlayerBuilder_new {

  private boolean isHuman;
  private String name;
  private int numStartingProperties, startCash;
  private Board board;

  public PlayerBuilder_new(int playerNumber) {
    isHuman = true;
    name = "Player " + playerNumber;
    numStartingProperties = 0;
    startCash = 0;
  }

  public PlayerBuilder_new isAI() {
    isHuman = false;
    return this;
  }

  public PlayerBuilder_new withName(String name) {
    this.name = name;
    return this;
  }

  public PlayerBuilder_new withStartingProperties(int numStarting) {
    numStartingProperties = numStarting;
    return this;
  }

  public PlayerBuilder_new withStartCash(int startCash) {
    this.startCash = startCash;
    return this;
  }

  public PlayerBuilder_new withBoard(Board board) {
    this.board = board;
    return this;
  }

  public Player build() {
    List<Property> starting = new ArrayList<>();
    for (int i = 0; i < numStartingProperties; i++) {
      starting.add(OwnableManager.getRandomProperty());
    }
    Player player = null;
    if (isHuman) {
      player = new Human(name, starting, false);
    } else {
      player = new AI(name, starting, true, board);
    }
    return player;
  }
}
