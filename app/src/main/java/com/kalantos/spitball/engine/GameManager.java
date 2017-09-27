package com.kalantos.spitball.engine;

import android.util.Log;
import com.kalantos.spitball.utils.HTTPSocket;
import com.kalantos.spitball.utils.exceptions.InvalidMoveException;
import com.kalantos.spitball.utils.exceptions.LimitMoveException;
import com.kalantos.spitball.utils.exceptions.UnderSizedSpitException;

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
    private int initialX, initialY, difficulty;
    private boolean ArtificialInteligence, onlineMove, isMyTurn, playerHasMoved;
    private boolean anyMove, limitedMove;
    private int greenBalls,pinkBalls;

    public boolean isFinishOnlineGame() {
        return finishOnlineGame;
    }

    private boolean finishOnlineGame = false;

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

    private void startOnlineGame(){
    /*
    * Setup turns in order to assign one color to each online player, and send a dummy move to avoid a crash while player2 fetches database.
    * */
        try {
            String jsonData = createJson( "METHODTYPE","MOVE","XINIT", "0","YINIT", "0","XLAST", "0",
                    "YLAST", "0","SPLIT", "0","GAMEID", Integer.toString(GameId),"TURN", Integer.toString(1));
            new HTTPSocket().execute("http://spitball.000webhostapp.com/gameMove.php","POST",jsonData).get();
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
                int refreshTimeMillis = 2000;
                int turnTimeMillis = 20000;
                while (!gameOver) {
                    for (int i = 0; i<(turnTimeMillis/refreshTimeMillis); i++) {
                        Log.d("GG","time: "+i+" isMyTurn: "+isMyTurn+" playerHasMoved: "+playerHasMoved);
                        if(!isMyTurn) {
                            onlineMoves = getOnlineMove();
                            updateBoard(onlineMoves);
                        }else{
                            if(finishOnlineGame){
                                Log.d("GG","ATTEMPTING LEFT GAME");
                                sendMoves(0,0,0,0,0,-1);
                                gameOver = true;
                                if(onlineTurn==0){
                                    greenBalls = 0;
                                }else{
                                    pinkBalls =0;
                                }
                                break;
                            }
                        }
                        if(playerHasMoved){
                            playerHasMoved = false;
                            isMyTurn=!isMyTurn;
                            break;
                        }
                        if (((turnTimeMillis / refreshTimeMillis) - 1) == i) {
                            boolean tt=isMyTurn;
                            isMyTurn=!isMyTurn;
                            Log.d("GG","intime:"+i+" toggle: "+tt+" ->"+isMyTurn);
                        }
                        try {
                            //TODO: test with a higher refresh rate
                            Thread.sleep(refreshTimeMillis);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

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

    public boolean swipeHandler(int actualX, int actualY) {
    /*
    * Receive a Tile (x and y position) and using turn, clicks and olderX and olderY manage to know
    * when balls must move/split or wait for another coordinate.
    * */
        if ((tiles[actualX][actualY].getBall() instanceof BallGreen) && clicks == 0 && (playerTurn % 2 == 0)) {
            initialX = actualX;
            initialY = actualY;
            clicks++;
            return true;

        } else if ((tiles[actualX][actualY].getBall() instanceof BallPink) && clicks == 0 && (playerTurn % 2 == 1)) {
            initialX = actualX;
            initialY = actualY;
            clicks++;
            return true;

        } else if (clicks == 1) {
            if (initialX == actualX && initialY == actualY) {
                //CANCEL SELECTION
                clicks = 0;
                anyMove=true;
                return false;
            } else if (Math.abs(initialX - actualX) > 2 || Math.abs(initialY - actualY) > 2) {
                //OUTBOUND MOVEMENT
                anyMove=true;
                clicks = 0;
                return false;
            } else if ((Math.abs(initialX - actualX) == 1 && Math.abs(initialY - actualY) == 1) || (Math.abs(initialX - actualX) == 0 && Math.abs(initialY - actualY) == 1) || (Math.abs(initialX - actualX) == 1 && Math.abs(initialY - actualY) == 0)) {
                //MOVE
                try{
                    clicks = 0;
                    move(initialX, initialY, actualX, actualY);
                    if (ArtificialInteligence) {
                    ArtificialMove(false);
                    }
                }catch (InvalidMoveException | LimitMoveException e){
                    Log.e("GAME",e.getMessage());
                }
                return false;
            }else if ((initialX - actualX == -2 && initialY == actualY) || (initialX - actualX == 2 && initialY == actualY) ||
                    (initialY - actualY == -2 && actualX == initialX) || (initialY - actualY == 2 && actualX == initialX) ) {
                //SPLIT
                try{
                    clicks = 0;
                    split(initialX, initialY, actualX, actualY);
                    if (ArtificialInteligence) {
                        ArtificialMove(false);
                    }
                }catch (UnderSizedSpitException e){
                    Log.e("GAME",e.getMessage());
                }
                return false;
            } else {
                clicks = 0;
                anyMove=true;
                return false;
            }
        }
        return false;
    }

    private void move(int initialY, int initialX, int finalY, int finalX) throws InvalidMoveException, LimitMoveException{
    /*
    * Move the ball from initial x and y to final x and y.
    * */
        anyMove=true;
        if ((!onlineMove && isMyTurn) || (onlineMove && !isMyTurn)|| GameId == 0) {
            if(tiles[finalY][finalX].battle(tiles[initialY][initialX].getBall(),limitedMove)){
                tiles[initialY][initialX].removeBall();
                sendMoves(initialY, initialX, finalY, finalX, 0, onlineTurn);
                if (GameId == 0) {
                    playerTurn=(playerTurn+1)%2;
                }
                playerHasMoved=true;
            }else{
                throw new LimitMoveException("You have reach the minimum ball limit.");
            }

        }else{
            throw new InvalidMoveException("Invalid move");
        }
    }

    private void updateBoard(int[] onlineMoves) {
    /*
    * Update local board using database received information.
    * */
        onlineMove = true;
        if (onlineMoves[5] != onlineTurn && onlineMoves[5] != -1) {
            if (onlineMoves[4] == 0) {
                try{
                    move(onlineMoves[1], onlineMoves[0], onlineMoves[3], onlineMoves[2]);
                    //ArtificialMove();
                }catch (InvalidMoveException | LimitMoveException e){
                    Log.e("ONLINE CONNECTION",e.getMessage());
                }
            } else {
                try{
                    split(onlineMoves[1], onlineMoves[0], onlineMoves[3], onlineMoves[2]);
                    //ArtificialMove();
                }catch (UnderSizedSpitException e){
                    Log.e("ONLINE CONNECTION",e.getMessage());
                }
            }
            playerHasMoved=true;
        }else if(onlineMoves[5] == -1){
            Log.d("GG","DETECTED GAMELEFT");
            gameOver = true;
            if(onlineTurn==0){
                pinkBalls =0;
            }else{
                greenBalls = 0;
            }
        }
        onlineMove = false;

    }

    private int[] getOnlineMove() {
    /*
    * Get last move in json format from online database and parse it to array form.
    * */
        try {
            String jsonData = createJson( "METHODTYPE","GETMOVE","GAMEID",Integer.toString(GameId));
            String st = new HTTPSocket().execute("http://spitball.000webhostapp.com/gameMove.php","POST",jsonData).get();
            int[] moves = new int[6];
            JSONObject json;
                json = new JSONObject(st);
                moves[0] = json.getInt("XINIT");
                moves[1] = json.getInt("YINIT");
                moves[2] = json.getInt("XLAST");
                moves[3] = json.getInt("YLAST");
                moves[4] = json.getInt("SPLIT");
                moves[5] = json.getInt("TURN");

            return moves;

        } catch (InterruptedException|ExecutionException|NullPointerException|JSONException e) {
            Log.e("ONLINE CONNECTION", e.getMessage());
        }

        return null;
    }

    private void ArtificialMove(boolean recursive) {
    /*
    * Manage AI moves/split depending in the game difficulty.
    * */
        int[] AIMoves;
        if(recursive){
            AIMoves = ArtificialInteligenceAlgorithm.RandomMove(tiles);
        }else {
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
        }
        try {
            if (AIMoves[4] != -1) {
                move(AIMoves[0], AIMoves[1], AIMoves[2], AIMoves[3]);
            } else {
                split(AIMoves[0], AIMoves[1], AIMoves[2], AIMoves[3]);
            }
        } catch (InvalidMoveException | LimitMoveException | UnderSizedSpitException e) {
            Log.e("AI", e.getMessage());
            ArtificialMove(true);
        }catch (Exception e){
            Log.e("AI", "end game");
            //catch exception: when game ends IA try to move causing an error.
            gameOver = true;
        }

    }
    private void sendMoves(int initialY, int initialX, int finalY, int finalX, int splitIdentifier, int onlineTurn){
    /*
    * Send move/split to server.
    * */
        if (!onlineMove && GameId != 0) {
            try {
                String jsonData = createJson( "METHODTYPE","MOVE","XINIT", Integer.toString(initialX),
                        "YINIT", Integer.toString(initialY),"XLAST", Integer.toString(finalX), "YLAST",
                        Integer.toString(finalY),"SPLIT", Integer.toString(splitIdentifier),"GAMEID",
                        Integer.toString(GameId),"TURN", Integer.toString(onlineTurn));
                new HTTPSocket().execute("http://spitball.000webhostapp.com/gameMove.php","POST",jsonData).get();
            } catch (InterruptedException|ExecutionException e) {
                Log.e("ONLINE CONNECTION", e.getMessage());
            }
        }
    }

    private void split(int initialY, int initialX, int finalY, int finalX) throws UnderSizedSpitException {
    /*
    * Receive a start coordinate of the original ball, and spit a smaller ball (33% size) into
    * the delta direction and reduce spitter ball size.
    * */
        anyMove=true;

        if ((!onlineMove && isMyTurn) || (onlineMove && !isMyTurn) || GameId == 0) {
            Ball splittedBall;
            int splittedBallSize = tiles[initialY][initialX].getBall().getSize() / 3;
            if (tiles[initialY][initialX].getBall() instanceof BallPink){

                splittedBall = new BallPink((int) (splittedBallSize * 1.2));
            }else{

                splittedBall = new BallGreen((int) (splittedBallSize * 1.2));
            }
            if (tiles[initialY][initialX].getBall().getSize() >= 10) {

                try {
                    tiles[initialY][initialX].getBall().setSize(tiles[initialY][initialX].getBall().getSize() - splittedBallSize);
                    tiles[finalY][finalX].battle(splittedBall,false);

                    if (GameId == 0) {
                        playerTurn=(playerTurn+1)%2;
                    }
                    playerHasMoved=true;
                    sendMoves(initialY, initialX, finalY, finalX, 1, onlineTurn);
                } catch (Exception e) {
                    Log.i("GAME","Some of you mass pour down the board");
                }
            }else{
                Log.e("GAME","Try to spit with a really small ball");
                throw new UnderSizedSpitException("Ball too small to split");
            }
        }
    }

    private String createJson(String... strings){
    /*
    * Create a Json with all data received and returns the json in string format.
    * */
        JSONObject json= new JSONObject();
        for(int i=0; i<strings.length; i=i+2){
            try{
                json.put(strings[i],strings[i+1]);
            }catch(JSONException e){
                Log.e("ONLINE CONNECTION", e.getMessage());
            }
        }
        return json.toString();
    }

    public int getGreenBalls() {
        return greenBalls;
    }

    public int getPinkBalls() {
        return pinkBalls;
    }

    public Tile[][] getTiles(){

        return tiles;
    }

    public boolean gameStatus(){

        return !gameOver;
    }

    public void setFinishOnlineGame(boolean finishOnlineGame) {
        this.finishOnlineGame = finishOnlineGame;
    }

    public boolean isOnlineGame(){
        if(GameId !=0){
            return true;
        }else{
            return false;
        }
    }

    public void updateStatus(){
    /*
    * Create board made up with Tiles.
    * */
        greenBalls = 0;
        pinkBalls = 0;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                //image
                if (tiles[i][j].getBall() instanceof BallGreen){
                    greenBalls++;
                } else if(tiles[i][j].getBall() instanceof BallPink){
                    pinkBalls++;
                }
            }
        }
        if((playerTurn ==1 && pinkBalls<=2) || (playerTurn ==0 && greenBalls<=2)){
            limitedMove=true;
        }else{
            limitedMove=false;
        }
        if(pinkBalls == 0 || greenBalls == 0){
            gameOver = true;
        }
    }

    public boolean anyMove() {
    /*
    * Returns true when player finish the move/split, and restart
    * the value to false in order to save processing power in gameActivity.
    * */
        boolean temporal=anyMove;
        anyMove=false;
        return  temporal;
    }

}