package edu.brown.cs.cmen.brownopoly.game;

/**
 * 
 * @author npucel
 *
 */
public class GameSettings {

  private int numHumans, numAI, startCash, startOwnables, numHousesforHotel;
  // freeParkingCash, landOnGoCash, passGoCash,
  private boolean fastPlay;

  // private BoardTheme theme

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
  public int getStartOwnables() {
    return startOwnables;
  }

  /**
   * @param startOwnables
   *          the startOwnables to set
   */
  public void setStartOwnables(int startOwnables) {
    this.startOwnables = startOwnables;
  }

  /**
   * @return the numHousesforHotel
   */
  public int getNumHousesforHotel() {
    return numHousesforHotel;
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