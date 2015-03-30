package edu.brown.cs.cmen.brownopoly.board;

import java.util.LinkedList;
import java.util.Queue;

import edu.brown.cs.cmen.brownopoly.cards.Card;
import edu.brown.cs.cmen.brownopoly.player.Player;

public class CardSquare extends BoardSquare {
  private Queue<Card> cards;
  public CardSquare(int id, LinkedList<Card> cards) {
    super(id);
    this.cards = cards;
  }

  @Override
  public int executeEffect(Player p) {
    Card card = cards.remove();
    card.doEffect(p);
    return 0;
  }

}
