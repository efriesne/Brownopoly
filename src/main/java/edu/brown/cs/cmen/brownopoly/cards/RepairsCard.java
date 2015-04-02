package edu.brown.cs.cmen.brownopoly.cards;

import edu.brown.cs.cmen.brownopoly.ownable.Property;
import edu.brown.cs.cmen.brownopoly.player.Player;

import java.util.List;

/**
 * Created by codyyu on 3/31/15.
 */
public class RepairsCard implements Card {
    private String name;
    private int houseCost;
    private int hotelCost;

    public RepairsCard(String name, int houseCost, int hotelCost) {
        this.name = name;
        this.houseCost = houseCost;
        this.hotelCost = hotelCost;
    }

    @Override
    public void play(Player player) {
        int totalCost = 0;
        List<Property> properties = player.getProperties();
        for(Property property : properties) {
            totalCost += (property.getNumHouses() * houseCost);
            if(property.hasHotel()) {
                totalCost += hotelCost;
            }
        }
        player.addToBalance(-1 * totalCost);
    }

    @Override
    public String getName() {
        return name;
    }
}
