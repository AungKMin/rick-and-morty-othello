/**
 * OthelloGUI.java
 *
 * Provide the GUI for the Othello game
 */
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class OthelloGUI {
    // the name of the configuration file
    private final String CONFIGFILE = "config.txt";

    private JLabel[][] slots;
    private JFrame mainFrame;
    private JTextField[] playerScore;
    private JTextField[] playerPoints;
    private ImageIcon[] playerIcon;
    private ImageIcon indicatorIcon;
    private ImageIcon computerIcon;
    private ImageIcon computerIconHard;
    private JLabel nextPlayerIcon;
    private JButton aiPlayerButton;
    private JButton aiPlayerButtonHard;

    private Color background = new Color(20, 20, 20);
    private Color textcolor = new Color(255, 255, 255);

    // name of the file paths of images
    private String logoIcon;
    private String[] iconFile;
    private String indicatorIconFile;
    private String computerIconFile;
    private String computerIconHardFile;

    private final int DEPTH = 2; // The maxium depth that the ai's minimax algorithm will search to
    private final int BIGDEPTH = 4; // The longer search depth for hard

    private Othello game;

    /**
     * Number of players
     */
    public final int NUMPLAYER = 2;

    /**
     * Number of rows on the game board
     */
    public final int NUMROW = 8;

    /**
     * Number of colums on the game board
     */
    public final int NUMCOL = 8;
    
    /**
     * Area of the board
     */
    public final int AREA = NUMROW * NUMCOL;

    /**
     * Number of games needed to be won to win the match
     */
    public int MAXGAME;

    /**
     * Constants defining the demensions of the different components
     * on the GUI
     */
    private final int PIECESIZE = 70;
    private final int PLAYPANEWIDTH = NUMCOL * PIECESIZE;
    private final int PLAYPANEHEIGHT = NUMROW * PIECESIZE;

    private final int INFOPANEWIDTH = 2 * PIECESIZE;
    private final int INFOPANEHEIGHT = PLAYPANEHEIGHT;

    private final int LOGOHEIGHT = 2 * PIECESIZE;
    private final int LOGOWIDTH = PLAYPANEWIDTH + INFOPANEWIDTH;

    private final int FRAMEWIDTH = (int)(LOGOWIDTH * 1.03);
    private final int FRAMEHEIGHT = (int)((LOGOHEIGHT + PLAYPANEHEIGHT) * 1.1);

    /** 
     * Constructor: 
     * intialize variables from config files
     * initialize the imageIcon array
     * initialize the slots array
     * create the main frame
     */ 
    public OthelloGUI() {
    
        initConfig();
        initImageIcon();
        initSlots();
        createMainFrame();

        game = new Othello(this);
        OthelloListener listener = new OthelloListener(game, this);

    }

    /**
     * Initialize the file paths of the images and the number of games in a match
     */ 
    private void initConfig() {

        iconFile = new String[NUMPLAYER]; // The player icons

        try {
            BufferedReader in = new BufferedReader(new FileReader(CONFIGFILE));
            MAXGAME = Integer.parseInt(in.readLine()); // The number of games a player needs to win to win the match
            logoIcon = in.readLine(); // The banner
            for (int i = 0; i < NUMPLAYER; i++) {
                iconFile[i] = in.readLine();
            }
            indicatorIconFile = in.readLine(); // the valid-move indicator image
            computerIconFile = in.readLine(); // the computer's icon
            computerIconHardFile = in.readLine(); // the harder computer's icon
        } catch (IOException iox) {
            System.out.println("Config file not found.");
        }

    }

    /**
     * Initialize playerIcon arrays, indicatorIcon, and computerIcon with graphic files 
     */
    private void initImageIcon() {
        playerIcon = new ImageIcon[NUMPLAYER];
        for (int i = 0; i < NUMPLAYER; i++) {
            playerIcon[i] = new ImageIcon(iconFile[i]);
        }
        indicatorIcon = new ImageIcon(indicatorIconFile);
        computerIcon = new ImageIcon(computerIconFile);
        computerIconHard = new ImageIcon(computerIconHardFile);
    }

    /**
     * Initialize the array of JLabels
     */ 
    private void initSlots() {
        slots = new JLabel[NUMROW][NUMCOL];
        for (int i = 0; i < NUMROW; i++) {
            for (int j = 0; j < NUMCOL; j++) {
                slots[i][j] = new JLabel();
                // slots[i][j].setFont(new Font("SansSerif", Font.BOLD, 18));
                slots[i][j].setPreferredSize(new Dimension(PIECESIZE, PIECESIZE));
                slots[i][j].setHorizontalAlignment(SwingConstants.CENTER);
                slots[i][j].setBorder(new LineBorder(Color.white));
            }
        }
    }

    /**
     * Create play panel
     */
    private JPanel createPlayPanel() {
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(PLAYPANEWIDTH, PLAYPANEHEIGHT));
        panel.setBackground(background);
        panel.setLayout(new GridLayout(NUMROW, NUMCOL));
        for (int i = 0; i < NUMROW; i++) {
            for (int j = 0; j < NUMCOL; j++) {
                panel.add(slots[i][j]);
            }
        }
        return panel;
    }

    /**
     * Create info panel
     */
    private JPanel createInfoPanel() {

        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(INFOPANEWIDTH, INFOPANEHEIGHT));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(background);

        Font headingFont = new Font("Serif", Font.PLAIN, 18);
        Font regularFont = new Font("Serif", Font.BOLD, 16);

        // Create a panel for the scoreboard
        JPanel scorePanel = new JPanel();
        scorePanel.setBackground(background);

        // Create the label to display "SCOREBOARD" heading
        JLabel scoreLabel = new JLabel("SCOREBOARD", JLabel.CENTER);
        scoreLabel.setFont(headingFont);
        scoreLabel.setForeground(textcolor);
        scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Create JLabels for players
        JLabel[] playerLabel = new JLabel[NUMPLAYER];
        for (int i = 0; i < NUMPLAYER; i++) {
            playerLabel[i] = new JLabel(playerIcon[i]);
        }

        // Create the array of textfield for players' score
        playerScore = new JTextField[NUMPLAYER];
        for (int i = 0; i < NUMPLAYER; i++) {
            playerScore[i] = new JTextField();
            playerScore[i].setFont(regularFont);
            playerScore[i].setText("0");
            playerScore[i].setEditable(false);
            playerScore[i].setHorizontalAlignment(JTextField.CENTER);
            playerScore[i].setPreferredSize(new Dimension(INFOPANEWIDTH - PIECESIZE - 50, 30));
        }
        
        // Create the array of textfield for players' points
        playerPoints = new JTextField[NUMPLAYER];
        for (int i = 0; i < NUMPLAYER; i++) {
            playerPoints[i] = new JTextField();
            playerPoints[i].setFont(regularFont);
            playerPoints[i].setText("0");
            playerPoints[i].setEditable(false);
            playerPoints[i].setHorizontalAlignment(JTextField.CENTER);
            playerPoints[i].setPreferredSize(new Dimension(INFOPANEWIDTH - PIECESIZE - 50, 30));
        }      

        scorePanel.add(scoreLabel);
        
        // Create the JPanels for the score panels consisting of the player icon, match score, and the number of pieces of each player
        JPanel[] playerScoreBoards = new JPanel[NUMPLAYER];
        for (int i = 0; i < NUMPLAYER; i++) { 
            playerScoreBoards[i] = new JPanel();
            playerScoreBoards[i].add(playerLabel[i]);
            playerScoreBoards[i].add(playerScore[i]);
            playerScoreBoards[i].add(playerPoints[i]);
            playerScoreBoards[i].setBackground(background);
            scorePanel.add(playerScoreBoards[i]);
        }
       
        JPanel nextPanel = new JPanel();
        nextPanel.setBackground(background);

        // Create the label to display "NEXT TURN" heading
        JLabel nextLabel = new JLabel("NEXT TURN", JLabel.CENTER);
        nextLabel.setFont(headingFont);
        nextLabel.setForeground(textcolor);
        nextLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        //nextLabel.setForeground(Color.white);

        // Create the JLabel for the nextPlayer
        nextPlayerIcon = new JLabel();
        System.out.println(nextPlayerIcon.getAlignmentX());
        nextPlayerIcon.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        nextPlayerIcon.setIcon(playerIcon[0]);

        nextPanel.add(nextLabel);
        nextPanel.add(nextPlayerIcon);

        JPanel aiPanel = new JPanel();
        aiPanel.setBackground(background);

        // Create the JLabel for the Computer heading
        JLabel aiLabel = new JLabel("COMPUTER", JLabel.CENTER);
        aiLabel.setFont(headingFont);
        aiLabel.setForeground(textcolor);
        aiLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Create the JButton to let the AI play for the current player
        aiPlayerButton = new JButton(computerIcon);
        aiPlayerButton.setPreferredSize(new Dimension(PIECESIZE, PIECESIZE));
        // Set up the action handler for the JButton
        aiPlayerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

               // Get the number of pieces on the board
               int[] points = game.getPoints();
               int pointSum = 0;
               for (int i = 0; i < NUMPLAYER; i++) { 
                  pointSum += points[i];
               }

               // Set the depth to the desired depth or the number of empty slots left
               int depth = Math.min(DEPTH, AREA - pointSum);
               // Get the current state of the board
               int[][] board = game.getBoard();
               // Call the ai to make a move and play the move
               game.play(AI.makeMove(game.getBoard(), game.currentPlayer(), depth));

            }
        });
        
        // Harder AI (searches deeper)
        // Create the JButton to let the AI play for the current player
        aiPlayerButtonHard = new JButton(computerIconHard);
        aiPlayerButtonHard.setPreferredSize(new Dimension(PIECESIZE, PIECESIZE));
        // Set up the action handler for the JButton
        aiPlayerButtonHard.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

               // Get the number of pieces on the board
               int[] points = game.getPoints();
               int pointSum = 0;
               for (int i = 0; i < NUMPLAYER; i++) { 
                  pointSum += points[i];
               }

               // Set the depth to the desired depth or the number of empty slots left
               int depth = Math.min(BIGDEPTH, AREA - pointSum);
               // Get the current state of the board
               int[][] board = game.getBoard();
               // Call the ai to make a move and play the move
               game.play(AI.makeMove(game.getBoard(), game.currentPlayer(), depth));

            }
        });

        aiPanel.add(aiLabel);
        aiPanel.add(aiPlayerButton);
        aiPanel.add(aiPlayerButtonHard);

        panel.add(scorePanel);
        panel.add(nextPanel);
        panel.add(aiPanel);

        return panel;

    }

    /**
     * create the main frame
     */
    private void createMainFrame() {

        // Create the main Frame
        mainFrame = new JFrame("Othello");
        JPanel panel = (JPanel) mainFrame.getContentPane();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // Create the panel for the logo
        JPanel logoPane = new JPanel();
        logoPane.setPreferredSize(new Dimension(LOGOWIDTH, LOGOHEIGHT));
        JLabel logo = new JLabel();
        logo.setIcon(new ImageIcon(logoIcon));
        logoPane.add(logo);

        // Create the bottom Panel which contains the play panel and info Panel
        JPanel bottomPane = new JPanel();
        bottomPane.setLayout(new BoxLayout(bottomPane, BoxLayout.X_AXIS));
        bottomPane.setPreferredSize(new Dimension(PLAYPANEWIDTH + INFOPANEWIDTH, PLAYPANEHEIGHT));
        bottomPane.add(createPlayPanel());
        bottomPane.add(createInfoPanel());

        // Add the logo and bottom panel to the main frame
        panel.add(logoPane);
        panel.add(bottomPane);

        mainFrame.setContentPane(panel);
        //   mainFrame.setPreferredSize(new Dimension(FRAMEWIDTH, FRAMEHEIGHT));
        mainFrame.setSize(FRAMEWIDTH, FRAMEHEIGHT);
        mainFrame.setVisible(true);

        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

    /**
     * Returns the row number of where the given JLabel is on
     * 
     * @param  label  the label whose row number to be requested
     * @return the row number
     */
    public int getRow(JLabel label) {

        int result = -1;
        for (int i = 0; i < NUMROW && result == -1; i++) {
            for (int j = 0; j < NUMCOL && result == -1; j++) {
                if (slots[i][j] == label) {
                    result = i;
                }
            }
        }
        return result;

    }

    /**
     * Returns the column number of where the given JLabel is on
     * 
     * @param  label  the label whose column number to be requested
     * @return the column number
     */
    public int getColumn(JLabel label) {

        int result = -1;
        for (int i = 0; i < NUMROW && result == -1; i++) {
            for (int j = 0; j < NUMCOL && result == -1; j++) {
                if (slots[i][j] == label) {
                    result = j;
                }
            }
        }
        return result;

    }

    /**
     * Add the mouse listener 
     */
    public void addListener(OthelloListener listener) {

        for (int i = 0; i < NUMROW; i++) {
            for (int j = 0; j < NUMCOL; j++) {
                slots[i][j].addMouseListener(listener);
            }
        }

    }

    /**
     * Display the specified player icon on the specified slot
     * 
     * @param  row  row of the slot
     * @param  col  column of the slot
     * @param  player  player to be displayed
     */
    public void setPiece(int row, int col, int player) {

        slots[row][col].setIcon(playerIcon[player]);

    }

    /**
     * Display an indicator icon on the specified slot
     * @param  row  the row of the specified slot
     * @param  col  the column of the specified slot
     */
    public void setIndicator(int row, int col) {

        slots[row][col].setIcon(indicatorIcon);
    
    }

    /**
     * Display the score on the textfield of the corresponding player
     * 
     * @param  player  the player whose score to be displayed
     * @param  score  the score to be displayed
     */
    public void setPlayerScore(int player, int score) {

        playerScore[player].setText(score + "");

    }

   /**
    * Display the points on the textfield of the corresponding player
    * 
    * @param  player  the player whose points are to be displayed
    * @param  points  the points to be displayed
    */
    public void setPlayerPoints(int player, int points) { 

        playerPoints[player].setText(points + "");

    }

    /**
     * Display the appropriate player icon under"Next Turn"
     * 
     * @param  player  the player number of the next player; its corresponding icon will be displayed under "Next Turn"
     */
    public void setNextPlayer(int player) {

        nextPlayerIcon.setIcon(playerIcon[player]);

    }

    /**
     * Reset the game board (clear all the pieces on the game board)
     */
    public void resetGameBoard() {

        for (int i = 0; i < NUMROW; i++) {
            for (int j = 0; j < NUMCOL; j++) {
                slots[i][j].setIcon(null);
            }
        }

    }

    /**
     * Display a pop up window displaying the message about invalid move
     */
    public void showInvalidMoveMessage() {

        JOptionPane.showMessageDialog(null, " This move is invalid", "Invalid Move", JOptionPane.PLAIN_MESSAGE, null);

    }

    /**
     * Display a pop up window specifying the number of opponents that was outflanked
     * 
     * @param  player  the player number who has outflanked opponents
     * @param  outflank  the number of opponents that were outflanked
     */
    public void showOutflankMessage(int player, int outflank) {

        JOptionPane.showMessageDialog(null, " outflanked " + outflank + " opponents.", "OutFlank!", JOptionPane.PLAIN_MESSAGE, playerIcon[player]);

    }

    /**
     * Display a pop up window displaying the message about a tie game
     */
    public void showTieGameMessage() {

        JOptionPane.showMessageDialog(null, " This game is tie.", "Tie Game", JOptionPane.PLAIN_MESSAGE, null);

    }

    /**
     * Display a pop up window specifying the winner of this game
     * 
     * @param  player  the player number of the winner of the game
     */
    public void showWinnerMessage(int player) {

        JOptionPane.showMessageDialog(null, " won this game!", "This game has a winner!", JOptionPane.PLAIN_MESSAGE, playerIcon[player]);

    }

    /**
     * Display a pop up window specifying the winner of the match
     * 
     * @param  player  the player number of the winner of the match
     */
    public void showFinalWinnerMessage(int player) {

        JOptionPane.showMessageDialog(null, " won the match with " + MAXGAME + " wins", "The match is finished", JOptionPane.PLAIN_MESSAGE, playerIcon[player]);
        System.exit(0);

    }

    /**
     * Create an OthelloGUI
     */ 
    public static void main(String[] args) {

        OthelloGUI gui = new OthelloGUI();

    }

}
