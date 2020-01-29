/* 
 * AI.java
 * 
 * This class contains the methods to make the game bot work. 
 * The bot uses the minimax search algorithm with alpha-beta pruning with a custom evaluator to find the best move. 
 * The evaluation of each position are based on: 
 * how many more pieces the player has over the opponent and
 * how favorable the player's piece positions are compared with the opponent
 */

import java.util.*;

public class AI {

    private final static int NUMPLAYER = 2; // Number of players
    private final static int NUMROW = 8; // Number of rows on the board
    private final static int NUMCOL = 8; // Number of columns on the board
    private final static int AREA = NUMROW*NUMCOL; // Area of the board

    private final static int INDICATOR = -2; // Represents valid-move indicator
    private final static int EMPTY = -1; // Represents an empty slot
    private final static int PLAYER1 = 0; // Represents player 1
    private final static int PLAYER2 = 1; // Represents player 2

    static int nodesExplored = 0; // Keeps track of nodes explored for testing purposes
    static int maximizingPlayer; // The player to make the move
    static int nonMaximizingPlayer; // The other player

    /*
     * Calculates the best move and returns it 
     * Calls minimax to evaluate every possible child position
     * 
     * @param  node  the current state of the board
     * @param  player  the current player
     * @param  depth  the depth of the minimax algorithm
     * @return coordinates of the best move
     */
    public static int[] makeMove(int[][] node, int player, int depth) {
       
    	nodesExplored = 0;

    	  // Let the player to play be the maximizing player and the other player the minimizing player
        maximizingPlayer = player;
        nonMaximizingPlayer = (player + 1) % NUMPLAYER;

        // The worst evaluation possible
        int bestEval = Integer.MIN_VALUE;
        // The best move to be returned
        int[] bestMove = null;

        // Evaluate each child position and pick the best position
        for (int[] move : allPossibleMoves(node, player)) { // For every child of the position, 
            int[][] newNode = boardAfterMove(node, player, move); // Get the board position of the child

            int childEval = minimax(newNode, depth - 1, Integer.MIN_VALUE, Integer.MAX_VALUE, false); // Call on minimax to evaluate the child's value
            if (childEval > bestEval) { // If the child value beats the best value, 
                bestEval = childEval; // It is now the best value
                bestMove = move;
            }
        }

        System.out.println("Nodes Explored: " + nodesExplored);
        return bestMove;
    }

    /*
     * The minimax function that will be caleld recursively
     * 
     * @param  node  the position of the board representing the node
     * @param  depth  the depth at which the node is at
     * @param  alpha  the best possible value for the maximizing player so far (alpha value)
     * @param  beta  the best possible value for the minimizing player so far (beta value)
     * @param  max  whether the node is the maximizing player's node
     */
    private static int minimax(int[][] node, int depth, int alpha, int beta, boolean max) {

        nodesExplored++;

        // If the function call reached the bottom of the tree, return a static evaluation
        if (depth == 0) {
            return evaluatePosition(node);
        }

        int bestEval; // The evaluation to return

        if (max) { // If the node is the maximizing player's node, 
            bestEval = Integer.MIN_VALUE; // set to the worst possible value
            for (int[] move : allPossibleMoves(node, maximizingPlayer)) { // for all child nodes, 
                int[][] newNode = boardAfterMove(node, maximizingPlayer, move); // find the corresponding board position of the node
                int childEval = minimax(newNode, depth - 1, alpha, beta, false); // recursive call to find the values of the child nodees
                bestEval = Math.max(bestEval, childEval); // update the best evaluation
                alpha = Math.max(alpha, bestEval); // update the alpha value 
                if (beta <= alpha) { // there has been a guranteed more-ideal or equally-ideal path
                    break;
                }
            }
        } else { // If the node is the minimizing player's node, 
            bestEval = Integer.MAX_VALUE; 
            for (int[] move : allPossibleMoves(node, nonMaximizingPlayer)) {
                int[][] newNode = boardAfterMove(node, nonMaximizingPlayer, move);
                int childEval = minimax(newNode, depth - 1, alpha, beta, true);
                bestEval = Math.min(bestEval, childEval);
                beta = Math.min(beta, bestEval);
                if (beta <= alpha) {
                    break;
                }
            }
        }

        return bestEval;

    }

    /*
     * Evaluate how good a position is: bigger means better for the maximizing player
     * 
     * @param  board  the state of the board
     * @return the evaluation score of the board position
     */
    private static int evaluatePosition(int[][] board) {

        int pieceDifference = evaluatePieceDifference(board); // The difference in how many pieces each player has
        int specialPieceDifference = evaluateSpecialPieceDifference(board); // The difference in the additional values of each piece 

        int evaluation = pieceDifference + specialPieceDifference; // The overall evaluation

        return evaluation;

    }

    /*
	 * Evaluate how many pieces the maximizing player has over the minimizing player
	 * 
	 * @param  board  the state of the board
	 * @return the number of pieces the maximizing player has over the minimizing player
     */
    private static int evaluatePieceDifference(int[][] board) {

        int maximizingPlayerPieces = 0; // Number of maximizing-player pieces
        int nonMaximizingPlayerPieces = 0; // Number of non-maximizing-player pieces

        for (int i = 0; i < NUMROW; i++) { 
            for (int j = 0; j < NUMCOL; j++) {
                if (board[i][j] == maximizingPlayer) {
                    maximizingPlayerPieces++;
                } else if (board[i][j] == nonMaximizingPlayer) {
                    nonMaximizingPlayerPieces++;
                }
            }
        }

        return maximizingPlayerPieces - nonMaximizingPlayerPieces;

    }

    /* 
     * Evaluate the difference in additional values of special pieces the maximizing player has over the minimizing player
     * @param  board  the state of the board
     * @return how much the maximizing player is favored by the additional values of pieces
     */ 
    private static int evaluateSpecialPieceDifference(int[][] board) { 

    	int maximizingPlayerSpecial = 0; // Sum of additional values of maximizing-player pieces
    	int nonMaximizingPlayerSpecial = 0; // Sum of additional values of non-maximizing-player pieces

    	// (Additional) values associated with positions
    	int[][] valueBoard = { 
    		{15, -2,  2,  1,  1,  2, -2, 15},
    		{-2, -4, -1, -1, -1, -1, -4, -2},
    		{2 , -1,  2,  0,  0,  2, -1,  2},
    		{1,  -1,  0,  0,  0,  0, -1,  1},
    		{1,  -1,  0,  0,  0,  0, -1,  1},    		
    		{2 , -1,  2,  0,  0,  2, -1,  2},
    		{-2, -4, -1, -1, -1, -1, -4, -2},
    		{15, -2,  2,  1,  1,  2, -2, 15},
    		};

    	// If a certain corner is already taken, the positions around it lose their additional values
    	if (board[0][0] == maximizingPlayer || board[0][0] == nonMaximizingPlayer) { 
             // Set the first row except the corner piece to 0
             for (int i = 1; i < 3; i++) { 
               valueBoard[0][i] = 0;
             }
             for (int i = 1; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    valueBoard[i][j] = 0;
                }
            }   		
    	}

    	if (board[0][7] == maximizingPlayer || board[0][7] == nonMaximizingPlayer) { 
             // Set the first row except the corner piece to 0
             for (int i = 5; i < 7; i++) { 
               valueBoard[0][i] = 0;
             }
             for (int i = 1; i < 3; i++) {
                for (int j = 5; j < 8; j++) {
                    valueBoard[i][j] = 0;
                }
            }   		
    	}

    	if (board[7][0] == maximizingPlayer || board[7][0] == nonMaximizingPlayer) { 
             // Set the last row except the corner piece to 0
             for (int i = 1; i < 3; i++) { 
                valueBoard[7][i] = 0;
             }
             for (int i = 5; i < 7; i++) {
                for (int j = 0; j < 3; j++) {
                    valueBoard[i][j] = 0;
                }
            }   		
    	}

    	if (board[7][7] == maximizingPlayer || board[7][7] == nonMaximizingPlayer) { 
             // Set the last row except the corner piece to 0
             for (int i = 5; i < 7; i++) { 
                valueBoard[7][i] = 0;
             }
             for (int i = 5; i < 7; i++) {
                for (int j = 5; j < 8; j++) {
                    valueBoard[i][j] = 0;
                }
            }   		
    	}

    	for (int i = 0; i < NUMROW; i++) { 
    		for (int j = 0; j < NUMCOL; j++) { 
    			if (board[i][j] == maximizingPlayer) { 
    				maximizingPlayerSpecial += valueBoard[i][j];
    			} else if (board[i][j] == nonMaximizingPlayer) { 
    				nonMaximizingPlayerSpecial += valueBoard[i][j];
    			}
    		}
    	}

    	return maximizingPlayerSpecial - nonMaximizingPlayerSpecial;

    }

    /*
     * Calculates all possible moves from a position
     *
     * @param  board  the current position
     * @param  player  the current player
     * @return an array list of coordinates of all possible moves
     */
    private static ArrayList < int[] > allPossibleMoves(int[][] board, int player) {

    	// All valid moves from the position
        ArrayList < int[] > moves = new ArrayList < int[] > ();

        for (int i = 0; i < NUMROW; i++) {
            for (int j = 0; j < NUMCOL; j++) {
                if (validMove(board, i, j)) { // if the move is valid,
                    moves.add(new int[] {i, j}); // add it to the array of valid moves
                }
            }
        }

        return moves;

    }

   /*
	* Determine whether a move is valid in the board position given
	* 
	* @param  board  the current position of the board
	* @param  row  the row the desired move is on
	* @param  col  the column the desired column is on
	*/
    private static boolean validMove(int[][] board, int row, int col) {

        if (board[row][col] == INDICATOR) { // If the move is on an indicator
            return true; // the move is valid
        } else { // else, it is invalid
            return false;
        }

    }

   /* 
    * Determine the board position after a move is made on the current board position
    * 
    * @param  board  the current position of the board
    * @param  curPlayer  the player that made the move
    * @param  move  the move to be made
    * @return the new state of the board
    */
    private static int[][] boardAfterMove(int[][] board, int curPlayer, int[] move) {

    	// Set the variables for the coordinates
    	int row = move[0];
    	int col = move[1];

        // Set enemy player
        int enemy = (curPlayer + 1) % NUMPLAYER;

        // Make a copy of the passed-in board
        int[][] newBoard = new int[NUMROW][NUMCOL];
        for (int i = 0; i < NUMROW; i++) {
        	for (int j = 0; j < NUMCOL; j++) {
        		newBoard[i][j] = board[i][j];
        	}
        }
        board = newBoard;

        // Place a player piece on the move coordinates
        board[row][col] = curPlayer;

        // Flanks: 
        // Go right
        int counter = col + 1;
        while (counter < NUMCOL && board[row][counter] == enemy) { // Determine the coordinate at the end of the continuous line of enemy pieces
            counter += 1;
        }
        if (counter < NUMCOL && board[row][counter] == curPlayer) { // Check if the end is a player piece
            for (int i = col + 1; i < counter; i++) { // If so, flip the enemies in the line
                board[row][i] = curPlayer;
            }
        }

        // Go left
        counter = col - 1;
        while (counter >= 0 && board[row][counter] == enemy) {
            counter -= 1;
        }
        if (counter >= 0 && board[row][counter] == curPlayer) {
            for (int i = col - 1; i >= counter; i--) {
                board[row][i] = curPlayer;
            }
        }

        // Go up
        counter = row + 1;
        while (counter < NUMROW && board[counter][col] == enemy) { // Determine the coordinate at the end of the continuous line of enemy pieces
            counter += 1;
        }
        if (counter < NUMROW && board[counter][col] == curPlayer) { // Check if the end is a player piece
            for (int i = row + 1; i < counter; i++) { // If so, flip the enemies in the line
                board[i][col] = curPlayer;
            }
        }

        // Go down
        counter = row - 1;
        while (counter >= 0 && board[counter][col] == enemy) {
            counter -= 1;
        }
        if (counter >= 0 && board[counter][col] == curPlayer) {
            for (int i = row - 1; i >= counter; i--) {
                board[i][col] = curPlayer;
            }
        }

        int numEnemies; // number of enemy pieces counted 

        // Go top left
        //    Coordinates of the slot at the end of the line of enemies
        int vertiCounter = row - 1;
        int horiCounter = col - 1;
        //    Find the longest line of enemies
        while (vertiCounter >= 0 && horiCounter >= 0 && board[vertiCounter][horiCounter] == enemy) { // Determine the coordinates at the end of the continuous line of enemy pieces 
            vertiCounter -= 1;
            horiCounter -= 1;
        }
        numEnemies = row - vertiCounter - 1; // Number of enemy pieces counted (the number of pieces between the end and the beginning of the line)  
        //    Outflank the line of enemies if surrounded          
        if (vertiCounter >= 0 && horiCounter >= 0 && board[vertiCounter][horiCounter] == curPlayer) { // Check if the end is a player piece 
            for (int i = 1; i <= numEnemies; i++) { // If so, flip the enemy pieces in the line
                board[row - i][col - i] = curPlayer;
            }
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
                board[row - i][col + i] = curPlayer;
            }
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
                board[row + i][col - i] = curPlayer;
            }
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
                board[row + i][col + i] = curPlayer;
            }
        }

        // Indicators:

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
            board[row - 1][col] = INDICATOR;
        }
        if (bot && board[row + 1][col] == EMPTY) { // Below
            board[row + 1][col] = INDICATOR;
        }
        if (left && board[row][col - 1] == EMPTY) { // Left
            board[row][col - 1] = INDICATOR;
        }
        if (right && board[row][col + 1] == EMPTY) { // Right
            board[row][col + 1] = INDICATOR;
        }
        if ((top && left) && board[row - 1][col - 1] == EMPTY) { // Top left
            board[row - 1][col - 1] = INDICATOR;
        }
        if ((top && right) && board[row - 1][col + 1] == EMPTY) { // Top right
            board[row - 1][col + 1] = INDICATOR;
        }
        if ((bot && right) && board[row + 1][col + 1] == EMPTY) { // Bottom right
            board[row + 1][col + 1] = INDICATOR;
        }
        if ((bot && left) && board[row + 1][col - 1] == EMPTY) { // Bottom left
            board[row + 1][col - 1] = INDICATOR;
        }

        return board;
        
    }
    
}





  
