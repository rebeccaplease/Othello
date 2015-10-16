import java.io.*;
import java.util.*;
//import java.awt.*;

public class Othello{
   static int size = 8;
   static int[][] board = new int[size][size];
   static ArrayList<Move> legalMoves = new ArrayList<Move>();
   static char blank = 0;

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
      System.out.println("Do you want to go first[1] or second[2]?");//need to catch invalid inputs
      int p = in.nextInt();
      char comp;
      Player player = new Player(p,false);
      Player computer;

      if(p == 1){
         comp = 2;
         computer = new Player(comp,true);
      }
      else{
         comp = 1;
         computer = new Player(comp,true);
      }

      //while(!gameOver()){
      while(true){
         if(p == 1){
            play(player, comp, in);
            //stateCheck();
            play(computer, p, in);
            //stateCheck();
         }
         else {
            play(computer, p, in);
            //stateCheck();
            play(player, comp, in);
            //stateCheck(); 
         }
      }
   }

   public static void printBoard(){
      //save string?
      System.out.println("    0   1   2   3   4   5   6   7");
      System.out.println("   ================================");
      for(int r = 0; r < size; r++){
         System.out.print(r + " |");
         for(int c = 0; c < size; c++){
            if(board[r][c] == blank)
               System.out.print("   |");
            else
               System.out.print(" " + board[r][c] + " |");
         }
         System.out.println("\n   --------------------------------");
      }
      //System.out.println("   ================================");
   }
   public static void printLegalMoves(){
      System.out.println("printing legal moves " + legalMoves.size());
      int index = 0;
      for(Move object: legalMoves){
         System.out.println("["+ index + "] " + object.toString());
         index++;
      }
   }

   public static void play(Player current, int enemy, Scanner in){
      validMove(current.symbol, enemy); //fill arraylist with possible moves
      if(legalMoves.size() == 0){ //if there are no valid moves

      }
      //print board with legal moves
      printBoard();
      printLegalMoves();
      Move chosen;
      if(!current.ai){ //if player is human 
         //need to check for valid input
         // boolean entered = false;

         System.out.println("Pick a move!");

         chosen = legalMoves.get(in.nextInt());
      }
      else{ //if computer
         int random = (int)(Math.random()*legalMoves.size());
         chosen = legalMoves.get(random);
      }
      board[chosen.row][chosen.col] = current.symbol;
      //flip valid pieces

      flip(chosen.row, chosen.col, current.symbol, enemy);

   }
   public static void flip(int r, int c, int player, int enemy){
      int row = r;
      int col = c;
      Move lastPos; //last valid player pos to flip pieces to
      lastPos = validSearch("down", r, c, player, enemy);
      if(lastPos != null){
         while(row != lastPos.row){
            board[++row][col] = player;
         }
         row = r;
      }

      lastPos = validSearch("up", r, c, player, enemy);
      if(lastPos != null){
         while(row != lastPos.row){
            board[--row][col] = player;
         }
         row = r;
      }

      lastPos = validSearch("left", r, c, player, enemy);
      if(lastPos != null){
         while(col != lastPos.col){
            board[row][--col] = player;
         }
         col = c;
      }

      lastPos = validSearch("right", r, c, player, enemy);
      if(lastPos != null){
         while(col != lastPos.col){
            board[row][++col] = player;
         }
         col = c;
      }

      lastPos = validSearch("leftup", r, c, player, enemy);
      if(lastPos != null){
         while(row != lastPos.row && col != lastPos.col){
            board[--row][--col] = player;
         }
         row = r;
         col = c;
      }
      lastPos = validSearch("rightup", r, c, player, enemy);
      if(lastPos != null){
         while(row != lastPos.row && col != lastPos.col){
            board[--row][++col] = player;
         }
         row = r;
         col = c;
      }

      lastPos = validSearch("leftdown", r, c, player, enemy);
      if(lastPos != null){
         while(row != lastPos.row && col != lastPos.col){
            board[++row][--col] = player;
         }
         row = r;
         col = c;
      }

      lastPos = validSearch("rightdown", r, c, player, enemy);
      if(lastPos != null){
         while(row != lastPos.row && col != lastPos.col){
            board[++row][++col] = player;
         }
         row = r;
         col = c;
      }
      
   }

  /* public static boolean flip(Move move, int player, int enemy, int row, int col){
      //base case
      //if out of bounds or space is blank, then it is not a valid direction
      if(row < 0 || row > size-1 || col < 0 || col > size-1 || board[row][col] == blank){
         return false;
      }

      if(board[row-1][col] == player ){
         //if the space away is player and not the adjacent position
         if(Math.abs(move.row - row) > 1|| Math.abs(move.col - col) > 1)
            return true;
      }
      else if(board[row-1][col] == enemy){
         //left
         if(flip(move, player, enemy, row-1, col)) //***
            board[row-1][col] = player;
      }

      if(board[row+1][col] == player ){
         if(Math.abs(move.row - row) > 1|| Math.abs(move.col - col) > 1)
            return true;
      }
      else if(board[row+1][col] == enemy){
         //right
         if(flip(move, player, enemy, row+1, col)) //****
            board[row+1][col] = player;
      }

      if(board[row][col-1] == player ){
         if(Math.abs(move.row - row) > 1|| Math.abs(move.col - col) > 1)
            return true;
      }
      else if(board[row][col-1] == enemy){
         //up
         if(flip(move, player, enemy, row, col-1))
            board[row][col-1] = player;
      }

      if(board[row][col+1] == player ){
         if(Math.abs(move.row - row) > 1|| Math.abs(move.col - col) > 1)
            return true;
      }
      else if(board[row][col+1] == enemy){
         //down
         if(flip(move, player, enemy, row, col+1))
            board[row][col+1] = player;
      }

      if(board[row-1][col-1] == player ){
         if(Math.abs(move.row - row) > 1|| Math.abs(move.col - col) > 1)
            return true;
      }
      else if(board[row-1][col-1] == enemy){
         //leftup
         if(flip(move, player, enemy, row-1, col-1))
            board[row-1][col-1] = player;
      }

      if(board[row-1][col+1] == player ){
         if(Math.abs(move.row - row) > 1|| Math.abs(move.col - col) > 1)
            return true;
      }
      else if(board[row-1][col+1] == enemy){
         //leftdown
         if(flip(move, player, enemy, row-1, col+1))
            board[row-1][col+1] = player;
      }

      if(board[row+1][col-1] == player ){
         if(Math.abs(move.row - row) > 1|| Math.abs(move.col - col) > 1)
            return true;
      }
      else if(board[row+1][col-1] == enemy){
         //rightup
         if(flip(move, player, enemy, row+1, col-1))
            board[row+1][col-1] = player;
      }

      if(board[row+1][col+1] == player ){
         if(Math.abs(move.row - row) > 1|| Math.abs(move.col - col) > 1)
            return true;
      }
      else if(board[row+1][col+1] == enemy){
         //rightup
         if(flip(move, player, enemy, row+1, col+1))
            board[row+1][col+1] = player;
      }
      
      return false;
   }
   */



	//find all legal moves and add to the legalMoves ArrayList
   public static void validMove(int player, int enemy){
   	//loop through board 
      legalMoves.clear(); //clear list
      for(int r = 0; r < size; r++){
         for(int c = 0; c < size; c++){
            if(board[r][c] == blank){

               Move added;
            	//search left
               added = validSearch("left", r, c, player, enemy);

            	//search right
               if(added == null)
                  added = validSearch("right", r, c, player, enemy);

            	//search below
               if(added == null)
                  added = validSearch("down", r, c, player, enemy);

            	//search above
               if(added == null)
                  added = validSearch("up", r, c, player, enemy);

            	//search diagonal left up
               if(added == null)
                  added = validSearch("leftup", r, c, player, enemy);

            	//search diagonal left down
               if(added == null)
                  added = validSearch("leftdown", r, c, player, enemy);

            	//search diagonal right up
               if(added == null)
                  added = validSearch("rightup", r, c, player, enemy);

            	//search diagonal right down
               if(added == null)
                  added = validSearch("rightdown", r, c, player, enemy);

            }
         }
      }
   }
   //returns index of valid move extent. null for no valid move in that direction
   public static Move validSearch(String dir, int r, int c, int player, int enemy){
   	//not end of board and the next space contains an enemy
      int orgR = r;
      int orgC = c;
      if(dir.equals("left")){
         while(c > 0) {  
            c--;

            if(board[r][c] == blank)
               return null;
            	//if friendly piece is encountered 
            else if(board[r][c] == player){
               //see if adjacent move is player
               if(Math.abs(orgC-c) == 1)
                  return null;
            		//add to list of valid moves (and directions?)
               legalMoves.add(new Move(orgR,orgC));
               return (new Move(r,c+1));
            }
         }
         return null;
      }

      else if(dir.equals("right")){
         while(c < size-1){  
            c++;

            if (board[r][c] == blank)
               return null;
            	//if friendly piece is encountered 
            else if(board[r][c] == player){
               //see if adjacent move is player
               if(Math.abs(orgC-c) == 1)
                  return null;
            		//add to list of valid moves (and directions?)
               legalMoves.add(new Move(orgR,orgC));
               return (new Move(r,c-1));
            }
         }
         return null;
      }
      else if(dir.equals("up")){
         while(r > 0){  
            r--;

            if (board[r][c] == blank)
               return null;
         		//if friendly piece is encountered 
            if(board[r][c] == player){
               //see if adjacent move is player
               if(Math.abs(orgR-r) == 1)
                  return null;
            		//add to list of valid moves (and directions?)
               legalMoves.add(new Move(orgR,orgC));
               return (new Move(r+1,c));
            }
         }
         return null;
      }
      else if(dir.equals("down")){
         while(r < size-1){  
            r++;
            if (board[r][c] == blank)
               return null;
         		//if friendly pieceis encountered 
            if(board[r][c] == player){
               //see if adjacent move is player
               if(Math.abs(orgR-r) == 1)
                  return null;
            		//add to list of valid moves (and directions?)
               legalMoves.add(new Move(orgR,orgC));
               return (new Move(r-1,c));
            }
         }
         return null;
      }

      else if(dir.equals("leftup")){
         while(c > 0 && r > 0 ){  
            c--;
            r--;
            if (board[r][c] == blank)
               return null;
         		//if friendly piece is encountered 
            if(board[r][c] == player){
               //see if adjacent move is player
               if(Math.abs(orgR-r) == 1 && Math.abs(orgC-c) == 1)
                  return null;
            			//add to list of valid moves (and directions?)
               legalMoves.add(new Move(orgR,orgC));
               return (new Move(r+1,c+1));
            }
         }
         return null;
      }
      else if(dir.equals("leftdown")){
         while(c > 0 && r > size-1){  
            c--;
            r++;
            if (board[r][c] == blank)
               return null;
         		//if friendly piece is encountered 
            if(board[r][c] == player){
               //see if adjacent move is player
               if(Math.abs(orgR-r) == 1 && Math.abs(orgC-c) == 1)
                  return null;
            			//add to list of valid moves (and directions?)
               legalMoves.add(new Move(orgR,orgC));
               return (new Move(r-1,c+1));
            }
         }
         return null;
      }
      else if(dir.equals("rightup")){
         while(c < size-1 && r > 0 ){  
            c++;
            r--;
            if (board[r][c] == blank)
               return null;
         		//if friendly piece is encountered 
            if(board[r][c] == player){
               //see if adjacent move is player
               if(Math.abs(orgR-r) == 1 && Math.abs(orgC-c) == 1)
                  return null;
            			//add to list of valid moves (and directions?)
               legalMoves.add(new Move(orgR,orgC));
               return (new Move(r+1,c-1));
            }
         }
         return null;
      }
      else if(dir.equals("rightdown")){
         while(c < size-1 && r > size-1 ){  
            c++;
            r++;
            if (board[r][c] == blank)
               return null;
         		//if friendly piece is encountered 
            if(board[r][c] == player){
               //see if adjacent move is player
               if(Math.abs(orgR-r) == 1 && Math.abs(orgC-c) == 1)
                  return null;
            			//add to list of valid moves (and directions?)
               legalMoves.add(new Move(orgR,orgC));
               return (new Move(r-1,c-1));
            }
            return null;
         }
      }
      return null;
   }

   public static class Move{
      int row;
      int col;
      public Move(int r, int c){
         row = r;
         col = c;
      }
      public String toString(){
         return "("+ (row) + "," + (col) + ")";
      }
   }
   public static class Player{
      int symbol;
      boolean ai;
      public Player(int s, boolean comp){
         symbol = s;
         ai = comp;
      }
   }
}
