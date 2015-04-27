package edu.brown.cs.cmen.brownopoly.cards;

import java.util.List;

import edu.brown.cs.cmen.brownopoly.board.Board;
import edu.brown.cs.cmen.brownopoly.ownable.Property;
import edu.brown.cs.cmen.brownopoly.player.Player;

/**
 * Created by codyyu on 3/31/15.
 */
public class RepairsCard implements Card {
  /**
   * 
   */
  private static final long serialVersionUID = -4707985178138567104L;
  private String name;
  private int houseCost;
  private int hotelCost;

  public RepairsCard(String name, int houseCost, int hotelCost) {
    this.name = name;
    this.houseCost = houseCost;
    this.hotelCost = hotelCost;
  }

  @Override
  public String play(Player player) {
    int totalCost = 0;
    List<Property> properties = player.getProperties();
    for (Property property : properties) {
      totalCost += (property.getNumHouses() * houseCost);
      if (property.hasHotel()) {
        totalCost += hotelCost;
        totalCost -= houseCost;
      }
    }
    Board.freeParking += totalCost;
    player.addToBalance(-1 * totalCost);
    return ":\nPay $" + totalCost + " to cover the repairs!";
  }

  @Override
  public String getName() {
    return name;
  }
}
