package edu.brown.cs.cmen.brownopoly.game;

import edu.brown.cs.cmen.brownopoly.player.Player;

import java.lang.reflect.Array;
import java.util.Arrays;

public class TradeProposal {
  private Player initializer;
  private Player recipient;
  private String[] initProps;
  private int initMoney;
  private String[] recipProps;
  private int recipMoney;
  public TradeProposal(Player initializer, Player recipient, String[] initProps, int initMoney, String[] recipProps, int recipMoney) {
    this.initializer = initializer;
    this.recipient = recipient;
    this.initProps = initProps;
    this.initMoney = initMoney;
    this.recipProps = recipProps;
    this.recipMoney = recipMoney;
  }
  public Player getInitializer() {
    return initializer;
  }
  public Player getRecipient() {
    return recipient;
  }
  public String[] getInitProps() {
    return initProps;
  }
  public int getInitMoney() {
    return initMoney;
  }
  public String[] getRecipProps() {
    return recipProps;
  }
  public int getRecipMoney() {
    return recipMoney;
  }

  @Override
  public int hashCode() {
    return initMoney ^ recipMoney;
  }

  @Override
  public boolean equals(Object o) {
    if(!(o instanceof TradeProposal)) {
      return false;
    }
    TradeProposal that = (TradeProposal) o;
    return initializer.getId().equals(that.getInitializer().getId()) &&
            recipient.getId().equals(that.getRecipient().getId()) &&
            Arrays.deepEquals(initProps, that.getInitProps()) &&
            Arrays.deepEquals(recipProps, that.getRecipProps()) &&
            initMoney == that.getInitMoney() &&
            recipMoney == that.getRecipMoney();
  }
}
