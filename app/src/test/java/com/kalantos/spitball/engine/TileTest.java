package com.kalantos.spitball.engine;
import android.util.Log;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

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
        tile.battle(inmigrantBall,false);
        assertEquals(30,tile.getBall().getSize());
        assertTrue(tile.getBall() instanceof BallGreen);
    }

    @Test
    public void battleInmigrantTest() throws Exception {

        Tile tile = new Tile();
        tile.setBall(10,BallType.BALLPINK);
        BallPink inmigrantBall = new BallPink(20);
        tile.battle(inmigrantBall,false);
        assertEquals(30,tile.getBall().getSize());
        assertTrue(tile.getBall() instanceof BallPink);
    }

    @Test
    public void battleInmigrantLimitedTest() throws Exception {

        Tile tile = new Tile();
        tile.setBall(10,BallType.BALLPINK);
        BallPink inmigrantBall = new BallPink(20);
        assertFalse(tile.battle(inmigrantBall,true));
        assertTrue(tile.getBall() instanceof BallPink);
        assertEquals(10,tile.getBall().getSize());
    }

    @Test
    public void battleLocalLimitedTest() throws Exception {

        Tile tile = new Tile();
        tile.setBall(20,BallType.BALLGREEN);
        BallGreen inmigrantBall = new BallGreen(10);
        assertFalse(tile.battle(inmigrantBall,true));
        assertEquals(20,tile.getBall().getSize());
        assertTrue(tile.getBall() instanceof BallGreen);
    }

}
