import java.io.*;
import java.util.*;

public class Othello{
//================================================================================
// Constants
//================================================================================
   static int SIZE = 8;       //board SIZE
   static char blank = '0';   //blank space
   static boolean skipped = false; //keep track if previous turn was skipped
   static boolean gameOver = false; //True if two turns are skipped in a row

   //Color constants for printing
   public static final String ANSI_RESET = "\u001B[0m";
   public static final String ANSI_BLACK = "\u001B[30m";
   public static final String ANSI_RED = "\u001B[31m";
   public static final String ANSI_GREEN = "\u001B[32m";
   public static final String ANSI_YELLOW = "\u001B[33m";
   public static final String ANSI_BLUE = "\u001B[34m";
   public static final String ANSI_PURPLE = "\u001B[35m";
   public static final String ANSI_CYAN = "\u001B[36m";
   public static final String ANSI_WHITE = "\u001B[37m";

//================================================================================
// Setup
//================================================================================

   public static void main(String[] args){
      init();
   }

   public static void init(){
      char[][] board = new char[8][8];
      ArrayList<Move> legalMoves = new ArrayList<Move>();

      for(int r = 0; r < SIZE; r++){
         for(int c = 0; c < SIZE; c++){
            board[r][c] = blank;
         }
      }

      board[3][3] = '#';
      board[3][4] = 'Z';
      board[4][3] = 'Z';
      board[4][4] = '#';


      Scanner in = new Scanner(System.in);
      System.out.println("Do you want to go first[1] or second[2]? [3] for computer v. computer");//need to catch invalid inputs
      int p = in.nextInt();
      char comp;
      Player player;
      Player computer;

      if(p == 1){
         //comp = Z;
         player = new Player('#',false);
         computer = new Player('Z',true);
      }
      else if (p == 2){
         comp = 1;
         player = new Player('Z',false);
         computer = new Player('#',true);
      }
      else{ //computer v computer
         comp = 1;
         p = 2;
         player = new Player('Z',true);
         computer = new Player('#',true);
      }

      while(!gameOver){
      //while(true){
         if(p == 1){
            play(player, computer, in, board, legalMoves);
            gameOver = stateCheck(player, computer);

            play(computer, player, in, board, legalMoves);
            gameOver = stateCheck(player, computer);
         }
         else {
            play(computer, player, in, board, legalMoves);
            gameOver = stateCheck(player, computer);

            play(player, computer, in, board, legalMoves);
            gameOver = stateCheck(player, computer);
         }
      }
      printBoard(board, legalMoves);
      System.out.println("Your score: " + player.score);
      System.out.println("Computer score: " + computer.score);
      if(player.score > computer.score)
         System.out.println("You win!");
      else if(player.score < computer.score)
         System.out.println("Computer wins!");
      else
         System.out.println("Tie!");
   }
   /**
    * Option for loading a preset 8x8 board in. Must specify what two chars are used
    * for move pieces to translate them to 'Z' and '#'.
    * Zeros are used to indicate empty spaces.
    *
    * @param   filename   .txt file format.
    * @param   board      Board to load in
    * @param   one        One move piece character.
    * @param   two        Other move piece character.
    */
   public static void loadBoard(String filename, char[][] board, char one, char two)
      throws FileNotFoundException{
      Scanner sc = new Scanner(new File(filename));

      int r = 0;
      int c = 0;
      while(sc.hasNext()){
         String temp = sc.nextLine();
         for(int k = 0; k < SIZE; k++){
            char add = temp.charAt(k);
            if(add == one)
               board[r][c] = 'Z';
            else if(add == two)
               board[r][c] = '#';
            else
               board[r][c] = add;
         }
      }
      sc.close();
   }

  //================================================================================
  // Gameplay Methods
  //================================================================================

   public static void play(Player current, Player enemy, Scanner in,
    char[][] board, ArrayList<Move> legalMoves){
      validSearch(current.symbol, enemy.symbol, board, legalMoves); //fill arraylist with possible moves

    //print board with legal moves
      printBoard(board, legalMoves);
      System.out.println("Current Scores: ");
      System.out.println(current.symbol + ": " + current.score);
      System.out.println(enemy.symbol + ": " + enemy.score);
      printLegalMoves(legalMoves);
      Move chosen;
      if(legalMoves.size() == 0){ //if there are no valid moves
         if(skipped)
            gameOver = true;
         else
            skipped = true;
      }

      else{
         skipped = false;
         if(current.symbol == 'Z'){
            System.out.println(ANSI_CYAN+"Pick a move!"+ANSI_RESET);
         }
         else{
            System.out.println(ANSI_RED+"Pick a move!"+ANSI_RESET);
         }
         if(!current.ai){ //if player is human
         //need to check for valid input

            chosen = legalMoves.get(in.nextInt());
         }
         else{ //if computer

            if(current.symbol == 'Z')
               chosen = minimax(board, current, enemy, System.nanoTime(), legalMoves);
            else{
               int random = (int)(Math.random()*(legalMoves.size()));
               System.out.println(random);
               chosen = legalMoves.get(random);
            }
         }
         //flip valid pieces

         board = flip(chosen, current, enemy, board);
         current.score++;
         scoreUpdate(board, current, enemy);
      }
   }
   /**
    * Check for the end of the game.
    *
    * @param   a       Player
    * @param   b       Player
    */
   public static boolean stateCheck(Player a, Player b){
      //64 pieces on board
      if(a.score + b.score == 64)
         return true;
      else if(a.score == 0 || b.score == 0) //no possible moves for a player
         return true;
      else
         return false;
   }
   /**
    * Calculate score based on the number of pieces on the board and update Player values.
    *
    * @param   board   Current board to check.
    * @param   a       Player
    * @param   b       Player
    */
   public static void scoreUpdate(char[][] board, Player a, Player b){
      a.score = 0;
      b.score = 0;
      for(int i = 0; i < SIZE; i++){
         for(int j = 0; j < SIZE; j++){
            if(board[i][j] == a.symbol)
               a.score++;
            else if(board[i][j] == b.symbol)
               b.score++;
         }
      }
   }

 //================================================================================
 // Minimax Search
 //================================================================================

   //min wants to return the minimum of the maximum values. (minValue)
   //but max wants to return the maximum of the minimum values. (maxValue)
   public static Move minimax(char[][] b, Player min, Player max, long startTime,
   ArrayList<Move> legalMoves) {
      if(legalMoves.size() == 1) //if there is only one possible move
         return legalMoves.get(0);
      Value prev = null;
      Value best = null;
      for(int d = 2; d < 20; d++){ //iterative deepening
         best = minValue(b, min, max, d, 1, legalMoves, startTime, -1000, 1000);
         if(best.cutoff){
            return prev.move;
         }

         if(best.randomMove){ //if all moves are equally likely, stop there
            return best.move;
         }
         System.out.println("depth: "+d);
         System.out.println("time: " + (System.nanoTime()-startTime)/1000000000.0 + "\n");
         prev = best;

      }
      return best.move; //return position of best move
   }
   //returns heuristic value for the move in question
   public static Value maxValue(char[][] b, Player min, Player max, int depth, int currentDepth,
    ArrayList<Move> legalMoves, long startTime, int alpha, int beta){
      //if 4.9 seconds have elapsed
      if(System.nanoTime() - startTime > 4.9*1000000000){
         return new Value(true);
      }

      if(legalMoves.size() == 0 || depth == currentDepth){
         return new Value(heuristic(b, max, min, legalMoves, currentDepth));
      }
      int v = -1000;
      ArrayList<Value> equal = new ArrayList<Value>();
      //Move chosen = null;
      for(Move a: legalMoves){
       //for holding a copy of the array to make moves on
         char[][] bCopy = new char[SIZE][SIZE];
       //copy the array
         for(int k = 0; k < SIZE; k++){
            System.arraycopy( b[k], 0, bCopy[k], 0, b[k].length );
         }
         ArrayList<Move> legal = new ArrayList<Move>();

         //find min value of result of picking legalMove a
         bCopy = flip(a, max, min, bCopy); //pick move a and update the board
         validSearch(min.symbol, max.symbol, bCopy, legal); //find new legal moves for next player based on move a

        //  System.out.println("max");
        //  System.out.println(a);
        //  printBoard(bCopy, legal);
        //  System.out.println("Current Depth: "+ currentDepth);

         Value returned = minValue(bCopy, min, max, depth, currentDepth+1, legal, startTime, alpha, beta);
         if(returned.cutoff){
            return returned;
         }
         int low = returned.val;
         // System.out.println("Heuristic: "+ low+ " Depth: " + currentDepth);
         //return maximum of minimum values
        //  if(v < low){
        //     v = low;
        //     chosen = a;
        //  }
         if(v < low){
            equal.clear();
            v = low;
            if(v >= beta){
               return new Value(v);
            }
            equal.add(new Value(a, v));
         }
         else if(v == low){
            equal.add(new Value(a, low));
         }
         if(v > alpha){
            alpha = v;
         }
      }
      //if all results have the same heuristic, pick one randomly
      if(equal.size() > 1 ){
         int random = (int)(Math.random()*(equal.size()));
         Value best = equal.get(random);
         if(equal.size() == legalMoves.size())
            best.randomMove = true;
         return best;
      }
      return equal.get(0);
      //return new Value(chosen, v);
   }

   public static Value minValue(char[][] b, Player min, Player max, int depth, int currentDepth,
    ArrayList<Move> legalMoves, long startTime, int alpha, int beta){
      //if 4.9 seconds have elapsed
      if(System.nanoTime() - startTime > 4.9*1000000000){
         return new Value(true);
      }

      if(legalMoves.size() == 0 || depth == currentDepth){
         return new Value(heuristic(b, min, max, legalMoves, currentDepth));
      }
      //store heuristic value
      int v = 1000;
      ArrayList<Value> equal = new ArrayList<Value>();
      //Move chosen = null;
      for(Move a: legalMoves){
        //for holding a copy of the array to make moves on
         char[][] bCopy = new char[SIZE][SIZE];
        //copy the array
         for(int k = 0; k < SIZE; k++){
            System.arraycopy( b[k], 0, bCopy[k], 0, b[k].length );
         }
         ArrayList<Move> legal = new ArrayList<Move>();

       //find min value of result of picking legalMove a
         bCopy = flip(a, min, max, bCopy); //pick move a and update the board
         validSearch(max.symbol, min.symbol, bCopy, legal); //find new legal moves for other player based on move a

        //  System.out.println("min");
        //  System.out.println(a);
        //  printBoard(bCopy, legal);
        //  System.out.println("Current Depth: "+ currentDepth);

         //return minimum of maximum value
         Value returned = maxValue(bCopy, min, max, depth, currentDepth+1, legal, startTime, alpha, beta);
         if(returned.cutoff){
            return returned;
         }
         int high = returned.val;
         //System.out.println("Heuristic: "+ high+ " Depth: " + currentDepth);
         if(v > high){
            equal.clear();
            v = high;
            if(v <= alpha){
               return new Value(v);
            }
            equal.add(new Value(a, v));
         }
         else if(v == high){
            equal.add(new Value(a, high));
         }

         if(v < beta){
            beta = v;
         }
      }
      //if all results have the same heuristic, pick one randomly
      if(equal.size() > 1 ){
         int random = (int)(Math.random()*(equal.size()));
         Value best = equal.get(random);
         if(equal.size() == legalMoves.size())
            best.randomMove = true;
         return best;
      }
      return equal.get(0);
   }
   //evaluate position from min's perspective
   public static int heuristic(char[][] board, Player min, Player max,
      ArrayList<Move> legalMoves, int depth){
      int h = 0;
      int outside = 5;
      int corner = 20;
      for(int n = 0; n < SIZE; n++){
         for(int m = 0; m < SIZE; m++){
            char c = board[n][m];
            if(c == min.symbol){
              //corner move
               if((n == 0 || n == SIZE-1) && (m == 0 || m == SIZE-1)){
                  h += corner;
               }
               //outside move
               else if(n == 0 || m == 0 || n == SIZE-1 || m == SIZE-1){
                  h += outside;
               }
               //any other position
               else{
                  h++;
               }
            }

            if(c == max.symbol){
            //corner move
               if((n == 0 || n == SIZE-1) && (m == 0 || m == SIZE-1)){
                  h -= corner;
               }
               //outside move
               else if(n == 0 || m == 0 || n == SIZE-1 || m == SIZE-1){
                  h -= outside;
               }
               //any other position
               else{
                  h--;
               }
            }
         }
      }
      return h-depth;
   }

//================================================================================
// Legal Moves Methods
//================================================================================

/**
 * Finds all legal moves and add to the legalMoves ArrayList.
 * Search the board and check if blank spaces are legal moves. checkMove checks a location
 * and adds a legal move to the legalMoves ArrayList if it is valid.
 *
 * @param   current     Player piece.
 * @param   enemy       Enemy piece.
 * @param   board       Current board to check.
 * @param   legalMoves  Add move with positions to ArrayList if it is legal.
 */
   public static void validSearch(int current, int enemy,
      char[][] board, ArrayList<Move> legalMoves){
      legalMoves.clear();
      for(int r = 0; r < SIZE; r++)
         for(int c = 0; c < SIZE; c++)
            if(board[r][c] == blank)
               checkMove(r, c, current, enemy, board, legalMoves);
   }

/**
 * Given the row and column of a move,earches all directions for legal moves
 * and stores furthest location in the legalMoves ArrayList.
 * Check if adjacent piece is not blank for friendly.  Then search until the end
 * of the board.  If a friendly piece is encountered, save the last enemy position.
 * Else if the end of the board is reached or a blank space is encountered, then
 * don't do anything.
 *
 * @param   r           Row of move location to check.
 * @param   c           Column of move location to check.
 * @param   current     Current player piece.
 * @param   enemy       Opponent piece.
 * @param   board       Current board to check.
 * @param   legalMoves  Add move with positions to ArrayList if it is legal.
 */
   public static void checkMove(int r, int c, int current, int enemy,
        char[][] board, ArrayList<Move> legalMoves){
      //Hold original move location for checking each direction
      int orgR = r;
      int orgC = c;
      //For holding farthest locations of tiles to be flipped. max 8 for 8 directions
      Position[] p = new Position[8];
      //Count of valid positions added to p
      int i = 0;

      //Left
      while(c > 0) {
         c--;
         if(board[r][c] == blank)
            break;
         //If a friendly piece is encountered
         else if(board[r][c] == current){
           //Check if adjacent move is friendly (not a legal move)
            if(Math.abs(orgC-c) == 1)
               break;
            p[i++] = new Position(r,c+1);
            break;
         }
      }
      c = orgC;

      //Right
      while(c < SIZE-1){
         c++;
         if (board[r][c] == blank)
            break;
          //If a friendly piece is encountered
         else if(board[r][c] == current){
            //Check if adjacent move is friendly (not a legal move)
            if(Math.abs(orgC-c) == 1)
               break;
            p[i++] = new Position(r,c-1);
            break;
         }
      }
      c = orgC;

      //Up
      while(r > 0){
         r--;
         if (board[r][c] == blank)
            break;
            //If a friendly piece is encountered
         else if(board[r][c] == current){
            //Check if adjacent move is friendly (not a legal move)
            if(Math.abs(orgR-r) == 1)
               break;
            p[i++] = new Position(r+1,c);
            break;
         }
      }
      r = orgR;

      //Down
      while(r < SIZE-1){
         r++;
         if (board[r][c] == blank)
            break;
            //If a friendly piece is encountered
         else if(board[r][c] == current){
            //Check if adjacent move is friendly (not a legal move)
            if(Math.abs(orgR-r) == 1)
               break;
            p[i++] = new Position(r-1,c);
            break;
         }
      }
      r = orgR;

      //LeftUp
      while(c > 0 && r > 0 ){
         c--;
         r--;
         if (board[r][c] == blank)
            break;
            //If a friendly piece is encountered
         else if(board[r][c] == current){
            //Check if adjacent move is friendly (not a legal move)
            if(Math.abs(orgR-r) == 1 && Math.abs(orgC-c) == 1)
               break;
            p[i++] = new Position(r+1,c+1);
            break;
         }
      }
      c = orgC;
      r = orgR;

      //LeftDown
      while(c > 0 && r < SIZE-1){
         c--;
         r++;
         if (board[r][c] == blank)
            break;
            //If a friendly piece is encountered
         else if(board[r][c] == current){
            //Check if adjacent move is friendly (not a legal move)
            if(Math.abs(orgR-r) == 1 && Math.abs(orgC-c) == 1)
               break;
            p[i++] = new Position(r-1,c+1);
            break;
         }
      }
      c = orgC;
      r = orgR;

      //RightUp
      while(c < SIZE-1 && r > 0 ){
         c++;
         r--;
         if (board[r][c] == blank)
            break;
          //If a friendly piece is encountered
         else if(board[r][c] == current){
            //Check if adjacent move is friendly (not a legal move)
            if(Math.abs(orgR-r) == 1 && Math.abs(orgC-c) == 1)
               break;
            p[i++] = new Position(r+1,c-1);
            break;
         }
      }
      c = orgC;
      r = orgR;

      //RightDown
      while(c < SIZE-1 && r < SIZE-1 ){
         c++;
         r++;
         if (board[r][c] == blank)
            break;
          //If a friendly piece is encountered
         else if(board[r][c] == current){
            //Check if adjacent move is friendly (not a legal move)
            if(Math.abs(orgR-r) == 1 && Math.abs(orgC-c) == 1)
               break;
            p[i++] = new Position(r-1,c-1);
            break;
         }
      }

   //Add this legal move to the ArrayList if there was at least 1 valud position.
      if(i > 0)
         legalMoves.add(new Move(orgR,orgC,p,i));
   }

/**
 * Update the board with the result of placing a move down from the current Player.
 * Given the flip positions for a move in the legalMoves ArrayList, place the piece
 * and flip all pieces between the positions.  Modifies the board directly.
 *
 * @param   move        Chosen move to place down. Holds array of positions to flip.
 * @param   current     Current player piece.
 * @param   enemy       Enemy piece.
 * @param   board       Current board to modify.
 */
   public static char[][] flip(Move move, Player current, Player enemy, char[][] board){
      int row = move.valid.row;
      int col = move.valid.col;
      Position[] pos = move.locations;
       //Place the piece down on the Move location
      board[row][col] = current.symbol;

       //Iterate through valid flip positions
      for(int k = 0; k < move.index; k++){
         int r = row;
         int c = col;
         Position p = pos[k];
          //Up or Down
         if(p.col == c){
             //Down
            if(p.row > r){
               while(r != p.row)
                  board[++r][c] = current.symbol;
            }
             //Up
            else{
               while(r != p.row)
                  board[--r][c] = current.symbol;
            }
         }

          //Right or Left
         else if(p.row == r){
             //Right
            if(p.col > c){
               while(c != p.col)
                  board[r][++c] = current.symbol;
            }
             //Left
            else{
               while(c != p.col)
                  board[r][--c] = current.symbol;
            }
         }
          //LeftUp or LeftDown
         else if(p.col < c){
             //LeftUp
            if(p.row < r){
               while(r != p.row && c != p.col)
                  board[--r][--c] = current.symbol;
            }
             //LeftDown
            else{
               while(r != p.row && c != p.col)
                  board[++r][--c] = current.symbol;
            }
         }
          //RightUp or RightDown
         else if(p.col > c){
             //RightUp
            if(p.row < r){
               while(r != p.row && c != p.col)
                  board[--r][++c] = current.symbol;
            }
             //RightDown
            else{
               while(r != p.row && c != p.col)
                  board[++r][++col] = current.symbol;
            }
         }
      }
      return board;
   }
//================================================================================
// ASCII Interface Methods
//================================================================================

/**
 * Print board and legal moves onto board.
 *
 * @param   board        Current board to print.
 * @param   legalMoves   Legal moves to print.
 */
   public static void printBoard(char[][] board, ArrayList<Move> legalMoves){
      //Holding a copy of the array to print legal moves
      char[][] bCopy = new char[SIZE][SIZE];
      //copy the array
      for(int k = 0; k < SIZE; k++){
         System.arraycopy( board[k], 0, bCopy[k], 0, board[k].length );
      }

      for(Move object: legalMoves){
         bCopy[object.valid.row][object.valid.col] = ' ';
      }

      System.out.println("\n      A       B       C       D       E       F       G       H");
      System.out.println("   ===============================================================");
      for(int r = 0; r < SIZE; r++){
         System.out.println("  |       |       |       |       |       |       |       |       |");
         System.out.print(r + " |");
         for(int c = 0; c < SIZE; c++){
            int current = bCopy[r][c];
            if(current == blank)
               System.out.print("       |");
            else
               if(current == 'Z')
                  System.out.print("   " +ANSI_CYAN+ board[r][c] +ANSI_RESET+ "   |");
               else if (current == '#')
                  System.out.print("   " +ANSI_RED+ board[r][c] +ANSI_RESET+ "   |");
               else
                  System.out.print(ANSI_GREEN + "  ["+ "_" + "]  "+ ANSI_RESET+ "|");
         }
         System.out.print(" " + r);
         System.out.println("\n  |       |       |       |       |       |       |       |       |");
         System.out.println("   ---------------------------------------------------------------");
      }
      System.out.println("      A       B       C       D       E       F       G       H\n");
   }

    /**
     * Print legal moves with indexes for choosing.
     *
     * @param   legalMoves   Legal moves to print.
     */
   public static void printLegalMoves(ArrayList<Move> legalMoves){
      int index = 0;
      for(Move object: legalMoves){
         System.out.println("["+ index++ + "] " + object.toString());
      }
   }


//================================================================================
// Helper Classes
//================================================================================

/**
 * The Move class holds its own location, an array of valid flip locations,
 * and the number of valid flip locations in the array
 *
 * @param  valid       The move's Position (row, col)
 * @param  locations   The locations of furthest flippable pieces (up to 8)
 * @param  index       The number of elements in locations
 */
   public static class Move{
    //================================================================================
    // Properties
    //================================================================================
      Position valid;
      Position[] locations;
      int index;
    //================================================================================
    // Constructors
    //================================================================================
      public Move(int r, int c, Position[] p, int i){
         valid = new Position(r,c);
         locations = p;
         index = i;
      }
    //================================================================================
    // Methods
    //================================================================================
      public String toString(){
         int c = (char)valid.col + 'A';
         return "("+ (valid.row) + "," + (char)c + ")";
      }
   }

/**
 * The Position class holds the (row, col) location as a tuple.
 *
 * @param  row   Row, or x location. Zero indexed from the left.
 * @param  col   Column, y location. Zero indexed from the top.
 */
   public static class Position{
    //================================================================================
    // Properties
    //================================================================================
      int row;
      int col;
    //================================================================================
    // Constructors
    //================================================================================
      public Position(int r, int c){
         row = r;
         col = c;
      }
   }

/**
 * The Player class has the player's symbol, whether it is playing using the AI,
 * and its current score based on the number of pieces.
 *
 * @param  symbol    Player's playing piece on the board.
 * @param  ai        True for computer using the AI, false for human player.
 * @param  score     Current score based on the number of pieces on the board.
 */
   public static class Player{
    //================================================================================
    // Properties
    //================================================================================
      char symbol;
      boolean ai;
      int score = 2;
    //================================================================================
    // Constructors
    //================================================================================
      public Player(char s, boolean computer){
         symbol = s;
         ai = computer;
      }
   }
/**
 * The Value class is used in alpha-beta search to store and return values.
 *
 * @param  move        Associated move
 * @param  val         Heuristic value
 * @param  cutoff      True if time limit is reached. Exit from further searching.
 * @param  randomMove  True if all moves are equally likely at the top level,
 *                     so a random move may be returned without further searching.
 */
   public static class Value{
    //================================================================================
    // Properties
    //================================================================================
      Move move;
      int val;
      boolean cutoff = false;
      boolean randomMove = false;
    //================================================================================
    // Constructors
    //================================================================================
      public Value(Move m, int h){
         move = m;
         val = h;
      }
      public Value(int h){
         val = h;
      }
      public Value(boolean c){
         cutoff = c;
      }
   }
}
