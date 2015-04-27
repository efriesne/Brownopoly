package edu.brown.cs.cmen.brownopoly.web;

import edu.brown.cs.cmen.brownopoly.customboards.BoardTheme;
import edu.brown.cs.cmen.brownopoly.game.GameSettings;
import edu.brown.cs.cmen.brownopoly.game.MonopolyConstants;
import edu.brown.cs.cmen.brownopoly.ownable.OwnableManager;

public class TitleDeed {
  private int[] rentCosts;
  private int houseCost;
  private int mortgageValue;
  private String type;
  private boolean isElectricCo = false;
  private String name;
  private int[] color;
  
  public TitleDeed(BoardTheme theme, int idx) {
    switch (OwnableManager.ownableType(idx)) {
      case "property":
        this.type = "property";
        this.houseCost = MonopolyConstants.getHouseCost(idx);
        this.rentCosts = MonopolyConstants.getPropertyRentArray(idx);
        this.mortgageValue = MonopolyConstants.getPropertyPrice(idx)/2;
        break;
      case "railroad":
        this.type = "railroad";
        break;
      case "utility":
        this.type = "utility";
        break;
      case "":
        break;
    }
    
    this.name = (theme.getNames())[idx];
    this.color = (theme.getColors())[idx];
    
    if (idx == 12) {
      isElectricCo = true;
    }

  }
}
