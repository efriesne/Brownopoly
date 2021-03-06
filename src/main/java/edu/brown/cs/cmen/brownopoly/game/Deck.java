package edu.brown.cs.cmen.brownopoly.game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import edu.brown.cs.cmen.brownopoly.cards.Card;
import edu.brown.cs.cmen.brownopoly.cards.EveryPlayerCard;
import edu.brown.cs.cmen.brownopoly.cards.GetOutOfJailFree;
import edu.brown.cs.cmen.brownopoly.cards.GoBack3Spaces;
import edu.brown.cs.cmen.brownopoly.cards.GoToJail;
import edu.brown.cs.cmen.brownopoly.cards.MonetaryCard;
import edu.brown.cs.cmen.brownopoly.cards.NearestRailroad;
import edu.brown.cs.cmen.brownopoly.cards.NearestUtility;
import edu.brown.cs.cmen.brownopoly.cards.PositionCard;
import edu.brown.cs.cmen.brownopoly.cards.RepairsCard;

public class Deck implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1014583767225334524L;
  private List<Card> deck;
  private boolean isChance;
  private String[] names;

  public Deck(boolean isChance, String[] names) {
    deck = new ArrayList<>();
    this.names = names;
    this.isChance = isChance;
    if (isChance) {
      refill(MonopolyConstants.CHANCE_DECK);
    } else {
      refill(MonopolyConstants.COMMUNITY_DECK);
    }
    shuffle();
  }

  public void addCard(String name) {
    Card toAdd;
    switch (name) {
      case "Advance to Go":
        toAdd = new PositionCard("Advance to Go", 0);
        break;
      case "Bank Dividend":
        toAdd = new MonetaryCard("Bank Dividend", 50);
        break;
      case "Go Back 3 Spaces":
        toAdd = new GoBack3Spaces();
        break;
      case "Nearest Utility":
        toAdd = new NearestUtility();
        break;
      case "Go to Jail":
        toAdd = new GoToJail();
        break;
      case "Poor Tax":
        toAdd = new MonetaryCard("Poor Tax", -15);
        break;
      case "St. Charles Place":
        toAdd = new PositionCard("Advance to " + names[11], 11);
        break;
      case "Elected Chairman":
        toAdd = new EveryPlayerCard("Elected Chairman", 50);
        break;
      case "Nearest Railroad":
        toAdd = new NearestRailroad();
        break;
      case "Reading Railroad":
        toAdd = new PositionCard("Advance to " + names[5], 5);
        break;
      case "Boardwalk":
        toAdd = new PositionCard("Advance to " + names[39], 39);
        break;
      case "Loan Matures":
        toAdd = new MonetaryCard("Loan Matures", 150);
        break;
      case "Illinois Ave.":
        toAdd = new PositionCard("Advance to " + names[24], 24);
        break;
      case "Get Out of Jail Free":
        toAdd = new GetOutOfJailFree();
        break;
      case "General Repairs":
        toAdd = new RepairsCard("General Repairs", 25, 100);
        break;
      case "Doctor Fee":
        toAdd = new MonetaryCard("Doctor Fee", -50);
        break;
      case "XMAS Fund":
        toAdd = new MonetaryCard("Christmas Fund", 100);
        break;
      case "Opera Opening":
        toAdd = new EveryPlayerCard("Opera Opening", -50);
        break;
      case "Inheritance":
        toAdd = new MonetaryCard("Inheritance", 100);
        break;
      case "Services":
        toAdd = new MonetaryCard("Services Rendered", 25);
        break;
      case "Income Tax Refund":
        toAdd = new MonetaryCard("Tax Refund", 20);
        break;
      case "Sale of Stock":
        toAdd = new MonetaryCard("Sale of Stock", 45);
        break;
      case "School Tax":
        toAdd = new MonetaryCard("School Tax", -150);
        break;
      case "Street Repairs":
        toAdd = new RepairsCard("Street Repairs", 40, 115);
        break;
      case "Bank Error":
        toAdd = new MonetaryCard("Bank Error", 200);
        break;
      case "Life Insurance":
        toAdd = new MonetaryCard("Life Insurance", 100);
        break;
      case "Pay Hospital":
        toAdd = new MonetaryCard("Pay Hospital", -100);
        break;
      case "Beauty Contest":
        toAdd = new MonetaryCard("Beauty Contest", 10);
        break;
      default:
        toAdd = null;
        break;
    }
    if (toAdd != null) {
      deck.add(toAdd);
    }
  }

  public Card draw() {
    if (isEmpty()) {
      if (isChance) {
        refill(MonopolyConstants.CHANCE_DECK);
      } else {
        refill(MonopolyConstants.COMMUNITY_DECK);
      }
      shuffle();
    }
    return deck.remove(0);
  }

  public void refill(List<String> cards) {
    for (String card : cards) {
      addCard(card);
    }
  }

  public void shuffle() {
    List<Card> shuffled = new ArrayList<>();
    while (!isEmpty()) {
      Random rand = new Random();
      int randomIndex = rand.nextInt(deck.size());
      shuffled.add(deck.remove(randomIndex));
    }
    deck = shuffled;
  }

  public boolean isEmpty() {
    return deck.isEmpty();
  }
}
