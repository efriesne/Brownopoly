package edu.brown.cs.cmen.brownopoly.ownable;

import java.util.Map;

public class OwnableManager {

  private static final OwnableFactory factory = new OwnableFactory();
  private static final Map<Integer, Property> properties = factory
      .buildProperties();
  private static final Map<Integer, Railroad> railroads = factory
      .buildRailroads();
  private static final Map<Integer, Property> utilities = factory
      .buildUtilities();

  public static Property getProperty(int i) {
    return null;
  }

  public static Property getRandomProperty() {
    // TODO Auto-generated method stub
    return null;
  }

}
