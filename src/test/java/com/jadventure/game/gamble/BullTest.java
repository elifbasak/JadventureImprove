package com.jadventure.game.gamble;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class BullTest {
    @Test
    public void oneBullTest() {
        Gambling gamble = new Gambling(null);
        assertEquals(1, gamble.getBull("1326", "1234"));
    }

    @Test
    public void twoBullsTest() {
        Gambling gamble = new Gambling(null);
        assertEquals(2, gamble.getBull("1564", "1234"));
    }

    @Test
    public void threeBullsTest() {
        Gambling gamble = new Gambling(null);
        assertEquals(3, gamble.getBull("1534", "1234"));
    }

    @Test
    public void fourBullsTest() {
        Gambling gamble = new Gambling(null);
        assertEquals(4, gamble.getBull("1234", "1234"));
    }
}
