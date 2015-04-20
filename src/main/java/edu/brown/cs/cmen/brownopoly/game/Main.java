package edu.brown.cs.cmen.brownopoly.game;

public class Main {

  /**
   * Runs the mainline.
   *
   * @param args
   *          - args from the mainline
   */
  public static void main(String[] args) {
    new Main(args).run();
  }

  private Main(String[] args) {
  }

  private void run() {
    new GUIRunner();
  }
}
