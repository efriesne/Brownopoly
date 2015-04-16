package edu.brown.cs.cmen.brownopoly.ownable;

import java.util.Map;

import edu.brown.cs.cmen.brownopoly.customboards.BoardTheme;

public class OwnableManager {

  private static BoardTheme theme;
  private static OwnableFactory factory;
  private static Map<Integer, Property> properties;
  private static Map<Integer, Railroad> railroads;
  private static Map<Integer, Utility> utilities;

  public static void setTheme(BoardTheme t) {
    theme = t;
    factory = new OwnableFactory(theme);
    properties = factory.buildProperties();
    railroads = factory.buildRailroads();
    utilities = factory.buildUtilities();
  }

  public static Property getProperty(int i) {
    if (!properties.containsKey(i)) {
      throw new IllegalArgumentException("invalid property id");
    }
    return properties.get(i);
  }

  public static Railroad getRailroad(int i) {
    if (!railroads.containsKey(i)) {
      throw new IllegalArgumentException("invalid railroad id");
    }
    return railroads.get(i);
  }

  public static Utility getUtility(int i) {
    if (!utilities.containsKey(i)) {
      throw new IllegalArgumentException("invalid utilities id");
    }
    return utilities.get(i);
  }

  public static Property getRandomProperty() {
    return null;
  }

}
