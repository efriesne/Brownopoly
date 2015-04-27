package edu.brown.cs.cmen.brownopoly.cards;

import edu.brown.cs.cmen.brownopoly.player.Player;

/**
 * Created by codyyu on 3/31/15.
 */
public class NearestRailroad implements Card {
  /**
   * 
   */
  private static final long serialVersionUID = 6056554115301949330L;
  private String name = "Advance to Nearest Railroad";

  @Override
  public String play(Player player) {
    int position = player.getPosition();
    int distance1 = (5 - position + 40) % 40;
    int distance2 = (15 - position + 40) % 40;
    int distance3 = (25 - position + 40) % 40;
    int distance4 = (35 - position + 40) % 40;
    if (distance1 <= 10) {
      player.move(distance1);
      // return ":\nMove forward " + distance1 + " spaces!";
    } else if (distance2 <= 10) {
      player.move(distance2);
      // return ":\nMove forward " + distance2 + " spaces!";
    } else if (distance3 <= 10) {
      player.move(distance3);
      // return ":\nMove forward " + distance3 + " spaces!";
    } else {
      player.move(distance4);
      // return ":\nMove forward " + distance4 + " spaces!";
    }
    return "";

  }

  @Override
  public String getName() {
    return name;
  }
}
