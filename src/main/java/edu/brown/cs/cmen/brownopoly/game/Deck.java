package edu.brown.cs.cmen.brownopoly.game;

import edu.brown.cs.cmen.brownopoly.cards.Card;
import edu.brown.cs.cmen.brownopoly.cards.MonetaryCard;

import java.util.ArrayList;
import java.util.List;

public class Deck {
    List<Card> deck;
    public Deck() {
        deck = new ArrayList<>();
    }
    public Deck(List<String> names) {
        deck = new ArrayList<>();
        for(String name : names) {
            addCard(name);
        }
    }
    public void addCard(String name) {
        Card toAdd;
        switch (name) {
            case "Advance to Go":
                toAdd = new AdvanceToGo();
                break;
            case "Bank Dividend":
                toAdd = new MonetaryCard("Bank Dividend", 50);
                break;
            case "Go Back 3 Spaces":
                toAdd = new Back3Spaces();
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
                toAdd = new CharlesPlace();
                break;
            case "Elected Chairman":
                toAdd = new ElectedChairman();
                break;
            case "Nearest Railroad":
                toAdd = new NearestRailroad();
                break;
            case "Reading Railroad":
                toAdd = new ReadingRailroad();
                break;
            case "Boardwalk":
                toAdd = new Boardwalk();
                break;
            case "Loan Matures":
                toAdd = new MonetaryCard("Loan Matures", 150);
                break;
            case "Illinois Ave.":
                toAdd = new Illinois();
                break;
            case "Get Out of Jail Free":
                toAdd = new JailFree();
                break;
            case "General Repairs":
                toAdd = new GeneralRepairs();
                break;
            case "Doctor Fee":
                toAdd = new MonetaryCard("Doctor Fee", -50);
                break;
            case "XMAS Fund":
                toAdd = new MonetaryCard("Christmas Fund", 100);
                break;
            case "Opera Opening":
                toAdd = new Opera();
                break;
            case "Inheritance":
                toAdd = new MonetaryCard("Inheritance", 100);
                break;
            case "Services":
                toAdd = new MonetaryCard("Services", 25);
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
                toAdd = new StreetRepairs();
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
        if(toAdd != null) {
            deck.add(toAdd);
        }
    }
}