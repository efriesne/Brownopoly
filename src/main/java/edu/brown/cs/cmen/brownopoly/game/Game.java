package edu.brown.cs.cmen.brownopoly.game;

import edu.brown.cs.cmen.brownopoly.gamestate.Loader;
import edu.brown.cs.cmen.brownopoly.gamestate.Saver;
import edu.brown.cs.cmen.brownopoly.player.PlayerBuilder;

/**
 * 
 * @author npucel
 *
 */
public class Game {

  private static GameSettings settings;
  private Saver saver;
  private Loader loader;
  private PlayerBuilder playerBuilder;
  private Referee ref;

  public Game(Referee ref, GameSettings settings) {
    this.settings = settings;
    this.ref = ref;
  }

  public static final int numHousesForHotel() {
    return settings.getNumHousesforHotel();
  }

  public Referee getReferee() {
    return ref;
  }
}
