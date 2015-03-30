package edu.brown.cs.cmen.brownopoly.player;

import java.util.List;

import edu.brown.cs.cmen.brownopoly.ownable.Monopoly;
import edu.brown.cs.cmen.brownopoly.ownable.Property;
import edu.brown.cs.cmen.brownopoly.ownable.Railroad;
import edu.brown.cs.cmen.brownopoly.ownable.Utility;

public class Human implements Player {
  private String name;
  private List<Property> properties;
  private List<Monopoly> monopolies;
  private List<Railroad> railroads;
  private List<Utility> utilities;
  private int getOutOfJailFree;
  private boolean inJail;
  private int position;
  private int account;
  private boolean isBankrupt;
  private int turnsInJail;
  
  public Human (String name) {
    this.name = name;
  }
  
  @Override
  public int move(int increment) {
    position += increment;
    return position;
  }

  @Override
  public boolean payRent(Property property) {
     
     return false;
  }

  @Override
  public void collectRent(int rent) {
    account += rent;
  }

  @Override
  public boolean buyProperty(Property property) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public void removeProperty(Property property) {
    properties.remove(property);
  }

  @Override
  public void receiveTrade(List<Property> properties, int moneyToGet) {
    properties.addAll(properties);
    account += moneyToGet;
  }

  @Override
  public int getMoney() {
    return account;
  }

  @Override
  public List<Property> getProperties() {
     return properties;
  }

  @Override
  public List<Monopoly> getMonopolies() {
    return monopolies;
  }

  @Override
  public void addMonopoly() {
    
  }

  @Override
  public void addJailFree() {
    getOutOfJailFree++;
  }

  @Override
  public boolean hasJailFree() {
    return getOutOfJailFree > 0;
  }

  @Override
  public void useJailFree() {
    getOutOfJailFree--;
  }

  @Override
  public boolean isInJail() {
    return inJail;
  }

  @Override
  public int getTurnsInJail() {
    return turnsInJail;
  }

}
