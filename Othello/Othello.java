/* 
* Othello.java
* 
* Student Author: Aung Khant Min
* Teacher: Ms. Lam
* Class: ICS3U1
* Date: Januray 16, 2019
* Description: 
* This class represents a Othello game. 
* The rules play like usual except for one change:
* a valid move is any slot adjacent to an existing piece.
*/

public class Othello {

	/* constants */   
   final int MAXGAME;  	            // the number of games a player needs to win to win the match  
   final int NUMPLAYER;             // number of players in the game
   final int NUMROW;		            // number of rows in the game board
   final int NUMCOL;	 	            // number of columns in the game board
   final int EMPTY = -1;            // represents an empty square on the game board 	
   final int PLAYER1 = 0;           // identification of player 1
   final int PLAYER2 = 1;           // identification of player 2 
   final int NUMINITIALCORDS;       // number of initial pieces
   final int[] INITIALROWCORDS;     // row coordinates (y-coordinate) of the starting points
   final int[] INITIALCOLCORDS;     // column coordinates (x-coordinate) of the start 
   final int[] INITIALPLAYERCORDS;  // corresponding player of the starting points

   OthelloGUI gui;                  // user interface object
   int numMove;                     // number of moves since the beginning of the game - unused so far
   int curPlayer;                   // the current player
   int board[][];                   // 2D representation of the game board
   int score[];                     // the match scores or the number of games each player has won
   int points[];                    // the points or the number of pieces each player has on the board

   /**
    * Constructor: initializes the gui object, the number of games in a match, 
    * the number of players, the dimensions of the board, a representation of the board, 
    * and the coordinates of the initial points
    * 
    * @param  gui  the gui that will be the graphical interface of the game
    */
   public Othello(OthelloGUI gui) {
   
      this.gui = gui;
      NUMPLAYER = gui.NUMPLAYER;
      NUMROW = gui.NUMROW;
      NUMCOL = gui.NUMCOL;
      MAXGAME = gui.MAXGAME;
      
      board = new int[NUMROW][NUMCOL];
      
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
      
      score = new int[NUMPLAYER];
      points = new int[NUMPLAYER];
      
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
   private void newGame () { 
   
      // Set the player turn to player 1
      curPlayer = PLAYER1;
      gui.setNextPlayer(PLAYER1);
       
      // Set the points to 0
      for (int i = 0; i < NUMPLAYER; i++) { 
         points[i] = 0;
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
   private void initBoard () {
    
      // Make every slot empty
      for (int i = 0; i < NUMROW; i++) { 
         for (int j = 0; j < NUMCOL; j++) {
            board[i][j] = EMPTY; 
         }
      }
      gui.resetGameBoard();

      // Set up the initial pieces
      for (int i = 0; i < NUMINITIALCORDS; i++) { 
         board[INITIALROWCORDS[i]][INITIALCOLCORDS[i]] = INITIALPLAYERCORDS[i];
         gui.setPiece(INITIALROWCORDS[i], INITIALCOLCORDS[i], INITIALPLAYERCORDS[i]);
      }
      
      /* The tie tester   
      board[0][0] = PLAYER2;
      gui.setPiece(0, 0, PLAYER2);
      for (int i = 1; i < NUMCOL; i++) {    
         board[0][i] = PLAYER1;
         gui.setPiece(0, i, PLAYER1);
      }
      for (int i = 1; i < NUMROW; i++) { 
         board[i][0] = PLAYER1;
         gui.setPiece(i, 0, PLAYER1);
      }
      for (int i = 1; i < NUMCOL - 1; i++) { 
         board[NUMROW - 1][i] = PLAYER1;
         gui.setPiece(NUMROW - 1, i, PLAYER1);
      }
      for (int i = 1; i < NUMROW - 1; i++) {
         board[i][NUMCOL - 1] = PLAYER1; 
         gui.setPiece(i, NUMCOL - 1, PLAYER1);
      }
      for (int i = 1; i < NUMROW - 2; i++ ) {
         for (int j = 1; j < NUMCOL - 1; j++) { 
            board[i][j] = PLAYER2;
            gui.setPiece(i, j, PLAYER2);
         }
      }
      for (int i = 1; i < 6; i++) { 
         board[NUMROW - 2][i] = PLAYER1;
         gui.setPiece(NUMROW - 2, i, PLAYER1);
      } 
      for (int i = 6; i < NUMCOL - 1; i++) {
         board[NUMROW - 2][i] = PLAYER2;
         gui.setPiece(NUMROW - 2, i, PLAYER2);          
      }
      
      points[PLAYER1] = 31;
      points[PLAYER2] = 32;
      */
   }
   
   /**
    * Return whether a move is valid. If not, display an invalid-move textbox
    * 
    * @param  row  the row number
    * @param  col  the column number
    */
   private boolean validMove (int row, int col) { 
      
      // Check whether the slot is occupied
      if (board[row][col] != EMPTY) { 
         gui.showInvalidMoveMessage();
         return false;
      }
      
      // Does the slot have a row above it?
      boolean top = row > 0;
      // below?
      boolean bot = row < NUMROW - 1;
      // Does the slot have a column to its left?
      boolean left = col > 0;
      // right?
      boolean right = col < NUMCOL - 1;
      
      // Check if there is an existing piece around the slot
      if (top && board[row - 1][col] != EMPTY) {                  // Above
         return true;
      }
      if (bot && board[row + 1][col] != EMPTY) {                  // Below
         return true;
      }
      if (left && board[row][col - 1] != EMPTY) {                 // Left
         return true;
      }
      if (right && board[row][col + 1] != EMPTY) {                // Right
         return true;
      }
      if ((top && left) && board[row - 1][col - 1] != EMPTY) {    // Top left
         return true;
      }
      if ((top && right) && board[row - 1][col + 1] != EMPTY) {   // Top right
         return true;
      }
      if ((bot && right) && board[row + 1][col + 1] != EMPTY) {   // Bottom right
         return true;
      }
      if ((bot && left) && board[row + 1][col - 1] != EMPTY) {    // Bottom left
         return true;
      }
      
      // The slot has no pieces around it, invalid 
      gui.showInvalidMoveMessage();
      return false;
      
   }     
   
   /**
    * Update the board graphically and in the array.
    * 
    * @param  row  the row number
    * @param  col  the column number
    */
   private void updateBoard (int row, int col) {
   
      board[row][col] = curPlayer;
      gui.setPiece(row, col, curPlayer);  
      flank(row, col);     
      
   }      
   
   /**
   * flank 
   * Outflank any appropriate enemy pieces. 
   * Update the board graphically and in the array. 
   * Display a textbox saying how many pieces were outflanked.
   * 
   * @param row the row number
   * @param col the column number
   * @return the number of peices outflanked.
   */  
   private int flank (int row, int col) { 
      // Number of total flanks
      int numFlanks = flankHori(row, col) + flankVerti(row, col) + flankDiag(row, col);
      if (numFlanks > 0) { 
         gui.showOutflankMessage(curPlayer, numFlanks);
         // Update the number of points the player has
      }
      updateScore(numFlanks);
      return numFlanks;
      
   }
   
   /**
   * flankHori
   * Outflank any appropriate enemy pieces in the row. 
   * Update the board graphically and in the array. 
   * 
   * @param row the row number
   * @param col the column number
   * @return the number of peices outflanked in the row.
   */  
   private int flankHori (int row, int col) { 
      int numFlanks = 0;
      int enemy;
      if (curPlayer == PLAYER1) { 
         enemy = PLAYER2;
      } else { 
         enemy = PLAYER1;
      }
      // Go right
      int counter = col + 1;
      while (counter < NUMCOL && board[row][counter] == enemy) { 
         counter += 1;
      }
      if (counter < NUMCOL && board[row][counter] == curPlayer) { 
         for (int i = col + 1; i < counter; i++) {
            gui.setPiece(row, i, curPlayer);
            board[row][i] = curPlayer; 
         }
         numFlanks += counter - col - 1; 
      }
   
      // Go left
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
   * flankVerti 
   * Outflank any appropriate enemy pieces in the column. 
   * Update the board graphically and in the array. 
   * 
   * @param row the row number
   * @param col the column number
   * @return the number of peices outflanked in the column.
   */  
   private int flankVerti (int row, int col) { 
      int numFlanks = 0;
      int enemy;
      if (curPlayer == PLAYER1) { 
         enemy = PLAYER2;
      } else { 
         enemy = PLAYER1;
      }
      // Go up
      int counter = row + 1;
      while (counter < NUMROW && board[counter][col] == enemy) { 
         counter += 1;
      }
      if (counter < NUMROW && board[counter][col] == curPlayer) { 
         for (int i = row + 1; i < counter; i++) {
            gui.setPiece(i, col, curPlayer);
            board[i][col] = curPlayer; 
         }
         numFlanks += counter - row - 1; 
      }
   
      // Go down
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
   * flankDiag 
   * Outflank any appropriate enemy pieces in the one or two diagonals. 
   * Update the board graphically and in the array. 
   * 
   * @param row the row number
   * @param col the column number
   * @return the number of peices outflanked in the column.
   */  
   private int flankDiag (int row, int col) { 
      int numFlanks = 0;
      int enemy;
      int temp; // temporary variable that will be added to the number of flanks.
      if (curPlayer == PLAYER1) { 
         enemy = PLAYER2;
      } else { 
         enemy = PLAYER1;
      }
      // Go top left
      int vertiCounter = row - 1;
      int horiCounter = col - 1;
      while (vertiCounter >= 0 && horiCounter >= 0 && board[vertiCounter][horiCounter] == enemy) { 
         vertiCounter -= 1;
         horiCounter -= 1;
      }
      temp = row - vertiCounter - 1; // Number of enemies
      //    Outflank the line of enemies if surrounded
      if (vertiCounter >= 0 && horiCounter >= 0 && board[vertiCounter][horiCounter] == curPlayer) { 
         for (int i = 1; i <= temp; i++) {
            gui.setPiece(row - i, col - i, curPlayer);
            board[row - i][col - i] = curPlayer; 
         }
         numFlanks += temp;
      }
        
      // Go top right
      vertiCounter = row - 1;
      horiCounter = col + 1;
      //    Find the longest line of enemies
      while (vertiCounter >= 0 && horiCounter < NUMCOL && board[vertiCounter][horiCounter] == enemy) { 
         vertiCounter -= 1;
         horiCounter += 1;
      }
      temp = row - vertiCounter - 1; // Number of enemies
      //    Outflank the line of enemies if surrounded
      if (vertiCounter >= 0 && horiCounter < NUMCOL && board[vertiCounter][horiCounter] == curPlayer) { 
         for (int i = 1; i <= temp; i++) {
            gui.setPiece(row - i, col + i, curPlayer);
            board[row - i][col + i] = curPlayer; 
         }
         numFlanks += temp;
      }
      
      // Go bottom left
      vertiCounter = row + 1;
      horiCounter = col - 1;
      //    Find the longest line of enemies
      while (vertiCounter < NUMROW && horiCounter >= 0 && board[vertiCounter][horiCounter] == enemy) { 
         vertiCounter += 1;
         horiCounter -= 1;
      }
      temp = vertiCounter - row - 1; // Number of enemies
      //    Outflank the line of enemies if surrounded
      if (vertiCounter < NUMROW && horiCounter >= 0 && board[vertiCounter][horiCounter] == curPlayer) { 
         for (int i = 1; i <= temp; i++) {
            gui.setPiece(row + i, col - i, curPlayer);
            board[row + i][col - i] = curPlayer; 
         }
         numFlanks += temp;
      }
   
      // Go bottom right
      vertiCounter = row + 1;
      horiCounter = col + 1;
      //    Find the longest line of enemies
      while (vertiCounter < NUMROW && horiCounter < NUMCOL && board[vertiCounter][horiCounter] == enemy) { 
         vertiCounter += 1;
         horiCounter += 1;
      }
      temp = vertiCounter - row - 1; // Number of enemies
      //    Outflank the line of enemies if surrounded
      if (vertiCounter < NUMROW && horiCounter < NUMCOL && board[vertiCounter][horiCounter] == curPlayer) { 
         for (int i = 1; i <= temp; i++) {
            gui.setPiece(row + i, col + i, curPlayer);
            board[row + i][col + i] = curPlayer; 
         }
         numFlanks += temp;
      }
                
      return numFlanks;
   }
   
   /**
   * updateScore 
   * Update the scores. 
   * 
   * @param piecesTurnedOver the number of pieces turned over
   */  
   private void updateScore (int piecesTurnedOver) { 
      // Increase the score of the player by 1 + the number of pieces outflanked
      points[curPlayer] += piecesTurnedOver + 1; 
      if (curPlayer == PLAYER1) { 
         points[PLAYER2] -= piecesTurnedOver;
      } else { 
         points[PLAYER1] -= piecesTurnedOver;
      }
   }
   
   /**
   * updateMatchScore 
   * Update the match score. If the match score changes, i.e. a player won or tied, display a textbox saying this. 
   * If the match is over, display a textbox saying this instead.
   */
   private void updateMatchScore () { 
      // Check if the game is over
      int sumPoints = 0;
      for (int i = 0; i < NUMPLAYER; i++) { 
         sumPoints += points[i];
      }
      if (sumPoints == NUMCOL * NUMROW) { 
         // Find the winner of the game or the players who tied 
         int highestPoints = points[PLAYER1];
         int highestPlayer = PLAYER1;
         int numTies = 0;
         for (int i = PLAYER1 + 1; i < NUMPLAYER; i++) { 
            if (points[i] > highestPoints ) { 
               highestPoints = points[i];
               highestPlayer = i;
            } else if (points[i] == highestPoints) {
               numTies++;
            }
         }
         int[] tiedPlayers;
         // If there were ties, find the tied players.
         if (numTies > 0) { 
            tiedPlayers = new int[numTies + 1];
            int counter = 0;
            for (int i = PLAYER1; i < NUMPLAYER; i++) { 
               if (points[i] == highestPoints) { 
                  tiedPlayers[counter] = i;
                  counter++;
               }
            }              
         }
         //Update the match scores as appropriate
         if (numTies > 0) { 
            gui.showTieGameMessage();
         } else { 
            gui.showWinnerMessage(highestPlayer);
            score[highestPlayer]++;
            gui.setPlayerScore(highestPlayer, score[highestPlayer]);
         }
         // If the player won the match, display it appropriately.
         if (score[highestPlayer] == MAXGAME) { 
            gui.showFinalWinnerMessage(highestPlayer);  
         }        
         newGame();              
      }
   }  
   
   /**
   * setNextPlayer
   * Set the current player variable to the next player. Also change the current player graphically.
   * 
   */  
   private void setNextPlayer() { 
      if (curPlayer == PLAYER1) {
         curPlayer = PLAYER2;
      } else { 
         curPlayer = PLAYER1;
      }
      gui.setNextPlayer(curPlayer);
   }

   /**
   * play
   * This method will be called when a square is clicked.  Parameter "row" and "column" is 
   * the location of the square that is clicked by the user
   */

   public void play (int row, int col) {
    // TO DO:  implement the logic of the game
      if (validMove(row, col)) { 
         updateBoard(row, col);
         setNextPlayer();
         updateMatchScore();
      }
      System.out.println(points[0] + " "  + points[1]);
   } 
   
   public void runnerCode(int fat, int skinny) { 
      for (int i = 0; i < NUMROW; i++) { 
         for (int j = 0; j < NUMCOL; j++) { 
            play(i, j);
         }
      }
   }
   
}