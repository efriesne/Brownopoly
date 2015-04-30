package edu.brown.cs.cmen.brownopoly.game;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
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

import edu.brown.cs.cmen.brownopoly.gamestate.MemoryManager;
import edu.brown.cs.cmen.brownopoly.ownable.Ownable;
import edu.brown.cs.cmen.brownopoly.ownable.OwnableManager;
import edu.brown.cs.cmen.brownopoly.ownable.Property;
import edu.brown.cs.cmen.brownopoly.web.BoardJSON;
import edu.brown.cs.cmen.brownopoly.web.PlayerJSON;
import edu.brown.cs.cmen.brownopoly.web.TitleDeed;
import edu.brown.cs.cmen.brownopoly.web.TradeProposalJSON;
import freemarker.template.Configuration;

public class GUIRunner {
  private static final Gson GSON = new Gson();
  private static final int STATUS = 500;

  private Game game;
  private Referee ref;
  private MemoryManager manager;

  public GUIRunner() {
    manager = new MemoryManager();
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
    Spark.post("/loadDefaults", new LoadDefaultsHandler());
    Spark.post("/loadPlayer", new LoadPlayerHandler());
    Spark.post("/loadDeeds", new LoadDeedsHandler());
    Spark.post("/roll", new RollHandler());
    Spark.post("/createGameSettings", new CreateGameSettingsHandler());
    Spark.post("/startTurn", new StartTurnHandler());
    Spark.post("/move", new MoveHandler());
    Spark.post("/play", new PlayHandler());
    Spark.post("/tradeSetUp", new TradeLoaderHandler());
    Spark.post("/trade", new TradeHandler());
    Spark.post("/checkSaved", new CheckExistingSavedGameHandler());
    Spark.post("/checkFilename", new CheckSaveFilenameHandler());
    Spark.post("/save", new SaveGameHandler());
    Spark.post("/getSavedGames", new GetSavedGamesHandler());
    Spark.post("/deleteSavedGames", new DeleteSavedHandler());
    Spark.post("loadGame", new LoadGameHandler());
    Spark.post("/startAITurn", new StartAIHandler());
    Spark.post("/removeBankrupts", new BankruptcyHandler());
    Spark.post("/getGameState", new GameStateHandler());
    Spark.post("/getOutOfJail", new JailHandler());
    Spark.post("/mortgageAI", new MortgageAIHandler());
    Spark.post("/checkTradeMoney", new TradeMoneyHandler());

    /**********/
    Spark.post("/test", new DummyHandler());
    /********/

    Spark.post("/manage", new ManageHandler());
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

      GameSettings gs = new GameSettings(MonopolyConstants.DEFAULT_THEME, false);
      gs.addHumanName("Emma");
      gs.addHumanName("Marley");
      gs.addAIName("Nickie");

      game = new GameFactory().createGame(gs);

      // serializable test
      try {
        manager.save(game, "game1");
      } catch (IOException e) {
        System.out.println("IOException");
      }

      gs = new GameSettings(MonopolyConstants.DEFAULT_THEME, false);
      gs.addHumanName("Bob");
      gs.addHumanName("Jim");
      // gs.addAIName("Fred");
      // gs.addAIName("Bill");

      game = new GameFactory().createGame(gs);

      // serializable test
      try {
        manager.save(game, "game2");
      } catch (IOException e) {
        System.out.println("IOException");
      }

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

  /**
   * Gets the inputted line using JQuery, it is then read in and autocorrect.
   * Results are sent back to the server.
   *
   * @author mprafson
   *
   */
  private class LoadDefaultsHandler implements Route {
    @Override
    public Object handle(Request req, Response res) {
      Map<String, Object> variables = ImmutableMap.of("defaultNames",
          MonopolyConstants.DEFAULT_BOARD_NAMES, "defaultColors",
          MonopolyConstants.DEFAULT_BOARD_COLORS);
      return GSON.toJson(variables);
    }
  }

  private class GameStateHandler implements Route {

    @Override
    public Object handle(Request req, Response res) {
      Map<String, Object> variables = ImmutableMap.of("state",
          ref.getCurrGameState());
      return GSON.toJson(variables);
    }
  }

  private class LoadDeedsHandler implements Route {

    @Override
    public Object handle(Request req, Response res) {
      TitleDeed[] deeds = new TitleDeed[40];
      for (int i = 0; i < 40; i++) {
        deeds[i] = new TitleDeed(game.getTheme(), i);
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

      GameSettings gs = new GameSettings(MonopolyConstants.DEFAULT_THEME,
          fastPlay);
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

  private class TradeMoneyHandler implements Route {

    @Override
    public Object handle(Request req, Response res) {
      QueryParamsMap qm = req.queryMap();
      String recipientID = qm.value("recipientID");
      boolean invalidMoney;
      try {
        int initMoney = Integer.parseInt(qm.value("initMoney"));
        int recipMoney = Integer.parseInt(qm.value("recipMoney"));
        invalidMoney = ref.checkTradeMoney(recipientID, initMoney, recipMoney);
      } catch (NumberFormatException e) {
        invalidMoney = true;
      }
      Map<String, Object> variables = ImmutableMap.of("invalidMoney",
          invalidMoney);
      return GSON.toJson(variables);
    }
  }

  private class TradeHandler implements Route {

    @Override
    public Object handle(Request req, Response res) {
      QueryParamsMap qm = req.queryMap();
      String recipientID = qm.value("recipient");
      String[] initProps = GSON.fromJson(qm.value("initProps"), String[].class);
      String[] recipProps = GSON.fromJson(qm.value("recipProps"),
          String[].class);
      int initMoney = Integer.parseInt(qm.value("initMoney"));
      int recipMoney = Integer.parseInt(qm.value("recipMoney"));
      boolean accepted = ref.trade(recipientID, initProps, initMoney,
          recipProps, recipMoney);
      PlayerJSON currPlayer = ref.getCurrPlayer();
      Map<String, Object> variables = ImmutableMap.of("accepted", accepted,
          "initiator", currPlayer);
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
      ref.nextTurn();
      PlayerJSON currPlayer = ref.getCurrPlayer();
      Map<String, Object> variables = ImmutableMap.of("player", currPlayer,
          "numPlayers", ref.getNumPlayers());
      return GSON.toJson(variables);
    }
  }

  private class StartAIHandler implements Route {

    @Override
    public Object handle(Request req, Response res) {
      System.out.println("AI 1");
      TradeProposalJSON trade = ref.getAITrade();
      System.out.println("AI 2");
      String build = ref.getAIBuild();
      System.out.println("AI 3");
      Map<String, Object> variables = ImmutableMap.of("AI",
          ref.getCurrPlayer(), "trade", trade, "build", build);
      return GSON.toJson(variables);
    }
  }

  private class JailHandler implements Route {

    @Override
    public Object handle(Request req, Response res) {
      QueryParamsMap qm = req.queryMap();
      int jailCard = Integer.parseInt(qm.value("jailCard"));
      ref.releaseFromJail(jailCard);
      Map<String, Object> variables = ImmutableMap.of("player",
          ref.getCurrPlayer());
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
      PlayerJSON currPlayer = ref.getCurrPlayer();
      Map<String, Object> variables = ImmutableMap.of("squareName", name,
          "inputNeeded", inputNeeded, "player", currPlayer);
      return GSON.toJson(variables);
    }
  }

  private class BankruptcyHandler implements Route {

    @Override
    public Object handle(Request req, Response res) {
      ref.removeBankruptPlayers();
      Map<String, Object> variables = ImmutableMap.of();
      return GSON.toJson(variables);
    }
  }

  private class MortgageAIHandler implements Route {

    @Override
    public Object handle(Request req, Response res) {
      QueryParamsMap qm = req.queryMap();
      String playerID = qm.value("player");
      String message = ref.mortgageAI(playerID);
      Map<String, Object> variables = ImmutableMap.of("player",
          ref.getPlayerJSON(playerID), "mortgage", message);
      return GSON.toJson(variables);
    }
  }

  private class PlayHandler implements Route {

    @Override
    public Object handle(Request req, Response res) {
      System.out.println(1);
      QueryParamsMap qm = req.queryMap();
      System.out.println(2);
      int input = Integer.parseInt(qm.value("input"));
      System.out.println(3);
      String message = ref.play(input);
      System.out.println(4);
      PlayerJSON currPlayer = ref.getCurrPlayer();
      System.out.println(5);
      Map<String, Object> variables = ImmutableMap.of("message", message,
          "player", currPlayer);
      return GSON.toJson(variables);
    }
  }

  private class ManageHandler implements Route {

    public Object handle(Request request, Response response) {
      QueryParamsMap qm = request.queryMap();
      String playerID = qm.value("playerID");
      String[][] mortgages = GSON.fromJson(qm.value("mortgages"),
          String[][].class);
      String[][] houseTransactions = GSON.fromJson(qm.value("houses"),
          String[][].class);
      for (int i = 0; i < mortgages.length; i++) {
        assert mortgages[i].length == 2;
        int ownableId = Integer.parseInt(mortgages[i][0]);
        boolean mortgaging = Boolean.valueOf(mortgages[i][1]);
        ref.handleMortgage(ownableId, mortgaging, playerID);
      }
      for (int i = 0; i < houseTransactions.length; i++) {
        assert houseTransactions[i].length == 2;
        int propId = Integer.parseInt(houseTransactions[i][0]);
        int numHouses = Integer.parseInt(houseTransactions[i][1]);
        boolean buying = numHouses >= 0;
        ref.handleHouseTransactions(propId, buying, numHouses);
      }
      Map<String, Object> variables = ImmutableMap.of("player",
          ref.getPlayerJSON(playerID));
      return GSON.toJson(variables);
    }

  }

  private class ValidHouseHandler implements Route {

    @Override
    public Object handle(Request request, Response response) {
      QueryParamsMap qm = request.queryMap();
      String playerID = qm.value("playerID");
      boolean builds = Boolean.valueOf(qm.value("builds"));
      String[][] houseTransactions = GSON.fromJson(qm.value("houses"),
          String[][].class);
      String[][] mortgages = GSON.fromJson(qm.value("mortgages"),
          String[][].class);
      // System.out.println("--OWNABLEMANAGER--");
      adjustHypotheticalTransactions(houseTransactions, mortgages, true);
      // System.out.println("--MONOPOLY---");
      int[] validHouses = builds ? ref.findValidBuilds(playerID) : ref
          .findValidSells(playerID);
      int[] validMorts = ref.findValidMortgages(!builds, playerID);
      // printArray(validHouses);
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

  private class CheckExistingSavedGameHandler implements Route {

    @Override
    public Object handle(Request arg0, Response arg1) {
      boolean exists = game.getSavedFilename() != null;
      Map<String, Object> variables = ImmutableMap.of("exists", exists);
      return GSON.toJson(variables);
    }
  }

  private class CheckSaveFilenameHandler implements Route {

    @Override
    public Object handle(Request arg0, Response arg1) {
      QueryParamsMap qm = arg0.queryMap();
      String uncheckedName = qm.value("name");
      boolean exists = manager.doesFileExist(uncheckedName);
      boolean valid = manager.isNameValid(uncheckedName);
      Map<String, Object> variables = ImmutableMap.of("valid", valid, "exists",
          exists);
      return GSON.toJson(variables);
    }
  }

  private class SaveGameHandler implements Route {

    @Override
    public Object handle(Request req, Response resp) {
      QueryParamsMap qm = req.queryMap();
      boolean exists = Boolean.valueOf(qm.value("exists"));
      boolean errorFound = false;
      String filename = null;
      try {
        if (exists) {
          filename = game.getSavedFilename();
          manager.save(game, filename);
        } else {
          filename = qm.value("file");
          game.setSavedFilename(filename);
          manager.save(game, filename);
        }
      } catch (IOException e) {
        errorFound = true;
      }
      Map<String, Object> variables = ImmutableMap.of("error", errorFound,
          "filename", filename);
      return GSON.toJson(variables);
    }
  }

  private class GetSavedGamesHandler implements Route {

    @Override
    public Object handle(Request arg0, Response arg1) {
      String[] fileNames = null;
      Map<String, Object> variables;
      try {
        fileNames = manager.getSavedGames();
      } catch (Exception e) {
        variables = ImmutableMap.of("error", true);
        return GSON.toJson(variables);
      }
      variables = ImmutableMap.of("games", fileNames);
      return GSON.toJson(variables);
    }
  }

  private class DeleteSavedHandler implements Route {

    @Override
    public Object handle(Request request, Response response) {
      boolean error = false;
      try {
        manager.removeSavedGames();
      } catch (IOException e) {
        error = true;
      }
      return GSON.toJson(ImmutableMap.of("error", error));
    }

  }

  private class LoadGameHandler implements Route {

    @Override
    public Object handle(Request arg0, Response arg1) {
      QueryParamsMap qm = arg0.queryMap();
      String filename = qm.value("file");
      boolean errorFound = false;
      try {
        game = manager.load(filename);
      } catch (ClassNotFoundException e) {
        errorFound = true;
      } catch (IOException e) {
        errorFound = true;
      }
      if (game == null || errorFound) {
        // invalid settings inputted
        Map<String, Object> variables = ImmutableMap.of("error",
            "unable to load");
        return GSON.toJson(variables);
      }
      ref = game.getReferee();
      BoardJSON board = new BoardJSON(game.getTheme());
      Map<String, Object> variables = ImmutableMap.of("state",
          ref.getCurrGameState(), "board", board, "fastPlayer",
          game.isFastPlay());
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
