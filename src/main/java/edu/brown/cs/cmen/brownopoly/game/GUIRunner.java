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
    GameSettings gs = new GameSettings();
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

  private class NewGameHandler implements Route {

    @Override
    public Object handle(Request request, Response response) {
      QueryParamsMap qm = request.queryMap();
      GameSettings settings = GSON.fromJson(qm.value("settings"),
          GameSettings.class);
      // check settings variable
      game = new GameFactory().createGame(settings);
      if (game == null) {
        // invalid settings inputted
        Map<String, Object> variables = ImmutableMap.of("error",
            "invalid settings");
        return GSON.toJson(variables);
      }
      ref = game.getReferee();
      BoardJSON board = new BoardJSON(settings.getTheme());
      Map<String, Object> variables = ImmutableMap.of("state",
          ref.getCurrGameState(), "board", board);
      return GSON.toJson(variables);
    }

  }

  private class RollHandler implements Route {

    @Override
    public Object handle(Request req, Response res) {
      /*
       * // ref.roll()? Dice dice = ref.rollDice(); Player curr = null; boolean
       * goToJail = false; if (dice.numDoubles() == 3) { curr.moveToJail();
       * goToJail = true; } Map<String, Object> variables =
       * ImmutableMap.of("dice", dice, "jail", goToJail); return
       * GSON.toJson(variables);
       */
      // roll dice
      // if utility square -> prompt user to roll again
      // if unowned property -> prompt user to make decision
      boolean inputNeeded = ref.roll();
      Map<String, Object> variables = ImmutableMap.of("inputNeeded", inputNeeded, "dice", ref.getDice());
      return GSON.toJson(variables);
    }
  }

  // hopefully we will have general enough code to not need this class
  private class InJailRollHandler implements Route {
    // when the user is in jail, a use jail free card/pay to get out of
    // jail/roll button
    // maps to this handler
    @Override
    public Object handle(Request req, Response res) {
      boolean paidBail = ref.mustPayBail();
      boolean canMove = false;
      Dice dice = null;
      if (!paidBail) { // can try to roll doubles
        dice = ref.rollDice();
        if (dice.isDoubles()) {
          canMove = true;
        }
      }
      Map<String, Object> variables = ImmutableMap.of("paid", paidBail, "dice",
          dice, "move", canMove);
      return GSON.toJson(variables);
    }
  }

  private class StartTurnHandler implements Route {

    @Override
    public Object handle(Request req, Response res) {
      ref.nextTurn();
      Player currplayer = null; // ref.getPlayer();
      // Trader trade = referee.getTrade();
      Map<String, Object> variables = ImmutableMap.of("player",
          currplayer.getName());
      return GSON.toJson(variables);
    }
  }

  private class MoveHandler implements Route {

    @Override
    public Object handle(Request req, Response res) {
      ref.move();
      BoardSquare square = null; /* ref.getCurrSquare(); */
      Map<String, Object> variables = ImmutableMap.of("square",
          square.getName(), "input", square.getInput());
      return GSON.toJson(variables);
    }
  }

  private class SquareEffectHandler implements Route {

    @Override
    public Object handle(Request req, Response res) {
      QueryParamsMap qm = req.queryMap();
      int input = Integer.parseInt(qm.value("input"));
      // String turnInfo = play(input);
      // Map<String, Object> variables = ImmutableMap.of("info", turnInfo,
      // "player", currplayer);
      // return GSON.toJson(variables);
      return null;
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
