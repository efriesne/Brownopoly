package edu.brown.cs.cmen.brownopoly.ownable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OwnableManager {

  private static Map<Integer, Property> properties = new HashMap<>();
  private static Map<Integer, Railroad> railroads = new HashMap<>();
  private static Map<Integer, Utility> utilities = new HashMap<>();
  private static Map<Integer, Ownable> unowned = new HashMap<>();

  public static void initMonopolies() {
    for (Property p : properties.values()) {
      p.joinMonopoly();
    }
  }

  public static void addProperty(Property p) {
    properties.put(p.getId(), p);
    addUnowned(p);
  }

  public static Property getProperty(int i) {
    return properties.get(i);
  }

  public static void addRailroad(Railroad r) {
    railroads.put(r.getId(), r);
    addUnowned(r);
  }

  public static Railroad getRailroad(int i) {
    return railroads.get(i);
  }

  public static void addUtility(Utility u) {
    utilities.put(u.getId(), u);
    addUnowned(u);
  }

  public static Utility getUtility(int i) {
    return utilities.get(i);
  }

  public static void addUnowned(Ownable o) {
    unowned.put(o.getId(), o);
  }

  public static void addOwned(Ownable o) {
    unowned.remove(o.getId());
  }

  public static boolean isOwned(int id) {
    return !unowned.containsKey(id);
  }

  public static Property getRandomProperty() {
    List<Property> unowned = findUnownedProperties();
    if (unowned.isEmpty()) {
      return null;
    }
    return unowned.get((int) (Math.random() * unowned.size()));
  }

  public static Ownable getOwnable(int id) {
    if (railroads.containsKey(id)) {
      return railroads.get(id);
    }
    if (properties.containsKey(id)) {
      return properties.get(id);
    }
    if (utilities.containsKey(id)) {
      return utilities.get(id);
    }
    throw new IllegalArgumentException("Invalid id");
  }

  public static List<Property> findUnownedProperties() {
    List<Property> unowned = new ArrayList<>();
    for (Property p : properties.values()) {
      if (p.owner() == null) {
        unowned.add(p);
      }
    }
    return unowned;
  }
}
