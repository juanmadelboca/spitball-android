package com.kalantos.spitballv001;

import android.app.Activity;
import android.content.Context;
import android.media.Image;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;


public class Tile extends Activity{
private Ball ball;
public ImageView imageView;

public Tile(Context context){
	ball= new Ball(0);
	imageView= new ImageView(context);
	}

protected void setBall(int tamaño, BallType tipo){
	switch(tipo){
	case BALLGREEN:	
		this.ball=new BallGreen(tamaño);
		break;
	case BALLPINK:
		this.ball=new BallPink(tamaño);
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

public void batalla(Ball ballentrante){
	while(true){
if((ballentrante instanceof BallGreen	&&	ball instanceof BallGreen)){
		int nuevoTamaño=ballentrante.getSize()+ball.getSize();
		ball.setSize(nuevoTamaño);
		break;
	//	System.out.println("arreglar aca1");
		}
if((ballentrante instanceof BallPink	&&	ball instanceof BallPink)){
		int nuevoTamaño=ballentrante.getSize()+ball.getSize();
		ball.setSize(nuevoTamaño);
		break;
		//System.out.println("arreglar aca2");
			}
if(ballentrante.getSize()>= ball.getSize()){
						int cte=ball.getSize()+ballentrante.getSize();
						if(ballentrante instanceof BallGreen){
							ball= new BallGreen(cte);
							break;
								}
						if (ballentrante instanceof BallPink){
									ball=new BallPink(cte);
									break;
								}	
}else{
	//System.out.println("arreglar aca");
	ball.setSize(ball.getSize()+ballentrante.getSize());
	break;
		}
	}

}
}
