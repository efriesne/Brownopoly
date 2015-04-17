package edu.brown.cs.cmen.brownopoly.game;

import java.util.Map;

import spark.Request;
import spark.Response;
import spark.Route;

import com.google.common.collect.ImmutableMap;

import edu.brown.cs.cmen.brownopoly.player.Player;
import edu.brown.cs.cmen.brownopoly_util.Dice;

public class RefereeGuiHandlers {
  private Referee referee;
  private class StartTurnHandler implements Route {

    @Override
    public Object handle(Request req, Response res) {
      Player p = referee.nextTurn();      
      Map<String, Object> variables = ImmutableMap.of("player", p);
      return GSON.toJson(variables);
    }
  }
  
  private class RollHandler implements Route {

    @Override
    public Object handle(Request req, Response res) {
      Dice dice = rollDice();
      boolean goToJail = false;
      if (dice.numDoubles() == 3) {
        curr.moveToJail();
        goToJail = true;
      }
      Map<String, Object> variables = ImmutableMap.of("dice", dice, "jail", goToJail);
      return GSON.toJson(variables);
    }
  }
  
  private class InJailRollHandler implements Route {
    //when the user is in jail, a use jail free card/pay to get out of jail/roll button
    //maps to this handler
    @Override
    public Object handle(Request req, Response res) {
      boolean paidBail = mustPayBail();
      boolean canMove = false;
      Dice dice = null;
      if (!paidBail) { //can try to roll doubles
        dice = rollDice();
        if (dice.isDoubles()) {
          canMove = true;
        }
      }
      Map<String, Object> variables = ImmutableMap.of("paid", paidBail, "dice", dice,
          "move", canMove);
      return GSON.toJson(variables);
    }
  }
  
  private class MoveHandler implements Route {
  
    @Override
    public Object handle(Request req, Response res) {
      
      String resp = play();
      String square = board.getSquare(curr.getPosition()).getName();
      
      Map<String, Object> variables = ImmutableMap.of("player", curr, "dice", dice,
          "move", canMove);
      return GSON.toJson(variables);
    }
  }
  
  private class SquareEffectHandler implements Route {
    
    @Override
    public Object handle(Request req, Response res) {
      
      String resp = play();
      String square = board.getSquare(curr.getPosition()).getName();
      
      Map<String, Object> variables = ImmutableMap.of("player", curr, "dice", dice,
          "move", canMove);
      return GSON.toJson(variables);
    }
  }
  
}
