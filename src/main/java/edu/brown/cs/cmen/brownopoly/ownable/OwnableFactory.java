package edu.brown.cs.cmen.brownopoly.ownable;

import java.util.Collection;
import java.util.HashSet;

import edu.brown.cs.cmen.brownopoly.game.GameSettings;
import edu.brown.cs.cmen.brownopoly.game.MonopolyConstants;

public class OwnableFactory {

  private String[] names;
  private int[][] colors;

  public OwnableFactory(GameSettings settings) {
    this.names = settings.getTheme().getNames();
    this.colors = settings.getTheme().getColors();
  }

  public Collection<Ownable> buildAll() {
    // create props
    Collection<Ownable> ownables = new HashSet<>();
    for (int id : MonopolyConstants.PROPERTY_IDS) {
      ownables.add(new Property(id, names[id], colors[id]));
    }
    // create utilities
    for (int id : MonopolyConstants.UTILITY_IDS) {
      ownables.add(new Utility(id, names[id]));
    }
    // create railroads
    for (int id : MonopolyConstants.RAILROAD_IDS) {
      ownables.add(new Railroad(id, names[id]));
    }
    return ownables;
  }

}
