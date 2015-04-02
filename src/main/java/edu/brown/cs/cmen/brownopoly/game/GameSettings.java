package edu.brown.cs.cmen.brownopoly.game;

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

  private BoardTheme theme;

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
