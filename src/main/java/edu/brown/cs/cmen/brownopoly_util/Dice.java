package edu.brown.cs.cmen.brownopoly_util;

public class Dice {
  private Die die1;
  private Die die2;
  private int doubles;

  public Dice() {
    die1 = new Die();
    die2 = new Die();
    doubles = 0;
  }

  public void roll() {
    int roll1 = die1.roll();
    int roll2 = die2.roll();
    if (roll1 == roll2) {
      doubles++;
    }
  }

  public boolean isDoubles() {
    return die1.getRoll() == die2.getRoll();
  }

  public int numDoubles() {
    return doubles;
  }

  public int getFirstRoll() {
    return die1.getRoll();
  }

  public int getSecondRoll() {
    return die2.getRoll();
  }

  public int getRollSum() {
    return getFirstRoll() + getSecondRoll();
  }
}
