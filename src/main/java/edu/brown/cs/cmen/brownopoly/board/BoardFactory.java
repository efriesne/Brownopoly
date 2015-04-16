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
        int[][] colors = theme.getColors();
        Board board = new Board();
        BoardSquare[] array = board.getBoard();
        //make squares for each space in the array
        array[0] = new GoSquare(names[0], 0);
        array[1] = new PropertySquare(1, names[1], colors[1]);
        array[2] = new CardSquare(names[2], 2, )
        return board;
    }
}
