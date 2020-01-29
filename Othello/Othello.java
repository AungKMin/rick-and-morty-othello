/* 
 * Othello.java
 *
 * The game runs all the logic of the game: 
 *   Outflanks
 *   Detecting the end of a game or match and displaying winner
 * Please refer to README.txt for all the extras
 */

public class Othello {

    /* constants */
    final int MAXGAME; // the number of games a player needs to win to win the match  
    final int NUMPLAYER; // number of players in the game
    final int NUMROW; // number of rows in the game board
    final int NUMCOL; // number of columns in the game board
    final int AREA; // number of slots on the board
    final int INDICATOR = -2; // represents a valid-move indicator on the game board
    final int EMPTY = -1; // represents an empty square on the game board   
    final int PLAYER1 = 0; // identification of player 1
    final int PLAYER2 = 1; // identification of player 2
    final int NUMINITIALCORDS; // number of initial pieces
    final int[] INITIALROWCORDS; // row coordinates (y-coordinate) of the starting points
    final int[] INITIALCOLCORDS; // column coordinates (x-coordinate) of the start 
    final int[] INITIALPLAYERCORDS; // corresponding player of the starting points

    OthelloGUI gui; // user interface object
    int numMove; // number of moves since the beginning of the game - unused so far
    int curPlayer; // the current player
    int board[][]; // 2D representation of the game board
    int score[]; // the match scores or the number of games each player has won
    int points[]; // the points or the number of pieces each player has on the board

    /**
     * Constructor: initializes the gui object, the number of games in a match, 
     * the number of players, the dimensions of the board, a representation of the board, 
     * and the coordinates of the initial points
     * 
     * @param  gui  the gui object that will be the graphical interface of the game
     */
    public Othello(OthelloGUI gui) {

        this.gui = gui; // Assign the passed-in gui to the object's gui
        // Assign constants from the gui object
        NUMPLAYER = gui.NUMPLAYER; 
        NUMROW = gui.NUMROW;
        NUMCOL = gui.NUMCOL;
        AREA = NUMROW*NUMCOL;
        MAXGAME = gui.MAXGAME;

        board = new int[NUMROW][NUMCOL]; // initialize the board

        // Create the arrays containing information about the initial set up
        NUMINITIALCORDS = 4;
        // The row coordinate (y-coordinate) of the starting points
        INITIALROWCORDS = new int[NUMINITIALCORDS];
        INITIALROWCORDS[0] = 3;
        INITIALROWCORDS[1] = 4;
        INITIALROWCORDS[2] = 3;
        INITIALROWCORDS[3] = 4;
        // The column coordinate (x-coordinate) of the starting points
        INITIALCOLCORDS = new int[NUMINITIALCORDS];
        INITIALCOLCORDS[0] = 3;
        INITIALCOLCORDS[1] = 3;
        INITIALCOLCORDS[2] = 4;
        INITIALCOLCORDS[3] = 4;
        // The corresponding player of the starting points
        INITIALPLAYERCORDS = new int[NUMINITIALCORDS];
        INITIALPLAYERCORDS[0] = PLAYER2;
        INITIALPLAYERCORDS[1] = PLAYER1;
        INITIALPLAYERCORDS[2] = PLAYER1;
        INITIALPLAYERCORDS[3] = PLAYER2;

        score = new int[NUMPLAYER]; // make score array
        points = new int[NUMPLAYER]; // make points array

        // Start a match
        newMatch();

    }

    /**
     * Create a new match: reset match scores, player points, turn, and board.
     */
    private void newMatch() {

        // Set the match scores to 0
        for (int i = 0; i < NUMPLAYER; i++) {
            score[i] = 0;
        }

        // Start a new game
        newGame();

    }

    /**
     * Create a new game: reset player points, turn, and board.
     */
    private void newGame() {

        // Set the player turn to player 1
        curPlayer = PLAYER1;
        gui.setNextPlayer(PLAYER1);

        // Set the points to 0
        for (int i = 0; i < NUMPLAYER; i++) {
            points[i] = 0;
            gui.setPlayerPoints(i, 0);
        }

        // Increase the points based on how many pieces each player starts out with
        for (int i = 0; i < NUMINITIALCORDS; i++) {
            points[INITIALPLAYERCORDS[i]]++;
        }

        // Set up the board to the initial position
        initBoard();

    }

    /**
     * Set up the board to the starting formation. Update graphics and the board array.
     */
    private void initBoard() {

        // Make every slot empty
        for (int i = 0; i < NUMROW; i++) {
            for (int j = 0; j < NUMCOL; j++) {
                board[i][j] = EMPTY;
            }
        }
        gui.resetGameBoard(); // gui reset board

        // Set up the initial pieces and the indicators
        for (int i = 0; i < NUMINITIALCORDS; i++) {
            board[INITIALROWCORDS[i]][INITIALCOLCORDS[i]] = INITIALPLAYERCORDS[i]; // update board
            gui.setPiece(INITIALROWCORDS[i], INITIALCOLCORDS[i], INITIALPLAYERCORDS[i]); // gui set piece
            updateIndicators(INITIALROWCORDS[i], INITIALCOLCORDS[i]); // indicators
        }

    }

    /**
     * Return whether a move is valid. If not, display an invalid-move textbox
     * 
     * @param  row  the row number
     * @param  col  the column number
     */
    private boolean validMove(int row, int col) {
         
        if (board[row][col] == INDICATOR) { // If the move is on an indicator
            return true; // the move is valid
        } else { // else, it is invalid
            gui.showInvalidMoveMessage(); // display invalid message
            return false;
        }

    }

    /**
     * Update the board graphically and in the array.
     * 
     * @param  row  the row number
     * @param  col  the column number
     */
    private void updateBoard (int row, int col) {

        // Place the player piece on the clicked slot
        board[row][col] = curPlayer;
        gui.setPiece(row, col, curPlayer);

        // Update indicators
        updateIndicators(row, col);

        // Flank as appropriate
        flank(row, col);

    }

    /**
     * Outflank any appropriate enemy pieces. 
     * Update the board graphically and in the array. 
     * Display a textbox saying how many pieces were outflanked.
     * Update the player point scores with the flanks
     * 
     * @param  row  the row number
     * @param  col  the column number
     * @return the number of peices outflanked.
     */
    private int flank(int row, int col) {

        // Number of total flanks
        int numFlanks = flankHori(row, col) + flankVerti(row, col) + flankDiag(row, col);
        if (numFlanks > 0) {
            gui.showOutflankMessage(curPlayer, numFlanks); // if there were any outflanks, display a message
        }

        // Update the number of points the player has
        updateScore(numFlanks);

        return numFlanks;

    }

    /**
     * Outflank any appropriate enemy pieces in the row. 
     * Update the board graphically and in the array. 
     * 
     * @param  row  the row number
     * @param  col  the column number
     * @return the number of peices outflanked in the row.
     */
    private int flankHori(int row, int col) {

        int numFlanks = 0; // number of flanks

        // Set enemy player
        int enemy = (curPlayer + 1) % NUMPLAYER;

        // Go right
        int counter = col + 1; // Keep track of the continuous line of enemy pieces
        while (counter < NUMCOL && board[row][counter] == enemy) { // Determine the coordinate at the end of the continuous line of enemy pieces
            counter += 1; // keep count of enemy piece number
        }
        if (counter < NUMCOL && board[row][counter] == curPlayer) { // Check if the end is a player piece
            for (int i = col + 1; i < counter; i++) { // If so, flip the enemies in the line
                gui.setPiece(row, i, curPlayer); // update gui
                board[row][i] = curPlayer; // update array
            }
            numFlanks += counter - col - 1; // The number of pieces flipped (number of pieces between the end of the line and the beginning)
        }

        // Go left
        // Subtract from the counter now
        counter = col - 1;
        while (counter >= 0 && board[row][counter] == enemy) {
            counter -= 1;
        }
        if (counter >= 0 && board[row][counter] == curPlayer) {
            for (int i = col - 1; i >= counter; i--) {
                gui.setPiece(row, i, curPlayer);
                board[row][i] = curPlayer;
            }
            numFlanks += col - counter - 1;
        }

        return numFlanks;

    }

    /**
     * Outflank any appropriate enemy pieces in the column. 
     * Update the board graphically and in the array. 
     * 
     * @param  row  the row number
     * @param  col  the column number
     * @return the number of peices outflanked in the column.
     */
    private int flankVerti(int row, int col) {

        // The number of flanks counted
        int numFlanks = 0;

        // Set enemy player
        int enemy = (curPlayer + 1) % NUMPLAYER;

        // Go down
        int counter = row + 1; // Keep track of the continuous line of enemy pieces
        while (counter < NUMROW && board[counter][col] == enemy) { // Determine the coordinate at the end of the continuous line of enemy pieces
            counter += 1; // update enemy piece number
        }
        if (counter < NUMROW && board[counter][col] == curPlayer) { // Check if the end is a player piece
            for (int i = row + 1; i < counter; i++) { // If so, flip the enemies in the line
                gui.setPiece(i, col, curPlayer); // Update gui piece
                board[i][col] = curPlayer; // update array
            }
            numFlanks += counter - row - 1; // The number of pieces flipped (number of pieces between the end of the line and the beginning)
        }

        // Go up
        counter = row - 1;
        while (counter >= 0 && board[counter][col] == enemy) {
            counter -= 1;
        }
        if (counter >= 0 && board[counter][col] == curPlayer) {
            for (int i = row - 1; i >= counter; i--) {
                gui.setPiece(i, col, curPlayer);
                board[i][col] = curPlayer;
            }
            numFlanks += row - counter - 1;
        }

        return numFlanks;

    }

    /**
     * Outflank any appropriate enemy pieces in the one or two diagonals. 
     * Update the board graphically and in the array. 
     * 
     * @param  row  the row number
     * @param  col  the column number
     * @return the number of peices outflanked in the column.
     */
    private int flankDiag(int row, int col) {

        int numFlanks = 0; // number of flanks
        int numEnemies; // number of enemies

        // Set enemy player
        int enemy = (curPlayer + 1) % NUMPLAYER;

        // Go top left
        //    Coordinates of the slot at the end of the line of enemies
        int vertiCounter = row - 1;
        int horiCounter = col - 1;
        //    Find the longest line of enemies
        while (vertiCounter >= 0 && horiCounter >= 0 && board[vertiCounter][horiCounter] == enemy) { // Determine the coordinates at the end of the continuous line of enemy pieces 
            vertiCounter -= 1; // update vertical 
            horiCounter -= 1; // update horizontal
        }
        // Number of enemy pieces counted (the number of pieces between the end and the beginning of the line)  
        numEnemies = row - vertiCounter - 1; 
        //    Outflank the line of enemies if surrounded          
        if (vertiCounter >= 0 && horiCounter >= 0 && board[vertiCounter][horiCounter] == curPlayer) { // Check if the end is a player piece 
            for (int i = 1; i <= numEnemies; i++) { // If so, flip the enemy pieces in the line
                gui.setPiece(row - i, col - i, curPlayer); // set gui piece
                board[row - i][col - i] = curPlayer; // update array
            }
            numFlanks += numEnemies; // Add the number of pieces flipped
        }

        // Go top right
        vertiCounter = row - 1;
        horiCounter = col + 1;
        //    Find the longest line of enemies
        while (vertiCounter >= 0 && horiCounter < NUMCOL && board[vertiCounter][horiCounter] == enemy) {
            vertiCounter -= 1;
            horiCounter += 1;
        }
        numEnemies = row - vertiCounter - 1;
        //    Outflank the line of enemies if surrounded
        if (vertiCounter >= 0 && horiCounter < NUMCOL && board[vertiCounter][horiCounter] == curPlayer) {
            for (int i = 1; i <= numEnemies; i++) {
                gui.setPiece(row - i, col + i, curPlayer);
                board[row - i][col + i] = curPlayer;
            }
            numFlanks += numEnemies;
        }

        // Go bottom left
        vertiCounter = row + 1;
        horiCounter = col - 1;
        //    Find the longest line of enemies
        while (vertiCounter < NUMROW && horiCounter >= 0 && board[vertiCounter][horiCounter] == enemy) {
            vertiCounter += 1;
            horiCounter -= 1;
        }
        numEnemies = vertiCounter - row - 1;
        //    Outflank the line of enemies if surrounded
        if (vertiCounter < NUMROW && horiCounter >= 0 && board[vertiCounter][horiCounter] == curPlayer) {
            for (int i = 1; i <= numEnemies; i++) {
                gui.setPiece(row + i, col - i, curPlayer);
                board[row + i][col - i] = curPlayer;
            }
            numFlanks += numEnemies;
        }

        // Go bottom right
        vertiCounter = row + 1;
        horiCounter = col + 1;
        //    Find the longest line of enemies
        while (vertiCounter < NUMROW && horiCounter < NUMCOL && board[vertiCounter][horiCounter] == enemy) {
            vertiCounter += 1;
            horiCounter += 1;
        }
        numEnemies = vertiCounter - row - 1;
        //    Outflank the line of enemies if surrounded
        if (vertiCounter < NUMROW && horiCounter < NUMCOL && board[vertiCounter][horiCounter] == curPlayer) {
            for (int i = 1; i <= numEnemies; i++) {
                gui.setPiece(row + i, col + i, curPlayer);
                board[row + i][col + i] = curPlayer;
            }
            numFlanks += numEnemies;
        }

        return numFlanks;

    }

    /** 
     * Update the player point scores.
     * 
     * @param piecesTurnedOver the number of pieces turned over
     */
    private void updateScore(int piecesTurnedOver) {

        points[curPlayer] += piecesTurnedOver + 1; // Number of pieces the current player gained (pieces flanked + the piece placed)
        // Take away the number of pieces turned over from opponent
        if (curPlayer == PLAYER1) {
            points[PLAYER2] -= piecesTurnedOver; // If player 1 is current player, subtract from player 2
        } else {
            points[PLAYER1] -= piecesTurnedOver; // if player 2 is current, subtract from player 1
        }
        
        // Update the players' points displayed
        for (int i = PLAYER1; i < NUMPLAYER; i++) { 
            gui.setPlayerPoints(i, points[i]);
        }

    }

    /** 
     * Update the match score. 
     * If the match score changes, i.e. a player won or tied, display a textbox saying this. 
     * If the match is over, also display a textbox saying this.
     */
    private void updateMatchScore() {

        // Find out the number of slots occupied by each player
        int sumPoints = 0;
        for (int i = PLAYER1; i < NUMPLAYER; i++) {
            sumPoints += points[i];
        }

        if (sumPoints == AREA) { // If the board is filled (and the game is over)

            // Find the winner of the game or the players who tied 
            int highestPoints = points[PLAYER1]; // let the highest points be player 1 first
            int highestPlayer = PLAYER1; // let the highest points be player 1 first
            int numTies = 0; // number of ties (additional highest score players)
            //    Find the player with the highest points
            for (int i = 1; i < NUMPLAYER; i++) {
                if (points[i] > highestPoints) { 
                    highestPoints = points[i];
                    highestPlayer = i;
                }
            }

            //    Find the number of ties
            for (int i = 0; i < NUMPLAYER; i++) { 
                if (points[i] == highestPoints) { 
                    numTies++;
                }
            }

            numTies--; // Subtract one from the number of ties for account

            int[] tiedPlayers; // Array for tied players if any

            // If there were ties, find the tied players.
            if (numTies > 0) {
                tiedPlayers = new int[numTies + 1];
                int counter = 0; // Current index of tiedPlayers
                // All players who have the highest number of points are tied
                for (int i = 0; i < NUMPLAYER; i++) {
                    if (points[i] == highestPoints) {
                        tiedPlayers[counter] = i; // put player in the ties array
                        counter++; // increase the index of the ties array
                    }
                }
            }

            // Update the match scores and displays as appropriate
            if (numTies > 0) { // If there is a tie
                gui.showTieGameMessage(); // show tie message
            } else { // If not, 
                gui.showWinnerMessage(highestPlayer); // show winner message
                score[highestPlayer]++; // update match score
                gui.setPlayerScore(highestPlayer, score[highestPlayer]); // update gui match score
            }

            // If the player won the whole match, display it appropriately.
            if (score[highestPlayer] == MAXGAME) {
                gui.showFinalWinnerMessage(highestPlayer);
            }

            newGame(); // make new game

        }

    }

    /**
     * Set the current player variable to the next player. Also change the current player graphically.
     */
    private void setNextPlayer() {

        curPlayer = (curPlayer + 1) % NUMPLAYER; // set player to the next player numerically; if its the last player's turn, loop back to the first player

        gui.setNextPlayer(curPlayer);

    }

    /** 
     * Update the indicators as appropriate based on the piece placed
     * 
     * @param  row  The row that the set peice is on 
     * @param  col  The column that the set piece is on
     */
    private void updateIndicators(int row, int col) {

        // Does the slot have a row above it?
        boolean top = row > 0;
        // below?
        boolean bot = row < NUMROW - 1;
        // Does the slot have a column to its left?
        boolean left = col > 0;
        // right?
        boolean right = col < NUMCOL - 1;

        // Check if any slots around the piece placed are empty, if so place an indicator
        if (top && board[row - 1][col] == EMPTY) { // Above
            gui.setIndicator(row - 1, col); // set indicator in gui
            board[row - 1][col] = INDICATOR; // set indicator in board array
        }
        if (bot && board[row + 1][col] == EMPTY) { // Below
            gui.setIndicator(row + 1, col);
            board[row + 1][col] = INDICATOR;
        }
        if (left && board[row][col - 1] == EMPTY) { // Left
            gui.setIndicator(row, col - 1);
            board[row][col - 1] = INDICATOR;
        }
        if (right && board[row][col + 1] == EMPTY) { // Right
            gui.setIndicator(row, col + 1);
            board[row][col + 1] = INDICATOR;
        }
        if ((top && left) && board[row - 1][col - 1] == EMPTY) { // Top left
            gui.setIndicator(row - 1, col - 1);
            board[row - 1][col - 1] = INDICATOR;
        }
        if ((top && right) && board[row - 1][col + 1] == EMPTY) { // Top right
            gui.setIndicator(row - 1, col + 1);
            board[row - 1][col + 1] = INDICATOR;
        }
        if ((bot && right) && board[row + 1][col + 1] == EMPTY) { // Bottom right
            gui.setIndicator(row + 1, col + 1);
            board[row + 1][col + 1] = INDICATOR;
        }
        if ((bot && left) && board[row + 1][col - 1] == EMPTY) { // Bottom left
            gui.setIndicator(row + 1, col - 1);
            board[row + 1][col - 1] = INDICATOR;
        }

    }

    /**
     * Gives the current player
     *
     * @return the current player
     */
    public int currentPlayer() {
    
        return curPlayer;
        
    }

    /**
     * Gives the current point scores of the players as an array
     *
     * @return the scores of the players as an array on NUMPLAYER length
     */
    public int[] getPoints() { 
    
        int[] newPoints = new int[NUMPLAYER]; // The point array to be returned
        for (int i = 0; i < NUMPLAYER; i++) { 
            newPoints[i] = points[i]; // Create the new array
        } 
        return newPoints;
        
    }

    /**
     * Gives the current state of the board
     *
     * @return the state of the board
     */
    public int[][] getBoard() {
    
        int[][] newBoard = new int[NUMROW][NUMCOL]; // The board array to be returned
        for (int i = 0; i < NUMROW; i++) { 
            for (int j = 0; j < NUMCOL; j++) { 
                newBoard[i][j] = board[i][j]; // Create the new board
            }
        } 
        return newBoard;
        
    }

    /**
     * Runs the logic that should run after a slot is clicked
     *  
     * @param  row  the row the clicked slot is on
     * @param  col  the column the clicked slot is on
     * @return the player who plays after
     */
    public void play(int[] move) {

        if (validMove(move[0], move[1])) { // If the move is valid,
          updateBoard(move[0], move[1]); // update the board appropriately
          setNextPlayer(); // give the turn to the next player
          updateMatchScore(); // update the match score appropriately
        }

    }
    
}