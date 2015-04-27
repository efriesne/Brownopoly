package edu.brown.cs.cmen.brownopoly.board;

import java.io.Serializable;

import edu.brown.cs.cmen.brownopoly.game.Deck;

public class Board implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 6026634894616634525L;
  private BoardSquare[] board;
  private Deck communityChest;
  private Deck chance;

  public static int freeParking;

  public Board() {
    board = new BoardSquare[40];
    communityChest = new Deck(false);
    chance = new Deck(true);
    freeParking = 50;
  }

  // doesn't return copy because factory must be able to access/modify
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
