package edu.brown.cs.cmen.brownopoly_util;

import java.io.Serializable;
import java.util.Random;

class Die implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 35100210597656455L;
  private int num;
  private Random random = new Random();

  public int roll() {
    num = 1;
    //num = random.nextInt(6) + 1;
    return num;
  }

  public int getRoll() {
    return num;
  }
}
