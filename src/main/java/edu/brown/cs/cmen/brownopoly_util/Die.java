package edu.brown.cs.cmen.brownopoly_util;

class Die {
  private int num;

  public int roll() {
    num = (int) Math.random() * 6 + 1;
    return num;
  }

  public int getRoll() {
    return num;
  }
}
