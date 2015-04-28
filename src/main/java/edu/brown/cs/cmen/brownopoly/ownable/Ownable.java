package edu.brown.cs.cmen.brownopoly.ownable;

import java.io.Serializable;

import edu.brown.cs.cmen.brownopoly.player.Player;

/**
 * 
 * @author npucel
 *
 */
public interface Ownable extends Serializable {

  int rent();

  Player owner();

  void setOwner(Player p);

  int price();

  void mortgage();

  void demortgage();

  boolean isMortgaged();

  String getName();

  String getType();

  int getId();

}
