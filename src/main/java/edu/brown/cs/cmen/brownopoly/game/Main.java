package edu.brown.cs.cmen.brownopoly.game;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
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

import edu.brown.cs.cmen.brownopoly.player.Player;
import edu.brown.cs.cmen.brownopoly_util.Dice;
import freemarker.template.Configuration;

public class Main {
  private static final Gson GSON = new Gson();
  private static final int STATUS = 500;
  private String[] args;
  private Game game;
  private Referee ref;

  /**
   * Runs the mainline.
   *
   * @param args
   *          - args from the mainline
   */
  public static void main(String[] args) {
    new Main(args).run();
  }

  private Main(String[] args) {
    this.args = args;
  }

  private void run() {
     new GUIRunner();
//    runSparkServer();
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

    // Do we need this?
    /*
     * Allows for the connection to the DB to be closed. Waits for the user to
     * hit "enter" or "CTRL-D"
     */
    /*
     * try (BufferedReader br = new BufferedReader(new InputStreamReader(
     * System.in, "UTF-8"))) { String line = null; while ((line = br.readLine())
     * != null) { if (line.equals("")) { break; } } } catch
     * (UnsupportedEncodingException e) {
     * System.err.println("ERROR: Main, runSparkServer: Not UTF-8"); } catch
     * (IOException e) {
     * System.err.println("ERROR: Main, runSparkServer: reader failure."); }
     * finally { Spark.stop(); }
     */
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

  private class NewGameHandler implements Route {

    @Override
    public Object handle(Request request, Response response) {
      QueryParamsMap qm = request.queryMap();
      GameSettings settings = GSON.fromJson(qm.value("settings"),
          GameSettings.class);
      game = new GameFactory().createGame(settings);
      if (game == null) {
        // invalid settings inputted
        Map<String, Object> variables = ImmutableMap.of("error",
            "invalid settings");
        return GSON.toJson(variables);
      }
      ref = game.getReferee();
      return null;
    }

  }

  private class StartTurnHandler implements Route {

    @Override
    public Object handle(Request req, Response res) {
      Player p = ref.nextTurn();
      Map<String, Object> variables = ImmutableMap.of("player", p);
      return GSON.toJson(variables);

    }
  }

  private class RollHandler implements Route {

    @Override
    public Object handle(Request req, Response res) {
      // ref.roll()?
      Dice dice = ref.rollDice();
      Player curr = null;
      boolean goToJail = false;
      if (dice.numDoubles() == 3) {
        curr.moveToJail();
        goToJail = true;
      }
      Map<String, Object> variables = ImmutableMap.of("dice", dice, "jail",
          goToJail);
      return GSON.toJson(variables);
    }
  }

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
