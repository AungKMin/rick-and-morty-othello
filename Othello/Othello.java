/**
* Othello.java
*
* This class represents a Othello (TM)
* game, which allows two players to place
* pieces onto a board.  Each move can 
* result in outflanking 0 or more opponent's 
* piece.
*/

   public class Othello {
   
   	/* constants */   
  		final int MAXGAME;  	  // the number of games a player needs to win to win the match  
		final int NUMPLAYER;   // number of players in the game
      final int NUMROW;		  // number of rows in the game board
      final int NUMCOL;	 	  // number of columns in the game board
      final int EMPTY = -1;  // represents an empty square on the game board 	
      final int PLAYER1 = 0; // identification of player 1
      final int PLAYER2 = 1; // identification of player 2 
      final int NUMINITIALCORDS;
      final int[] INITIALROWCORDS;
      final int[] INITIALCOLCORDS;
      final int[] INITIALPLAYERCORDS;
   
      OthelloGUI gui;
      int numMove;
      int curPlayer;
      int board[][];
      int score[];
      int points[];
   
      /**
      * Constructor:  Othello
      */
      public Othello(OthelloGUI gui) {
         this.gui = gui;
         NUMPLAYER = gui.NUMPLAYER;
         NUMROW = gui.NUMROW;
         NUMCOL = gui.NUMCOL;
         MAXGAME = gui.MAXGAME;
      
         // TO DO:  creation of arrays, and initialization of variables should be added here
         board = new int[NUMROW][NUMCOL];
         // Create the arrays containing information about the initial set up
         NUMINITIALCORDS = 4;
         INITIALROWCORDS = new int[NUMINITIALCORDS];
         INITIALROWCORDS[0] = 3;
         INITIALROWCORDS[1] = 4;
         INITIALROWCORDS[2] = 3;
         INITIALROWCORDS[3] = 4;
         INITIALCOLCORDS = new int[NUMINITIALCORDS];
         INITIALCOLCORDS[0] = 3;
         INITIALCOLCORDS[1] = 3;
         INITIALCOLCORDS[2] = 4;
         INITIALCOLCORDS[3] = 4;
         INITIALPLAYERCORDS = new int[NUMINITIALCORDS];
         INITIALPLAYERCORDS[0] = PLAYER2;
         INITIALPLAYERCORDS[1] = PLAYER1;
         INITIALPLAYERCORDS[2] = PLAYER1;
         INITIALPLAYERCORDS[3] = PLAYER2;
         
         // Create the match score and points arrays
         score = new int[NUMPLAYER];
         points = new int[NUMPLAYER];
         
         newMatch();
      }
      
      /**
      * newMatch
      * Create a new match: reset match scores, player points, turn, and board.
      */
      private void newMatch() { 
         for (int i = 0; i < NUMPLAYER; i++) { 
            score[i] = 0;
         }
         newGame();
      }
      
      /**
      * newGame 
      * Create a new game: reset player points, turn, and board.
      */
      private void newGame () { 
          curPlayer = PLAYER1;
          for (int i = 0; i < NUMPLAYER; i++) { 
            points[i] = 0;
          }    
          // Set the points for each player based on the initial board placement.    
          for (int i = 0; i < NUMINITIALCORDS; i++) { 
            points[INITIALPLAYERCORDS[i]]++;
          }  
          initBoard();
      }
      
      /**
      * initBoard 
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
         // Set up the four initial pieces
         for (int i = 0; i < NUMINITIALCORDS; i++) { 
            board[INITIALROWCORDS[i]][INITIALCOLCORDS[i]] = INITIALPLAYERCORDS[i];
            gui.setPiece(INITIALROWCORDS[i], INITIALCOLCORDS[i], INITIALPLAYERCORDS[i]);
         }           
      }
      
      /**
      * validMove 
      * Return whether a move is valid. If not, display a textbox saying this.
      * 
      * @param row the row number
      * @param col the column number
      */
      private boolean validMove (int row, int col) { 
         if (board[row][col] != EMPTY) { 
            gui.showInvalidMoveMessage();
            return false;
         }
         boolean top = row > 0;
         boolean bot = row < NUMROW - 1;
         boolean left = col > 0;
         boolean right = col < NUMCOL - 1;
         // Check if there is a piece somewhere around the clicked slot
         if (top && board[row - 1][col] != EMPTY) { 
            return true;
         }
         if (bot && board[row + 1][col] != EMPTY) { 
            return true;
         }
         if (left && board[row][col - 1] != EMPTY) { 
            return true;
         }
         if (right && board[row][col + 1] != EMPTY) {
            return true;
         }
         // Top left
         if ((top && left) && board[row - 1][col - 1] != EMPTY) { 
            return true;
         }
         // Top right
         if ((top && right) && board[row - 1][col + 1] != EMPTY) { 
            return true;
         }
         // Bottom right
         if ((bot && right) && board[row + 1][col + 1] != EMPTY) { 
            return true;
         }
         // Bottom left
         if ((bot && left) && board[row + 1][col - 1] != EMPTY) { 
            return true;
         }
         gui.showInvalidMoveMessage();
         return false;
      }     
      
      /**
      * updateBoard 
      * Update the board graphically and in the array.
      * 
      * @param row the row number
      * @param col the column number
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
            updateMatchScore();
            setNextPlayer();
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