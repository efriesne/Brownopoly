package edu.brown.cs.cmen.brownopoly.game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import edu.brown.cs.cmen.brownopoly.customboards.BoardTheme;

/**
 * 
 * @author npucel
 *
 */
public class GameSettings implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = -6783923829172530490L;
  private static final int NUM_HOUSES_FASTPLAY = 3;
  private static final int NUM_HOUSES_NO_FASTPLAY = 4;

  private int startProperties, numHousesforHotel;
  // freeParkingCash, landOnGoCash, passGoCash,
  private boolean fastPlay;
  private final List<String> humanNames = new ArrayList<>();
  private final List<String> aiNames = new ArrayList<>();
  private BoardTheme theme;

  public GameSettings(BoardTheme theme, boolean fastPlay) {
    this.theme = theme;
    setFastPlay(fastPlay);
  }

  /**
   * @return the numHumans
   */
  public int getNumHumans() {
    return humanNames.size();
  }

  /**
   * @return the numAI
   */
  public int getNumAI() {
    return aiNames.size();
  }

  public void addHumanName(String name) {
    humanNames.add(name);
  }

  public String getHumanName(int i) {
    assert i < humanNames.size() && i >= 0;
    return humanNames.get(i);
  }

  public void addAIName(String name) {
    aiNames.add(name);
  }

  public String getAIName(int i) {
    assert i < aiNames.size() && i >= 0;
    return aiNames.get(i);
  }

  /**
   * @return the startOwnables
   */
  public int getStartProperties() {
    return startProperties;
  }

  /**
   * @param startOwnables
   *          the startOwnables to set
   */
  private void setStartProperties(int startOwnables) {
    this.startProperties = startOwnables;
  }

  /**
   * @return the numHousesforHotel
   */
  public int getNumHousesforHotel() {
    return numHousesforHotel;
  }

  /**
   * @return the board theme
   */
  public BoardTheme getTheme() {
    return theme;
  }

  /**
   * @param theme
   *          the board theme
   */
  private void setTheme(BoardTheme theme) {
    this.theme = theme;
  }

  /**
   * @return the fastPlay
   */
  public boolean isFastPlay() {
    return fastPlay;
  }

  /**
   * @param fastPlay
   *          the fastPlay to set
   */
  private void setFastPlay(boolean fastPlay) {
    this.fastPlay = fastPlay;
    if (fastPlay) {
      numHousesforHotel = NUM_HOUSES_FASTPLAY;
      startProperties = 3;
    } else {
      numHousesforHotel = NUM_HOUSES_NO_FASTPLAY;
      startProperties = 0;
    }
  }
}
