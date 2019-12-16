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
   
      OthelloGUI gui;
      int numMove;
      int curPlayer;
      int board[][];
      int score[];
   
     /**
      * Constructor:  Othello
      */
      public Othello(OthelloGUI gui) {
         this.gui = gui;
         NUMPLAYER = gui.NUMPLAYER;
         NUMROW = gui.NUMROW;
         NUMCOL = gui.NUMCOL;
         MAXGAME = gui.MAXGAME;
         curPlayer = PLAYER1;
      
         // TO DO:  creation of arrays, and initialization of variables should be added here
      }
   
     /**
      * play
      * This method will be called when a square is clicked.  Parameter "row" and "column" is 
      * the location of the square that is clicked by the user
      */
   
      public void play (int row, int column) {
       // TO DO:  implement the logic of the game
         
         gui.setPiece(row, column, curPlayer);
         if (curPlayer == PLAYER1) { 
            curPlayer = PLAYER2;
         } else { 
            curPlayer = PLAYER1;
         }
      } 
   }