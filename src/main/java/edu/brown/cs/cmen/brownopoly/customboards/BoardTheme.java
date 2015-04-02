package edu.brown.cs.cmen.brownopoly.customboards;

import java.util.Arrays;

/**
 * 
 * @author npucel
 *
 */
public class BoardTheme {
    private String[] names;
    public BoardTheme(String[] names) {
        this.names = names;
    }
    public String[] getNames() {
        return Arrays.copyOf(names, names.length);
    }
}
