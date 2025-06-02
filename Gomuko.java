import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.scene.layout.CornerRadii;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import java.util.List;
import javafx.scene.shape.Circle;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.animation.ScaleTransition;
import javafx.util.Duration;

public class Gomoku extends Application{
  
  // this field stores the default value for the amount of columns on the board
  private int columns = 19;
  
  // this field stores the default value for the amount of rows on the board
  private int rows = 19;
  
  // this field stores the size of each tile
  int tileSize = 32;
  
  // this field stores extra space for label, button, and VBox spacing
  int labelAndButtonHeight = 100;
  
  /* this field stores the players' turns
   * if it is true, it is the first player's turn
   * if it is false, it is the second player's turn
   */
  private boolean isBlackTurn = true;
  
  // declares button array 
  private Button[][] boardButtons;
  
  // creates the text describing who's turn it is
  Label statusLabel = new Label("");
  
  // this field stores an array which tracks the state of the board after a stone is placed
  private int[][] boardState;
 
  // this field creates a GomokuLogic object
  private GomokuLogic gomokuLogic;
  
  // this field sets up the default number of stones needed to win
  private int toWin = 5;

  
  // this method allows the user to change the dimensions of the board
  public void setDimensions(int columns, int rows){
    this.columns = columns;
    this.rows = rows;
  }
  
  public void start(Stage primaryStage){
    // returns a Parameters object to get and use the user input
    List<String> args = getParameters().getRaw();
    // uses the list object in the parseArgs method to implement the game rules and board dimensions from user input
    parseArgs(args);
    // maps the buttons onto the array tracking the state of the board
    boardState = new int[rows][columns];
    // creates the gridpane for the buttons to be mapped to
    GridPane board = new GridPane();
    // sets the board up to look like a gomuko grid
    board.setPadding(new Insets(10));
    // centers the contents of the grid
    board.setAlignment(javafx.geometry.Pos.CENTER);
    // initializes the buttons array 
    boardButtons = new Button[rows][columns];
    // sets up logic
    gomokuLogic = new GomokuLogic(toWin, rows, columns);
    
     // this loop creates each button, styles them, and adds them to the gridpane
    for (int indexR = 0; indexR < rows; indexR++){
      for (int indexC = 0; indexC < columns; indexC++){
        
        // creates labels for the rows and columns, leaving the top left corner blank
        if (indexR == 0 && indexC !=0) {
          // labels the columns as A,B,C, ...
          javafx.scene.control.Label label = new javafx.scene.control.Label(String.valueOf((char)('A' + indexC - 1)));
          label.setStyle("-fx-font-weight: bold;");
          board.add(label, indexC, indexR);
        }
        
        // formats the rows
        else if (indexC == 0 && indexR !=0){
          // labels the rows as A,B,C, ...
          javafx.scene.control.Label label = new javafx.scene.control.Label(String.valueOf(indexR));
          label.setStyle("-fx-font-weight: bold;");
          board.add(label, indexC, indexR);
        }
       
        // creates the normal game tiles
        else if (indexR != 0 && indexC != 0){
          Button b = new Button();
          // sets the size of each tile
          b.setMinSize(30, 30);
          // prevents resizing when the graphic is added
          b.setPrefSize(30, 30);
          b.setMaxSize(30, 30);
          // formats the buttons to look like green tiles
          b.setStyle("-fx-background-color: #228B22; -fx-border-color: white; -fx-border-width: 0.5px;");
          // locks the values for the index per iteration to make them effectively final
          int finalR = indexR;
          int finalC = indexC;
          // boolean to keep track of tiles that have been used, using an array to make it effectively final
          boolean[] used = { false };
          
          // button action handler
          b.setOnAction(e -> {
            // do nothing if the button is already used
            if (used[0]){
              return;
            }
            else {
              // sets the button as used
              used[0] = true;
              // creates a circle graphic to simulate the black and white gomuko "stones"
              Circle stone = new Circle(12);
              // determines the stone color
              stone.setFill(isBlackTurn ? Color.BLACK : Color.WHITE); 
              // wraps the stone in StackPane to prevent layout shift
              StackPane wrapper = new StackPane(stone);
              // matches the button's size
              wrapper.setPrefSize(30, 30);
              wrapper.setMaxSize(30, 30);
              // places the stone wrapped in a StackPane as the button's graphic
              b.setGraphic(wrapper);
              b.setText("");
              // updates the state of the board 
              int player = isBlackTurn ? 1 : 2;
              boardState[finalR - 1][finalC - 1] = player;
              // loop for displaying a win screen and disabling all buttons after a player has won
              String result = gomokuLogic.checkWin(boardState, finalR - 1, finalC - 1, player);
              switch (result) {
                case "win":
                  // formatting of win text
                  statusLabel.setText("Player of " + (player == 1 ? "Black" : "White") + " Tiles Wins!");
                  statusLabel.setStyle("-fx-font-size: 35px; -fx-font-weight: bold;");
                  statusLabel.setMaxWidth(Double.MAX_VALUE);
                  statusLabel.setAlignment(javafx.geometry.Pos.CENTER);
                  // animating the status label upon win
                  ScaleTransition st = new ScaleTransition(Duration.millis(300), statusLabel);
                  st.setFromX(1);
                  st.setToX(1.2);
                  st.setFromY(1);
                  st.setToY(1.2);
                  st.setAutoReverse(true);
                  st.setCycleCount(2);
                  st.play();
                  disableBoard();
                  return;
                case "four-four":
                  boardState[finalR - 1][finalC - 1] = 0;
                  boardButtons[finalR - 1][finalC - 1].setGraphic(null);
                  used[0] = false;
                  statusLabel.setText("Illegal move: " + result + ". Try again.");
                  return;
                case "three-three":
                  boardState[finalR - 1][finalC - 1] = 0;
                  boardButtons[finalR - 1][finalC - 1].setGraphic(null);
                  used[0] = false;
                  statusLabel.setText("Illegal move: " + result + ". Try again.");
                  return;
                case "normal":
                  isBlackTurn = !isBlackTurn;
                  statusLabel.setText("Player of " + (isBlackTurn ? "Black" : "White") + " Tiles's turn");
                  break;
              }
              // calls animation method
              animateStonePlacement(stone);
            }
          });
          boardButtons[indexR - 1][indexC - 1] = b;
          board.add(b, indexC, indexR);
        }
      }
    }
    // creates another button to reset the game
    Button resetButton = new Button("New Game");
    // adds a style to the button
    resetButton.setStyle("-fx-font-size: 14px; -fx-background-color: #00CED1; -fx-text-fill: white;");
    // relaunches the game
    resetButton.setOnAction(e -> start(primaryStage));
    
    // declares a vertical layout with spacing
    VBox contentBox = new VBox(10);
    // centers it
    contentBox.setAlignment(javafx.geometry.Pos.CENTER);
    contentBox.getChildren().addAll(board, statusLabel, resetButton);
    // creates outer wrapper to center everything
    StackPane root = new StackPane(contentBox);
    // makes the label look better
    statusLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
    
    // sets up the scene and ensures there is enough space to see the board clearly
    
    // for smaller boards
    int minWidth = 450;
    int minHeight = 400;
    
    // for larger boards
    int calculatedWidth = columns * tileSize;
    int calculatedHeight = rows * tileSize + labelAndButtonHeight;
    
    int finalWidth = Math.max(calculatedWidth, minWidth);
    int finalHeight = Math.max(calculatedHeight, minHeight);
    Scene scene = new Scene(root, finalWidth, finalHeight);
    
    // sets the title of the stage 
    primaryStage.setTitle("Gomoku Game");
    // sets up the stage
    primaryStage.setScene(scene);
    // makes the stage visible
    primaryStage.show();
  }
  
  // takes a list of strings from user input to determine the game rules and dimensions of the board
  private void parseArgs(List<String> args) {
    // try-catch block to handle input that isn't aligned perfectly
    try {
      // if the user only provides one argument, it is treated as the number of connected blocks to win
      if (args.size() == 1) {
        int parsed = Integer.parseInt(args.get(0));
        if (parsed == 3) {
          System.out.println("Invalid arguments, 3 tiles to win violates game rules. Defaults used instead.");
          toWin = 5;
        } 
        else {
          toWin = parsed;
        } 
      } 
      // if the user provides two arguments, they are treated as the board dimensions
      else if (args.size() == 2) {
        rows = Integer.parseInt(args.get(0));
        columns = Integer.parseInt(args.get(1));
      } 
      // if the user provides three arguments: first is used for game rules and others for board dimensions
      else if (args.size() == 3) {
        int parsed = Integer.parseInt(args.get(0));
        if (parsed == 3) {
          System.out.println("Invalid arguments, 3 tiles to win violates game rules. Defaults used instead.");
          toWin = 5;
        }
        else {
          toWin = parsed;
          rows = Integer.parseInt(args.get(1));
          columns = Integer.parseInt(args.get(2));
        }
      }
      if (rows < 5 || columns < 5) {
        System.out.println("Invalid board size. Minimum is 5x5. Defaults used instead.");
        rows = 19;
        columns = 19;
      }
    } 
    // If any arguments can't be converted to a number, prints an error message and keeps defaults
    catch (NumberFormatException e) {
      System.out.println("Invalid arguments. Winning requirements and board dimensions will be the default.");
    }
  }

  // helper method that disables board after a win
  private void disableBoard() {
    for (int r = 0; r < rows - 1; r++) {
      for (int c = 0; c < columns - 1; c++){
        boardButtons[r][c].setDisable(true);
      }
    }
  }

  // animates the stone placement
  private void animateStonePlacement(Circle stone) {
    // creates a ScaleTransistion to show a change in the size of the stones for 200 miliseconds
    javafx.animation.ScaleTransition st = new javafx.animation.ScaleTransition(javafx.util.Duration.millis(200), stone);
    // starts the stone size at 0 
    st.setFromX(0);
    st.setFromY(0);
    // ends the stone size at 1 (full size)
    st.setToX(1);
    st.setToY(1);
    // lets the animation run only 1 time
    st.setCycleCount(1);
    // starts the animation
    st.play();
  }
  
  // main method launches the game as soon as it is run
  public static void main(String[] args) {
    launch(args);
  }  
                                                            
}
