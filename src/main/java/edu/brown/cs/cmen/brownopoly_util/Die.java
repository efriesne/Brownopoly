package edu.brown.cs.cmen.brownopoly_util;

import java.util.Random;

class Die {
  private int num;
  private Random random = new Random();

  public int roll() {
    num = random.nextInt(6) + 1;
    return num;
  }

  public int getRoll() {
    return num;
  }
}
