package edu.brown.cs.cmen.brownopoly.ownable;

import edu.brown.cs.cmen.brownopoly.player.Player;

/**
 * 
 * @author npucel
 *
 */
public interface Ownable {

  public int rent();

  public Player owner();

  public void setOwner(Player p);

  public int price();

  public void mortgage();

  public void demortgage();

}
