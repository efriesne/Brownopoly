package edu.brown.cs.cmen.brownopoly.game;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import edu.brown.cs.cmen.brownopoly.board.Board;
import edu.brown.cs.cmen.brownopoly.board.BoardFactory;
import edu.brown.cs.cmen.brownopoly.ownable.Ownable;
import edu.brown.cs.cmen.brownopoly.ownable.OwnableFactory;
import edu.brown.cs.cmen.brownopoly.ownable.OwnableManager;
import edu.brown.cs.cmen.brownopoly.player.Player;
import edu.brown.cs.cmen.brownopoly.player.PlayerBuilder;

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
    // number of starting properties shouldn't exceed _____
    // starting cash should be between certain range

    // Create Ownables
    Collection<Ownable> ownables = new OwnableFactory(settings).buildAll();
    OwnableManager.populate(ownables);

    // Create Board
    // Write separate method to create basic board so that it can be used in
    // Loader.loadGame()
    Board board = new BoardFactory(settings).build();

    // Create Players
    // PlayerBuilder pBuilder = new PlayerBuilder();
    List<Player> players = new PlayersListBuilder().buildPlayers(settings,
        board);

    Referee ref = new Referee(board, players, settings.isFastPlay());
    return new Game(ref, settings, ownables);
  }

  private class PlayersListBuilder {

    List<Player> buildPlayers(GameSettings settings, Board board) {
      List<Player> players = new LinkedList<Player>();
      String id_prefix = "player_";
      int counter = 0;
      for (int i = 0; i < settings.getNumHumans(); i++) {
        String name = settings.getHumanName(i);
        Player p = new PlayerBuilder(i).withName(name)
            .withStartingProperties(settings.getStartProperties())
            .withBoard(board).withID(id_prefix + counter).build();
        players.add(p);
        counter++;
      }
      for (int i = 0; i < settings.getNumAI(); i++) {
        String name = settings.getAIName(i);
        Player p = new PlayerBuilder(i + settings.getNumHumans()).isAI()
            .withName(name)
            .withStartingProperties(settings.getStartProperties())
            .withBoard(board).withID(id_prefix + counter).build();
        players.add(p);
        counter++;

      }
      for (Player player1 : players) {
        for (Player player2 : players) {
          if (!player1.getId().equals(player2.getId())) {
            player1.addOpponent(player2);
          }
        }
      }
      return players;
    }
  }
}
