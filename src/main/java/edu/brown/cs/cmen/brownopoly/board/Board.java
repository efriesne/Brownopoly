package edu.brown.cs.cmen.brownopoly.board;

import edu.brown.cs.cmen.brownopoly.game.Deck;

public class Board {
  private BoardSquare[] board;
    private Deck communityChest;
    private Deck chance;

    public static int freeParking;

    public Board() {
        board = new BoardSquare[40];
        communityChest = new Deck(false);
        chance = new Deck(true);
        freeParking = 0;
    }

    //doesn't return copy because factory must be able to access/modify
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
