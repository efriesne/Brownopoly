package edu.brown.cs.cmen.brownopoly.ownable;

import edu.brown.cs.cmen.brownopoly.player.Player;

/**
 * 
 * @author npucel
 *
 */
public interface Ownable {

  int rent();

  Player owner();

  void setOwner(Player p);

  int price();

  void mortgage();

  void demortgage();

  boolean isMortgaged();

  String getName();

  int getId();

}
