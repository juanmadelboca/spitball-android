package com.kalantos.spitball.engine;
import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class TileTest {

    @Test
    public void battleLocalTest() throws Exception {

        Tile tile = new Tile();
        tile.setBall(20,BallType.BALLGREEN);
        BallPink inmigrantBall = new BallPink(10);
        tile.battle(inmigrantBall);
        assertEquals(30,tile.getBall().getSize());
        assertTrue(tile.getBall() instanceof BallGreen);
    }
    @Test
    public void battleInmigrantTest() throws Exception {

        Tile tile = new Tile();
        tile.setBall(10,BallType.BALLPINK);
        BallPink inmigrantBall = new BallPink(20);
        tile.battle(inmigrantBall);
        assertEquals(30,tile.getBall().getSize());
        assertTrue(tile.getBall() instanceof BallPink);
    }

}
