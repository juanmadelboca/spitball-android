package com.kalantos.spitball.engine;

import android.util.Log;
import com.kalantos.spitball.utils.SendMoveTask;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.concurrent.ExecutionException;

/**
 * Manage all the game, create the game and is responsible of getting online moves, and process all
 * the commands received from Game Activity in order to reflect the moves inserted by the player
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

    public GameManager(int GameId, int difficulty, int onlineTurn, boolean ArtificialInteligence) {

        this.GameId = GameId;
        this.difficulty = difficulty;
        this.onlineTurn = onlineTurn;
        this.ArtificialInteligence = ArtificialInteligence;
        loadTiles();
        inicialize();
        if (GameId != 0) {
            startOnlineGame();
            startOnlineThread();
        }

    }

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

    private void startOnlineGame(){
    /*
    * Setup turns in order to assign one color to each online player, and send a dummy move to avoid a crash while player2 fetches database.
    * */
        try {
            new SendMoveTask().execute("http://spitball.000webhostapp.com/gameMove.php", "MOVE", "0", "0", "0", "0", "0", Integer.toString(GameId), Integer.toString(1)).get();
        } catch (InterruptedException|ExecutionException e) {
            e.printStackTrace();
        }
        if (onlineTurn == 1) {
            playerTurn++;
            isMyTurn = false;
        } else {
            isMyTurn = true;
        }
    }

    private void loadTiles(){
    /*
    * Create board made up with Tiles.
    * */
        tiles = new Tile[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                tiles[i][j] = new Tile();
            }
        }
    }
    private void startOnlineThread(){
    /*
    * Start a thread that fetch moves from database periodically.
    * */
        Thread refreshOnlineThread = new Thread(new Runnable() {
            @Override
            public void run() {
                int[] onlineMoves;
                while (!gameOver) {
                    onlineMoves=getOnlineMove();
                    updateBoard(onlineMoves);
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
    /*
    * Initializes ball positions.
    * */
        tiles[1][3].setBall(20, BallType.BALLGREEN);
        tiles[2][2].setBall(20, BallType.BALLGREEN);
        tiles[3][3].setBall(20, BallType.BALLGREEN);
        tiles[1][5].setBall(20, BallType.BALLPINK);
        tiles[2][6].setBall(20, BallType.BALLPINK);
        tiles[3][5].setBall(20, BallType.BALLPINK);
    }

    public boolean ClickGestion(int i, int j) {
    /*
    * Receive a Tile (x and y position) and using turn, clicks and olderX and olderY manage to know
    * when balls must move/split or wait for another coordinate.
    * */
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
    }

    public boolean swipeGestion(int i, int j) {
    /*
    * Receive a Tile (x and y position) and using turn, clicks and olderX and olderY manage to know
    * when balls must move/split or wait for another coordinate.
    * TODO: DUPLICATED!
    * TODO: CHANGE GESTION TO HANDLER
    * */
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
                //UP-LEFT CORNER
                move(ax, ay, ax - 1, ay - 1);
            } else if (ay - j < 0 && ax - i > 0 && ay - j == -(ax - i)) {
                //UP-RIGHT CORNER
                move(ax, ay, ax - 1, ay + 1);
            } else if (ay - j > 0 && ax - i < 0 && ay - j == -(ax - i)) {
                //DOWN-LEFT CORNER
                move(ax, ay, ax + 1, ay - 1);
            } else if (ay - j > 0 && ax - i > 0 && ay - j == -(ax - i)) {
                //DOWN-RIGHT CORNER
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

    private void move(int initialY, int initialX, int finalY, int finalX) {
    /*
    * Move the ball from initial x and y to final x and y.
    * */
        anyMove=true;
        if ((!onlineMove && isMyTurn) || (onlineMove && !movelock) || GameId == 0) {

            tiles[finalY][finalX].battle(tiles[initialY][initialX].getBall());
            tiles[initialY][initialX].removeBall();
            clicks = 0;
            movelock = true;
        }
        if (!onlineMove && GameId != 0) {
            try {
                new SendMoveTask().execute("http://spitball.000webhostapp.com/gameMove.php", "MOVE", Integer.toString(initialX), Integer.toString(initialY), Integer.toString(finalX), Integer.toString(finalY), Integer.toString(0), Integer.toString(GameId), Integer.toString(onlineTurn)).get();
            } catch (InterruptedException|ExecutionException e) {
                e.printStackTrace();
            }
        }
        if (GameId == 0) {
            playerTurn++;
        }
    }

    private void updateBoard(int[] onlineMoves) {
    /*
    * Update local board using database received information.
    * */
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

    private int[] getOnlineMove() {
    /*
    * Get last move in json format from online database and parse it to array form.
    * */
        try {
            String st = new SendMoveTask().execute("http://spitball.000webhostapp.com/gameMove.php", "GETMOVE", "5", "1", "5", "1", "1", String.valueOf(GameId), "1").get();
            int[] moves = new int[6];
            JSONObject json;
            Log.d("JSON-POST",st);
                json = new JSONObject(st);
                moves[0] = json.getInt("XINIT");
                moves[1] = json.getInt("YINIT");
                moves[2] = json.getInt("XLAST");
                moves[3] = json.getInt("YLAST");
                moves[4] = json.getInt("SPLIT");
                moves[5] = json.getInt("TURN");

            return moves;

        } catch (InterruptedException|ExecutionException|NullPointerException|JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void ArtificialMove() {
    /*
    * Manage AI moves/split depending in the game difficulty.
    * */
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
                //catch exception: when game ends IA try to move causing an error.
                gameOver = true;
            }
        }
    }

    private void split(int i, int j, int y, int x) {
    /*
    * Spit a smaller ball (33% size) and reduce spitter ball size.
    * TODO: DUPLICATE CODE, GENERALIZE SPITTING.
    * */
        anyMove=true;

        if (!onlineMove && GameId != 0) {
            try {
                new SendMoveTask().execute("http://spitball.000webhostapp.com/gameMove.php", "MOVE", Integer.toString(j), Integer.toString(i), Integer.toString(x), Integer.toString(y), Integer.toString(999), Integer.toString(GameId), Integer.toString(onlineTurn)).get();
            } catch (InterruptedException|ExecutionException e) {
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
                        Log.i("GAME","Some of you mass pour down the board");
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
                        Log.i("GAME","Some of you mass pour down the board");
                    }
                }
            }
            clicks = 0;
            movelock = true;
        }

        if (GameId == 0) {
            playerTurn++;
        }

    }
}