package com.kalantos.spitball.engine;

import java.util.ArrayList;
import java.util.Random;

/*
* This class manages all AI movements, use static methods to provide movements when needed.
* TODO: LEGIBLE VARIABLES.
* */
public class ArtificialInteligenceAlgorithm {

    public static final int HEIGHT = 6;
    public static final int WIDTH = 10;

    public static int[] RandomMove(Tile[][] tiles) {
    /*
    * Make a random move, use a vector with all balls in the board, and then choose
    * a direction and make a totally random move.
    * */
        int[] coordinates;
        int [] AIvector= ArtificialInteligenceAlgorithm.getBall(tiles);
        Random random = new Random(System.currentTimeMillis());
        int seed = random.nextInt(2);
        int x, y;
        if (seed == 1) {
            x = -1;
        } else {
            x = 1;
        }

        seed = random.nextInt(2);
        if (seed == 1) {
            y = -1;
        } else {
            y = 1;
        }
        int newY = AIvector[0] + y;

        int newX = AIvector[1] + x;
        if ((newY < HEIGHT && newY >= 0) && (newX < WIDTH && newX >= 0)) {
            coordinates = new int[]{AIvector[0], AIvector[1], newY, newX,0};
        } else {
            coordinates = ArtificialInteligenceAlgorithm.RandomMove(tiles);
        }
        return coordinates;

    }

    public static int[] easyMove(Tile[][] tiles) {
    /*
    * Start a battle against any ball from oposite team, even if the enemy ball has a bigger
    * size.
    * If no enemy ball is in range make an automove.
    * */
        ArrayList<int[]> posibleVectors = ArtificialInteligenceAlgorithm.getAIBalls(tiles);
        int coordinates[] = {-1, -1, -1, -1};
        for (int k = 0; k < posibleVectors.size(); k++) {
            int[] temp = posibleVectors.get(k);
            int y = temp[0];
            int x = temp[1];
            for (int i = -1; i < 2; i++) {
                for (int j = -1; j < 2; j++) {
                    if ((y + i < HEIGHT) && (y + i >= 0) && (x + j < WIDTH) && (x + j >= 0)) {
                        if (tiles[y + i][x + j].getBall() instanceof BallGreen && tiles[y + i][x + j].getBall().getSize() <= tiles[y][x].getBall().getSize()) {
                            coordinates = new int[]{y, x, y + i, x + j,0};
                        }
                    }
                }
            }
        }
        if (coordinates[3] == -1) {
            System.out.println("randomMove");
            return ArtificialInteligenceAlgorithm.RandomMove(tiles);
        } else {
            System.out.println("easyMove");
            return coordinates;
        }
    }

    public static int[] getBall(Tile[][] tiles){
    /*
    * Get one random ball from all AI balls in board.
    * */
        ArrayList<int[]> posibleVectors = ArtificialInteligenceAlgorithm.getAIBalls(tiles);
        Random random = new Random(System.currentTimeMillis());
        int index = random.nextInt(posibleVectors.size());
        return posibleVectors.get(index);
    }

    public static int[] getBiggestBall(Tile[][] tiles){
    /*
    * Get the biggest ball from AI balls in board.
    * */
        ArrayList<int[]> posibleVectors = ArtificialInteligenceAlgorithm.getAIBalls(tiles);
        int[]biggestBall= posibleVectors.get(0);
        for(int i=0;i<posibleVectors.size()-1;i++) {
            if (tiles[posibleVectors.get(i)[0]][posibleVectors.get(i)[1]].getBall().getSize() > tiles[posibleVectors.get(i + 1)[0]][posibleVectors.get(i + 1)[1]].getBall().getSize()) {
                biggestBall=posibleVectors.get(i);
            } else {
                biggestBall=posibleVectors.get(i+1);
            }
        }
        return biggestBall;
    }

    public static ArrayList<int[]> getAIBalls(Tile[][] tiles) {
    /*
    * Get the positions of AI balls in board.
    * */
        ArrayList<int[]> posibleVectors = new ArrayList<>();


        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 10; j++) {

                if (tiles[i][j].getBall() instanceof BallPink) {
                    int[] temp = {i, j};
                    posibleVectors.add(temp);
                }
            }
        }
        return posibleVectors;
    }
    public static ArrayList<int[]> getPlayerBalls(Tile[][] tiles) {
    /*
    * Get the positions of human player balls in board.
    * */
        ArrayList<int[]> posibleVectors = new ArrayList<>();


        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 10; j++) {

                if (tiles[i][j].getBall() instanceof BallGreen) {
                    int[] temp = {i, j};
                    posibleVectors.add(temp);
                }
            }
        }
        return posibleVectors;
    }

    public static int[] hardMove(Tile[][] tiles,boolean chaser) {
    /*
    * Start a battle against an smaller enemy ball, if cant find one or all near enemy balls
    * are bigger, make a random move.
    * */
        ArrayList<int[]> posibleVectors = ArtificialInteligenceAlgorithm.getAIBalls(tiles);
        for (int k = 0; k < posibleVectors.size(); k++) {
            int[] temp = posibleVectors.get(k);
            int y = temp[0];
            int x = temp[1];
            //move
            for (int i = -1; i < 2; i++) {
                for (int j = -1; j < 2; j++) {
                    if ((y + i < HEIGHT) && (y + i >= 0) && (x + j < WIDTH) && (x + j >= 0)) {
                        if (tiles[y + i][x + j].getBall() instanceof BallGreen && tiles[y + i][x + j].getBall().getSize() <= tiles[y][x].getBall().getSize()) {
                           return new int[]{y, x, y + i, x + j,0};
                        }
                    }
                }
            }
            //split
                for (int h = -2; h < 3; h += 2) {
                    for (int l = -2; l < 3; l +=2) {
                        if ((y + h < HEIGHT) && (y + h >= 0) && (x + l < WIDTH) && (x + l >= 0)) {
                            if (Math.abs(l)!=Math.abs(h)&&tiles[y + h][x + l].getBall() instanceof BallGreen && tiles[y + h][x + l].getBall().getSize() <= tiles[y][x].getBall().getSize() / 3) {

                                System.out.println("soy split");
                                return new int[]{y, x,  h, l,-1};

                            }
                        }
                    }
                }

        }
                if(chaser){
                return ArtificialInteligenceAlgorithm.chaserMove(tiles);
                }else{
                   return ArtificialInteligenceAlgorithm.RandomMove(tiles);
                }
    }

    public static int[] chaserMove(Tile[][] tiles) {
    /*
    * Move towards the smallest enemy ball.
    * */
        int[] ball = ArtificialInteligenceAlgorithm.getBiggestBall(tiles);

        ArrayList<int[]> playerBalls = ArtificialInteligenceAlgorithm.getPlayerBalls(tiles);

        int distance = 1000;
        int index=0;
        int[] AI;
        for (int k = 0; k < playerBalls.size(); k++) {
            AI = playerBalls.get(k);
            int newDistance = Math.abs(ball[0] - AI[0]) + Math.abs(ball[1] - AI[1]);
            if (newDistance < distance&&tiles[ball[0]][ball[1]].getBall().getSize()>tiles[AI[0]][AI[1]].getBall().getSize()) {
                distance = newDistance;
                index = k;
            }
        }
        AI = playerBalls.get(index);
        if((ball[0]-AI[0])>0){
            return new int[]{ball[0], ball[1], ball[0]-1, ball[1],1};
        }else if((ball[0]-AI[0])<0){
            return new int[]{ball[0], ball[1], ball[0]+1, ball[1],1};
        }else{
            if((ball[1]-AI[1])>0){
                return new int[]{ball[0], ball[1], ball[0], ball[1]-1,1};
            }
            else{
                return new int[]{ball[0], ball[1], ball[0], ball[1]+1,1};
            }
        }

    }
}
