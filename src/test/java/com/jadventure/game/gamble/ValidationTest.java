package com.jadventure.game.gamble;

import org.junit.Test;

public class ValidationTest {
    @Test(expected = IllegalArgumentException.class)
    public void nullInputTest() {
        Gambling gamble = new Gambling(null);
        gamble.validateInput(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void differentLengthTest() {
        Gambling gamble = new Gambling(null);
        gamble.validateInput("123");
    }

    @Test(expected = IllegalArgumentException.class)
    public void notANumberTest() {
        Gambling gamble = new Gambling(null);
        gamble.validateInput("abcd");
    }

    @Test(expected = IllegalArgumentException.class)
    public void beginWithZeroTest() {
        Gambling gamble = new Gambling(null);
        gamble.validateInput("0123");
    }

    @Test(expected = IllegalArgumentException.class)
    public void duplicateTest() {
        Gambling gamble = new Gambling(null);
        gamble.validateInput("1231");
    }

    @Test
    public void validInputTest() {
        Gambling gamble = new Gambling(null);
        gamble.validateInput("1234");
    }
}
