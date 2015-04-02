package edu.brown.cs.cmen.brownopoly.board;

import edu.brown.cs.cmen.brownopoly.customboards.BoardTheme;
import edu.brown.cs.cmen.brownopoly.game.GameSettings;

public class BoardFactory {
    private GameSettings gameSettings;

    public BoardFactory(GameSettings gameSettings) {
        this.gameSettings = gameSettings;
    }

    public Board build() {
        BoardTheme theme = gameSettings.getTheme();
        String[] names = theme.getNames();
        Board board = new Board();
        BoardSquare[] array = board.getBoard();
        //make squares for each space in the array
        return board;
    }
}
