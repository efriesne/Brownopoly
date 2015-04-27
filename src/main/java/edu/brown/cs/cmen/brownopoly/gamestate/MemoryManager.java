package edu.brown.cs.cmen.brownopoly.gamestate;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import edu.brown.cs.cmen.brownopoly.game.Game;

public class MemoryManager {

  private final String defLocation = "saved/";

  public MemoryManager() {

  }

  public void save(Game game, String location) throws IOException {
    // check location is valid
    FileOutputStream fileOut = new FileOutputStream(defLocation + location
        + ".ser");
    ObjectOutputStream out = new ObjectOutputStream(fileOut);
    out.writeObject(game);
    out.close();
    fileOut.close();
  }

  public Game load(String location) throws IOException, ClassNotFoundException {
    FileInputStream fileIn = new FileInputStream(defLocation + location
        + ".ser");
    ObjectInputStream in = new ObjectInputStream(fileIn);
    Game g = (Game) in.readObject();
    in.close();
    fileIn.close();
    return g;
  }

}
