package com.kalantos.spitball.GameManager;

import com.kalantos.spitball.engine.Ball;
import com.kalantos.spitball.engine.GameManager;
import com.kalantos.spitball.engine.Tile;

import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class swipeHandler {

    @Test
    public void cancelMove() throws Exception {

        GameManager game = new GameManager(0, 1, 0, false);
        Tile[][] tiles= game.getTiles();
        Method method = GameManager.class.getDeclaredMethod("swipeHandler", int.class, int.class);
        method.setAccessible(true);
        method.invoke(game, 1,3);
        method.invoke(game, 1,3);
        assertEquals(tiles,game.getTiles());
        assertEquals(20,game.getTiles()[1][3].getBall().getSize());

    }

    @Test
    public void outBoundsTest() throws Exception {

        GameManager game = new GameManager(0, 1, 0, false);
        Tile[][] tiles= game.getTiles();
        Method method = GameManager.class.getDeclaredMethod("swipeHandler", int.class, int.class);
        method.setAccessible(true);
        method.invoke(game, 1,3);
        method.invoke(game, 1,6);
        assertEquals(tiles,game.getTiles());
        assertEquals(20,game.getTiles()[1][3].getBall().getSize());

    }

    @Test
    public void moveRightTest() throws Exception {

        GameManager game = new GameManager(0, 1, 0, false);
        assertEquals(game.getTiles()[1][3].getBall().getSize(),20);
        Method method = GameManager.class.getDeclaredMethod("swipeHandler", int.class, int.class);
        method.setAccessible(true);
        method.invoke(game, 1,3);
        method.invoke(game, 1,4);
        assertEquals(0,game.getTiles()[1][3].getBall().getSize());
        assertEquals(20,game.getTiles()[1][4].getBall().getSize());
    }
    @Test
    public void moveUpRightTest() throws Exception {

        GameManager game = new GameManager(0, 1, 0, false);
        assertEquals(game.getTiles()[1][3].getBall().getSize(),20);
        Method method = GameManager.class.getDeclaredMethod("swipeHandler", int.class, int.class);
        method.setAccessible(true);
        method.invoke(game, 1,3);
        method.invoke(game, 0,4);
        assertEquals(0,game.getTiles()[1][3].getBall().getSize());
        assertEquals(20,game.getTiles()[0][4].getBall().getSize());
    }

    @Test
    public void moveUpTest() throws Exception {

        GameManager game = new GameManager(0, 1, 0, false);
        assertEquals(game.getTiles()[1][3].getBall().getSize(),20);
        Method method = GameManager.class.getDeclaredMethod("swipeHandler", int.class, int.class);
        method.setAccessible(true);
        method.invoke(game, 1,3);
        method.invoke(game, 0,3);
        assertEquals(0,game.getTiles()[1][3].getBall().getSize());
        assertEquals(20,game.getTiles()[0][3].getBall().getSize());
    }

    @Test
    public void moveUpLeftTest() throws Exception {

        GameManager game = new GameManager(0, 1, 0, false);
        assertEquals(game.getTiles()[1][3].getBall().getSize(),20);
        Method method = GameManager.class.getDeclaredMethod("swipeHandler", int.class, int.class);
        method.setAccessible(true);
        method.invoke(game, 1,3);
        method.invoke(game, 0,2);
        assertEquals(0,game.getTiles()[1][3].getBall().getSize());
        assertEquals(20,game.getTiles()[0][2].getBall().getSize());
    }

    @Test
    public void moveLeftTest() throws Exception {

        GameManager game = new GameManager(0, 1, 0, false);
        assertEquals(game.getTiles()[1][3].getBall().getSize(),20);
        Method method = GameManager.class.getDeclaredMethod("swipeHandler", int.class, int.class);
        method.setAccessible(true);
        method.invoke(game, 1,3);
        method.invoke(game, 1,2);
        assertEquals(0,game.getTiles()[1][3].getBall().getSize());
        assertEquals(20,game.getTiles()[1][2].getBall().getSize());
    }

    @Test
    public void moveDownLeftTest() throws Exception {

        GameManager game = new GameManager(0, 1, 0, false);
        assertEquals(game.getTiles()[1][3].getBall().getSize(),20);
        Method method = GameManager.class.getDeclaredMethod("swipeHandler", int.class, int.class);
        method.setAccessible(true);
        method.invoke(game, 1,3);
        method.invoke(game, 2,2);
        assertEquals(0,game.getTiles()[1][3].getBall().getSize());
        assertEquals(40,game.getTiles()[2][2].getBall().getSize());
    }

    @Test
    public void moveDownTest() throws Exception {

        GameManager game = new GameManager(0, 1, 0, false);
        assertEquals(game.getTiles()[1][3].getBall().getSize(),20);
        Method method = GameManager.class.getDeclaredMethod("swipeHandler", int.class, int.class);
        method.setAccessible(true);
        method.invoke(game, 1,3);
        method.invoke(game, 2,3);
        assertEquals(0,game.getTiles()[1][3].getBall().getSize());
        assertEquals(20,game.getTiles()[2][3].getBall().getSize());
    }

    @Test
    public void moveDownRightTest() throws Exception {

        GameManager game = new GameManager(0, 1, 0, false);
        assertEquals(game.getTiles()[1][3].getBall().getSize(),20);
        Method method = GameManager.class.getDeclaredMethod("swipeHandler", int.class, int.class);
        method.setAccessible(true);
        method.invoke(game, 1,3);
        method.invoke(game, 2,4);
        assertEquals(0,game.getTiles()[1][3].getBall().getSize());
        assertEquals(20,game.getTiles()[2][4].getBall().getSize());
    }

    @Test
    public void splitRightTest() throws Exception {

        GameManager game = new GameManager(0, 1, 0, false);
        Tile[][] tiles= game.getTiles();
        Method method = GameManager.class.getDeclaredMethod("swipeHandler", int.class, int.class);
        method.setAccessible(true);
        int  ballSize= game.getTiles()[1][3].getBall().getSize();
        method.invoke(game, 1,3);
        method.invoke(game, 1,5);
        assertEquals(tiles,game.getTiles());
        assertEquals((ballSize-(ballSize/3)),game.getTiles()[1][3].getBall().getSize());
        assertEquals((int)((int)(ballSize/3 )*1.2)+20,game.getTiles()[1][5].getBall().getSize());

    }

    @Test
    public void splitUpTest() throws Exception {

        GameManager game = new GameManager(0, 1, 0, false);
        Tile[][] tiles= game.getTiles();
        Method method = GameManager.class.getDeclaredMethod("swipeHandler", int.class, int.class);
        method.setAccessible(true);
        int  ballSize= game.getTiles()[1][3].getBall().getSize();
        method.invoke(game, 2,2);
        method.invoke(game, 0,2);
        assertEquals(tiles,game.getTiles());
        assertEquals((ballSize-(ballSize/3)),game.getTiles()[2][2].getBall().getSize());
        assertEquals((int)((int)(ballSize/3 )*1.2),game.getTiles()[0][2].getBall().getSize());

    }

    @Test
    public void splitLeftTest() throws Exception {

        GameManager game = new GameManager(0, 1, 0, false);
        Tile[][] tiles= game.getTiles();
        Method method = GameManager.class.getDeclaredMethod("swipeHandler", int.class, int.class);
        method.setAccessible(true);
        int  ballSize= game.getTiles()[1][3].getBall().getSize();
        method.invoke(game, 1,3);
        method.invoke(game, 1,1);
        assertEquals(tiles,game.getTiles());
        assertEquals((ballSize-(ballSize/3)),game.getTiles()[1][3].getBall().getSize());
        assertEquals((int)((int)(ballSize/3 )*1.2),game.getTiles()[1][1].getBall().getSize());

    }

    @Test
    public void splitDownTest() throws Exception {

        GameManager game = new GameManager(0, 1, 0, false);
        Tile[][] tiles= game.getTiles();
        Method method = GameManager.class.getDeclaredMethod("swipeHandler", int.class, int.class);
        method.setAccessible(true);
        int  ballSize= game.getTiles()[1][3].getBall().getSize();
        method.invoke(game, 1,3);
        method.invoke(game, 3,3);
        assertEquals(tiles,game.getTiles());
        assertEquals((ballSize-(ballSize/3)),game.getTiles()[1][3].getBall().getSize());
        assertEquals((int)((int)(ballSize/3 )*1.2)+20,game.getTiles()[3][3].getBall().getSize());

    }
}