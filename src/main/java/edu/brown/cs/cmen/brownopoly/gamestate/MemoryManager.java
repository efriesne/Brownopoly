package edu.brown.cs.cmen.brownopoly.gamestate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.commons.io.FileUtils;

import com.google.common.base.CharMatcher;

import edu.brown.cs.cmen.brownopoly.game.Game;

public class MemoryManager {

  private static final String DEFAULT_LOCATION = "saved/";
  private final String myLocation;
  private final String ext = ".ser";

  public MemoryManager() {
    myLocation = DEFAULT_LOCATION;
    new File(myLocation).mkdir();
  }

  public MemoryManager(String location) {
    myLocation = location == null ? DEFAULT_LOCATION : location;
    new File(myLocation).mkdir();
  }

  public String getLocation() {
    return myLocation;
  }

  public boolean isNameValid(String name) {
    // allowed chars: alphanumeric, spaces, -_
    if (name == null || name.equals("")) {
      return false;
    }
    CharMatcher matcher = CharMatcher.JAVA_LETTER_OR_DIGIT.or(CharMatcher
        .anyOf(" -_"));
    return matcher.matchesAllOf(name);
  }

  public boolean doesFileExist(String name) {
    name = convert(name);
    String full = new StringBuilder(myLocation).append(name).append(ext)
        .toString();
    return new File(full).isFile();
  }

  public void save(Game game, String location) throws IOException {
    if (!isNameValid(location)) {
      throw new IOException("Invalid file name");
    }
    location = convert(location);
    String full = new StringBuilder(myLocation).append(location).append(ext)
        .toString();
    // check location is valid
    FileOutputStream fileOut = new FileOutputStream(full);
    ObjectOutputStream out = new ObjectOutputStream(fileOut);
    out.writeObject(game);
    out.close();
    fileOut.close();
  }

  public Game load(String location) throws IOException, ClassNotFoundException {
    if (!isNameValid(location)) {
      throw new IOException("Invalid file name");
    }
    location = convert(location);
    String full = new StringBuilder(myLocation).append(location).append(ext)
        .toString();
    FileInputStream fileIn = new FileInputStream(full);
    ObjectInputStream in = new ObjectInputStream(fileIn);
    Game g = (Game) in.readObject();
    in.close();
    fileIn.close();
    return g;
  }

  public String[] getSavedGames() {
    File folder = new File(myLocation);
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

  public void removeSavedGames() throws IOException {
    File folder = new File(myLocation);
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
    return matcher.replaceFrom(filename, "---");
  }

  private String parse(String convertedName) {
    // replace each instance of '---' with a space
    return convertedName.replaceAll("---", " ");
  }

}
