import java.io.*;
import java.util.*;
//import java.awt.*;

public class Othello{
   static int size = 8;
   static int[][] board = new int[size][size];
   static ArrayList<Move> legalMoves = new ArrayList<Move>();
   static char blank = 0;
   static boolean skipped = false; //keep track if previous turn was skipped
   static boolean gameOver = false;

   public static void main(String[] args){

      init();

   }

   public static void loadBoard(String filename) throws FileNotFoundException{

      Scanner sc = new Scanner(new File(filename));

      int r = 0;
      int c = 0;
      while(sc.hasNext()){
         if(c > size-1){
            r++;
            c = 0;
         }
         board[r][c] = sc.nextInt();
         c++;
      }
      sc.close();
   }


   public static void init(){
      for(int r = 0; r < size; r++){
         for(int c = 0; c < size; c++){
            board[r][c] = blank;
         }
      }

      board[3][3] = 1;
      board[3][4] = 2;
      board[4][3] = 2;
      board[4][4] = 1;


      Scanner in = new Scanner(System.in);
      System.out.println("Do you want to go first[1] or second[2]? [3] for computer v. computer");//need to catch invalid inputs
      int p = in.nextInt();
      char comp;
      Player player;
      Player computer;

      if(p == 1){
         comp = 2;
         player = new Player(p,false);
         computer = new Player(comp,true);
      }
      else if (p == 2){
         comp = 1;
         player = new Player(p,false);
         computer = new Player(comp,true);
      }
      else{ //computer v computer
        comp = 1;
        player = new Player(p,true);
        computer = new Player(comp,true);
      }

      while(!gameOver){
      //while(true){
         if(p == 1){
            play(player, computer, in);
            gameOver = stateCheck(player, computer);
            play(computer, player, in);
            gameOver = stateCheck(player, computer);
         }
         else {
            play(computer, player, in);
            gameOver = stateCheck(player, computer);
            play(player, computer, in);
            gameOver = stateCheck(player, computer);
         }
      }
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

   public static void printBoard(){
    //save string?
      System.out.println("     A     B     C     D     E     F     G     H");
      System.out.println("   ================================================");
      for(int r = 0; r < size; r++){
         System.out.println("  |     |     |     |     |     |     |     |     |");
         System.out.print(r + " |");
         for(int c = 0; c < size; c++){
            if(board[r][c] == blank)
               System.out.print("     |");
            else
               System.out.print("  " + board[r][c] + "  |");
         }
         System.out.print(" " + r);
         System.out.println("\n  |     |     |     |     |     |     |     |     |");
         System.out.println("   -----------------------------------------------");
      }
      System.out.println("     A     B     C     D     E     F     G     H");
    //System.out.println("   ================================");
   }

   public static void printLegalMoves(){
    //System.out.println("printing legal moves " + legalMoves.size());
      int index = 0;
      for(Move object: legalMoves){
         if (index != 0){
            System.out.println("["+ index + "] " + object.toString());
            index++;
         }
         else
            index++;
      }
   }

   public static void play(Player current, Player enemy, Scanner in){
      validMove(current.symbol, enemy.symbol); //fill arraylist with possible moves
    //print board with legal moves
      printBoard();
      System.out.println("Current Scores: ");
      System.out.println(current.symbol + ": " + current.score);
      System.out.println(enemy.symbol + ": " + enemy.score);
      printLegalMoves();
      Move chosen;
      if(legalMoves.size() == 1){ //if there are no valid moves
         if(skipped)
            gameOver = true;
         else
            skipped = true;
      }

      else{
         skipped = false;

         if(!current.ai){ //if player is human
         //need to check for valid input
         // boolean entered = false;

            System.out.println("Pick a move!");

            chosen = legalMoves.get(in.nextInt());
         }
         else{ //if computer
            int random = (int)(Math.random()*(legalMoves.size()-1)) + 1; //not including zero
            chosen = legalMoves.get(random);
         }

         board[chosen.valid.row][chosen.valid.col] = current.symbol;
      //flip valid pieces

         flip(chosen, current, enemy);
         current.score++;
      }

   }
   public static void flip(Move move, Player current, Player enemy){
      int row = move.valid.row;
      int col = move.valid.col;
      Position[] pos = move.locations;

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
                  current.score++;
                  enemy.score--;
               }
            }
            //up
            else{
               while(r != p.row){
                  board[--r][c] = current.symbol;
                  current.score++;
                  enemy.score--;
               }
            }
         }

         //right or left
         else if(p.row == r){
         //right
            if(p.col > c){
               while(c != p.col){
                  board[r][++c] = current.symbol;
                  current.score++;
                  enemy.score--;
               }
            }
            //left
            else{
               while(c != p.col){
                  board[r][--c] = current.symbol;
                  current.score++;
                  enemy.score--;
               }
            }
         }
         //leftup or leftdown
         else if(p.col < c){
         //leftup
            if(p.row < r){
               while(r != p.row && c != p.col){
                  board[--r][--c] = current.symbol;
                  current.score++;
                  enemy.score--;
               }
            }
            //leftdown
            else{
               while(r != p.row && c != p.col){
                  board[++r][--c] = current.symbol;
                  current.score++;
                  enemy.score--;
               }
            }
         }
         //rightup or rightdown
         else if(p.col > c){
         //rightup
            if(p.row < r){
               while(r != p.row && c != p.col){
                  board[--r][++c] = current.symbol;
                  current.score++;
                  enemy.score--;
               }
            }
            //rightdown
            else{
               while(r != p.row && c != p.col){
                  board[++r][++col] = current.symbol;
                  current.score++;
                  enemy.score--;
               }
            }
         }
      }
   }

//find all legal moves and add to the legalMoves ArrayList
   public static void validMove(int player, int enemy){
   //loop through board
      legalMoves.clear(); //clear list
      legalMoves.add(null); //skip turn option
      for(int r = 0; r < size; r++)
         for(int c = 0; c < size; c++)
            if(board[r][c] == blank)
            //determine if valid move
            //add furthest flip location(s)
               validSearch(r, c, player, enemy);
   }
//returns index of valid move extent. null for no valid move in that direction
//not end of board and the next space contains an enemy
   public static void validSearch(int r, int c, int player, int enemy){
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
         return "("+ (valid.row) + "," + (valid.col) + ")";
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
      int symbol;
      boolean ai;
      int score = 2;
      public Player(int s, boolean comp){
         symbol = s;
         ai = comp;
      }
   }
}
