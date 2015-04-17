package edu.brown.cs.cmen.brownopoly.customboards;

import java.util.Arrays;

/**
 * 
 * @author npucel
 *
 */
public class BoardTheme {
  private String[] names;
  private int[][] colors;

  public BoardTheme(String[] names, int[][] colors) {
    this.names = names;
    this.colors = colors;
  }

  public String[] getNames() {
    return Arrays.copyOf(names, names.length);
  }

  public int[][] getColors() {
    return Arrays.copyOf(colors, colors.length);
  }
}
