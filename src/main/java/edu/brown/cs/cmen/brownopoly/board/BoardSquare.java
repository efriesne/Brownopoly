package edu.brown.cs.cmen.brownopoly.board;

import edu.brown.cs.cmen.brownopoly.player.Player;

public abstract class BoardSquare {
  private int id;
  private String name;

  public BoardSquare(String name, int id) {
    this.name = name;
    this.id = id;
  }

  public int getId() {
    return id;
<<<<<<< HEAD
  }  
=======
  }

  // public String getSquareName() {
  // return MonopolyConstants.getName(id);
  // }
>>>>>>> 35d92d7f3545b6ddd2e935e463b0a9f51fa7840e

  public String getName() {
    return name;
  }

<<<<<<< HEAD
  public abstract int setupEffect();
  
  public abstract String executeEffect(Player p, int userInput);
  
=======
  public abstract String executeEffect(Player p);

>>>>>>> 35d92d7f3545b6ddd2e935e463b0a9f51fa7840e
}
