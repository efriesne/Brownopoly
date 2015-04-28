package edu.brown.cs.cmen.brownopoly.game;

import java.io.Serializable;

/**
 * 
 * @author npucel
 *
 */
public class Game implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = -762425012327340606L;
  private static GameSettings settings;
  private Referee ref;

  public Game(Referee ref, GameSettings settings) {
    Game.settings = settings;
    this.ref = ref;
  }

  public static final int numHousesForHotel() {
    return settings.getNumHousesforHotel();
  }

  public Referee getReferee() {
    return ref;
  }
}
