package edu.brown.cs.cmen.brownopoly.gamestate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.google.common.base.CharMatcher;

import edu.brown.cs.cmen.brownopoly.game.Game;

public class MemoryManager {

  private final String defLocation = "saved/";
  private final String ext = ".ser";

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
    String full = new StringBuilder(defLocation).append(name).append(ext)
        .toString();
    return new File(full).isFile();
  }

  public void save(Game game, String location) throws IOException {
    if (!isNameValid(location)) {
      throw new IOException("Invalid file name");
    }
    location = convert(location);
    String full = new StringBuilder(defLocation).append(location).append(ext)
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
    String full = new StringBuilder(defLocation).append(location).append(ext)
        .toString();
    FileInputStream fileIn = new FileInputStream(full);
    ObjectInputStream in = new ObjectInputStream(fileIn);
    Game g = (Game) in.readObject();
    in.close();
    fileIn.close();
    return g;
  }

  public String[] getSavedGames() throws FileNotFoundException {
    File folder = new File(defLocation);
    if (!folder.exists()) {
      throw new FileNotFoundException();
    }
    File[] files = folder.listFiles();
    String[] names = new String[files.length];
    for (int i = 0; i < files.length; i++) {
      names[i] = parse(removeExt(files[i].getName()));
    }
    return names;
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
