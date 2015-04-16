package edu.brown.cs.cmen.brownopoly.game;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import edu.brown.cs.cmen.brownopoly.board.Board;
import edu.brown.cs.cmen.brownopoly.board.BoardFactory;
import edu.brown.cs.cmen.brownopoly.ownable.OwnableManager;
import edu.brown.cs.cmen.brownopoly.ownable.Property;
import edu.brown.cs.cmen.brownopoly.player.Human;
import edu.brown.cs.cmen.brownopoly.player.Player;

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
    // check for valid settings
    // number of AI players, Human players shouldn't exceed ____
    // number of starting properties shouldn't exceed_____
    // starting cash should be between certain range

    // Create Board
    // Write separate method to create basic board so that it can be used in
    // Loader.loadGame()
    Board board = new BoardFactory(settings).build();

    // Create Players
    Queue<Player> players = new LinkedList<Player>();
    for (int i = 0; i < settings.getNumHumans(); i++) {
      List<Property> starting = new ArrayList<>();
      for (int j = 0; j < settings.getStartProperties(); j++) {
        starting.add(OwnableManager.getRandomProperty());
      }
      String name = settings.getHumanName(i);
      players.add(new Human(name, starting));
    }
    for (int i = 0; i < settings.getNumAI(); i++) {
      List<Property> starting = new ArrayList<>();
      for (int j = 0; j < settings.getStartProperties(); j++) {
        starting.add(OwnableManager.getRandomProperty());
      }
      String name = settings.getAIName(i);
      // players.add(new AI(name, starting));
      // issue: AIs created before Game is created
      // solution: have AI method makeDecision() take in a GameState, that way
      // it doesn't need to hold it itself
    }

    // Create Referee
    // new Referee(board, players)

    // Create Game
    // new Game(Referee, ?settings?)

    return null;
  }
}
