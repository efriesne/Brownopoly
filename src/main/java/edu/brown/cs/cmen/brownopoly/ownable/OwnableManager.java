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
    if (!properties.containsKey(i)) {
      throw new IllegalArgumentException("invalid property id");
    }
    return properties.get(i);
  }

  public static void addRailroad(Railroad r) {
    railroads.put(r.getId(), r);
    addUnowned(r);
  }

  public static Railroad getRailroad(int i) {
    if (!railroads.containsKey(i)) {
      throw new IllegalArgumentException("invalid railroad id");
    }
    return railroads.get(i);
  }

  public static void addUtility(Utility u) {
    utilities.put(u.getId(), u);
    addUnowned(u);
  }

  public static void addUnowned(Ownable o) {
    unowned.put(o.getId(), o);
  }

  public static void addOwned(Ownable o) {
    if(unowned.containsKey(o.getId())) {
      unowned.remove(o.getId());
    }
  }

  public static boolean isOwned(int id) {
    if(unowned.containsKey(id)) {
      return unowned.get(id).owner() == null;
    } else {
      return false;
    }
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
