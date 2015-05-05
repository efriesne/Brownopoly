package edu.brown.cs.cmen.brownopoly.game;

import java.util.Arrays;
import java.util.List;

import edu.brown.cs.cmen.brownopoly.customboards.BoardTheme;

/**
 * 
 * @author npucel
 *
 */
public class MonopolyConstants {

  public static final int INITIAL_BANK_BALANCE = 1500;
  public static final int NUM_BOARDSQUARES = 40;
  public static final int GO_CASH = 200;
  public static final int EXPECTED_DICE_ROLL = 7;
  public static final int JAIL_BAIL = 50;
  public static final int INCOME_TAX = 200;
  public static final int LUXURY_TAX = 75;
  public static final int DEFAULT_RISK_AVERSION_LEVEL = 2;
  public static final int NUM_OWNABLES = 28;
  public static final double OWNED_CAPACITY_THRESHOLD = 0.75;
  public static final int DEFAULT_FREE_PARKING = 50;
  public static final int AI_DESIRED_ROUNDS_COMPENSATION = 50;
  public static final int AI_MINIMUM_SAFE_BALANCE = 75;

  // Board indexes/ids
  public static final int[] PROPERTY_IDS = {1, 3, 6, 8, 9, 11, 13, 14, 16, 18,
      19, 21, 23, 24, 26, 27, 29, 31, 32, 34, 37, 39};
  public static final int[] RAILROAD_IDS = {5, 15, 25, 35};
  public static final int[] UTILITY_IDS = {12, 28};
  public static final int[] CHANCE_IDS = {7, 22, 36};
  public static final int[] COMMUNITY_IDS = {2, 17, 33};
  public static final int GO_ID = 0;
  public static final int INCOMETAX_ID = 4;
  public static final int LUXURYTAX_ID = 38;
  public static final int JUSTVISITING_ID = 10;
  public static final int FREEPARKING_ID = 20;
  public static final int GOTOJAIL_ID = 30;

  private static final int[] RAILROAD_RENTS = {0, 25, 50, 100, 200};

  public static int getRailroadRent(int numRailroads) {
    checkValidIndex(numRailroads, RAILROAD_RENTS);
    return RAILROAD_RENTS[numRailroads];
  }

  private static final int[] UTILITY_RENTS = {0, 4, 10};

  public static int getUtilityRent(int numUtilities) {
    checkValidIndex(numUtilities, RAILROAD_RENTS);
    return UTILITY_RENTS[numUtilities];
  }

  /*
   * Constants arrays for property rents, one for if the GameSettings specifies
   * a Player needs 4 houses for a hotel, and one for if the Player needs 3
   * houses for a hotel (fast play)
   */
  private static final int[][] PROPERTY_RENTS_3 = {null,
  /* 1 */{2, 10, 30, 90, 250}, null,
  /* 3 */{4, 20, 60, 180, 450}, null, null,
  /* 6 */{6, 30, 90, 270, 550}, null,
  /* 8 */{6, 30, 90, 270, 550},
  /* 9 */{8, 40, 100, 300, 600}, null,
  /* 11 */{10, 50, 150, 450, 750}, null,
  /* 13 */{10, 50, 150, 450, 750},
  /* 14 */{12, 60, 180, 500, 900}, null,
  /* 16 */{14, 70, 200, 550, 950}, null,
  /* 18 */{14, 70, 200, 550, 950},
  /* 19 */{16, 80, 220, 600, 1000}, null,
  /* 21 */{18, 90, 250, 700, 1050}, null,
  /* 23 */{18, 90, 250, 700, 1050},
  /* 24 */{20, 100, 300, 750, 1100}, null,
  /* 26 */{22, 110, 330, 800, 1150},
  /* 27 */{22, 110, 330, 800, 1150}, null,
  /* 29 */{24, 120, 360, 850, 1200}, null,
  /* 31 */{26, 130, 390, 900, 1275},
  /* 32 */{26, 130, 390, 900, 1275}, null,
  /* 34 */{28, 150, 450, 1000, 1400}, null, null,
  /* 37 */{35, 175, 500, 1100, 1500}, null,
  /* 39 */{50, 200, 600, 1400, 2000}};

  private static final int[][] PROPERTY_RENTS_4 = {null,
  /* 1 */{2, 10, 30, 90, 160, 250}, null,
  /* 3 */{4, 20, 60, 180, 320, 450}, null, null,
  /* 6 */{6, 30, 90, 270, 400, 550}, null,
  /* 8 */{6, 30, 90, 270, 400, 550},
  /* 9 */{8, 40, 100, 300, 450, 600}, null,
  /* 11 */{10, 50, 150, 450, 625, 750}, null,
  /* 13 */{10, 50, 150, 450, 625, 750},
  /* 14 */{12, 60, 180, 500, 700, 900}, null,
  /* 16 */{14, 70, 200, 550, 750, 950}, null,
  /* 18 */{14, 70, 200, 550, 750, 950},
  /* 19 */{16, 80, 220, 600, 800, 1000}, null,
  /* 21 */{18, 90, 250, 700, 875, 1050}, null,
  /* 23 */{18, 90, 250, 700, 875, 1050},
  /* 24 */{20, 100, 300, 750, 925, 1100}, null,
  /* 26 */{22, 110, 330, 800, 975, 1150},
  /* 27 */{22, 110, 330, 800, 975, 1150}, null,
  /* 29 */{24, 120, 360, 850, 1025, 1200}, null,
  /* 31 */{26, 130, 390, 900, 1100, 1275},
  /* 32 */{26, 130, 390, 900, 1100, 1275}, null,
  /* 34 */{28, 150, 450, 1000, 1200, 1400}, null, null,
  /* 37 */{35, 175, 500, 1100, 1300, 1500}, null,
  /* 39 */{50, 200, 600, 1400, 1700, 2000}};

  public static int getPropertyRent(int id, int houses) {
    switch (Game.numHousesForHotel()) {
      case 3:
        checkValidIndex2D(id, houses, PROPERTY_RENTS_3);
        return PROPERTY_RENTS_3[id][houses];
      case 4:
        checkValidIndex2D(id, houses, PROPERTY_RENTS_4);
        return PROPERTY_RENTS_4[id][houses];
      default:
        throw new RuntimeException("Invalid number of Houses for Hotel");
    }
  }

  public static int[] getPropertyRentArray(int id) {
    switch (Game.numHousesForHotel()) {
      case 3:
        checkValidIndex2D(id, 0, PROPERTY_RENTS_3);
        return PROPERTY_RENTS_3[id];
      case 4:
        checkValidIndex2D(id, 0, PROPERTY_RENTS_4);
        return PROPERTY_RENTS_4[id];
      default:
        throw new RuntimeException("Invalid number of Houses for Hotel");
    }
  }

  public static final List<String> CHANCE_DECK = Arrays.asList("Advance to Go",
      "Bank Dividend", "Go Back 3 Spaces", "Nearest Utility", "Go to Jail",
      "Poor Tax", "St. Charles Place", "Elected Chairman", "Nearest Railroad",
      "Reading Railroad", "Boardwalk", "Loan Matures", "Illinois Ave.",
      "Get Out of Jail Free", "General Repairs");

  public static final List<String> COMMUNITY_DECK = Arrays.asList("Doctor Fee",
      "XMAS Fund", "Opera Opening", "Inheritance", "Services",
      "Income Tax Refund", "Sale of Stock", "School Tax", "Street Repairs",
      "Bank Error", "Life Insurance", "Pay Hospital", "Beauty Contest",
      "Get Out of Jail Free", "Go to Jail", "Advance to Go");

  public static final int[][] DEFAULT_BOARD_COLORS = { {},
  /* 1 */{75, 0, 130}, {},
  /* 3 */{75, 0, 130}, {}, {},
  /* 6 */{135, 206, 235}, {},
  /* 8 */{135, 206, 235},
  /* 9 */{135, 206, 235}, {},
  /* 11 */{255, 0, 255}, {},
  /* 13 */{255, 0, 255},
  /* 14 */{255, 0, 255}, {},
  /* 16 */{255, 165, 0}, {},
  /* 18 */{255, 165, 0},
  /* 19 */{255, 165, 0}, {},
  /* 21 */{255, 0, 0}, {},
  /* 23 */{255, 0, 0},
  /* 24 */{255, 0, 0}, {},
  /* 26 */{255, 255, 0},
  /* 27 */{255, 255, 0}, {},
  /* 29 */{255, 255, 0}, {},
  /* 31 */{0, 128, 0},
  /* 32 */{0, 128, 0}, {},
  /* 34 */{0, 128, 0}, {}, {},
  /* 37 */{0, 33, 217}, {},
  /* 39 */{0, 33, 217}};

  public static final String[] DEFAULT_BOARD_NAMES = {"GO",
      "MEDITERRANEAN AVENUE", "COMMUNITY CHEST", "BALTIC AVENUE", "INCOME TAX",
      "READING RAILROAD", "ORIENTAL AVENUE", "CHANCE", "VERMONT AVENUE",
      "CONNECTICUT AVENUE", "JUST VISITING", "ST. CHARLES PLACE",
      "ELECTRIC COMPANY", "STATES AVENUE", "VIRGINIA AVENUE",
      "PENNSYLVANIA RAILROAD", "ST. JAMES PLACE", "COMMUNITY CHEST",
      "TENNESSEE AVENUE", "NEW YORK AVENUE", "FREE PARKING", "KENTUCKY AVENUE",
      "CHANCE", "INDIANA AVENUE", "ILLINOIS AVENUE", "B. & O. AVENUE",
      "ATLANTIC AVENUE", "VENTNOR AVENUE", "WATER WORKS", "MARVIN GARDENS",
      "GO TO JAIL", "PACIFIC AVENUE", "NORTH CAROLINA AVENUE",
      "COMMUNITY CHEST", "PENNSYLVANIA AVENUE", "SHORT LINE", "CHANCE",
      "PARK PLACE", "LUXURY TAX", "BOARDWALK"};

  public static final BoardTheme DEFAULT_THEME = new BoardTheme(
      DEFAULT_BOARD_NAMES, DEFAULT_BOARD_COLORS);

  /**
   * 2D array that maps a property's ID to the IDs of the other properties in
   * its Monopoly
   */
  private static final int[][] PROPERTY_TO_MONOPOLY = {null,
  /* 1 */{3}, null,
  /* 3 */{1}, null, null,
  /* 6 */{8, 9}, null,
  /* 8 */{6, 9},
  /* 9 */{6, 8}, null,
  /* 11 */{13, 14}, null,
  /* 13 */{11, 14},
  /* 14 */{11, 13}, null,
  /* 16 */{18, 19}, null,
  /* 18 */{16, 19},
  /* 19 */{16, 18}, null,
  /* 21 */{23, 24}, null,
  /* 23 */{21, 24},
  /* 24 */{21, 23}, null,
  /* 26 */{27, 29},
  /* 27 */{26, 29}, null,
  /* 29 */{26, 27}, null,
  /* 31 */{32, 34},
  /* 32 */{31, 34}, null,
  /* 34 */{31, 32}, null, null,
  /* 37 */{39}, null,
  /* 39 */{37}};

  public static int[] getPropertiesInMonopoly(int id) {
    return PROPERTY_TO_MONOPOLY[id];
  }

  // array of prices
  private static final int[] PRICES = {-1,
  /* 1 */60, -1,
  /* 3 */60, -1, -1,
  /* 6 */100, -1,
  /* 8 */100,
  /* 9 */120, -1,
  /* 11 */140, -1,
  /* 13 */140,
  /* 14 */160, -1,
  /* 16 */180, -1,
  /* 18 */180,
  /* 19 */200, -1,
  /* 21 */220, -1,
  /* 23 */220,
  /* 24 */240, -1,
  /* 26 */260,
  /* 27 */260, -1,
  /* 29 */280, -1,
  /* 31 */300,
  /* 32 */300, -1,
  /* 34 */320, -1, -1,
  /* 37 */350, -1,
  /* 39 */400};

  public static int getPropertyPrice(int id) {
    checkValidIndex(id, PRICES);
    return PRICES[id];
  }

  // array of prices
  private static final int[] HOUSE_COSTS = {-1,
  /* 1 */50, -1,
  /* 3 */50, -1, -1,
  /* 6 */50, -1,
  /* 8 */50,
  /* 9 */50, -1,
  /* 11 */100, -1,
  /* 13 */100,
  /* 14 */100, -1,
  /* 16 */100, -1,
  /* 18 */100,
  /* 19 */100, -1,
  /* 21 */150, -1,
  /* 23 */150,
  /* 24 */150, -1,
  /* 26 */150,
  /* 27 */150, -1,
  /* 29 */150, -1,
  /* 31 */200,
  /* 32 */200, -1,
  /* 34 */200, -1, -1,
  /* 37 */200, -1,
  /* 39 */200};

  public static int getHouseCost(int id) {
    checkValidIndex(id, HOUSE_COSTS);
    return HOUSE_COSTS[id];
  }

  private static void checkValidIndex2D(int ind1, int ind2, int[][] array) {
    if (ind1 < 0 || ind1 >= array.length) {
      throw new IllegalArgumentException("Invalid Index: " + ind1);
    }
    try {
      checkValidIndex(ind2, array[ind1]);
    } catch (IllegalArgumentException e) {
      System.out.println("Property id: " + ind1);
      System.out.println("Houses: " + ind2);
      throw new IllegalArgumentException();
    }
  }

  private static void checkValidIndex(int ind, int[] array) {
    if (ind < 0 || ind >= array.length) {
      throw new IllegalArgumentException("Invalid Index: " + ind);
    }
  }

  private MonopolyConstants() {
  }

}
