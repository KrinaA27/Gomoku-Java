/**
 * GomokuLogic.java
 * 
 * This class contains the core game logic for Gomoku. It determines the win condition,
 * and detects illegal moves such as three-three and four-four rule violations.
 * 
 * @author Krina Amin
 */

public class GomokuLogic{
  
  /** Number of rows on the board */
  private int rows;
  
  /** Number of columns on the board */
  private int columns;
  
  /** Number of tiles in a row required to win */
  private int toWin = 5;
  
  /** Directions to check for lines: vertical, horizontal, diagonal, and anti-diagonal */
  private final int[][] directions = {
    // vertical direction
    {1, 0}, 
    // horizontal direction
    {0, 1}, 
    // diagonal direction
    {1, 1}, 
    // perpendicular diagonal direction
    {1, -1} 
  };
  
  /**
   * Constructs the logic handler for Gomoku.
   *
   * @param toWin    number of tiles needed to win
   * @param rows     number of rows on the board
   * @param columns  number of columns on the board
   */
  public GomokuLogic(int toWin, int rows, int columns) {
    this.toWin = toWin;
    this.rows = rows;
    this.columns = columns;
  }
  
  /**
   * Evaluates the game board and determines if the current move is a win,
   * a three-three or four-four violation, or a normal move.
   *
   * @param board  the game board
   * @param row    row of the placed piece
   * @param col    column of the placed piece
   * @param player the current player (1 or 2)
   * @return a string representing the result: "win", "three-three", "four-four", or "normal"
   */
  public String checkWin(int[][] board, int row, int col, int player) {
    // stores count of open threes
    int openThrees = 0;
    // stores count of open fours
    int openFours = 0;
    // stores maximum number of connected tiles in any direction
    int totalInLine = 0;

    // Loop through all directions to analyze the board
    for (int[] dir : directions) {
      int dRow = dir[0];
      int dCol = dir[1];
      
      int inLine = numberInLine(board, row, col, dRow, dCol) + numberInLine(board, row, col, -dRow, -dCol) - 1;
      
      boolean open1 = isOpen(board, row, col, dRow, dCol);
      boolean open2 = isOpen(board, row, col, -dRow, -dCol);
      
      // Count open threes (3 in line with both ends open)
      if (inLine == toWin - 2 && open1 && open2) {
        openThrees++;
      }
      
      // Count open fours (4 in line with both ends open)
      else if (inLine == toWin - 1 && open1 && open2) {
        openFours++;
      }
      
      // Track longest line for possible win (ignoring overlines)
      if (inLine >= toWin) {
        totalInLine = Math.max(totalInLine, inLine);
      }
    }
   
    // Check rule violations before allowing a win
    if (openFours >= 2){
      return "four-four";
    }
    if (openThrees >= 2){
      return "three-three";
    }
    if (totalInLine == toWin){
      return "win";
    }
    return "normal";
  }
  
  /**
   * Counts the number of consecutive stones in a given direction starting from (row, col).
   *
   * @param board  the game board
   * @param row    starting row
   * @param col    starting column
   * @param dRow   row direction delta
   * @param dCol   column direction delta
   * @return the number of stones in line in the given direction
   */
  public int numberInLine(int[][] board, int row, int col, int dRow, int dCol) {
    // current player value
    int player = board[row][col];
    int count = 1;
    
    int r = row + dRow;
    int c = col + dCol;
    
    // Count consecutive stones of the same player
    while (r >= 0 && r < rows && c >= 0 && c < columns && board[r][c] == player) {
      count++;
      r += dRow;
      c += dCol;
    }
    return count;
  }
  
  /**
   * Checks if a line of same-player stones in a direction ends in an empty cell.
   *
   * @param board  the game board
   * @param row    starting row
   * @param col    starting column
   * @param dRow   row direction delta
   * @param dCol   column direction delta
   * @return true if the next cell after the line is empty, false otherwise
   */
  public boolean isOpen(int[][] board, int row, int col, int dRow, int dCol) {
    // current player value
    int player = board[row][col];
    
    int r = row + dRow;
    int c = col + dCol;
    
    // Move through same-player stones
    while (r >= 0 && r < rows && c >= 0 && c < columns && board[r][c] == player) {
      r += dRow;
      c += dCol;
    }
    
    // Return true if next cell is in bounds and empty
    return r >= 0 && r < rows && c >= 0 && c < columns && board[r][c] == 0;
  }
}
  