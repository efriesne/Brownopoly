package edu.brown.cs.cmen.brownopoly.board;

public class Board {
  private BoardSquare[] board;

    public Board() {
        board = new BoardSquare[40];
    }
  public BoardSquare getSquare(int index) {
    return board[index];
  }
}
