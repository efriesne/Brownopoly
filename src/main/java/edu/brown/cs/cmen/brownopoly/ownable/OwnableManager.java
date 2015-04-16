package edu.brown.cs.cmen.brownopoly.ownable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OwnableManager {

  private static Map<Integer, Property> properties;
  private static Map<Integer, Railroad> railroads;
  private static Map<Integer, Utility> utilities;
  private static Map<Integer, Boolean> owned;

  public static void addProperty(int id, Property p) {
    properties.put(id, p);
  }

  public static Property getProperty(int i) {
    if (!properties.containsKey(i)) {
      throw new IllegalArgumentException("invalid property id");
    }
    return properties.get(i);
  }

  public static void addRailroad(int id, Railroad r) {
    railroads.put(id, r);
  }

  public static Railroad getRailroad(int i) {
    if (!railroads.containsKey(i)) {
      throw new IllegalArgumentException("invalid railroad id");
    }
    return railroads.get(i);
  }

  public static void addUtility(int id, Utility u) {
    utilities.put(id, u);
  }

  public static Utility getUtility(int i) {
    if (!utilities.containsKey(i)) {
      throw new IllegalArgumentException("invalid utilities id");
    }
    return utilities.get(i);
  }

  public static Property getRandomProperty() {
    List<Property> unowned = findUnownedProperties();
    if (unowned.isEmpty()) {
      return null;
    }
    return unowned.get((int) (Math.random() * unowned.size()));
  }

  private static List<Property> findUnownedProperties() {
    List<Property> unowned = new ArrayList<>();
    for (Property p : properties.values()) {
      if (p.owner() == null) {
        unowned.add(p);
      }
    }
    return unowned;
  }
}
