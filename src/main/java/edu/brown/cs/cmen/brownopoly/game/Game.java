package edu.brown.cs.cmen.brownopoly.game;

/**
 * 
 * @author npucel
 *
 */
public class Game {

  private static GameSettings settings;
  private SaverLoader saverLoader;
  private PlayerBuilder playerBuilder;

  public static final int numHousesForHotel() {
    return settings.getNumHousesforHotel();
  }
}
