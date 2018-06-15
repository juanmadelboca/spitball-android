package com.kalantos.spitball.engine;

import android.app.Activity;
import android.util.Log;

/*
* Class used to make the game board, host the balls and manage battles.
* */
public class Tile extends Activity{

	private Ball ball;

	public Tile(){
		ball = new Ball(0);
	}

	protected void setBall(int size, BallType tipo){
		switch(tipo){
		case BALLGREEN:
			this.ball = new BallGreen(size);
			break;
		case BALLPINK:
			this.ball = new BallPink(size);
			break;
			default: break;
		}
	}

	protected void removeBall(){
		ball = new Ball(0);
	}

	public Ball getBall(){
		return ball;
	}

	protected boolean battle(Ball immigrantBall, boolean limitedMove) {
	/*
	* Define which ball will survive when 2 balls enter in one Tile, also redefine the winner
	* ball size. Returns true if movement can be done.
	* */
		if ((immigrantBall instanceof BallGreen && ball instanceof BallGreen && limitedMove)
				|| (immigrantBall instanceof BallPink && ball instanceof BallPink && limitedMove)) {
			Log.i("GAME", "You have too little balls to perform that move.");
			return false;
		} else {
			if (immigrantBall.getSize() >= ball.getSize()) {
				int newSize = ball.getSize() + immigrantBall.getSize();
				if (immigrantBall instanceof BallGreen) {
					ball = new BallGreen(newSize);
				}
				if (immigrantBall instanceof BallPink) {
					ball = new BallPink(newSize);
				}
			} else {
				ball.setSize(ball.getSize() + immigrantBall.getSize());
			}
			return true;
		}
	}
}

