package edu.brown.cs.cmen.brownopoly.game;

import edu.brown.cs.cmen.brownopoly.player.Human;
import edu.brown.cs.cmen.brownopoly.player.Player;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * JUnit Tests for MonetaryCard.java
 */
public class DeckTest {

    @BeforeClass
    public static void setUpClass() throws Exception {
        // (Optional) Code to run before any tests begin goes here.
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        // (Optional) Code to run after all tests finish goes here.
    }

    @Before
    public void setUp() {
        // (Optional) Code to run before each test case goes here.
    }

    @After
    public void tearDown() {
        // (Optional) Code to run after each test case goes here.
    }

    @Test
    public void fillsDeckUponCreation() {
        Deck deck = new Deck(true);
        assert(!deck.isEmpty());
    }

}