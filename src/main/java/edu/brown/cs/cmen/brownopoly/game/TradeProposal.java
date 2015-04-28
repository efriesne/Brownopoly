package edu.brown.cs.cmen.brownopoly.game;

import edu.brown.cs.cmen.brownopoly.player.Player;

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
}
