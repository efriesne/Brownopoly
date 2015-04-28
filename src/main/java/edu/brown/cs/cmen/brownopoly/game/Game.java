package edu.brown.cs.cmen.brownopoly.game;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Collection;

import edu.brown.cs.cmen.brownopoly.ownable.Ownable;
import edu.brown.cs.cmen.brownopoly.ownable.OwnableManager;

/**
 * 
 * @author npucel
 *
 */
public class Game implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = -762425012327340606L;
  private static GameSettings settings;
  private Referee ref;
  private Collection<Ownable> ownables;

  public Game(Referee ref, GameSettings settings, Collection<Ownable> ownables) {
    Game.settings = settings;
    this.ref = ref;
    this.ownables = ownables;
  }

  public static final int numHousesForHotel() {
    return settings.getNumHousesforHotel();
  }

  public Referee getReferee() {
    return ref;
  }

  private void readObject(ObjectInputStream in) throws IOException,
      ClassNotFoundException {
    in.defaultReadObject();
    OwnableManager.populate(ownables);
  }
}
