package com.kalantos.spitball;

import java.util.ArrayList;
import java.util.Random;
///hacer niveles, que a mas dificultad la compu tenga mas bolas
/**
 * Created by kalantos on 03/08/16.
 * Little AI for the game
 */
public class ArtificialInteligenceAlgorithm {
    public static final int HEIGHT = 6;
    public static final int WIDTH = 10;

    public static int[] RandomMove(Tile[][] tiles) {
        //crea movimientos aleatorios a partir de las bolas obtenidas por medio de getBall
        int[] coordinates;        
        //obtiene una de las Bolas de AI
        int [] AIvector= ArtificialInteligenceAlgorithm.getBall(tiles);
        Random random = new Random();
        //genera una
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
        //come a cualquier bola de otro color dentro del rango (solo moviendose) sin importar si tiene menor o mayor
        //tamaño
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
        
        ArrayList<int[]> posibleVectors = ArtificialInteligenceAlgorithm.getAIBalls(tiles);
        Random random = new Random();
        int index = random.nextInt(posibleVectors.size());
        return posibleVectors.get(index);
    }

    public static ArrayList<int[]> getAIBalls(Tile[][] tiles) {
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

    public static int[] hardMove(Tile[][] tiles) {
        //come a las bolas en rango que tengan menor tamaño, si no hay ninguna o esta tiene mayor tamaño
        //hace un movimiento aleatorio

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

                return ArtificialInteligenceAlgorithm.RandomMove(tiles);




    }
  /*  public static int[] chaserMove(Tile[][] tiles) {
        //hace un movimiento hacia la bola mas cercana de menor tamaño

        int[] ball = ArtificialInteligenceAlgorithm.getBall(tiles);
        
        ArrayList<int[]> posibleMove = new ArrayList<>();

        for (int k=0;k<posibleMove.size() ;k++ ) {
            posibleMove
            }
            

        return posibleVectors;
        */
}
