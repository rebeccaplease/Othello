import java.io.*;
import java.util.*;
//import java.awt.*;

public class Othello{
   static int size = 8;
   static int[][] board = new int[size][size];
   static ArrayList<Move> legalMoves = new ArrayList<Move>();
   static char blank = 0;

   public static void main(String[] args) throws FileNotFoundException{
      Scanner sc = new Scanner(new File("test2.txt"));
   
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
      char one = 1;
      char two = 2;
      printBoard();
      validMove(two, one);
      //print out legal moves
      for(Move object: legalMoves){
         System.out.println(object.toString());
      }
      
   }

   public static void printBoard(){
      for(int r = 0; r < size; r++){
         for(int c = 0; c < size; c++){
            System.out.print(board[r][c] + " ");
         }
         System.out.println();
      }
   }

	//find all legal moves and add to the legalMoves ArrayList
   public static void validMove(int player, int enemy){
   	//loop through board 
      for(int r = 0; r < size; r++){
         for(int c = 0; c < size; c++){
            if(board[r][c] == blank){
            
               boolean added = false;
            	//search left
               added = validSearch("left", r, c, player, enemy);
            
            	//search right
               if(!added)
                  added = validSearch("right", r, c, player, enemy);
            
            	//search below
               if(!added)
                  added = validSearch("down", r, c, player, enemy);
            
            	//search above
               if(!added)
                  added = validSearch("up", r, c, player, enemy);
            
            	//search diagonal left up
               if(!added)
                  added = validSearch("leftup", r, c, player, enemy);
            
            	//search diagonal left down
               if(!added)
                  added = validSearch("leftdown", r, c, player, enemy);
            
            	//search diagonal right up
               if(!added)
                  added = validSearch("rightup", r, c, player, enemy);
            
            	//search diagonal right down
               if(!added)
                  added = validSearch("rightdown", r, c, player, enemy);
            
            }
         }
      }
   }

   public static boolean validSearch(String dir, int r, int c, int player, int enemy){
   	//not end of board and the next space contains an enemy
      int orgR = r;
      int orgC = c;
      if(dir.equals("left")){
         while(c > 0) {  
            c--;
         
            if(board[r][c] == blank)
               return false;
            	//if friendly piece is encountered 
            else if(board[r][c] == player){
               //see if adjacent move is player
               if(Math.abs(orgC-c) == 1)
                  return false;
            		//add to list of valid moves (and directions?)
               legalMoves.add(new Move(orgR,orgC));
               return true;
            }
         }
         return false;
      }
      
      else if(dir.equals("right")){
         while(c < size-1){  
            c++;
         
            if (board[r][c] == blank)
               return false;
            	//if friendly piece is encountered 
            else if(board[r][c] == player){
               //see if adjacent move is player
               if(Math.abs(orgC-c) == 1)
                  return false;
            		//add to list of valid moves (and directions?)
               legalMoves.add(new Move(orgR,orgC));
               return true;
            }
         }
         return false;
      }
      else if(dir.equals("up")){
         while(r > 0){  
            r--;
            
            if (board[r][c] == blank)
               return false;
         		//if friendly piece is encountered 
            if(board[r][c] == player){
               //see if adjacent move is player
               if(Math.abs(orgR-r) == 1)
                  return false;
            		//add to list of valid moves (and directions?)
               legalMoves.add(new Move(orgR,orgC));
               return true;
            }
         }
      
         return false;
      }
      else if(dir.equals("down")){
         while(r > size-1){  
            r++;
            if (board[r][c] == blank)
               return false;
         		//if friendly piece is encountered 
            if(board[r][c] == player){
               //see if adjacent move is player
               if(Math.abs(orgR-r) == 1)
                  return false;
            		//add to list of valid moves (and directions?)
               legalMoves.add(new Move(orgR,orgC));
               return true;
            }
         }
         return false;
      }
      
      else if(dir.equals("leftup")){
         while(c > 0 && r > 0 ){  
            c--;
            r--;
            if (board[r][c] == blank)
               return false;
         		//if friendly piece is encountered 
            if(board[r][c] == player){
               //see if adjacent move is player
               if(Math.abs(orgR-r) == 1 && Math.abs(orgC-c) == 1)
                  return false;
            			//add to list of valid moves (and directions?)
               legalMoves.add(new Move(orgR,orgC));
               return true;
            }
         }
         return false;
      }
      else if(dir.equals("leftdown")){
         while(c > 0 && r > size-1){  
            c--;
            r++;
            if (board[r][c] == blank)
               return false;
         		//if friendly piece is encountered 
            if(board[r][c] == player){
               //see if adjacent move is player
               if(Math.abs(orgR-r) == 1 && Math.abs(orgC-c) == 1)
                  return false;
            			//add to list of valid moves (and directions?)
               legalMoves.add(new Move(orgR,orgC));
               return true;
            }
         }
         return false;
      }
      else if(dir.equals("rightup")){
         while(c < size-1 && r > 0 ){  
            c++;
            r--;
            if (board[r][c] == blank)
               return false;
         		//if friendly piece is encountered 
            if(board[r][c] == player){
               //see if adjacent move is player
               if(Math.abs(orgR-r) == 1 && Math.abs(orgC-c) == 1)
                  return false;
            			//add to list of valid moves (and directions?)
               legalMoves.add(new Move(orgR,orgC));
               return true;
            }
         }
         return false;
      }
      else if(dir.equals("rightdown")){
         while(c < size-1 && r > size-1 ){  
            c++;
            r++;
            if (board[r][c] == blank)
               return false;
         		//if friendly piece is encountered 
            if(board[r][c] == player){
               //see if adjacent move is player
               if(Math.abs(orgR-r) == 1 && Math.abs(orgC-c) == 1)
                  return false;
            			//add to list of valid moves (and directions?)
               legalMoves.add(new Move(orgR,orgC));
               return true;
            }
         }
         return false;
      }
      return false;
   }

   public static class Move{
      int row;
      int col;
      public Move(int r, int c){
         row = r;
         col = c;
      }
      public String toString(){
         return "["+row + "," + col + "]";
      }
   }
}