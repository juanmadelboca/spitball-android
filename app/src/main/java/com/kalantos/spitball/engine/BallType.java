package com.kalantos.spitball.engine;
public enum BallType {
BALLGREEN,BALLPINK;
	public String toString(){
		switch (this){
		case BALLGREEN: return "BallGreen";
		case BALLPINK:	return "BallPink";
		default: return null;
		}
	}
}
