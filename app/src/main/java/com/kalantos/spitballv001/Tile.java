package com.kalantos.spitballv001;

import android.app.Activity;
import android.content.Context;
import android.widget.ImageView;


public class Tile extends Activity{
private Ball ball;
public ImageView imageView;

public Tile(Context context){
	ball= new Ball(0);
	imageView= new ImageView(context);
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


protected void removeBall(){
	ball=new Ball(0);
}

protected Ball getBall(){
	return ball;
}


	protected ImageView getImageView(){
		return imageView;
	}

	protected void batalla(Ball immigrantBall){

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
