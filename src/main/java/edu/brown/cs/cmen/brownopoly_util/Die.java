package edu.brown.cs.cmen.brownopoly_util;

public class Die {
  public int roll() {
    return (int) Math.random()*6 + 1;
  }
}
