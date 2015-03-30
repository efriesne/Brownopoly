package edu.brown.cs.cmen.brownopoly.board;

import edu.brown.cs.cmen.brownopoly.game.GameSettings;

public class Board {
  private BoardSquare[] board;

  public Board(GameSettings settings) {
    board = createBoardSquares(settings);
  }

  public BoardSquare get(int index) {
    return board[index];
  }

  private BoardSquare[] createBoardSquares(GameSettings settings) {
    BoardSquare[] board = new BoardSquare[40];

    return board;
  }
}
