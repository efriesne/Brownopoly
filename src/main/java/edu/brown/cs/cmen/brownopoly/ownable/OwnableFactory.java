package edu.brown.cs.cmen.brownopoly.ownable;

import java.util.Map;

import edu.brown.cs.cmen.brownopoly.customboards.BoardTheme;

class OwnableFactory {

  private BoardTheme theme;

  public OwnableFactory(BoardTheme theme) {
    this.theme = theme;
  }

  Map<Integer, Property> buildProperties() {
    return null;
  }

  Map<Integer, Railroad> buildRailroads() {
    return null;
  }

  Map<Integer, Utility> buildUtilities() {
    return null;
  }

}
