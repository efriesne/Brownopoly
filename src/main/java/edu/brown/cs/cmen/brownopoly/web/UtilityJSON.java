package edu.brown.cs.cmen.brownopoly.web;

import edu.brown.cs.cmen.brownopoly.ownable.Utility;

class UtilityJSON {

  private boolean mortgaged;
  private String name;
  private int id;

  public UtilityJSON(Utility utility) {
    mortgaged = utility.isMortgaged();
    name = utility.getName();
    id = utility.getId();
  }

}
