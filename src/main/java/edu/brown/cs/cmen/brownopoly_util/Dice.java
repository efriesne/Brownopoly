package edu.brown.cs.cmen.brownopoly_util;

public class Dice {
  private Die d1;
  private Die d2;
  private int doubles = 0;
  private boolean rolledDoubles = false;
  private int rollSum;

  public Dice() {
    d1 = new Die();
    d2 = new Die();
  }

  public void roll() {
    rolledDoubles = false;
    
    int roll1 = d1.roll();
    int roll2 = d2.roll();
    
    if (roll1 == roll2) {
      rolledDoubles = true;
      doubles++;
    }
    
    rollSum = roll1 + roll2;
  }
  
  public boolean isDoubles() {
    return rolledDoubles;
  }
  
  public int numDoubles()  {
    return doubles;
  }
  
  public int getFirstRoll() {
    return d1.getRoll();
  }
  public int getSecondRoll() {
    return d2.getRoll();
  }
  
  public int getRollSum() {
    return rollSum;
  }
}
