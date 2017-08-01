package com.kalantos.spitball.engine;

import android.util.Log;

import com.kalantos.spitball.utils.SendMoveTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

/**
 * Created by Juanma on 27/7/2017.
 */


public class GameManager {
    private Tile[][] tiles;
    private final int width = 10;
    private final int height = 6;
    private boolean gameOver = false;
    public int clicks = 0;
    private int playerTurn = 0;
    private int GameId, onlineTurn;
    private int ax, ay, difficulty;
    private boolean ArtificialInteligence, onlineMove, isMyTurn, movelock;
    private boolean anyMove;

    //metricas de pantalla? puedo pedirlas sin activity?

    public Tile[][] getTiles(){
        return tiles;
    }

    public boolean gameStatus(){
        return !gameOver;
    }

    public void setGameStatus(boolean status){
        gameOver= status;
    }
    public boolean detectMoves(){

        boolean temporal=anyMove;
        anyMove=false;
        return  temporal;
    }
    public GameManager(int GameId, int difficulty, int onlineTurn, boolean ArtificialInteligence) {

        this.GameId = GameId;
        this.difficulty = difficulty;
        this.onlineTurn = onlineTurn;
        this.ArtificialInteligence = ArtificialInteligence;
        inicialize();
        if (GameId != 0) {
            startOnlineGame();
        }

    }

    private void startOnlineGame(){
            if (onlineTurn == 1) {
                playerTurn++;
                isMyTurn = false;
            } else {
                isMyTurn = true;
                try {
                    new SendMoveTask().execute("http://spitball.servegame.com/gameMove.php", "MOVE", "0", "0", "0", "0", "0", Integer.toString(GameId), Integer.toString(1)).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        startOnlineThread();
    }
    private void loadTiles(){
        tiles = new Tile[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                tiles[i][j] = new Tile();
            }
        }
    }
    private void startOnlineThread(){

        Thread refreshOnlineThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!gameOver) {
                    getOnlineMove();
                    Log.d("THREAD", "IS MY TURN: " + isMyTurn);
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        refreshOnlineThread.start();
    }

    private void inicialize() {
        //inicializa los valores de las bolas iniciales
        loadTiles();
        tiles[1][3].setBall(20, BallType.BALLGREEN);
        tiles[2][2].setBall(20, BallType.BALLGREEN);
        tiles[3][3].setBall(20, BallType.BALLGREEN);
        tiles[1][5].setBall(20, BallType.BALLPINK);
        tiles[2][6].setBall(20, BallType.BALLPINK);
        tiles[3][5].setBall(20, BallType.BALLPINK);
    }

    public boolean ClickGestion(int i, int j) {
        //recibe dos enteros que le indican el Tile presionado y por medio de eso gestiona las acciones a realizar
        //por medio de la variable global clicks y turn maneja los clicks para saber cuando corresponde mover o esperar una coordenada
        if ((tiles[i][j].getBall() instanceof BallGreen) && clicks == 0 && (playerTurn % 2 == 0)) {
            ax = i;
            ay = j;
            clicks++;
            return true;

        } else if ((tiles[i][j].getBall() instanceof BallPink) && clicks == 0 && (playerTurn % 2 == 1)) {
            ax = i;
            ay = j;
            clicks++;
            return true;

        } else if (clicks == 1) {
            //CANCEL SELECTION
            if (ax == i && ay == j) {
                clicks = 0;
                anyMove=true;
                return false;
            } else if (Math.abs(ax - i) > 2 || Math.abs(ay - j) > 2) {
                //OUTBOUND MOVEMENT
                clicks = 0;
                anyMove=true;
                return false;
            } else if ((Math.abs(ax - i) == 1 && Math.abs(ay - j) == 1) || (Math.abs(ax - i) == 0 && Math.abs(ay - j) == 1) || (Math.abs(ax - i) == 1 && Math.abs(ay - j) == 0)) {
                //MOVE
                move(ax, ay, i, j);
            } else if (ax - i == -2 && ay == j) {
                //SPLIT DOWN
                split(ax, ay, 2, 0);
            } else if (ax - i == 2 && ay == j) {
                //SPLIT UP
                split(ax, ay, -2, 0);
            } else if (ay - j == -2 && i == ax) {
                //SPLIT RIGHT
                split(ax, ay, 0, 2);
            } else if (ay - j == 2 && i == ax) {
                //SPLIT LEFT
                split(ax, ay, 0, -2);
            } else {
                clicks = 0;
                anyMove=true;
                return false;
            }
            ArtificialMove();
        }
        return false;
        //split no anula animacion, outof bounds tb
    }

    public boolean swipeGestion(int i, int j) {
        //recibe dos enteros que le indican el Tile presionado y por medio de eso gestiona las acciones a realizar
        if ((tiles[i][j].getBall() instanceof BallGreen) && clicks == 0 && (playerTurn % 2 == 0)) {
            ax = i;
            ay = j;
            clicks++;
            return true;

        } else if ((tiles[i][j].getBall() instanceof BallPink) && clicks == 0 && (playerTurn % 2 == 1)) {
            ax = i;
            ay = j;
            clicks++;
            return true;

        } else if (clicks == 1) {
            //CANCEL SELECTION
            if (ax == i && ay == j) {
                clicks = 0;
                anyMove=true;
                return false;
            } else if (Math.abs(ax - i) > 2 || Math.abs(ay - j) > 2) {
                //OUTBOUND MOVEMENT
                anyMove=true;
                clicks = 0;
                return false;
            } else if ((Math.abs(ax - i) == 1 && Math.abs(ay - j) == 1) || (Math.abs(ax - i) == 0 && Math.abs(ay - j) == 1) || (Math.abs(ax - i) == 1 && Math.abs(ay - j) == 0)) {
                //MOVE
                move(ax, ay, i, j);
            } else if (ax - i < 0 && ay == j) {
                //SWIPE DOWN
                move(ax, ay, ax + 1, ay);
            } else if (ax - i > 0 && ay == j) {
                //SWIPE UP
                move(ax, ay, ax - 1, ay);
            } else if (ay - j < 0 && i == ax) {
                //SWIPE RIGHT
                move(ax, ay, ax, ay + 1);
            } else if (ay - j > 0 && i == ax) {
                //SWIPE LEFT
                move(ax, ay, ax, ay - 1);
            } else if (ay - j > 0 && ax - i > 0 && ay - j == (ax - i)) {
                //SWIPE CORNERS
                //esq superior izq
                move(ax, ay, ax - 1, ay - 1);
            } else if (ay - j < 0 && ax - i > 0 && ay - j == -(ax - i)) {
                //esq superior derecha
                move(ax, ay, ax - 1, ay + 1);
            } else if (ay - j > 0 && ax - i < 0 && ay - j == -(ax - i)) {
                //esq inferior izq
                move(ax, ay, ax + 1, ay - 1);
            } else if (ay - j > 0 && ax - i > 0 && ay - j == -(ax - i)) {
                //esq inferior derecha
                move(ax, ay, ax + 1, ay + 1);
            } else {
                clicks = 0;
                anyMove=true;
                return false;
            }
            ArtificialMove();
        }
        return false;
    }

    private void move(int i, int j, int y, int x) {
        // primeros 2 los originales 2 dos a donde van
        //mueve la bola
        anyMove=true;
        if ((!onlineMove && isMyTurn) || (onlineMove && !movelock) || GameId == 0) {

            tiles[y][x].battle(tiles[i][j].getBall());
            tiles[i][j].removeBall();
            clicks = 0;
            movelock = true;
        }
        //debug();
        if (!onlineMove && GameId != 0) {
            try {
                new SendMoveTask().execute("http://spitball.servegame.com/gameMove.php", "MOVE", Integer.toString(j), Integer.toString(i), Integer.toString(x), Integer.toString(y), Integer.toString(0), Integer.toString(GameId), Integer.toString(onlineTurn)).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        if (GameId == 0) {
            playerTurn++;
        }
    }

    private void getOnlineMove() {
        //obtiene las coordenadas del ultimo movimiento y lo ejecuta de forma local

        int[] onlineMoves = receiveJSON();
        onlineMove = true;
        if (onlineMoves[5] != onlineTurn) {
            if (onlineMoves[4] == 0) {
                move(onlineMoves[1], onlineMoves[0], onlineMoves[3], onlineMoves[2]);
            } else {
                split(onlineMoves[1], onlineMoves[0], onlineMoves[3], onlineMoves[2]);
            }
            movelock = true;
            isMyTurn = true;
        } else {
            isMyTurn = false;
            movelock = false;

        }
        onlineMove = false;


    }

    private int[] receiveJSON() {
        try {
            String st = new SendMoveTask().execute("http://spitball.servegame.com/gameMove.php", "GETMOVE", "5", "1", "5", "1", "1", String.valueOf(GameId), "1").get();
            //Log.d("RECIBO PARSE",st);
            //parsea un JSON para obtener un array con los movimientos
            int[] moves = new int[6];
            JSONObject json = null;
            try {
                json = new JSONObject(st);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                moves[0] = json.getInt("XINIT");
                moves[1] = json.getInt("YINIT");
                moves[2] = json.getInt("XLAST");
                moves[3] = json.getInt("YLAST");
                moves[4] = json.getInt("SPLIT");
                moves[5] = json.getInt("TURN");
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
            return moves;

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void ArtificialMove() {
        //va en bloque try catch porque cuando termina el juego la AI intenta mover y genera excepcion de esta forma cuando esta
        //excepcion ocurre no tenogo problemas
        if (ArtificialInteligence) {
            try {
                int[] AIMoves;
                switch (difficulty) {
                    case 0:
                        AIMoves = ArtificialInteligenceAlgorithm.easyMove(tiles);
                        break;
                    case 1:
                        AIMoves = ArtificialInteligenceAlgorithm.hardMove(tiles, false);
                        break;
                    case 2:
                        AIMoves = ArtificialInteligenceAlgorithm.hardMove(tiles, true);
                        break;
                    default:
                        AIMoves = ArtificialInteligenceAlgorithm.easyMove(tiles);
                        break;
                }
                if (AIMoves[4] != -1) {
                    move(AIMoves[0], AIMoves[1], AIMoves[2], AIMoves[3]);
                } else {
                    split(AIMoves[0], AIMoves[1], AIMoves[2], AIMoves[3]);
                }


            } catch (Exception e) {
                //finishGame();
                gameOver = true;
            }
        }
    }

    private void split(int i, int j, int y, int x) {
        //escupe una bola 33% del tamaño de ella misma
        anyMove=true;

        if (!onlineMove && GameId != 0) {
            try {
                new SendMoveTask().execute("http://spitball.servegame.com/gameMove.php", "MOVE", Integer.toString(j), Integer.toString(i), Integer.toString(x), Integer.toString(y), Integer.toString(999), Integer.toString(GameId), Integer.toString(onlineTurn)).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        if ((!onlineMove && isMyTurn) || (onlineMove && !movelock) || GameId == 0) {

            int splittedBallSize = tiles[i][j].getBall().getSize() / 3;

            if (tiles[i][j].getBall() instanceof BallGreen) {
                BallGreen splittedBall = new BallGreen((int) (splittedBallSize * 1.2));
                if (tiles[i][j].getBall().getSize() >= 10) {
                    try {
                        tiles[i][j].getBall().setSize(tiles[i][j].getBall().getSize() - splittedBallSize);
                        tiles[i + y][j + x].battle(splittedBall);
                    } catch (Exception e) {
                        System.out.println("un poco de tu masa se cayo del tablero");
                    }
                }
            }
            if (tiles[i][j].getBall() instanceof BallPink) {
                BallPink splitttedBall = new BallPink((int) (splittedBallSize * 1.2));
                if (tiles[i][j].getBall().getSize() >= 10) {
                    try {
                        tiles[i][j].getBall().setSize(tiles[i][j].getBall().getSize() - splittedBallSize);
                        tiles[i + y][j + x].battle(splitttedBall);
                    } catch (Exception e) {
                        System.out.println("un poco de tu masa se cayo del tablero");
                    }
                }
            }
            /* TO-DO
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    paint();
                }
            });*/
            clicks = 0;
            movelock = true;
        }

        if (GameId == 0) {
            playerTurn++;
        }

    }

    private void debug() {
        //metodo para crear una matriz con los valores size de las bolas
        //util para debugear fallas graficas
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                System.out.print(tiles[i][j].getBall().getSize() + "    ");
            }
            System.out.println("\n");
        }
    }
}
