package com.kalantos.spitball.GameManager;

import com.kalantos.spitball.engine.GameManager;

import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class swipeHandler {
    GameManager game;
    public swipeHandler(){
        game = new GameManager(0, 1, 0, true);
    }
    @Test
    public void cancelMove() throws Exception {
        assertFalse(game.detectMoves());
        Method method = GameManager.class.getDeclaredMethod("swipeHandler", int.class, int.class);
        method.setAccessible(true);
        method.invoke(game, 1,3);
        method.invoke(game, 1,3);
        assertFalse(game.detectMoves());
    }
}