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

import edu.brown.cs.cmen.brownopoly.ownable.Ownable;
import edu.brown.cs.cmen.brownopoly.ownable.OwnableManager;
import edu.brown.cs.cmen.brownopoly.ownable.Property;
import edu.brown.cs.cmen.brownopoly.web.BoardJSON;
import edu.brown.cs.cmen.brownopoly.web.PlayerJSON;
import edu.brown.cs.cmen.brownopoly.web.TitleDeed;
import freemarker.template.Configuration;

public class GUIRunner {
  private static final Gson GSON = new Gson();
  private static final int STATUS = 500;

  // private Player dummy;
  private BoardJSON board;
  // private BoardTheme theme;
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

    // dummy = new Human("Marley", list, false, "player_1");
    // theme = new BoardTheme(MonopolyConstants.DEFAULT_BOARD_NAMES,
    // MonopolyConstants.DEFAULT_BOARD_COLORS);
    // board = new BoardJSON(theme);

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
    Spark.post("/loadDeeds", new LoadDeedsHandler());
    Spark.post("/roll", new RollHandler());
    Spark.post("/createGameSettings", new CreateGameSettingsHandler());
    Spark.post("/startTurn", new StartTurnHandler());
    Spark.post("/move", new MoveHandler());
    Spark.post("/play", new PlayHandler());
    Spark.post("/tradeSetUp", new TradeLoaderHandler());

    /**********/
    //Spark.post("/test", new DummyHandler());
    /********/

    Spark.post("/mortgage", new MortgageHandler());
    Spark.post("/findValids", new ValidHouseHandler());

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

  private class DummyHandler implements Route {

    @Override
    public Object handle(Request req, Response res) {

      gs = new GameSettings(MonopolyConstants.DEFAULT_THEME, false);
      gs.addHumanName("Emma");
      gs.addHumanName("Marley");
      gs.addAIName("Nickie");

      game = new GameFactory().createGame(gs);
      if (game == null) {
        // invalid settings inputted
        Map<String, Object> variables = ImmutableMap.of("error",
            "invalid settings");
        return GSON.toJson(variables);
      }
      ref = game.getReferee();
      ref.fillDummyPlayer();
      BoardJSON board = new BoardJSON(gs.getTheme());
      Map<String, Object> variables = ImmutableMap.of("state",
          ref.getCurrGameState(), "board", board);
      return GSON.toJson(variables);

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

      String playerID = GSON.fromJson(qm.value("playerID"), String.class);
      PlayerJSON p = ref.getCurrGameState().getPlayerByID(playerID);
      // System.out.println(p);
      if (p != null) {
        Map<String, Object> variables = ImmutableMap.of("player", p);
        return GSON.toJson(variables);
      } else {
        throw new IllegalArgumentException();
      }
    }
  }

  private class LoadDeedsHandler implements Route {

    @Override
    public Object handle(Request req, Response res) {
      TitleDeed[] deeds = new TitleDeed[40];
      for (int i = 0; i < 40; i++) {
        deeds[i] = new TitleDeed(gs.getTheme(), i);
      }

      Map<String, Object> variables = ImmutableMap.of("deeds", deeds);
      return GSON.toJson(variables);
    }
  }

  private class CreateGameSettingsHandler implements Route {

    @Override
    public Object handle(Request req, Response res) {
      QueryParamsMap qm = req.queryMap();
      String[][] players = GSON.fromJson(qm.value("players"), String[][].class);

      boolean fastPlay = false;
      String gamePlay = GSON.fromJson(qm.value("gamePlay"), String.class);
      if (gamePlay.equals("fast")) {
        fastPlay = true;
      }

      gs = new GameSettings(MonopolyConstants.DEFAULT_THEME, fastPlay);
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

      assert gs.getNumAI() == countedNumAI;
      assert gs.getNumHumans() == countedNumHuman;

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

  private class TradeLoaderHandler implements Route {

    @Override
    public Object handle(Request req, Response res) {
      Map<String, Object> variables = ImmutableMap.of("state",
          ref.getCurrGameState());
      return GSON.toJson(variables);
    }
  }

  private class RollHandler implements Route {

    @Override
    public Object handle(Request req, Response res) {
      boolean canMove = ref.roll();
      Map<String, Object> variables = ImmutableMap.of("dice", ref.getDice(),
          "canMove", canMove);
      return GSON.toJson(variables);
    }
  }

  private class StartTurnHandler implements Route {

    @Override
    public Object handle(Request req, Response res) {
      PlayerJSON currPlayer = new PlayerJSON(ref.nextTurn());
      Map<String, Object> variables = ImmutableMap.of("player", currPlayer);
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
      PlayerJSON currPlayer = new PlayerJSON(ref.getCurrPlayer());
      Map<String, Object> variables = ImmutableMap.of("squareName", name,
          "inputNeeded", inputNeeded, "player", currPlayer);
      return GSON.toJson(variables);
    }
  }

  private class PlayHandler implements Route {

    @Override
    public Object handle(Request req, Response res) {
      QueryParamsMap qm = req.queryMap();
      int input = Integer.parseInt(qm.value("input"));
      String message = ref.play(input);
      PlayerJSON currPlayer = new PlayerJSON(ref.getCurrPlayer());
      Map<String, Object> variables = ImmutableMap.of("message", message,
          "player", currPlayer);
      return GSON.toJson(variables);
    }
  }

  private class MortgageHandler implements Route {

    public Object handle(Request request, Response response) {
      QueryParamsMap qm = request.queryMap();

      /*
       * boolean mortgaging = Boolean.valueOf(qm.value("mortgaging")); int
       * ownableId = Integer.parseInt(qm.value("ownableID"));
       */

      String[][] mortgages = GSON.fromJson(qm.value("mortgages"),
          String[][].class);
      String[][] houseTransactions = GSON.fromJson(qm.value("houses"),
          String[][].class);
      System.out.println(mortgages);
      for (int i = 0; i < mortgages.length; i++) {
        assert mortgages[i].length == 2;
        int ownableId = Integer.parseInt(mortgages[i][0]);
        boolean mortgaging = Boolean.valueOf(mortgages[i][1]);
        ref.handleMortgage(ownableId, mortgaging);
      }
      for (int i = 0; i < houseTransactions.length; i++) {
        assert houseTransactions[i].length == 2;
        int propId = Integer.parseInt(houseTransactions[i][0]);
        int numHouses = Integer.parseInt(houseTransactions[i][1]);
        boolean buying = numHouses >= 0;
        ref.handleHouseTransactions(propId, buying, numHouses);
      }
      PlayerJSON curr = new PlayerJSON(ref.getCurrPlayer());
      Map<String, Object> variables = ImmutableMap.of("player", curr);
      return GSON.toJson(variables);
    }

  }

  private class ValidHouseHandler implements Route {

    @Override
    public Object handle(Request request, Response response) {
      QueryParamsMap qm = request.queryMap();
      boolean builds = Boolean.valueOf(qm.value("builds"));
      String[][] houseTransactions = GSON.fromJson(qm.value("houses"),
          String[][].class);
      String[][] mortgages = GSON.fromJson(qm.value("mortgages"),
          String[][].class);
      adjustHypotheticalTransactions(houseTransactions, mortgages, true);
      int[] validHouses = builds ? ref.findValidBuilds() : ref.findValidSells();
      int[] validMorts = ref.findValidMortgages(!builds);
      // printArray(validMorts);
      adjustHypotheticalTransactions(houseTransactions, mortgages, false);
      Map<String, Object> variables = ImmutableMap.of("validHouses",
          validHouses, "validMortgages", validMorts);
      return GSON.toJson(variables);
    }

    private void printArray(int[] arr) {
      StringBuilder arrStr = new StringBuilder("[");
      for (int x : arr) {
        arrStr.append(x).append(", ");
      }
      arrStr.append("]");
      System.out.println(arrStr.toString());
    }

    private void adjustHypotheticalTransactions(String[][] houses,
        String[][] mortgages, boolean first) {
      for (int i = 0; i < houses.length; i++) {
        assert houses[i].length == 2;
        // get the property, figure out the number of houses the user wants to
        // add/remove from this property
        int id = Integer.parseInt(houses[i][0]);
        int numHouses = Integer.parseInt(houses[i][1]);
        Property p = OwnableManager.getProperty(id);
        boolean negated = false;
        if (numHouses < 0) {
          negated = true;
          numHouses *= -1;
        }
        for (int j = 0; j < numHouses; j++) {
          // if numHouses was negative, the user plans to sell the houses
          // when first is true, enact the user's changes, when it is
          // false, the changes should be undone
          if ((negated && !first) || (!negated && first)) {
            p.addHouse();
          } else {
            p.removeHouse();
          }
        }
      }
      for (int i = 0; i < mortgages.length; i++) {
        assert mortgages[i].length == 2;
        int id = Integer.parseInt(mortgages[i][0]);
        Ownable o = OwnableManager.getOwnable(id);
        boolean mortgaging = Boolean.valueOf(mortgages[i][1]);
        if ((mortgaging && first) || (!mortgaging && !first)) {
          o.mortgage();
        } else {
          o.demortgage();
        }
      }
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
