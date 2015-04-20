package edu.brown.cs.cmen.brownopoly.web;

import edu.brown.cs.cmen.brownopoly.customboards.BoardTheme;
import edu.brown.cs.cmen.brownopoly.game.MonopolyConstants;

public class BoardJSON {

  private String[] names;
  private int[][] colors;
  private int[] prices;

  public BoardJSON(BoardTheme theme) {
    names = theme.getNames();
    colors = theme.getColors();
    prices = new int[names.length];
    for (int i = 0; i < prices.length; i++) {
      prices[i] = MonopolyConstants.getPropertyPrice(i);
    }
    prices[5] = 200;
    prices[12] = 150;
    prices[15] = 200;
    prices[25] = 200;
    prices[35] = 200;
    prices[28] = 150;
  }

}
