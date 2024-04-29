import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Circle;
import javafx.scene.paint.Color;
import javafx.scene.layout.Pane;
import javafx.scene.Node;
import javafx.scene.Group;

public class Connect4Controller 
{
    @FXML
    private Button clear, col1, col2, col3, col4, col5, col6, col7;
    
    @FXML
    private Pane pane00, pane01, pane02, pane03, pane04, pane05, pane06,
                 pane10, pane11, pane12, pane13, pane14, pane15, pane16,
                 pane20, pane21, pane22, pane23, pane24, pane25, pane26,
                 pane30, pane31, pane32, pane33, pane34, pane35, pane36,
                 pane40, pane41, pane42, pane43, pane44, pane45, pane46;

    @FXML
    private Label messageBox;
    
    @FXML
    private GridPane gameField;
    
    @FXML
    private Group paneGroup;
    
    private boolean player1_Turn;
    private final boolean ON = false, OFF = true;
    private final int radius = 15, rows = 5, columns = 7;
    private final int PLAYER_1_CHOICE = 1, PLAYER_2_CHOICE = 2, NO_RESULT = 0, PLAYER1_WINS = 1, PLAYER2_WINS = 2,
                      DEFAULT_VALUE = 10, PLAYER1_WINNING_SUM = 4, PLAYER2_WINNING_SUM = 8;
    private final int CONSECUTIVE_CELLS_3 = 3, LEFT = -1, RIGHT = 1, UP = 1, DOWN = -1, DIRECTIONS = 8;
    private int winner, availableChoices;
    private Pane[][] panes;
    private int[][] choiceMatrix;
    
    
    /*
     * Initializes parameters:
     * Default opening turn goes to player 1.
     * There are a total of 35 available choices.
     * Choice matrix keeps track of the cells each player has selected; 
     * each cell is given the arbitually, default value of 10.
     * Panes represents the game field.
     */
    public void initialize()
    {
        messageBox.setText("Player 1 turn");
        player1_Turn = true;
        int i = 0;
        winner = 0;
        availableChoices = 35;
        panes = new Pane[rows][columns];
        choiceMatrix = new int[rows][columns];
        
        for (int row = 0; row < rows; row++)
        {
            for (int col = 0; col < columns; col++)
            {
                panes[row][col] = ((Pane)paneGroup.getChildren().get(i));
                choiceMatrix[row][col] = DEFAULT_VALUE;
                i++;
            }
        }
    }

    /*
     * This function is activated when either buttons 1 to 7 are pressed.
     */
    @FXML
    void onButtonPressed(ActionEvent event) 
    {
        Pane tempPane = null;
        boolean hasEmptyCell = false;
        String buttonPressed =((Button)event.getSource()).getText();
        int selectedColumn = Integer.parseInt(buttonPressed) - 1;
        int selectedRow = 0;
        
        /*
         * Examines if there is an empty pane within the selected column,             
         * if yes, then saves it to temp with its row and sets a flag accordingly.
         */
        for (int row = 0; row < rows; row++) 
        {
            if (panes[row][selectedColumn].getChildren().size() == 0)
            {
                tempPane = panes[row][selectedColumn];
                selectedRow = row;
                hasEmptyCell = true;
            }
        }
        
        /*
           If there is an empty pane within the selected column;
           creates a circle that will fill the middle of the pane,
           if it's player 1 turn: makes the circle red and sets the corresponding cell in choice matrix to 1 or
           if it's player 2 turn: makes the circle blue and sets the corresponding cell in choice matrix to 2,
           decreases available choices by 1.           
        */ 
        if (hasEmptyCell) 
        {
            Circle circle = new Circle(tempPane.getPrefWidth()/2, tempPane.getPrefHeight()/2,radius);
            if (player1_Turn)
            {
                circle.setFill(Color.RED);
                choiceMatrix[selectedRow][selectedColumn] = PLAYER_1_CHOICE;
                messageBox.setText("Player 2 turn");
                player1_Turn = false;
            }
            else
            {
                circle.setFill(Color.BLUE);
                choiceMatrix[selectedRow][selectedColumn] = PLAYER_2_CHOICE;
                messageBox.setText("Player 1 turn");
                player1_Turn = true;
            }
            tempPane.getChildren().add(circle);
            availableChoices--;
        }
        else
        {
            messageBox.setText("Invalid Choice");
        }

        examine_Game(selectedRow,selectedColumn);
    }
    
    /*
     * Examines the current game's state.
     */
    private void examine_Game(int i, int j)
    {
        /*
         * Examines if either player has won or if game ended with a draw and sets the message and winner accordingly
         * and switches off the buttons 1 to 7.
         */
        int result = examine_Directions(i,j);
        if (result != NO_RESULT)
        {
            if (result == PLAYER1_WINS)
            {
                messageBox.setText("Player 1 wins!!!");
                winner = 1;
            }
            if (result == PLAYER2_WINS)
            {
                messageBox.setText("Player 2 wins!!!");
                winner = 2;
            }
            clear.setText("New Game");
            switch_Buttons(OFF); 
        }
        if (availableChoices == 0)
        {
            messageBox.setText("Draw!!!");
            clear.setText("New Game");
            winner = 0;
            switch_Buttons(OFF);
        }  
    }
    
    /*
     * Sums the selected pane with its 3 neighboring panes in either of 8 directions and compares the sums to the winning sums.
     */
    private int examine_Directions(int i, int j)
    {
        /*
         * If possible, sums the selected cell with the 3 consecutive cells in either direction.
         * If a direction can not be summed then this direction's sum will be 0 by default.
         * We sum the directions in a clock-wise fashion.
         * Then, we compare each sum to the winning sums: player 1 winning sum is 4 (1+1+1+1) and player 2 winning sum is 8 (2+2+2+2).
         */
        
        int[] directions = new int[DIRECTIONS];
        int result = NO_RESULT;
        
        directions[0] = sumColumn(i,j,UP);
        directions[1] = sumDiagon(i,j,RIGHT,UP);
        directions[2] = sumRow(i,j,RIGHT);
        directions[3] = sumDiagon(i,j,RIGHT,DOWN);
        directions[4] = sumColumn(i,j,DOWN);
        directions[5] = sumDiagon(i,j,LEFT,DOWN);
        directions[6] = sumRow(i,j,LEFT);
        directions[7] = sumDiagon(i,j,LEFT,UP);
        
        for (int d = 0; d < DIRECTIONS; d++)
        {
            if (directions[d] == PLAYER1_WINNING_SUM)
            {
                result = PLAYER1_WINS;
            }
            if (directions[d] == PLAYER2_WINNING_SUM)
            {
                result = PLAYER2_WINS;
            }
        }
        return result;
    }
    
    /*
     * If possible, sums the 4 consecutive cells in either direction, left or right, else returns the default sum of 0.
     */
    private int sumRow(int i, int j, int direction)
    {
        int sum = 0;
        if (direction == RIGHT)
        {
            if (j + CONSECUTIVE_CELLS_3 < columns)
            {
                for (int c = 0; c < CONSECUTIVE_CELLS_3 + 1; c++)
                {
                    sum += choiceMatrix[i][j+c];
                }            
            }            
        }
        else
        {
            if (0 < j - CONSECUTIVE_CELLS_3)
            {
                for (int c = 0; c < CONSECUTIVE_CELLS_3 + 1; c++)
                {
                    sum += choiceMatrix[i][j-c];
                }            
            }                  
        }
        return sum;
    }
    
    /*
     * If possible, sums the 4 consecutive cells in either direction, up or down, else returns the default sum of 0.
     */
    private int sumColumn(int i, int j, int direction)
    {
        int sum = 0;
        if (direction == UP && 0 <= i - CONSECUTIVE_CELLS_3)
        {
            for (int r = 0; r < CONSECUTIVE_CELLS_3 + 1; r++)
            {
                sum += choiceMatrix[i-r][j];
            }            
        }
        else
        {
            if (i + CONSECUTIVE_CELLS_3 < rows)
            {
                for (int r = 0; r < CONSECUTIVE_CELLS_3 + 1; r++)
                {
                    sum += choiceMatrix[i+r][j];
                }            
            }                  
        }
        return sum;
    }
    
    
    /*
     * If possible, sums the 4 consecutive cells in either of 4 directions; right and up,right and down, left and up or left and down.
     * Else returns the default sum of 0.
     */
    private int sumDiagon(int i, int j, int directionX, int directionY)
    {
        int sum = 0;
        if (directionX == RIGHT && directionY == UP && 0 <= i - CONSECUTIVE_CELLS_3 && j + CONSECUTIVE_CELLS_3 < columns)
        {
            for (int r = 0, c = 0; r < CONSECUTIVE_CELLS_3 + 1; r++, c++)
            {
                sum += choiceMatrix[i-r][j+c];
            }                 
        }
        else if (directionX == RIGHT && directionY == DOWN && i + CONSECUTIVE_CELLS_3 < rows && j + CONSECUTIVE_CELLS_3 < columns)
        {
            for (int r = 0, c = 0; r < CONSECUTIVE_CELLS_3 + 1; r++, c++)
            {
                sum += choiceMatrix[i+r][j+c];
            }                  
        }
        else if (directionX == LEFT && directionY == UP && 0 <= i - CONSECUTIVE_CELLS_3 && 0 <= j - CONSECUTIVE_CELLS_3)
        {
            for (int r = 0, c = 0; r < CONSECUTIVE_CELLS_3 + 1; r++, c++)
            {
                sum += choiceMatrix[i-r][j-c];
            }              
        }
        else
        {
             if (i + CONSECUTIVE_CELLS_3 < rows && 0 <= j - CONSECUTIVE_CELLS_3)
            {
                for (int r = 0, c = 0; r < CONSECUTIVE_CELLS_3 + 1; r++, c++)
                {
                    sum += choiceMatrix[i+r][j-c];
                }              
            }            
        }
        return sum;
    }

    /*
     * This function is activated when the Clear/New Game button is pressed.
     */
    @FXML
    void onClearPressed(ActionEvent event) 
    {
        /*
         * Resets the game field, the choice matrix and the total available choices.
         * If New Game has been pressed, then the next game's opening turn will be given to the winning player,
         * or player 1 if game ended with a draw, and switches on the buttons 1 to 7.
         * If Clear has been pressed, then the next game's opening turn will be given by default to player 1.
         */
        for (int row = 0; row < rows; row++)
        {
            for (int col = 0; col < columns; col++)
            {
                panes[row][col].getChildren().clear();
            }
        }
        for (int row = 0; row < rows; row++)
        {
            for (int col = 0; col < columns; col++)
            {
                choiceMatrix[row][col] = DEFAULT_VALUE;
            }
        }
        availableChoices = 35;
        
        if (clear.getText().equals("New Game"))
        {
            clear.setText("Clear");
            if (winner == 2)
            {
                player1_Turn = false;
                messageBox.setText("Player 2 turn");
            }
            else
            {
                player1_Turn = true;
                messageBox.setText("Player 1 turn");
            }
            switch_Buttons(ON); 
        }
        else
        {
            player1_Turn = true;
            messageBox.setText("Player 1 turn");           
        }
    }
    
    /*
     * Switches on or off the buttons 1 to 7.
     */
    private void switch_Buttons(boolean mode)
    {
        col1.setDisable(mode);
        col2.setDisable(mode);
        col3.setDisable(mode);
        col4.setDisable(mode);
        col5.setDisable(mode);
        col6.setDisable(mode);
        col7.setDisable(mode);
    }
}
