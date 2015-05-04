package edu.brown.cs.cmen.brownopoly.gamestate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;

import org.apache.commons.io.FileUtils;

import com.google.common.base.CharMatcher;

import edu.brown.cs.cmen.brownopoly.customboards.BoardTheme;
import edu.brown.cs.cmen.brownopoly.game.Game;

public class MemoryManager {

  private static final String DEFAULT_GAME_LOCATION = "savedGames/";
  private static final String DEFAULT_THEME_LOCATION = "savedThemes/";
  private final String gameLoc, themeLoc;
  private final String ext = ".ser";

  public MemoryManager() {
    gameLoc = DEFAULT_GAME_LOCATION;
    themeLoc = DEFAULT_THEME_LOCATION;
    new File(gameLoc).mkdir();
    new File(themeLoc).mkdir();
  }

  public MemoryManager(String gameLoc, String themeLoc) {
    this.gameLoc = gameLoc == null ? DEFAULT_GAME_LOCATION : gameLoc;
    this.themeLoc = themeLoc == null ? DEFAULT_THEME_LOCATION : themeLoc;
    new File(this.gameLoc).mkdir();
    new File(this.themeLoc).mkdir();
  }

  public String getGameLocation() {
    return gameLoc;
  }

  public String getThemeLocation() {
    return themeLoc;
  }

  public boolean isNameValid(String name) {
    // allowed chars: alphanumeric, spaces, -_
    if (name == null || name.equals("")) {
      return false;
    }
    CharMatcher matcher = CharMatcher.JAVA_LETTER_OR_DIGIT.or(CharMatcher
        .anyOf(" -_'"));
    return matcher.matchesAllOf(name);
  }

  public boolean doesGameFileExist(String name) {
    return doesFileExistHelper(name, true);
  }

  public boolean doesThemeFileExist(String name) {
    return doesFileExistHelper(name, false);
  }

  private boolean doesFileExistHelper(String name, boolean isGame) {
    name = convert(name);
    String folder = isGame ? gameLoc : themeLoc;
    String full = new StringBuilder(folder).append(name).append(ext).toString();
    return new File(full).isFile();
  }

  public void saveGame(Game game, String location) throws IOException {
    if (game == null) {
      throw new NullPointerException("Game cannot be null");
    }
    saveHelper(game, null, location);
  }

  public void saveTheme(BoardTheme theme, String location) throws IOException {
    if (theme == null) {
      throw new NullPointerException("Theme cannot be null");
    }
    saveHelper(null, theme, location);
  }

  private void saveHelper(Game game, BoardTheme theme, String location)
      throws IOException {
    if (!isNameValid(location)) {
      throw new IOException("Invalid file name");
    }
    location = convert(location);
    String folder = theme == null ? gameLoc : themeLoc;
    String full = new StringBuilder(folder).append(location).append(ext)
        .toString();
    // check location is valid
    FileOutputStream fileOut = new FileOutputStream(full);
    ObjectOutputStream out = new ObjectOutputStream(fileOut);
    if (theme == null) {
      out.writeObject(game);
    } else {
      out.writeObject(theme);
    }
    out.close();
    fileOut.close();
  }

  public Game loadGame(String location) throws IOException,
      ClassNotFoundException {
    if (!isNameValid(location)) {
      throw new IOException("Invalid file name");
    }
    location = convert(location);
    String full = new StringBuilder(gameLoc).append(location).append(ext)
        .toString();
    FileInputStream fileIn = new FileInputStream(full);
    ObjectInputStream in = new ObjectInputStream(fileIn);
    Game g = (Game) in.readObject();
    in.close();
    fileIn.close();
    return g;
  }

  public BoardTheme loadTheme(String location) throws IOException,
      ClassNotFoundException {
    if (!isNameValid(location)) {
      throw new IOException("Invalid file name");
    }
    location = convert(location);
    String full = new StringBuilder(themeLoc).append(location).append(ext)
        .toString();
    FileInputStream fileIn = new FileInputStream(full);
    ObjectInputStream in = new ObjectInputStream(fileIn);
    BoardTheme t = (BoardTheme) in.readObject();
    in.close();
    fileIn.close();
    return t;
  }

  public String[] getSavedGames() {
    return getSavedDataHelper(gameLoc);
  }

  public String[] getSavedThemes() {
    return getSavedDataHelper(themeLoc);
  }

  private String[] getSavedDataHelper(String folderName) {
    File folder = new File(folderName);
    if (!folder.exists()) {
      folder.mkdir();
    }
    File[] files = folder.listFiles();
    String[] names = new String[files.length];
    for (int i = 0; i < files.length; i++) {
      names[i] = parse(removeExt(files[i].getName()));
    }
    return names;
  }

  public void removeGame(String filename) throws IOException {
    filename = convert(filename);
    String full = new StringBuilder(gameLoc).append(filename).append(ext)
        .toString();
    Files.deleteIfExists(new File(full).toPath());
  }

  public void removeTheme(String filename) throws IOException {
    filename = convert(filename);
    String full = new StringBuilder(themeLoc).append(filename).append(ext)
        .toString();
    Files.deleteIfExists(new File(full).toPath());
  }

  public void removeSavedGames() throws IOException {
    removeAllHelper(gameLoc);
  }

  public void removeSavedThemes() throws IOException {
    removeAllHelper(themeLoc);
  }

  private void removeAllHelper(String folderName) throws IOException {
    File folder = new File(folderName);
    if (!folder.exists()) {
      folder.mkdir();
    }
    FileUtils.cleanDirectory(folder);
  }

  private String removeExt(String filename) {
    int ind = filename.indexOf('.');
    if (ind > -1) {
      assert ind < filename.length() - 1;
      assert filename.indexOf('.', ind + 1) == -1;
      filename = filename.substring(0, ind);
    }
    return filename;
  }

  private String convert(String filename) {
    // replace spaces with ---
    CharMatcher matcher = CharMatcher.is(' ');
    return matcher.replaceFrom(filename, "%20");
  }

  private String parse(String convertedName) {
    // replace each instance of '---' with a space
    return convertedName.replaceAll("%20", " ");
  }

}
