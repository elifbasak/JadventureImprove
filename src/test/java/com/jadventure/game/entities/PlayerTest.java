package com.jadventure.game.entities;

import com.jadventure.game.entities.Player;
import com.jadventure.game.entities.PlayerUtil;
import org.junit.Test;
import org.junit.Before;

import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class PlayerTest {

    @Test
    public void newRecruit() {
        Player player = Player.getInstance("recruit");
        double expected = 1.0;
        double actual = player.getArmour();
        assertEquals("Failure - new recruit not properly created", expected, actual, 0.01);
    }

    @Test
    public void newSewerRat() {
        Player player = Player.getInstance("sewerrat");
        double expected = 0;
        double actual = player.getArmour();
        assertEquals("Failure - new sewer rat not properly created", expected, actual, 0.01);
    }

    @Test
    public void oldPlayer() {
        Player player = PlayerUtil.load("test");
        String expected = "test";
        String actual = player.getName();
        assertEquals("Failure - old player not properly loaded", expected, actual);
    }
}
