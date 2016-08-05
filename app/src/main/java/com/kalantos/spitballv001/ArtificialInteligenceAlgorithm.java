package com.kalantos.spitballv001;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by kalantos on 03/08/16.
 */
public class ArtificialInteligenceAlgorithm {
    public static final int HEIGHT=6;
    public static final int WIDTH=10;
    public static int[] RandomMove(Tile[][] tiles) {
        int[] coordinates;
        ArrayList<int[]> posibleVectors = new ArrayList<>();


        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 10; j++) {

                if (tiles[i][j].getBall() instanceof BallPink) {
                    int[] temp = {i, j};
                    posibleVectors.add(temp);
                }
            }
        }

        Random random = new Random();
        int index=random.nextInt(posibleVectors.size());
        System.out.println(index);

        int [] AIvector= posibleVectors.get(index);
        int seed=random.nextInt(2);
        int x,y;
        if (seed==1){
            x= -1;
        }else{
            x= 1;
        }

        seed=random.nextInt(2);
        if (seed==1){
            y= -1;
        }else{
            y= 1;
        }
        int newY=AIvector[0]+y;
        int newX=AIvector[1]+x;
        if ((newY<HEIGHT && newY>=0)&& (newX<WIDTH&& newX>=0)){
            coordinates  =new int[]{AIvector[0],AIvector[1],newY,newX};
        }else{
            coordinates=ArtificialInteligenceAlgorithm.RandomMove(tiles);
        }
        System.out.println(""+coordinates[0]+coordinates[1]+coordinates[2]+coordinates[3]);
        return coordinates;

    }
}
