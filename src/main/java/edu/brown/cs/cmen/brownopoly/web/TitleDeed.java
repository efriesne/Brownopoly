package edu.brown.cs.cmen.brownopoly.web;

import edu.brown.cs.cmen.brownopoly.customboards.BoardTheme;
import edu.brown.cs.cmen.brownopoly.game.MonopolyConstants;
import edu.brown.cs.cmen.brownopoly.ownable.OwnableManager;
import edu.brown.cs.cmen.brownopoly.ownable.Railroad;

public class TitleDeed {
  private int[] rentCosts;
  private int houseCost;
  private int mortgageValue;
  private int boardIDX;
  private String type;
  private String name;
  private int[] color;
  private int buyPrice;
  private boolean isMortgaged = false;

  public TitleDeed(BoardTheme theme, int idx) {
    switch (OwnableManager.ownableType(idx)) {
      case "property":
        this.type = "property";
        this.houseCost = MonopolyConstants.getHouseCost(idx);
        this.rentCosts = MonopolyConstants.getPropertyRentArray(idx);
        this.mortgageValue = MonopolyConstants.getPropertyPrice(idx) / 2;
        this.buyPrice = MonopolyConstants.getPropertyPrice(idx);
        break;
      case "railroad":
        this.type = "railroad";
        this.buyPrice = 200;
        break;
      case "utility":
        this.type = "utility";
        this.buyPrice = 150;
        break;
      case "":
        break;
    }
    this.name = (theme.getNames())[idx];
    this.color = (theme.getColors())[idx];
    this.boardIDX = idx;
  }
}
