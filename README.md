# Othello

Implements alpha-beta search with iterative deepening to find the best move. 

To run in the command line: 
'''
javac Othello.java
java Othello
'''
The option to start a new game[1] or load in a board[2] is given. 

Entering 1 will start a new game with the default starting board.  
Entering 2 will call loadBoard, which reads a space separated 8x8 board with rows on new lines. ‘1’ and ‘2’ should be used in the file for piece representation.  1’s will be read as # and 2’s will be read as Z. Any other character will be read as a blank space.  

The playing options are: go first, go second, play against a human player, or play the AI against itself using two different heuristics.  You can also choose which piece (Z or #) you want to play as.  For the AI vs. AI option, the # player uses the final version of the heuristic while the Z player uses the first iteration, so the # player should win most of the time. 

A move is chosen by inputting the number from the printed list of legal moves.  Skips are handled automatically.  The end of the game is checked after each move.  Two consecutive skips means that no moves are possible and thus the end of the game is reached.  If all the pieces are placed, then that also means the end of the game.

My heuristic weights the current player’s corner positions heavily and edge positions with a slightly lower value.  X or C positions (next to the corner) were weighed negatively.  Other moves were given a weight of one for each piece. The heuristic also subtracts the opposing player’s piece position advantages.     

