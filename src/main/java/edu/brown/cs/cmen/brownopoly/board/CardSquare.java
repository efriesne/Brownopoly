package edu.brown.cs.cmen.brownopoly.board;

import edu.brown.cs.cmen.brownopoly.cards.Card;
import edu.brown.cs.cmen.brownopoly.game.Deck;
import edu.brown.cs.cmen.brownopoly.player.Player;

public class CardSquare extends BoardSquare {
  private Deck deck;
  public CardSquare(int id, Deck deck) {
    super(id);
    this.deck = deck;
  }

  @Override
  public String executeEffect(Player p) {
    Card card = deck.draw();
    card.play(p);
    return p.getName() + "drew" + card.getName() + "!";
  }

}
