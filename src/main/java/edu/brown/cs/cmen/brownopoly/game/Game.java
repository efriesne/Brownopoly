package edu.brown.cs.cmen.brownopoly.game;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Collection;

import edu.brown.cs.cmen.brownopoly.customboards.BoardTheme;
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
  private GameSettings settings;
  private static int numHousesForHotel;
  private Referee ref;
  private Collection<Ownable> ownables;
  // if a previous version of this game was saved, the fileName will be stored
  // here
  private String filename;

  public Game(Referee ref, GameSettings settings, Collection<Ownable> ownables) {
    this.settings = settings;
    this.ref = ref;
    this.ownables = ownables;
    numHousesForHotel = settings.getNumHousesforHotel();
  }

  public static final int numHousesForHotel() {
    return numHousesForHotel;
  }

  public Referee getReferee() {
    return ref;
  }

  private void readObject(ObjectInputStream in) throws IOException,
      ClassNotFoundException {
    in.defaultReadObject();
    OwnableManager.populate(ownables);
    numHousesForHotel = settings.getNumHousesforHotel();
  }

  public BoardTheme getTheme() {
    return settings.getTheme();
  }

  public void setSavedFilename(String name) {
    filename = name;
  }

  public String getSavedFilename() {
    return filename;
  }
}
