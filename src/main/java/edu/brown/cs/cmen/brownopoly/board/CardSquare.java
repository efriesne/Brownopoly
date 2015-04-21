package edu.brown.cs.cmen.brownopoly.board;

import edu.brown.cs.cmen.brownopoly.cards.Card;
import edu.brown.cs.cmen.brownopoly.cards.GoBack3Spaces;
import edu.brown.cs.cmen.brownopoly.game.Deck;
import edu.brown.cs.cmen.brownopoly.player.Player;

public class CardSquare extends BoardSquare {
  private Deck deck;

  public CardSquare(String name, int id, Deck deck) {
    super(name, id);
    this.deck = deck;
  }

  @Override
  public String executeEffect(Player p, int userInput) {
    Card card = deck.draw();
    String msg = card.play(p);
    return p.getName() + " drew " + card.getName() + ":\n" + msg;
  }

}
