package com.kalantos.spitball.engine;

import android.app.Activity;
import android.content.Context;
import android.widget.ImageView;


public class Tile extends Activity{
	private Ball ball;
	private ImageView imageView;
	private final int boundsX,boundsY;
	private boolean isPressed;

public Tile(Context context,int boundsY,int boundsX){
	ball= new Ball(0);
	imageView= new ImageView(context);
	this.boundsX=boundsX;
	this.boundsY=boundsY;
	this.isPressed=false;
	}

protected void setBall(int size, BallType tipo){
	switch(tipo){
	case BALLGREEN:
		this.ball=new BallGreen(size);
		break;
	case BALLPINK:
		this.ball=new BallPink(size);
		break;
		default: break;
	}
}
	protected int getBoundsX(){
		return	boundsX;
	}
	protected int getBoundsY(){
		return boundsY;
	}


protected void removeBall(){
	ball=new Ball(0);
}

protected Ball getBall(){
	return ball;
}
//BOUNCING
	protected boolean isPressed() {
		return isPressed;
	}
	protected void press(){
		isPressed=true;
	}
	protected void release(){
		isPressed=false;
	}

	protected ImageView getImageView(){
		return imageView;
	}

	//el metodo battle sirve para definir que bola quedara cuando se intersectan 2
	//y tambien redefine el tamaño
	protected void battle(Ball immigrantBall){

	if(immigrantBall.getSize()>= ball.getSize()){
						int newSize=ball.getSize()+immigrantBall.getSize();
						if(immigrantBall instanceof BallGreen){
							ball= new BallGreen(newSize);
								}
						if (immigrantBall instanceof BallPink){
									ball=new BallPink(newSize);
								}	
	}else{
		ball.setSize(ball.getSize()+immigrantBall.getSize());
			}


	}
}

