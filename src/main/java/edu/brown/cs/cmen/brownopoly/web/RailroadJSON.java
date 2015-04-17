package edu.brown.cs.cmen.brownopoly.web;

import edu.brown.cs.cmen.brownopoly.ownable.Railroad;

class RailroadJSON {

  private boolean mortgaged;
  private String name;
  private int id;

  public RailroadJSON(Railroad railroad) {
    mortgaged = railroad.isMortgaged();
    name = railroad.getName();
    id = railroad.getId();
  }

}
