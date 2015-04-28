package edu.brown.cs.cmen.brownopoly.board;

import edu.brown.cs.cmen.brownopoly.game.GameSettings;
import edu.brown.cs.cmen.brownopoly.game.MonopolyConstants;

public class BoardFactory {
  private String[] names;
  private int[][] colors;
  private Board board;
  private BoardSquare[] boardSquares;

  public BoardFactory(GameSettings gameSettings) {
    this.names = gameSettings.getTheme().getNames();
    this.colors = gameSettings.getTheme().getColors();
    this.board = new Board();
    this.boardSquares = board.getBoard();

  }

  public Board build() {
    // make squares for each space in the array
    int go = MonopolyConstants.GO_ID;
    int lTax = MonopolyConstants.LUXURYTAX_ID;
    int iTax = MonopolyConstants.INCOMETAX_ID;
    int jv = MonopolyConstants.JUSTVISITING_ID;
    int fp = MonopolyConstants.FREEPARKING_ID;
    int jail = MonopolyConstants.GOTOJAIL_ID;

    boardSquares[go] = new GoSquare(names[go], go);
    buildTaxSquare(iTax, MonopolyConstants.INCOME_TAX);
    boardSquares[jv] = new JustVisitingSquare(names[jv], jv);
    boardSquares[fp] = new FreeParkingSquare(names[fp], fp);
    boardSquares[jail] = new JailSquare(names[jail], jail);
    buildTaxSquare(lTax, MonopolyConstants.LUXURY_TAX);

    for (int idx : MonopolyConstants.PROPERTY_IDS) {
      buildOwnableSquare(idx);
    }
    for (int idx : MonopolyConstants.UTILITY_IDS) {
      buildOwnableSquare(idx);
    }
    for (int idx : MonopolyConstants.RAILROAD_IDS) {
      buildOwnableSquare(idx);
    }
    for (int idx : MonopolyConstants.CHANCE_IDS) {
      buildCardSquare(idx, true);
    }
    for (int idx : MonopolyConstants.COMMUNITY_IDS) {
      buildCardSquare(idx, false);
    }

    return board;
  }

  private void buildOwnableSquare(int id) {
    boardSquares[id] = new OwnableSquare(names[id], id);
  }

  /*
   * private void buildPropertySquare(int id) { boardSquares[id] = new
   * PropertySquare(id, names[id], colors[id]); }
   */
  private void buildCardSquare(int id, boolean isChance) {
    if (isChance) {
      boardSquares[id] = new CardSquare(names[id], id, board.getChance());
    } else {
      boardSquares[id] = new CardSquare(names[id], id,
          board.getCommunityChest());
    }
  }

  private void buildTaxSquare(int id, int tax) {
    boardSquares[id] = new TaxSquare(id, names[id], tax);
  }
  /*
   * private void buildRailroadSquare(int id) { boardSquares[id] = new
   * RailroadSquare(id, names[id]); }
   * 
   * private void buildUtilitySquare(int id) { boardSquares[id] = new
   * UtilitySquare(id, names[id]); }
   */
}
