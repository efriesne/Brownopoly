package edu.brown.cs.cmen.brownopoly.board;

import edu.brown.cs.cmen.brownopoly.game.Deck;

public class Board {
  private BoardSquare[] board;
    private Deck communityChest;
    private Deck chance;

    public Board() {
        board = new BoardSquare[40];
    }
  public BoardSquare getSquare(int index) {
    return board[index];
  }
}
