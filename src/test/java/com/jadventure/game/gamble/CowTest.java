package com.jadventure.game.gamble;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class CowTest {
    @Test
    public void oneCowTest() {
        Gambling gamble = new Gambling(null);
        assertEquals(1, gamble.getCow("9816", "1234"));
    }

    @Test
    public void twoCowsTest() {
        Gambling gamble = new Gambling(null);
        assertEquals(2, gamble.getCow("8521", "1234"));
    }

    @Test
    public void threeCowsTest() {
        Gambling gamble = new Gambling(null);
        assertEquals(3, gamble.getCow("3127", "1234"));
    }

    @Test
    public void fourCowsTest() {
        Gambling gamble = new Gambling(null);
        assertEquals(4, gamble.getCow("3142", "1234"));
    }
}
