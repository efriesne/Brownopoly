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

import edu.brown.cs.cmen.brownopoly.board.Board;
import edu.brown.cs.cmen.brownopoly.board.BoardFactory;
import edu.brown.cs.cmen.brownopoly.customboards.BoardTheme;
import edu.brown.cs.cmen.brownopoly.ownable.Property;
import edu.brown.cs.cmen.brownopoly.player.Human;
import edu.brown.cs.cmen.brownopoly.player.Player;
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
  private Board board;
  private BoardTheme theme;

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

    dummy = new Human("Marley", list, false);
    GameSettings gs = new GameSettings();
    theme = new BoardTheme(MonopolyConstants.DEFAULT_BOARD_NAMES,
        MonopolyConstants.DEFAULT_BOARD_COLORS);
    gs.setTheme(theme);
    BoardFactory fac = new BoardFactory(gs);
    board = fac.build();
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

    // Do we need this? // yes
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
      Map<String, Object> variables = ImmutableMap.of("board", theme);
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
