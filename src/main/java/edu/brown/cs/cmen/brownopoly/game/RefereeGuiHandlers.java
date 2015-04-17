package edu.brown.cs.cmen.brownopoly.game;

import java.util.Map;

import spark.Request;
import spark.Response;
import spark.Route;

import com.google.common.collect.ImmutableMap;

import edu.brown.cs.cmen.brownopoly.board.BoardSquare;
import edu.brown.cs.cmen.brownopoly.player.Player;
import edu.brown.cs.cmen.brownopoly_util.Dice;

public class RefereeGuiHandlers {
  private Referee referee;
  
    private class StartTurnHandler implements Route {

      @Override
      public Object handle(Request req, Response res) {
        referee.nextTurn();
        Player currplayer = referee.getPlayer();
        //Trader trade = referee.getTrade();
        Map<String, Object> variables = ImmutableMap.of("player", currplayer.getName());
        return GSON.toJson(variables);
      }
    }
    
    private class RollHandler implements Route {

      @Override
      public Object handle(Request req, Response res) {
        referee.roll();
        Player player = referee.getPlayer();
        Map<String, Object> variables = ImmutableMap.of("dice", referee.getDice(), "jail", player.isInJail());
        return GSON.toJson(variables);
      }
    }
    
    private class InJailRollHandler implements Route {
      //when the user is in jail, a use jail free card/pay to get out of jail/roll button
      //maps to this handler
      @Override
      public Object handle(Request req, Response res) {
        referee.rollInJail();
        Map<String, Object> variables = ImmutableMap.of("paid", paidBail, "dice", dice,
            "move", canMove);
        return GSON.toJson(variables);
      }
    }
    
    private class MoveHandler implements Route {
    
      @Override
      public Object handle(Request req, Response res) {
        referee.move();
        BoardSquare square = referee.getCurrSquare();
        Map<String, Object> variables = ImmutableMap.of("square", square.getName(), "input", square.getInput());
        return GSON.toJson(variables);
      }
    }
    
    private class SquareEffectHandler implements Route {
      
      @Override
      public Object handle(Request req, Response res) {
        QueryParamsMap qm = req.queryMap();
        int input = Integer.parseInt(qm.value("input"));
        String turnInfo = play(input);      
        Map<String, Object> variables = ImmutableMap.of("info", turnInfo, "player", currplayer);
        return GSON.toJson(variables);
      }
    }
  
}
