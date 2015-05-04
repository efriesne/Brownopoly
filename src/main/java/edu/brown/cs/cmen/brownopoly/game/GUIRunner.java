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

import edu.brown.cs.cmen.brownopoly.customboards.BoardTheme;
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
    Spark.post("/saveTheme", new SaveThemeHandler());
    Spark.post("/getSavedData", new GetSavedDataHandler());
    Spark.post("/deleteData", new DeleteDataHandler());
    Spark.post("/clearSavedData", new ClearSavedDataHandler());
    Spark.post("/loadGame", new LoadGameHandler());
    Spark.post("/loadTheme", new LoadThemeHandler());
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
    Spark.post("/findValids", new FindValidsHandler());

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
        manager.saveGame(game, "game1");
      } catch (IOException e) {
      }

      gs = new GameSettings(MonopolyConstants.DEFAULT_THEME, true);
      gs.addAIName("Bob");
      gs.addHumanName("Jim");
      // gs.addAIName("Fred");
      // gs.addAIName("Bill");

      game = new GameFactory().createGame(gs);

      // serializable test
      try {
        manager.saveGame(game, "game2");
      } catch (IOException e) {
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
      if (p != null) {
        Map<String, Object> variables = ImmutableMap.of("player", p);
        return GSON.toJson(variables);
      } else {
        throw new IllegalArgumentException();
      }
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

      BoardTheme theme = GSON.fromJson(qm.value("theme"), BoardTheme.class);
      theme = theme == null ? MonopolyConstants.DEFAULT_THEME : theme;

      GameSettings gs = new GameSettings(theme, fastPlay);
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
      String playerID = qm.value("playerID");
      String invalidMoney = "";
      try {
        int money = Integer.parseInt(qm.value("money"));
        int error = ref.checkTradeMoney(playerID, money);
        if (error == 0) {
          invalidMoney = "Money amount cannot be negative.";
        } else if (error == 1) {
          invalidMoney = "Money amount exceeds player's balance.";
        }
      } catch (NumberFormatException e) {
        invalidMoney = "Money amount cannot be non-numbers.";
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
      String init = ref.getCurrPlayer().getName();
      String recip = ref.getPlayerByID(recipientID).getName();
      String initPropNames = "";
      for (String id : initProps) {
        initPropNames += OwnableManager.getOwnable(Integer.parseInt(id))
            .getName() + ", ";
      }
      if (initPropNames.equals("")) {
        initPropNames = "no properties";
      }
      String recipPropNames = "";
      for (String id : recipProps) {
        recipPropNames += OwnableManager.getOwnable(Integer.parseInt(id))
            .getName() + ", ";
      }
      if (recipPropNames.equals("")) {
        recipPropNames = "no properties";
      }
      String tradeMessage = initPropNames + " and $" + initMoney
          + "  for " + recip + "'s " + recipPropNames + " and $" + recipMoney
          + ".";

      Map<String, Object> variables = ImmutableMap.of("accepted", accepted,
          "initiator", currPlayer, "msg", tradeMessage);
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
      String payOffMortgage = null;
      String build = null;
      TradeProposalJSON trade = null;
      try {
        payOffMortgage = ref.getAIPayOff();
        build = ref.getAIBuild();
        trade = ref.getAITrade();

      } catch (Exception e) {
        e.printStackTrace();
      }
      Map<String, Object> variables = ImmutableMap.of("AI",
          ref.getCurrPlayer(), "trade", trade, "build", build, "mortgage",
          payOffMortgage);
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
      boolean canBuy = true;
      if (inputNeeded) {
        canBuy = ref.playerCanBuy();
      }
      String name = ref.getCurrSquare().getName();
      PlayerJSON currPlayer = ref.getCurrPlayer();
      Map<String, Object> variables = ImmutableMap.of("squareName", name,
          "inputNeeded", inputNeeded, "player", currPlayer, "canBuy", canBuy);
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
      String[] itemsMortgaged = message.split(",");
      StringBuilder builder = new StringBuilder();
      String prop = null;
      int numHouses = 0;
      for (String item : itemsMortgaged) {
        String[] house = item.split("_");
        if (house.length == 2) {
          if (prop != null) {
            if (prop.equals(house[1])) {
              numHouses++;
            } else {
              builder.append("took " + numHouses + " off of " + prop + ", ");
              numHouses = 1;
            }
          } else {
            numHouses = 1;
          }
          prop = house[1];
        } else if (house.length == 1) {
          if (house[0] != "") {
            builder.append("mortgaged " + house[0] + ", ");
            prop = house[0];
          }
        }
      }

      String[] msg = builder.toString().split(", ");
      String mortgageString = "";
      for (int i = 0; i < msg.length; i++) {
        if (i == msg.length - 1) {
          mortgageString += "and " + msg[i] + ".";
        } else {
          mortgageString += msg[i] + ", ";
        }
      }

      Map<String, Object> variables = ImmutableMap.of("player",
          ref.getPlayerJSON(playerID), "mortgage", mortgageString);
      return GSON.toJson(variables);
    }
  }

  private class PlayHandler implements Route {

    @Override
    public Object handle(Request req, Response res) {
      QueryParamsMap qm = req.queryMap();
      int input = Integer.parseInt(qm.value("input"));
      String message = ref.play(input);
      PlayerJSON currPlayer = ref.getCurrPlayer();
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

  private class FindValidsHandler implements Route {

    @Override
    public Object handle(Request request, Response response) {
      QueryParamsMap qm = request.queryMap();
      String playerID = qm.value("playerID");
      boolean builds = Boolean.valueOf(qm.value("buildOrDemortgage"));
      String[][] houseTransactions = GSON.fromJson(qm.value("houses"),
          String[][].class);
      String[][] mortgages = GSON.fromJson(qm.value("mortgages"),
          String[][].class);
      adjustHypotheticalHouseTransactions(houseTransactions, true);
      adjustHypotheticalMortgages(mortgages, true);
      int[] validHouses = builds ? ref.findValidBuilds(playerID) : ref
          .findValidSells(playerID);
      int[] validMorts = ref.findValidMortgages(!builds, playerID);
      adjustHypotheticalHouseTransactions(houseTransactions, false);
      adjustHypotheticalMortgages(mortgages, false);

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
    }

    private void adjustHypotheticalHouseTransactions(String[][] houses,
        boolean first) {
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
    }

    private void adjustHypotheticalMortgages(String[][] mortgages, boolean first) {
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
      String filename = game.getSavedFilename();
      boolean exists = filename != null;
      filename = filename == null ? "" : filename;
      Map<String, Object> variables = ImmutableMap.of("exists", exists,
          "filename", filename);
      return GSON.toJson(variables);
    }
  }

  private class CheckSaveFilenameHandler implements Route {

    @Override
    public Object handle(Request arg0, Response arg1) {
      QueryParamsMap qm = arg0.queryMap();
      String uncheckedName = qm.value("name");
      Boolean isGame = Boolean.valueOf(qm.value("isGame"));
      boolean exists;
      if (isGame) {
        exists = manager.doesGameFileExist(uncheckedName);
      } else {
        exists = manager.doesThemeFileExist(uncheckedName);
      }
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
          manager.saveGame(game, filename);
        } else {
          filename = qm.value("file");
          game.setSavedFilename(filename);
          manager.saveGame(game, filename);
        }
      } catch (IOException e) {
        e.printStackTrace();
        errorFound = true;
      }
      Map<String, Object> variables = ImmutableMap.of("error", errorFound,
          "filename", filename);
      return GSON.toJson(variables);
    }
  }

  private class SaveThemeHandler implements Route {

    @Override
    public Object handle(Request request, Response response) {
      QueryParamsMap qm = request.queryMap();
      boolean errorFound = false;
      String filename = qm.value("file");
      BoardTheme theme = GSON.fromJson(qm.value("theme"), BoardTheme.class);
      try {
        manager.saveTheme(theme, filename);
      } catch (IOException e) {
        errorFound = true;
      }
      Map<String, Object> variables = ImmutableMap.of("error", errorFound,
          "filename", filename);
      return GSON.toJson(variables);
    }

  }

  private class GetSavedDataHandler implements Route {

    @Override
    public Object handle(Request arg0, Response arg1) {
      QueryParamsMap qm = arg0.queryMap();
      boolean isGames = Boolean.valueOf(qm.value("isGames"));
      String[] filenames = null;
      Map<String, Object> variables;
      try {
        if (isGames) {
          filenames = manager.getSavedGames();
        } else {
          filenames = manager.getSavedThemes();
        }
      } catch (Exception e) {
        variables = ImmutableMap.of("error", true);
        return GSON.toJson(variables);
      }
      variables = ImmutableMap.of("names", filenames);
      return GSON.toJson(variables);
    }
  }

  private class DeleteDataHandler implements Route {

    @Override
    public Object handle(Request request, Response response) {
      QueryParamsMap qm = request.queryMap();
      boolean isGame = Boolean.valueOf(qm.value("isGame"));
      String filename = qm.value("filename");
      boolean error = false;
      String[] names = null;
      try {
        if (isGame) {
          manager.removeGame(filename);
          names = manager.getSavedGames();
        } else {
          manager.removeTheme(filename);
          names = manager.getSavedThemes();
        }
      } catch (IOException e) {
        error = true;
      }
      return GSON.toJson(ImmutableMap.of("error", error, "names", names));
    }

  }

  private class ClearSavedDataHandler implements Route {

    @Override
    public Object handle(Request request, Response response) {
      boolean isGames = Boolean.valueOf(request.queryMap().value("isGames"));
      boolean error = false;
      try {
        if (isGames) {
          manager.removeSavedGames();
        } else {
          manager.removeSavedThemes();
        }
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
        game = manager.loadGame(filename);
      } catch (ClassNotFoundException e) {
        errorFound = true;
      } catch (IOException e) {
        errorFound = true;
      }
      if (game == null || errorFound) {
        // invalid settings inputted
        Map<String, Object> variables = ImmutableMap.of("error",
            "Unexpected error occurred while loading " + filename);
        return GSON.toJson(variables);
      }
      ref = game.getReferee();
      BoardJSON board = new BoardJSON(game.getTheme());
      Map<String, Object> variables = ImmutableMap.of("state",
          ref.getCurrGameState(), "board", board);
      return GSON.toJson(variables);
    }
  }

  private class LoadThemeHandler implements Route {

    @Override
    public Object handle(Request request, Response response) {
      QueryParamsMap qm = request.queryMap();
      String filename = qm.value("file");
      boolean errorFound = false;
      BoardTheme theme = null;
      try {
        theme = manager.loadTheme(filename);
      } catch (ClassNotFoundException e) {
        errorFound = true;
      } catch (IOException e) {
        errorFound = true;
      }
      if (theme == null || errorFound) {
        // invalid settings inputted
        Map<String, Object> variables = ImmutableMap.of("error",
            "Unexpected error occurred while loading " + filename);
        return GSON.toJson(variables);
      }
      Map<String, Object> variables = ImmutableMap.of("theme", theme);
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
  private class LoadDefaultsHandler implements Route {
    @Override
    public Object handle(Request req, Response res) {
      Map<String, Object> variables = ImmutableMap.of("defaultNames",
          MonopolyConstants.DEFAULT_BOARD_NAMES, "defaultColors",
          MonopolyConstants.DEFAULT_BOARD_COLORS);
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
