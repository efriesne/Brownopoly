package edu.brown.cs.cmen.brownopoly.game;

import java.util.Arrays;
import java.util.List;

/**
 * 
 * @author npucel
 *
 */
public class MonopolyConstants {

  private static final int[] RAILROAD_RENTS = {0, 25, 50, 100, 200};

  /*
   * Constants arrays for property rents, one for if the GameSettings specifies
   * a Player needs 4 houses for a hotel, and one for if the Player needs 3
   * houses for a hotel (fast play)
   */
  private static final int[][] PROPERTY_RENTS_3 = {
  /* 0 */{2, 10, 30, 90, 250}, /* 1 */{4, 20, 60, 180, 450},
  /* 2 */{6, 30, 90, 270, 550}, /* 3 */{6, 30, 90, 270, 550},
  /* 4 */{8, 40, 100, 300, 600}, /* 5 */{10, 50, 150, 450, 750},
  /* 6 */{10, 50, 150, 450, 750},/* 7 */{12, 60, 180, 500, 900},
  /* 8 */{14, 70, 200, 550, 950},/* 9 */{14, 70, 200, 550, 950},
  /* 10 */{16, 80, 220, 600, 1000},/* 11 */{18, 90, 250, 700, 1050},
  /* 12 */{18, 90, 250, 700, 1050},/* 13 */{20, 100, 300, 750, 1100},
  /* 14 */{22, 110, 330, 800, 1150},/* 15 */{22, 110, 330, 800, 1150},
  /* 16 */{24, 120, 360, 850, 1200},/* 17 */{26, 130, 390, 900, 1275},
  /* 18 */{26, 130, 390, 900, 1275},/* 19 */{28, 150, 450, 1000, 1400},
  /* 20 */{35, 175, 500, 1100, 1500},/* 21 */{50, 200, 600, 1400, 2000}};

  private static final int[][] PROPERTY_RENTS_4 = {
  /* 0 */{2, 10, 30, 90, 160, 250}, /* 1 */{4, 20, 60, 180, 320, 450},
  /* 2 */{6, 30, 90, 270, 400, 550}, /* 3 */{6, 30, 90, 270, 400, 550},
  /* 4 */{8, 40, 100, 300, 450, 600}, /* 5 */{10, 50, 150, 450, 625, 750},
  /* 6 */{10, 50, 150, 450, 625, 750},/* 7 */{12, 60, 180, 500, 700, 900},
  /* 8 */{14, 70, 200, 550, 750, 950},/* 9 */{14, 70, 200, 550, 750, 950},
  /* 10 */{16, 80, 220, 600, 800, 1000},/* 11 */{18, 90, 250, 700, 875, 1050},
  /* 12 */{18, 90, 250, 700, 875, 1050},/* 13 */{20, 100, 300, 750, 925, 1100},
  /* 14 */{22, 110, 330, 800, 975, 1150},
  /* 15 */{22, 110, 330, 800, 975, 1150},
  /* 16 */{24, 120, 360, 850, 1025, 1200},
  /* 17 */{26, 130, 390, 900, 1100, 1275},
  /* 18 */{26, 130, 390, 900, 1100, 1275},
  /* 19 */{28, 150, 450, 1000, 1200, 1400},
  /* 20 */{35, 175, 500, 1100, 1300, 1500},
  /* 21 */{50, 200, 600, 1400, 1700, 2000}};

    public static final List<String> CHANCE_DECK = Arrays.asList(
            "Advance to Go", "Bank Dividend", "Go Back 3 Spaces", "Nearest Utility",
            "Go to Jail", "Poor Tax", "St. Charles Place", "Elected Chairman",
            "Nearest Railroad", "Reading Railroad", "Boardwalk", "Loan Matures",
            "Illinois Ave.", "Get Out of Jail Free", "General Repairs"
    );

    public static final List<String> COMMUNITY_DECK = Arrays.asList(
            "Doctor Fee", "XMAS Fund", "Opera Opening", "Inheritance", "Services",
            "Income Tax Refund", "Sale of Stock", "School Tax", "Street Repairs",
            "Bank Error", "Life Insurance", "Pay Hospital", "Beauty Contest",
            "Get Out of Jail Free", "Go to Jail", "Advance to Go"
    );

  /**
   * 2D array that maps a property's ID to the IDs of the other properties in
   * its Monopoly
   */
  private static final int[][] PROPERTY_TO_MONOPOLY = {
  /* 0 */{1},/* 1 */{0},/* 2 */{3, 4},/* 3 */{2, 4},/* 4 */{2, 3},
  /* 5 */{6, 7},/* 6 */{5, 7},/* 7 */{5, 6},/* 8 */{9, 10},/* 9 */{8, 10},
  /* 10 */{8, 9},/* 11 */{12, 13},/* 12 */{11, 13},/* 13 */{11, 12},
  /* 14 */{15, 16},/* 15 */{14, 16},/* 16 */{14, 15},/* 17 */{18, 19},
  /* 18 */{17, 19},/* 19 */{17, 18},/* 20 */{21},/* 21 */{20}};

  // array of prices
  private static final int[] PRICES = {60, 60, 100, 100, 120, 140, 140, 160,
      180, 180, 200, 220, 220, 240, 260, 260, 280, 300, 300, 320, 350, 400};

  public static int getPropertyPrice(int id) {
    checkValidIndex(id, PRICES);
    return PRICES[id];
  }

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

  public static int getRailroadRent(int numRailroads) {
    checkValidIndex(numRailroads, RAILROAD_RENTS);
    return RAILROAD_RENTS[numRailroads];
  }

  public static int[] getPropertiesInMonopoly(int id) {
    return PROPERTY_TO_MONOPOLY[id];
  }

  private static void checkValidIndex2D(int ind1, int ind2, int[][] array) {
    if (ind1 < 0 || ind1 >= array.length) {
      throw new IllegalArgumentException("Invalid Index");
    }
    checkValidIndex(ind2, array[ind1]);
  }

  private static void checkValidIndex(int ind, int[] array) {
    if (ind < 0 || ind >= array.length) {
      throw new IllegalArgumentException("Invalid Index");
    }
  }

  private MonopolyConstants() {
  }

}
