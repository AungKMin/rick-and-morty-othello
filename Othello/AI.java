/* 
 * AI.java
 * 
 * Student Author: Aung Khant Min
 * Teacher: Ms. Lam
 * Class: ICS3U1
 * Date: Januray 16, 2019
 * Description: 
 * This class contains the methods to make the game bot work 
 * The bot uses the minimax search algorithm with alpha-beta pruning. 
 * The evaluation of each position are based on: 
 */

import java.util.*;

public class AI {

    private static int NUMPLAYER = 2;
    private static int NUMROW = 8;
    private static int NUMCOL = 8;
    private static int AREA = NUMROW*NUMCOL;

    private static int INDICATOR = -2;
    private static int EMPTY = -1;
    private static int PLAYER1 = 0;
    private static int PLAYER2 = 1;

    static int nodesExplored = 0;
    static int maximizingPlayer; // The player to make the move
    static int nonMaximizingPlayer; // The other player

    /*
     * Calculates the best move and returns it
     * 
     * @param  player  the current player
     * @param  depth  the depth of the minimax algorithm
     * @return coordinates of the best move
     */
    public static int[] makeMove(int[][] node, int player, int depth) {

    	nodesExplored = 0;
        int bestEval = Integer.MIN_VALUE;
        int[] bestMove = null;
        maximizingPlayer = player;
        nonMaximizingPlayer = (player + 1) % NUMPLAYER;

        for (int[] move: allPossibleMoves(node, player)) { // For every child of the position, 
            int[][] newNode = boardAfterMove(node, player, move);

            int childEval = minimax(newNode, depth - 1, Integer.MIN_VALUE, Integer.MAX_VALUE, false); // The current player is the maximizing player
            if (childEval > bestEval) {
                bestEval = childEval;
                bestMove = move;
            }
        }

        System.out.println("Nodes Explored: " + nodesExplored);
        return bestMove;
    }

    /*
     * Uses minimax with alpha-beta pruning to find the best move
     * 
     * 
     */
    private static int minimax(int[][] node, int depth, int alpha, int beta, boolean max) {

        nodesExplored++;

        if (depth == 0) {
            return evaluatePosition(node);
        }

        int bestEval;
        if (max) {
            bestEval = Integer.MIN_VALUE;
            for (int[] move : allPossibleMoves(node, maximizingPlayer)) {
                int[][] newNode = boardAfterMove(node, maximizingPlayer, move);
                int childEval = minimax(newNode, depth - 1, alpha, beta, false);
                bestEval = Math.max(bestEval, childEval);
                alpha = Math.max(alpha, bestEval);
                if (beta <= alpha) {
                    break;
                }
            }
        } else {
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
     * Evaluate the score of a position: the current player is the maximizing player
     * 
     * @param  board  the state of the board
     * @param  player  the player who made the move
     * @return the evaluation score of the board position
     */
    private static int evaluatePosition(int[][] board) {

        int pieceDifference = evaluatePieceDifference(board);

        int evaluation = pieceDifference;

        return evaluation;

    }

    /*
	 * Evaluate the difference in player points
	 * @param  board  the state of the board
	 * @return the number of pieces the maximizing player has over the minimizing player
     */
    private static int evaluatePieceDifference(int[][] board) {

        int maximizingPlayerPieces = 0;
        int nonMaximizingPlayerPieces = 0;
        for (int i = 0; i < NUMROW; i++) {
            for (int j = 0; j < NUMCOL; j++) {
                if (board[i][j] == maximizingPlayer) {
                    maximizingPlayerPieces++;
                } else if (board[i][j] == nonMaximizingPlayer) {
                    nonMaximizingPlayerPieces++;
                }
            }
        }
        //System.out.println("Piece difference: " + maximizingPlayerPieces + " - " + nonMaximizingPlayerPieces);
        return maximizingPlayerPieces - nonMaximizingPlayerPieces;

    }

    /* 
     * Evaluate the difference in values of special pieces the maximizing player has over the minimizing player
     */ 
    private static int evaluateSpecialPieceDifference(int[][] board) { 

    	

    }

    /*
     * Calculates all possible moves from a position
     *
     * @param  board  the current position
     * @param  player  the current player
     * @return an array of coordinates of all possible moves
     */
    private static ArrayList < int[] > allPossibleMoves(int[][] board, int player) {

        ArrayList < int[] > moves = new ArrayList < int[] > ();

        for (int i = 0; i < NUMROW; i++) {
            for (int j = 0; j < NUMCOL; j++) {
                if (validMove(board, i, j)) {
                    moves.add(new int[] {i, j});
                }
            }
        }

        return moves;

    }


    private static boolean validMove(int[][] board, int row, int col) {

        if (board[row][col] == INDICATOR) { // If the move is on an indicator
            return true; // the move is valid
        } else { // else, it is invalid
            return false;
        }

    }

    private static int[][] boardAfterMove(int[][] board, int curPlayer, int[] move) {

    	// Set the variables for the coordinates
    	int row = move[0];
    	int col = move[1];

        // Set enemy player
        int enemy = (curPlayer + 1) % NUMPLAYER;


        int[][] newBoard = new int[NUMROW][NUMCOL];
        for (int i = 0; i < NUMROW; i++) {
        	for (int j = 0; j < NUMCOL; j++) {
        		newBoard[i][j] = board[i][j];
        	}
        }
        board = newBoard;

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

        // Go top left
        //    Coordinates of the slot at the end of the line of enemies
        int vertiCounter = row - 1;
        int horiCounter = col - 1;
        //    Find the longest line of enemies
        while (vertiCounter >= 0 && horiCounter >= 0 && board[vertiCounter][horiCounter] == enemy) { // Determine the coordinates at the end of the continuous line of enemy pieces 
            vertiCounter -= 1;
            horiCounter -= 1;
        }
        int temp = row - vertiCounter - 1; // Number of enemy pieces counted (the number of pieces between the end and the beginning of the line)  
        //    Outflank the line of enemies if surrounded          
        if (vertiCounter >= 0 && horiCounter >= 0 && board[vertiCounter][horiCounter] == curPlayer) { // Check if the end is a player piece 
            for (int i = 1; i <= temp; i++) { // If so, flip the enemy pieces in the line
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
        temp = row - vertiCounter - 1;
        //    Outflank the line of enemies if surrounded
        if (vertiCounter >= 0 && horiCounter < NUMCOL && board[vertiCounter][horiCounter] == curPlayer) {
            for (int i = 1; i <= temp; i++) {
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
        temp = vertiCounter - row - 1;
        //    Outflank the line of enemies if surrounded
        if (vertiCounter < NUMROW && horiCounter >= 0 && board[vertiCounter][horiCounter] == curPlayer) {
            for (int i = 1; i <= temp; i++) {
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
        temp = vertiCounter - row - 1;
        //    Outflank the line of enemies if surrounded
        if (vertiCounter < NUMROW && horiCounter < NUMCOL && board[vertiCounter][horiCounter] == curPlayer) {
            for (int i = 1; i <= temp; i++) {
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





  