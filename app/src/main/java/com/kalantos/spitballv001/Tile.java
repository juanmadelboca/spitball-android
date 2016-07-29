package com.kalantos.spitballv001;

import android.app.Activity;
import android.content.Context;
import android.widget.Button;
import android.widget.ImageButton;


public class Tile extends Activity{
private Ball ball;
public ImageButton b;

public Tile(Context context){
	ball= new Ball(0);
	b= new ImageButton(context);
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

public void batalla(Ball ballentrante){
	while(true){
if((ballentrante instanceof BallGreen	&&	ball instanceof BallGreen)){
		int nuevoTamaño=ballentrante.getTamaño()+ball.getTamaño();
		ball.setTamaño(nuevoTamaño);
		break;
	//	System.out.println("arreglar aca1");
		}
if((ballentrante instanceof BallPink	&&	ball instanceof BallPink)){
		int nuevoTamaño=ballentrante.getTamaño()+ball.getTamaño();
		ball.setTamaño(nuevoTamaño);
		break;
		//System.out.println("arreglar aca2");
			}
if(ballentrante.getTamaño()>= ball.getTamaño()){
						int cte=ball.getTamaño()+ballentrante.getTamaño();
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
	ball.setTamaño(ball.getTamaño()+ballentrante.getTamaño());
	break;
		}
	}

}
}
