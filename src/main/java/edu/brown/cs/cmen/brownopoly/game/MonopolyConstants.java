package edu.brown.cs.cmen.brownopoly.game;

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

  // map from property ids to monopoly object

  // array of prices
  private static final int[] PRICES = {60, 60, 100, 100, 120, 140, 140, 160,
      180, 180, 200, 220, 220, 240, 260, 260, 280, 300, 300, 320, 350, 400};

  private MonopolyConstants() {
  }

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

}
