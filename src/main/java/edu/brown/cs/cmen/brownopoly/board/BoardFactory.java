package edu.brown.cs.cmen.brownopoly.board;

import edu.brown.cs.cmen.brownopoly.cards.Card;
import edu.brown.cs.cmen.brownopoly.customboards.BoardTheme;
import edu.brown.cs.cmen.brownopoly.game.GameSettings;

public class BoardFactory {
    private GameSettings gameSettings;
    private String[] names;
    private int[][] colors;
    private Board board;
    private BoardSquare[] boardSquares;

    public BoardFactory(GameSettings gameSettings) {
        this.gameSettings = gameSettings;
        this.names = gameSettings.getTheme().getNames();
        this.colors = gameSettings.getTheme().getColors();
        this.board = new Board();
        this.boardSquares = board.getBoard();

    }

    public Board build() {
        //make squares for each space in the array
        boardSquares[0] = new GoSquare(names[0], 0);
        buildPropertySquare(1);
        buildCardSquare(2, false);
        buildPropertySquare(3);
        buildTaxSquare(4, 200);
        //TODO: Reading Railroad, index 5
        buildPropertySquare(6);
        buildCardSquare(7, true);
        buildPropertySquare(8);
        buildPropertySquare(9);
        boardSquares[10] = new JustVisitingSquare(names[10], 10);
        buildPropertySquare(11);
        //TODO: Electric Company, index 12
        buildPropertySquare(13);
        buildPropertySquare(14);
        //TODO: Pennsylvania Railroad, index 15
        buildPropertySquare(16);
        buildCardSquare(17, false);
        buildPropertySquare(18);
        buildPropertySquare(19);
        boardSquares[20] = new FreeParkingSquare(names[20], 20);
        buildPropertySquare(21);
        buildCardSquare(22, true);
        buildPropertySquare(23);
        buildPropertySquare(24);
        //TODO: B. & O. Railroad, index 25
        buildPropertySquare(26);
        buildPropertySquare(27);
        //TODO: Water Works, index 28
        buildPropertySquare(29);
        boardSquares[30] = new JailSquare(names[30], 30);
        buildPropertySquare(31);
        buildPropertySquare(32);
        buildCardSquare(33, false);
        buildPropertySquare(34);
        //TODO: Short Line, index 35
        buildCardSquare(36, true);
        buildPropertySquare(37);
        buildTaxSquare(38, 75);
        buildPropertySquare(39);
        return board;
    }
    private void buildPropertySquare(int id) {
        boardSquares[id] = new PropertySquare(id, names[id], colors[id]);
    }
    private void buildCardSquare(int id, boolean isChance) {
        if(isChance) {
            boardSquares[id] = new CardSquare(names[id], id, board.getChance());
        } else {
            boardSquares[id] = new CardSquare(names[id], id, board.getCommunityChest());
        }
    }
    private void buildTaxSquare(int id, double tax) {
        boardSquares[id] = new TaxSquare(names[id], id, tax);
    }
}
