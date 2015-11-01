import java.io.*;
import java.util.*;
//import java.awt.*;

public class Othello{
   static int size = 8;
   //static char[][] board = new char[size][size];
   //static ArrayList<Move> legalMoves = new ArrayList<Move>();
   static char blank = '0';
   static boolean skipped = false; //keep track if previous turn was skipped
   static boolean gameOver = false;

   public static final String ANSI_RESET = "\u001B[0m";
   public static final String ANSI_BLACK = "\u001B[30m";
   public static final String ANSI_RED = "\u001B[31m";
   public static final String ANSI_GREEN = "\u001B[32m";
   public static final String ANSI_YELLOW = "\u001B[33m";
   public static final String ANSI_BLUE = "\u001B[34m";
   public static final String ANSI_PURPLE = "\u001B[35m";
   public static final String ANSI_CYAN = "\u001B[36m";
   public static final String ANSI_WHITE = "\u001B[37m";

   public static void main(String[] args){

      init();

   }

   public static void loadBoard(String filename, char[][] board) throws FileNotFoundException{

      Scanner sc = new Scanner(new File(filename));

      int r = 0;
      int c = 0;
      while(sc.hasNext()){
         String temp = sc.nextLine();
         for(int k = 0; k < size; k++){
            board[r][c] = temp.charAt(k);
         }
      }
      sc.close();
   }


   public static void init(){
      char[][] board = new char[8][8];
      ArrayList<Move> legalMoves = new ArrayList<Move>();

      for(int r = 0; r < size; r++){
         for(int c = 0; c < size; c++){
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
   public static boolean stateCheck(Player player, Player computer){
    //64 pieces on board
      if(player.score + computer.score == 64)
         return true;
      else if(player.score == 0 || computer.score == 0)
         return true;
      else
         return false;
   }


   public static void printBoard(char[][] board, ArrayList<Move> legalMoves){
     //for holding a copy of the array to make moves on
      char[][] bCopy = new char[size][size];
     //copy the array
      for(int k = 0; k < size; k++){
         System.arraycopy( board[k], 0, bCopy[k], 0, board[k].length );
      }

      for(Move object: legalMoves){
         bCopy[object.valid.row][object.valid.col] = ' ';
      }

      System.out.println("\n      A       B       C       D       E       F       G       H");
      System.out.println("   ===============================================================");
      for(int r = 0; r < size; r++){
         System.out.println("  |       |       |       |       |       |       |       |       |");
         System.out.print(r + " |");
         for(int c = 0; c < size; c++){
            int current = bCopy[r][c];
            if(current == blank)
               System.out.print("       |");
            else
               if(current == 'Z')
                  System.out.print("   " +ANSI_CYAN+ board[r][c] +ANSI_RESET+ "   |");
               else if (current == '#')
                  System.out.print("   " +ANSI_RED+ board[r][c] +ANSI_RESET+ "   |");
               else
                  System.out.print(ANSI_GREEN + "  ["+ board[r][c] + "]  "+ ANSI_RESET+ "|");
         }
         System.out.print(" " + r);
         System.out.println("\n  |       |       |       |       |       |       |       |       |");
         System.out.println("   ---------------------------------------------------------------");
      }
      System.out.println("      A       B       C       D       E       F       G       H\n");
   }

   public static void printLegalMoves(ArrayList<Move> legalMoves){
      int index = 0;
      for(Move object: legalMoves){
         System.out.println("["+ index++ + "] " + object.toString());
      }
   }

   public static void play(Player current, Player enemy, Scanner in,
    char[][] board, ArrayList<Move> legalMoves){
      validMove(current.symbol, enemy.symbol, board, legalMoves); //fill arraylist with possible moves

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

         //board[chosen.valid.row][chosen.valid.col] = current.symbol;
         //flip valid pieces

         board = flip(chosen, current, enemy, board);
         current.score++;
         scoreUpdate(board, current, enemy);
      }
   }

   //minimax search
   //min wants to return the minimum of the maximum values. (minValue)
   //but max wants to return the maximum of the minimum values. (maxValue)
   public static Move minimax(char[][] b, Player min, Player max, long startTime,
   ArrayList<Move> legalMoves) {
      Value prev = null;
      Value best = null;
      for(int d = 2; d < 20; d++){
        best = minValue(b, min, max, d, 1, legalMoves, startTime);
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
    ArrayList<Move> legalMoves, long startTime){
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
         char[][] bCopy = new char[size][size];
       //copy the array
         for(int k = 0; k < size; k++){
            System.arraycopy( b[k], 0, bCopy[k], 0, b[k].length );
         }
         ArrayList<Move> legal = new ArrayList<Move>();

         //find min value of result of picking legalMove a
         bCopy = flip(a, max, min, bCopy); //pick move a and update the board
         validMove(min.symbol, max.symbol, bCopy, legal); //find new legal moves for next player based on move a

        //  System.out.println("max");
        //  System.out.println(a);
        //  printBoard(bCopy, legal);
        //  System.out.println("Current Depth: "+ currentDepth);

         Value returned = minValue(bCopy, min, max, depth, currentDepth+1, legal, startTime);
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
         //chosen = a;
         equal.add(new Value(a, v));
      }
      else if(v == low){
        equal.add(new Value(a, low));
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
    ArrayList<Move> legalMoves, long startTime){
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
         char[][] bCopy = new char[size][size];
        //copy the array
         for(int k = 0; k < size; k++){
            System.arraycopy( b[k], 0, bCopy[k], 0, b[k].length );
         }
         ArrayList<Move> legal = new ArrayList<Move>();

       //find min value of result of picking legalMove a
         bCopy = flip(a, min, max, bCopy); //pick move a and update the board
         validMove(max.symbol, min.symbol, bCopy, legal); //find new legal moves for other player based on move a

        //  System.out.println("min");
        //  System.out.println(a);
        //  printBoard(bCopy, legal);
        //  System.out.println("Current Depth: "+ currentDepth);

         //return minimum of maximum value
         Value returned = maxValue(bCopy, min, max, depth, currentDepth+1, legal, startTime);
         if(returned.cutoff){
           return returned;
         }
         int high = returned.val;
         //System.out.println("Heuristic: "+ high+ " Depth: " + currentDepth);
         if(v > high){
            equal.clear();
            v = high;
            //chosen = a;
            equal.add(new Value(a, v));
         }
         else if(v == high){
           equal.add(new Value(a, high));
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
      for(int n = 0; n < size; n++){
         for(int m = 0; m < size; m++){
            char c = board[n][m];
            if(c == min.symbol){
              //corner move
               if((n == 0 || n == size-1) && (m == 0 || m == size-1)){
                  h += corner;
                }
               //outside move
               else if(n == 0 || m == 0 || n == size-1 || m == size-1){
                  h += outside;
                }
               //any other position
               else{
                  h++;
                }
              }

         if(c == max.symbol){
           //corner move
            if((n == 0 || n == size-1) && (m == 0 || m == size-1)){
               h -= corner;
             }
            //outside move
            else if(n == 0 || m == 0 || n == size-1 || m == size-1){
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
   //calculate score
   public static void scoreUpdate(char[][] board, Player a, Player b){
     a.score = 0;
     b.score = 0;
       for(int i = 0; i < size; i++){
         for(int j = 0; j < size; j++){
           if(board[i][j] == a.symbol){
             a.score++;
           }
           else if(board[i][j] == b.symbol){
             b.score++;
           }
         }
     }
   }

   //move is the chosen move
   //updates board with chosen move and resulting flips
   public static char[][] flip(Move move, Player current, Player enemy, char[][] board){
      int row = move.valid.row;
      int col = move.valid.col;
      Position[] pos = move.locations;
      //place the piece down
      board[row][col] = current.symbol;

    //loop through flip positions
      for(int k = 0; k < move.index; k++){
         int r = row;
         int c = col;
         Position p = pos[k];
      //up or down
         if(p.col == c){
         //down
            if(p.row > r){
               while(r != p.row){
                  board[++r][c] = current.symbol;

               }
            }
            //up
            else{
               while(r != p.row){
                  board[--r][c] = current.symbol;

               }
            }
         }

         //right or left
         else if(p.row == r){
         //right
            if(p.col > c){
               while(c != p.col){
                  board[r][++c] = current.symbol;

               }
            }
            //left
            else{
               while(c != p.col){
                  board[r][--c] = current.symbol;

               }
            }
         }
         //leftup or leftdown
         else if(p.col < c){
         //leftup
            if(p.row < r){
               while(r != p.row && c != p.col){
                  board[--r][--c] = current.symbol;

               }
            }
            //leftdown
            else{
               while(r != p.row && c != p.col){
                  board[++r][--c] = current.symbol;

               }
            }
         }
         //rightup or rightdown
         else if(p.col > c){
         //rightup
            if(p.row < r){
               while(r != p.row && c != p.col){
                  board[--r][++c] = current.symbol;

               }
            }
            //rightdown
            else{
               while(r != p.row && c != p.col){
                  board[++r][++col] = current.symbol;

               }
            }
         }
      }
      return board;
   }

//find all legal moves and add to the legalMoves ArrayList
   public static void validMove(int player, int enemy,
      char[][] board, ArrayList<Move> legalMoves){
      //loop through board
      legalMoves.clear(); //clear list
      //legalMoves.add(null); //skip turn option
      for(int r = 0; r < size; r++)
         for(int c = 0; c < size; c++)
            if(board[r][c] == blank)
            //determine if valid move
            //add furthest flip location(s)
               validSearch(r, c, player, enemy, board, legalMoves);
   }
//returns index of valid move extent. null for no valid move in that direction
//not end of board and the next space contains an enemy
   public static void validSearch(int r, int c, int player, int enemy,
        char[][] board, ArrayList<Move> legalMoves){
   //hold original move location for checking
      int orgR = r;
      int orgC = c;
      Position[] p = new Position[8]; //for holding farthest locations of tiles to be flipped
      int i = 0; //index for adding to p

   //left
      while(c > 0) {
         c--;
         if(board[r][c] == blank)
            break;
         //if friendly piece is encountered
         else if(board[r][c] == player){
         //see if adjacent move is player
            if(Math.abs(orgC-c) == 1)
               break;
            p[i++] = new Position(r,c+1);
            break;
         }
      }
      c = orgC;

   //right
      while(c < size-1){
         c++;
         if (board[r][c] == blank)
            break;
         //if friendly piece is encountered
         else if(board[r][c] == player){
         //see if adjacent move is player
            if(Math.abs(orgC-c) == 1)
               break;
            p[i++] = new Position(r,c-1);
            break;
         }
      }
      c = orgC;

   //up
      while(r > 0){
         r--;
         if (board[r][c] == blank)
            break;
      //if friendly piece is encountered
         if(board[r][c] == player){
         //see if adjacent move is player
            if(Math.abs(orgR-r) == 1)
               break;
            p[i++] = new Position(r+1,c);
            break;
         }
      }
      r = orgR;

   //down
      while(r < size-1){
         r++;
         if (board[r][c] == blank)
            break;
      //if friendly piece is encountered
         if(board[r][c] == player){
         //see if adjacent move is player
            if(Math.abs(orgR-r) == 1)
               break;
            p[i++] = new Position(r-1,c);
            break;
         }
      }
      r = orgR;

   //leftup
      while(c > 0 && r > 0 ){
         c--;
         r--;
         if (board[r][c] == blank)
            break;
      //if friendly piece is encountered
         if(board[r][c] == player){
         //see if adjacent move is player
            if(Math.abs(orgR-r) == 1 && Math.abs(orgC-c) == 1)
               break;
            p[i++] = new Position(r+1,c+1);
            break;
         }
      }
      c = orgC;
      r = orgR;

   //leftdown
      while(c > 0 && r < size-1){
         c--;
         r++;
         if (board[r][c] == blank)
            break;
      //if friendly piece is encountered
         if(board[r][c] == player){
         //see if adjacent move is player
            if(Math.abs(orgR-r) == 1 && Math.abs(orgC-c) == 1)
               break;
            p[i++] = new Position(r-1,c+1);
            break;
         }
      }
      c = orgC;
      r = orgR;

   //rightup
      while(c < size-1 && r > 0 ){
         c++;
         r--;
         if (board[r][c] == blank)
            break;
      //if friendly piece is encountered
         if(board[r][c] == player){
         //see if adjacent move is player
            if(Math.abs(orgR-r) == 1 && Math.abs(orgC-c) == 1)
               break;
            p[i++] = new Position(r+1,c-1);
            break;
         }
      }
      c = orgC;
      r = orgR;

   //rightdown
      while(c < size-1 && r < size-1 ){
         c++;
         r++;
         if (board[r][c] == blank)
            break;
      //if friendly piece is encountered
         if(board[r][c] == player){
         //see if adjacent move is player
            if(Math.abs(orgR-r) == 1 && Math.abs(orgC-c) == 1)
               break;
            p[i++] = new Position(r-1,c-1);
            break;
         }
      }
   //add this legal move to the array list
      if(i > 0)
         legalMoves.add(new Move(orgR,orgC,p,i));
   }

   public static class Move{
      Position valid; //this move's valid position
   //array to hold valid move locations (up to 8)
      Position[] locations;
      int index; //length of array;
      public Move(int r, int c, Position[] p, int i){
         valid = new Position(r,c);
         locations = p;
         index = i;
      }
   // //add farthest flip locations
   // public void add(Position[] p){
   //   location = p;
   // }
      public String toString(){
         int c = (char)valid.col + 'A';
         return "("+ (valid.row) + "," + (char)c + ")";
      }
   }
//(x,y) coordinates of a move
   public static class Position{
      int row;
      int col;

      public Position(int r, int c){
         row = r;
         col = c;
      }
   }

   public static class Player{
      char symbol;
      boolean ai;
      int score = 2;
      public Player(char s, boolean comp){
         symbol = s;
         ai = comp;
      }
   }
   public static class Value{
      Move move;
      int val;
      boolean cutoff = false;
      boolean randomMove = false;
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
