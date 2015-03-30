package edu.brown.cs.cmen.brownopoly.game;

/**
 * 
 * @author npucel
 *
 */
/* friendly */final class GameFactory {

  /**
   * Creates a Game and its aspects: Players, Properties, BoardSquares, Referee,
   * etc.
   * 
   * Used when the user decides to start a new game
   * 
   * @return the newly created Game
   */
  public Game createGame(GameSettings settings) {
    // Create Board
    // Write separate method to create basic board so that it can be used in
    // Loader.loadGame()

    // Create Players

    // Create Referee
    // new Referee(board, players)

    // Create Game
    // new Game(Referee, ?board?, ?settings?)

    return null;
  }
}
