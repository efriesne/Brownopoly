package edu.brown.cs.cmen.brownopoly.game;


/**
 * 
 * @author npucel
 *
 */
public class MonopolyConstants {

  // map from property ids to list of rents
  // HashMap<Integer, ArrayList<Intger>> rents
  private static final int[] RAILROAD_RENTS = { 0, 25, 50, 100, 200 };
  private static int[][] PROPERTY_RENTS;

  // map from property ids to monopoly object

  // array of prices
  private static final int[] PRICES = { 60, 60, 100, 100, 120, 140, 140, 160,
      180, 180, 200, 220, 220, 240, 260, 260, 280, 300, 300, 320, 350, 400 };

  private MonopolyConstants() {
  }

  public static int getPropertyPrice(int id) {
    checkValidID(id, PRICES);
    return PRICES[id];
  }

  public static int getPropertyRent(int id, int houses) {

    return 0;
  }

  public static int getRailroadRent(int numRailroads) {
    checkValidID(numRailroads, RAILROAD_RENTS);
    return RAILROAD_RENTS[numRailroads];
  }

  private static void checkValidID(int ind, int[] array) {
    if (ind < 0 || ind >= array.length) {
      throw new IllegalArgumentException("Invalid ID");
    }
  }

  /* friendly */static void intialize(GameSettings gs) {
    // properties should be created in Board
    // how will we get Monopolies in here?
    // init rents array/map based on number of houses for hotel
  }

}
