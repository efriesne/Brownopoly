package edu.brown.cs.cmen.brownopoly.board;

import edu.brown.cs.cmen.brownopoly.game.Deck;

public class Board {
  private BoardSquare[] board;
    private Deck communityChest;
    private Deck chance;

    public Board() {
        board = new BoardSquare[40];
        communityChest = new Deck(false);
        chance = new Deck(true);
    }

    public BoardSquare[] getBoard() {
        return board;
    }

    public Deck getChance() {
        return chance;
    }

    public Deck getCommunityChest() {
        return communityChest;
    }

  public BoardSquare getSquare(int index) {
    return board[index];
  }
}
