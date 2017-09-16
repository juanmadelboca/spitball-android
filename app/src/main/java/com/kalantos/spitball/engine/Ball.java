package com.kalantos.spitball.engine;

/*
* Parent class for player1 and player2/IA balls.
* */
public class Ball {
	private int size;

	public Ball(int size){
		this.size=size;
	}

	public void setSize(int size){
		this.size=size;
	}

	public int getSize(){
		return size;
	}

}
