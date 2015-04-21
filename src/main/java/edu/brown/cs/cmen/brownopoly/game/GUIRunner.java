package edu.brown.cs.cmen.brownopoly.game;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import spark.ExceptionHandler;
import spark.ModelAndView;
import spark.QueryParamsMap;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;
import spark.TemplateViewRoute;
import spark.template.freemarker.FreeMarkerEngine;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;

import edu.brown.cs.cmen.brownopoly.board.BoardSquare;
import edu.brown.cs.cmen.brownopoly.customboards.BoardTheme;
import edu.brown.cs.cmen.brownopoly.ownable.Property;
import edu.brown.cs.cmen.brownopoly.player.Human;
import edu.brown.cs.cmen.brownopoly.player.Player;
import edu.brown.cs.cmen.brownopoly.web.BoardJSON;
import edu.brown.cs.cmen.brownopoly.web.PlayerJSON;
import edu.brown.cs.cmen.brownopoly_util.Dice;
import freemarker.template.Configuration;

/**
 * Testing the GUI
 * 
 * @author Marley
 *
 */
public class GUIRunner {
  private static final Gson GSON = new Gson();
  private static final int STATUS = 500;

  private Player dummy;
  private BoardJSON board;
  private BoardTheme theme;
  private Game game;
  private Referee ref;
  private GameSettings gs;

  public GUIRunner() {
    List<Property> list = new ArrayList<>();
    // Property p = new Property(0, "Mediterranean Ave");
    // p.addHouse();
    // p.addHouse();
    // p.addHouse();
    // p.addHouse();
    // p.addHouse();
    // list.add(p);
    // list.add(new Property(0, "Baltic Ave"));
    // list.add(new Property(0, "Vermont Ave"));

    dummy = new Human("Marley", list, false, "player_1");
    theme = new BoardTheme(MonopolyConstants.DEFAULT_BOARD_NAMES,
        MonopolyConstants.DEFAULT_BOARD_COLORS);
    board = new BoardJSON(theme);

    runSparkServer();
  }

  private static FreeMarkerEngine createEngine() {
    Configuration config = new Configuration();
    File templates = new File("src/main/resources/spark/template/freemarker");
    try {
      config.setDirectoryForTemplateLoading(templates);
    } catch (IOException ioe) {
      System.out.printf("ERROR: Unable use %s for template loading.%n",
          templates);
      System.exit(1);
    }
    return new FreeMarkerEngine(config);
  }

  /**
   * Runs the spark server.
   *
   * @author mprafson
   *
   */
  private void runSparkServer() {
    Spark.externalStaticFileLocation("src/main/resources/static");
    Spark.exception(Exception.class, new ExceptionPrinter());
    // Spark.setPort(ip.getPort());

    FreeMarkerEngine freeMarker = createEngine();

    // Setup Spark Routes
    Spark.get("/monopoly", new FrontHandler(), freeMarker);
    Spark.post("/loadPlayer", new LoadPlayerHandler());
    Spark.post("/loadBoard", new LoadBoardHandler());
    Spark.post("/roll", new RollHandler());
    Spark.post("/createGameSettings", new CreateGameSettingsHandler());
    Spark.post("/startTurn", new StartTurnHandler());
    Spark.post("/move", new MoveHandler());
    Spark.post("/play", new PlayHandler());

    /*
     * Allows for the connection to the DB to be closed. Waits for the user to
     * hit "enter" or "CTRL-D"
     */
    try (BufferedReader br = new BufferedReader(new InputStreamReader(
        System.in, "UTF-8"))) {
      String line = null;
      while ((line = br.readLine()) != null) {
        if (line.equals("")) {
          break;
        }
      }
    } catch (UnsupportedEncodingException e) {
      System.err.println("ERROR: Main, runSparkServer: Not UTF-8");
    } catch (IOException e) {
      System.err.println("ERROR: Main, runSparkServer: reader failure.");
    } finally {
      Spark.stop();
    }
  }

  /**
   * Sets up the opening page.
   *
   * @author mprafson
   *
   */
  private static class FrontHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      Map<String, Object> variables = ImmutableMap.of("title", "Monopoly");
      return new ModelAndView(variables, "monopoly.ftl");
    }
  }

  /**
   * Gets the inputted line using JQuery, it is then read in and autocorrect.
   * Results are sent back to the server.
   *
   * @author mprafson
   *
   */
  private class LoadPlayerHandler implements Route {

    @Override
    public Object handle(Request req, Response res) {
      QueryParamsMap qm = req.queryMap();

      String name = GSON.fromJson(qm.value("player_name"), String.class);

      Player p = dummy;

      Map<String, Object> variables = ImmutableMap.of("player", p);

      return GSON.toJson(variables);

    }
  }
  
  private class CreateGameSettingsHandler implements Route {

    @Override
    public Object handle(Request req, Response res) {
      QueryParamsMap qm = req.queryMap();
      String[][] players = GSON.fromJson(qm.value("players"), String[][].class);
      gs = new GameSettings(null);
      int countedNumAI = 0;
      int countedNumHuman = 0;
      for (int i = 0; i < players.length; i++) {
        String name = players[i][0];
        String type = players[i][1];
        switch (type) {
          case "human":
            gs.addHumanName(name);
            countedNumHuman++;
            break;
          case "ai":
            gs.addAIName(name);
            countedNumAI++;
            break;
          default:
            throw new IllegalArgumentException();
        }
      }
      
      int actualNumAI = GSON.fromJson(qm.value("numAI"), Integer.class);
      int actualNumHuman = GSON.fromJson(qm.value("numHuman"), Integer.class);

      assert actualNumAI == countedNumAI;
      assert actualNumHuman == countedNumHuman;
      
      gs.setNumHumans(actualNumHuman);
      gs.setNumAI(actualNumAI);
      
      boolean fastPlay = false;
      String gamePlay = GSON.fromJson(qm.value("gamePlay"), String.class);
      if (gamePlay.equals("fast")) {
        fastPlay = true;
      }
      gs.setFastPlay(fastPlay);

      game = new GameFactory().createGame(gs);
      if (game == null) {
        // invalid settings inputted
        Map<String, Object> variables = ImmutableMap.of("error",
                "invalid settings");
        return GSON.toJson(variables);
      }
      ref = game.getReferee();
      BoardJSON board = new BoardJSON(gs.getTheme());
      Map<String, Object> variables = ImmutableMap.of("state",
              ref.getCurrGameState(), "board", board);
      return GSON.toJson(variables);

    }
  }

  /**
   * Gets the inputted line using JQuery, it is then read in and autocorrect.
   * Results are sent back to the server.
   *
   * @author mprafson
   *
   */
  private class LoadBoardHandler implements Route {

    @Override
    public Object handle(Request req, Response res) {
      QueryParamsMap qm = req.queryMap();
      Map<String, Object> variables = ImmutableMap.of("board", board);
      return GSON.toJson(variables);
    }
  }

  private class RollHandler implements Route {

    @Override
    public Object handle(Request req, Response res) {
      boolean canMove = ref.roll();
      Map<String, Object> variables = ImmutableMap.of("dice", ref.getDice(), "canMove", canMove);
      return GSON.toJson(variables);
    }
  }


  private class StartTurnHandler implements Route {

    @Override
    public Object handle(Request req, Response res) {
      PlayerJSON currplayer = new PlayerJSON(ref.nextTurn());
      Map<String, Object> variables = ImmutableMap.of("player",
          currplayer);
      return GSON.toJson(variables);
    }
  }

  private class MoveHandler implements Route {

    @Override
    public Object handle(Request req, Response res) {
      QueryParamsMap qm = req.queryMap();
      int dist = Integer.parseInt(qm.value("dist"));
      boolean inputNeeded = ref.move(dist);
      String name = ref.getCurrSquare().getName();
      PlayerJSON currplayer = new PlayerJSON(ref.getCurrPlayer());
      Map<String, Object> variables = ImmutableMap.of("squareName",
          name, "inputNeeded", inputNeeded, "player", currplayer);
      return GSON.toJson(variables);
    }
  }

  private class PlayHandler implements Route {

    @Override
    public Object handle(Request req, Response res) {
      QueryParamsMap qm = req.queryMap();
      int input = Integer.parseInt(qm.value("input"));
      String message = ref.play(input);
      PlayerJSON currplayer = new PlayerJSON(ref.getCurrPlayer());
      Map<String, Object> variables = ImmutableMap.of("message", message, "player", currplayer);
      return GSON.toJson(variables);
    }
  }

  /**
   * Handles exceptions. Sends them to the server.
   *
   * @author mprafson
   *
   */
  private static class ExceptionPrinter implements ExceptionHandler {
    @Override
    public void handle(Exception e, Request req, Response res) {
      res.status(STATUS);
      StringWriter stacktrace = new StringWriter();
      try (PrintWriter pw = new PrintWriter(stacktrace)) {
        pw.println("<pre>");
        e.printStackTrace(pw);
        pw.println("</pre>");
      }
      res.body(stacktrace.toString());
    }
  }
}
