package edu.brown.cs.cmen.brownopoly.board;

import edu.brown.cs.cmen.brownopoly.cards.Card;
import edu.brown.cs.cmen.brownopoly.game.Deck;
import edu.brown.cs.cmen.brownopoly.player.Player;

public class CardSquare extends BoardSquare {
  private Deck deck;
  private int input;

  public CardSquare(String name, int id, Deck deck) {
    super(name, id);
    this.deck = deck;
  }

  @Override
  public String executeEffect(Player p, int userInput) {
    Card card = deck.draw();
    card.play(p);
    // need to check if card's effect causes need for user input (e.g. mortgage)
    return p.getName() + " drew " + card.getName() + "!";
    // something about card effect
  }

  @Override
  public int getInput() {
    return 0;
  }

}
