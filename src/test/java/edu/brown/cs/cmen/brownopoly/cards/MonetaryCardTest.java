package edu.brown.cs.cmen.brownopoly.cards;

import edu.brown.cs.cmen.brownopoly.player.Human;
import edu.brown.cs.cmen.brownopoly.player.Player;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Assert.assertTrue;

/**
 * JUnit Tests for MonetaryCard.java
 */
public class MonetaryCardTest {

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
    public void basicStructure() {
        MonetaryCard card = new MonetaryCard("Test", 50);
        assertTrue(card.getAmount() == 50);
        assertTrue(card.getName().equals("Test"));
    }
    @Test
    public void playCardTest() {
        MonetaryCard card = new MonetaryCard("Test", 50);
        Player player = new Human("Person", null);
        int old = player.getBalance();
        card.play(player);
        assertTrue(old == (player.getBalance() - 50));
    }
}