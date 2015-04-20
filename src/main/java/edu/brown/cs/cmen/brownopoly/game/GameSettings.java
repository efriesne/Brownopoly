package edu.brown.cs.cmen.brownopoly.game;

import java.util.ArrayList;
import java.util.List;

import edu.brown.cs.cmen.brownopoly.customboards.BoardTheme;

/**
 * 
 * @author npucel
 *
 */
public class GameSettings {

  private int numHumans, numAI, startCash, startProperties, numHousesforHotel;
  // freeParkingCash, landOnGoCash, passGoCash,
  private boolean fastPlay;
  private final List<String> humanNames = new ArrayList<>();
  private final List<String> aiNames = new ArrayList<>();
  private BoardTheme theme;

  public GameSettings(BoardTheme theme) {
    if(theme == null) {
      this.theme = new BoardTheme(MonopolyConstants.DEFAULT_BOARD_NAMES, MonopolyConstants.DEFAULT_BOARD_COLORS);
    } else {
      this.theme = theme;
    }
  }

  /**
   * @return the numHumans
   */
  public int getNumHumans() {
    return numHumans;
  }

  /**
   * @param numHumans
   *          the numHumans to set
   */
  public void setNumHumans(int numHumans) {
    this.numHumans = numHumans;
  }

  /**
   * @return the numAI
   */
  public int getNumAI() {
    return numAI;
  }

  /**
   * @param numAI
   *          the numAI to set
   */
  public void setNumAI(int numAI) {
    this.numAI = numAI;
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
   * @return the startCash
   */
  public int getStartCash() {
    return startCash;
  }

  /**
   * @param startCash
   *          the startCash to set
   */
  public void setStartCash(int startCash) {
    this.startCash = startCash;
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
  public void setStartProperties(int startOwnables) {
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
  public void setTheme(BoardTheme theme) {
    this.theme = theme;
  }

  /**
   * @param numHousesforHotel
   *          the numHousesforHotel to set
   */
  public void setNumHousesforHotel(int numHousesforHotel) {
    this.numHousesforHotel = numHousesforHotel;
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
  public void setFastPlay(boolean fastPlay) {
    this.fastPlay = fastPlay;
  }

}
