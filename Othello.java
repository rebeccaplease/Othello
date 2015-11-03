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
   static long TIME_LIMIT = 4900000000L; //Time limit for search in nanoseconds
   static long delay = 10000000L;
   public static int TURN = 1;

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

/**
 * Initialize board. Use init for new games and loadBoard for loading in a board.
 */
   public static void main(String[] args) throws InputMismatchException, FileNotFoundException{
     char[][] board = new char[8][8];
     Scanner sc = new Scanner(System.in);
     System.out.println("Would you like to start[1] or load a board?[2]");
     boolean valid = false;
     int select = 0;
     while(!valid){
       try{
         select = sc.nextInt();
         valid = true;
       }
       catch(InputMismatchException e){
         System.out.println("Please enter a valid number.");
       }
     }
     if(select == 1)
      init(board);
     else{
      loadBoard(sc, board, '1', '2');
    }
     start(sc, board);
   }

/**
* Fill board with blanks and starting positions.
*
* @param   board      Board to load in.
*/
   public static void init(char[][] board){
      for(int r = 0; r < SIZE; r++)
         for(int c = 0; c < SIZE; c++)
            board[r][c] = blank;

      board[3][3] = '#';
      board[3][4] = 'Z';
      board[4][3] = 'Z';
      board[4][4] = '#';
    }

/**
 * Option for loading a preset 8x8 board in. Must specify what two chars are used
 * for move pieces to translate them to 'Z' and '#'. Zeros are used to
 * indicate empty spaces.
 *
 * @param   filename   .txt file format.
 * @param   board      Board to load in
 * @param   one        One move piece character.
 * @param   two        Other move piece character.
 */
     public static void loadBoard(Scanner input, char[][] board, char one, char two){
       System.out.println("Enter a filename.");
       Scanner file = null;
       boolean valid = false;
       while(!valid){
         try{
           file = new Scanner(new File(input.next()));
           valid = true;
         }
         catch(FileNotFoundException e){
           System.out.println("Please a valid filename.");
         }
       }
        //while(sc.hasNext()){
        for(int r = 0; r < SIZE; r++){
           String temp = file.nextLine();
           //System.out.println(temp);
           for(int c = 0; c < SIZE; c++){
              char add = temp.charAt(c*2);
              if(add == one)
                 board[r][c] = 'Z';
              else if(add == two)
                 board[r][c] = '#';
              else
                 board[r][c] = blank;
              System.out.print(add + " ");
           }
           System.out.println();
        }
        file.close();
     }
 /**
  * Start gameplay and continue until game is over.
  * 1 for going first
  * 2 for going second
  * 3 for PvP
  * 4 or anything else for computer v. computer.
  *
  * @param   board     Game board.
  */
    public static void start(Scanner in, char[][] board) throws InputMismatchException{
      ArrayList<Move> legalMoves = new ArrayList<Move>();
      printBoard(board, legalMoves);

      System.out.println("Do you want to go first[1] (#) or second[2] (Z)? [3] for PvP. [4] for computer v. computer");
      int p = in.nextInt();
      if(p != 3){
        System.out.println("Enter a time limit for the computer in seconds.");
        boolean valid = false;
        while(!valid){
        try {
        TIME_LIMIT = in.nextInt() * 1000000000L;
        valid = true;
      }
      catch(InputMismatchException e){
        System.out.println("Please type in an integer.");
      }
    }
      }

      System.out.println();
      boolean cVc = false;
      Player player;
      Player computer;

      if(p == 1){
         player = new Player('#',false);
         computer = new Player('Z',true);
      }
      else if (p == 2){
         player = new Player('Z',false);
         computer = new Player('#',true);
      }
      else if (p == 3){
         player = new Player('Z',false);
         computer = new Player('#',false);
      }
      else { //Computer v Computer
         p = 2;
         player = new Player('Z',true);
         computer = new Player('#',true);
         cVc = true;
      }
      scoreUpdate(board, player, computer);

      while(!gameOver){ //break out of loop?
         if(p == 1){
            play(player, computer, in, false, board, legalMoves);
            gameOver = stateCheck(player, computer);

            if(gameOver)
              break;

            play(computer, player, in, false, board, legalMoves);
            gameOver = stateCheck(player, computer);
         }
         else {
            play(computer, player, in, cVc, board, legalMoves);
            gameOver = stateCheck(player, computer);

            if(gameOver)
              break;

            play(player, computer, in, cVc, board, legalMoves);
            gameOver = stateCheck(player, computer);
         }
      }
      legalMoves.clear();
      printBoard(board, legalMoves);
      if(cVc)
        System.out.println("Computer " + player.symbol + " score: " + player.score);
      else
        System.out.println("Your score: " + player.score);
      System.out.println("Computer " + computer.symbol + " score: "+ computer.score);
      if(player.score > computer.score){
        int displayScore = player.score - computer.score;
        if(cVc)
          System.out.println("Computer "+ player.symbol + " wins! "+ displayScore);
        else
          System.out.println("You win! " + displayScore);
        }
      else if(player.score < computer.score){
        int displayScore = computer.score - player.score;
         System.out.println("Computer "+ computer.symbol + " wins! "+ displayScore);
       }
      else
         System.out.println("Tie!");
      in.close();
   }

  //================================================================================
  // Gameplay Methods
  //================================================================================

   public static void play(Player current, Player enemy, Scanner in, boolean cVc,
    char[][] board, ArrayList<Move> legalMoves) throws IndexOutOfBoundsException{
      //Fill arraylist with possible moves
      validSearch(current.symbol, enemy.symbol, board, legalMoves);

      //print board with legal moves
      printBoard(board, legalMoves);
      System.out.println("Current Scores: ");
      if(current.symbol == 'Z'){
         System.out.println(ANSI_CYAN+current.symbol + ": " + current.score+ANSI_RESET);
         System.out.println(ANSI_RED+enemy.symbol + ": " + enemy.score+ANSI_RESET);
      }
      else{
        System.out.println(ANSI_RED+current.symbol + ": " + current.score+ANSI_RESET);
        System.out.println(ANSI_CYAN+enemy.symbol + ": " + enemy.score+ANSI_RESET);
      }
      printLegalMoves(legalMoves);

      Move chosen = null;
      if(legalMoves.size() == 0){ //if there are no valid moves
         if(skipped)
            gameOver = true;
         else
            skipped = true;
      }

      else{
         skipped = false;
         if(current.symbol == 'Z'){
            System.out.println(ANSI_CYAN+"Pick a move! Z"+ANSI_RESET);
         }
         else{
            System.out.println(ANSI_RED+"Pick a move! #"+ANSI_RESET);
         }
         if(!current.ai){ //if player is human
           int choice;
           boolean valid = false;
           while(!valid){
             try {
               chosen = legalMoves.get(in.nextInt());
               valid = true;
             }
             catch(IndexOutOfBoundsException e){
               System.out.println("Please enter a valid move number.");
             }
           }
         }
         else{ //if computer
          if(cVc){
           if(current.symbol == 'Z')
              //player Z uses heuristic 1
              chosen = alphabeta(board, current, enemy, System.nanoTime(), legalMoves, 1);
            else{
              chosen = alphabeta(board, current, enemy, System.nanoTime(), legalMoves, 2);
              // int random = (int)(Math.random()*(legalMoves.size()));
              // System.out.println(random);
              // chosen = legalMoves.get(random);
             }
          }
          else{
            chosen = alphabeta(board, current, enemy, System.nanoTime(), legalMoves, 1);
           }
         }
         //Flip valid pieces
         board = flip(chosen, current, enemy, board);
         //Update score
         current.score++;
         scoreUpdate(board, current, enemy);
      }
      TURN++;
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
 // Alphabeta Search
 //================================================================================

   //min wants to return the minimum of the maximum values. (minValue)
   //but max wants to return the maximum of the minimum values. (maxValue)
   public static Move alphabeta(char[][] b, Player min, Player max, long startTime,
   ArrayList<Move> legalMoves, int ai) {
      if(legalMoves.size() == 1) //if there is only one possible move
         return legalMoves.get(0);
      Value prev = null;
      Value best = null;
      for(int d = 1; d < 20; d++){ //iterative deepening
        if(ai == 1) //player 2 uses min while player 1 uses max
            best = minValue(b, min, max, d, 0, legalMoves, startTime, -1000, 1000, ai);
        else
            best = maxValue(b, min, max, d, 0, legalMoves, startTime, -1000, 1000, ai);
         if(best.cutoff){
           System.out.println("Cutoff! time: " + (System.nanoTime()-startTime)/1000000000.0 + " seconds \n");
            return prev.move;
         }

         if(best.randomMove){ //if all moves are equally likely, stop there
            return best.move;
         }
         System.out.println("depth: "+d);
         System.out.println("time: " + (System.nanoTime()-startTime)/1000000000.0 + " seconds \n");
         prev = best;

      }
      return best.move; //return position of best move
   }
   //returns heuristic value for the move in question
   //max wants the minimum result of the maximum values (negative scores are better for max
   //while positive scores are better for min)
   public static Value maxValue(char[][] b, Player min, Player max, int depth, int currentDepth,
    ArrayList<Move> legalMoves, long startTime, int alpha, int beta, int ai){
      //if 4.9 seconds have elapsed
      if(System.nanoTime() - startTime > TIME_LIMIT-delay){ //subtract 1/10 of a second
         return new Value(true);
      }

      if(legalMoves.size() == 0 || depth == currentDepth){
        if(ai == 1)
          return new Value(heuristic1(b, min, max, legalMoves, currentDepth));
        else if (ai == 2)
          return new Value(heuristic2(b, min, max, legalMoves, currentDepth));
      }
      int v = -1000;
      ArrayList<Value> equal = new ArrayList<Value>();
      //Move chosen = null;
      for(Move a: legalMoves){
       //for holding a copy of the array to make moves on
         char[][] bCopy = new char[SIZE][SIZE];
       //copy the array
         copyArray(b,bCopy);
         ArrayList<Move> legal = new ArrayList<Move>();

         //find min value of result of picking legalMove a
         bCopy = flip(a, max, min, bCopy); //pick move a and update the board
         validSearch(min.symbol, max.symbol, bCopy, legal); //find new legal moves for next player based on move a

         Value returned = minValue(bCopy, min, max, depth, currentDepth+1, legal, startTime, alpha, beta, ai);
         if(returned.cutoff){
            return returned;
         }
         int low = returned.val;

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
   }

   public static Value minValue(char[][] b, Player min, Player max, int depth, int currentDepth,
    ArrayList<Move> legalMoves, long startTime, int alpha, int beta, int ai){
      //if 4.9 seconds have elapsed
      if(System.nanoTime() - startTime > TIME_LIMIT-delay){
         return new Value(true);
      }

      if(legalMoves.size() == 0 || depth == currentDepth){
        if(ai == 1)
         return new Value(heuristic1(b, min, max, legalMoves, currentDepth));
        else if (ai == 2)
          return new Value(heuristic2(b, min, max, legalMoves, currentDepth));
      }
      //store heuristic value
      int v = 1000;
      ArrayList<Value> equal = new ArrayList<Value>();

      for(Move a: legalMoves){
        //for holding a copy of the array to make moves on
         char[][] bCopy = new char[SIZE][SIZE];
        //copy the array
        copyArray(b, bCopy);

         ArrayList<Move> legal = new ArrayList<Move>();

         //find min value of result of picking legalMove a
         bCopy = flip(a, min, max, bCopy); //pick move a and update the board
         validSearch(max.symbol, min.symbol, bCopy, legal); //find new legal moves for other player based on move a

         //return minimum of maximum value
         Value returned = maxValue(bCopy, min, max, depth, currentDepth+1, legal, startTime, alpha, beta, ai);
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
   public static int heuristic1(char[][] board, Player min, Player max,
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
                 //adjacent to corner piece (C or X)
                 if(n == 1 || n == SIZE-2 || m == 1 || m == SIZE-2){
                   h -= 2;
                 }
                 else{
                  h += outside;
                }
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
                 if(n == 1 || n == SIZE-2 || m == 1 || m == SIZE-2){
                   h += 2;
                 }
                 else{
                  h -= outside;
                }
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
   //evaluate position from min's perspective - add points to better positions for min
   public static int heuristic2(char[][] board, Player min, Player max,
      ArrayList<Move> legalMoves, int depth){
        //legalMoves contains min's legal moves
      int h = 0;
      int outside = 5;
      int corner = 25;
      int other = 1;
      int corx = 2;
      for(int n = 0; n < SIZE; n++){
         for(int m = 0; m < SIZE; m++){
            char c = board[n][m];
            if(c == min.symbol){
              //corner move
               if((n == 0 || n == SIZE-1) && (m == 0 || m == SIZE-1)){
                 if(TURN + depth > 20)
                  h += corner-5;
                 else
                  h += corner;
               }
               //outside move
               else if(n == 0 || m == 0 || n == SIZE-1 || m == SIZE-1){
                 //adjacent to corner piece (C or X)
                 if(n == 1 || n == SIZE-2 || m == 1 || m == SIZE-2){
                   h -= corx;
                 }
                 else{
                  h += outside;
                }
               }
               //any other position
               else{
                  if(TURN + depth > 15)
                    h += other;
               }
            }

            if(c == max.symbol){ //multiply all by -1 if max.symbol
              //corner move
               if((n == 0 || n == SIZE-1) && (m == 0 || m == SIZE-1)){
                 if(TURN + depth > 15)
                  h -= corner+5;
                 else
                  h -= corner;
               }
               //outside move
               else if(n == 0 || m == 0 || n == SIZE-1 || m == SIZE-1){
                 //adjacent to corner piece (C or X)
                 if(n == 1 || n == SIZE-2 || m == 1 || m == SIZE-2){
                   h += corx;
                 }
                 else{
                  h -= outside;
                }
               }
               //any other position
               else{
                  if(TURN + depth > 20)
                    h -= other;
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
      copyArray(board,bCopy);

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
   /**
    * Copy array b into bCopy.
    *
    * @param   b      Board to be copied.
    * @param   bCopy  Copy of b.
    */
   public static void copyArray(char[][] b, char[][] bCopy){
    for(int k = 0; k < SIZE; k++){
       System.arraycopy( b[k], 0, bCopy[k], 0, b[k].length );
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
