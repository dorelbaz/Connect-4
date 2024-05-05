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
    private final int RADIUS = 15, ROWS = 5, COLUMNS = 7;
    private final int PLAYER_1_CHOICE = 1, PLAYER_2_CHOICE = 2, NO_RESULT = 0, PLAYER1_WINS = 1, PLAYER2_WINS = 2, DEFAULT_VALUE = 0;
    private final int CONSECUTIVE_CELLS_3 = 3, MAX_CELLS_IN_ROW = 7, MAX_CELLS_IN_COLUMN = 5, MAX_CELLS_IN_DIAGON = 5;
    private int winner, availableChoices;
    private Pane[][] panes;
    private int[][] choiceMatrix;
    
    
    /*
     * Initializes parameters:
     * Default opening turn goes to player 1.
     * There are a total of 35 available choices.
     * Choice matrix keeps track of the cells each player has selected; 
     * each cell is given the arbitually, default value of 0.
     * Panes represents the game field.
     */
    public void initialize()
    {
        messageBox.setText("Player 1 turn");
        player1_Turn = true;
        int i = 0;
        winner = 0;
        availableChoices = 35;
        panes = new Pane[ROWS][COLUMNS];
        choiceMatrix = new int[ROWS][COLUMNS];
        
        for (int row = 0; row < ROWS; row++)
        {
            for (int col = 0; col < COLUMNS; col++)
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
        for (int row = 0; row < ROWS; row++) 
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
            Circle circle = new Circle(tempPane.getPrefWidth()/2, tempPane.getPrefHeight()/2,RADIUS);
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

        // Examines every move in the game with the choice matrix.
        examine_Game(selectedRow,selectedColumn);
    }
    
    /*
     * Examines the current game's state with the choice matrix.
     */
    private void examine_Game(int i, int j)
    {
        /*
         * Examines if either player has won or if game ended with a draw and sets the message and winner accordingly
         * and switches off buttons 1 to 7.
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
     * Examines whether for a given cell within the choice matrix, if there are 4 consecutive none-zero numbers, including the cell itself, in either of 8 directions.
     */
    private int examine_Directions(int i, int j)
    {
        int[] row = new int[MAX_CELLS_IN_ROW];
        int [] column = new int[MAX_CELLS_IN_COLUMN]; 
        int [] leftDiagon = new int[MAX_CELLS_IN_DIAGON];  
        int [] rightDiagon = new int[MAX_CELLS_IN_DIAGON];
        int result = NO_RESULT;
        
        // Initialises the arrays to 0's.
        init_Array(row);
        init_Array(column);
        init_Array(leftDiagon);
        init_Array(rightDiagon);
        
        // Gets the row of the selected cell, its column and the diagons relative to it from the choice matrix.
        get_Row(i,row);
        get_Column(j,column);
        get_Diagon(i,j,leftDiagon,true);
        get_Diagon(i,j,rightDiagon,false);
        
        // Examines each direction for the result. Whether player 1 won (result = 1), player 2 (result = 2) or no one (result = 0).
        result = examine_Cells(row);
        result = Math.max(result, examine_Cells(column));
        result =  Math.max(result, examine_Cells(leftDiagon));
        result =  Math.max(result, examine_Cells(rightDiagon));
        
        return result;
    }
    
    /*
     * Resets an array.
     */ 
    private void init_Array(int[] array)
    {
        for (int i = 0; i < array.length; i++)
        {
            array[i] = 0;
        }
    }
        
    /*
     * Gets the row of a given cell within the choice matrix.
     */
    private void get_Row(int i, int[] row)
    {
        for (int c = 0; c < row.length; c++)
        {
            row[c] = choiceMatrix[i][c];
        }
    }
    
    /*
     * Gets the column of a given cell within the choice matrix.
     */
    private void get_Column(int j, int[] column)
    {
        for (int r = 0; r < column.length; r++)
        {
            column[r] = choiceMatrix[r][j];
        }
    }
    
    
    /*
     * Gets the left and right diagons of a given cell within the choice matrix.
     */
    private void get_Diagon(int i, int j, int[] diagon, boolean getLeftDiagon)
    {
        int diagonStartingRow = i, diagonStartingColumn = j, r = 0, c = 0, d = 0;

        if (getLeftDiagon)
        {
            /*
             * The left diagon starting cell is the upper most left cell relative to the cell itself or the given cell itself.
             * Then, we get the diagon's cells left to right, top to bottom. The maximum cells contained in a left diagon is 5.
             */
            while (diagonStartingRow - 1 >= 0 && diagonStartingColumn - 1 >= 0)
            {
                diagonStartingRow--;
                diagonStartingColumn--;
            }
            
            r = diagonStartingRow; 
            c = diagonStartingColumn;
            while (r < ROWS && c < COLUMNS && d < diagon.length)
            {
                diagon[d] = choiceMatrix[r][c];
                r++;
                c++;
                d++;
            }            
        }
        else
        {
            /*
             * The right diagon starting cell is the upper most right cell relative to the cell itself or the given cell itself.
             * Then, we get the diagon's cell right to left, top to bottom. The maximum cells contained in a right diagon is 5.
             */  
            while (diagonStartingRow - 1 >= 0 && diagonStartingColumn + 1 < COLUMNS)
            {
                diagonStartingRow--;
                diagonStartingColumn++;
            }
            
            r = diagonStartingRow; 
            c = diagonStartingColumn;
            while (r < ROWS && 0 <= c && d < diagon.length)
            {
                diagon[d] = choiceMatrix[r][c];
                r++;
                c--;
                d++;
            }            
        }
    }
    
    /*
     * Examines if there are 4 consecutive 1's or 2's, representing player 1 and 2 choices, respectively. 
     */
    private int examine_Cells(int[] cells)
    {
        int result = NO_RESULT;  
        for (int c = 0; c + CONSECUTIVE_CELLS_3 < cells.length; c++)
        {
            if (cells[c] == 1 && cells[c+1] == 1 && cells[c+2] == 1 && cells[c+3] == 1)
            {
                result = PLAYER1_WINS;
            }
            if (cells[c] == 2 && cells[c+1] == 2 && cells[c+2] == 2 && cells[c+3] == 2)
            {
                result = PLAYER2_WINS;
            }
        }
        return result;
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
        for (int row = 0; row < ROWS; row++)
        {
            for (int col = 0; col < COLUMNS; col++)
            {
                panes[row][col].getChildren().clear();
            }
        }
        for (int row = 0; row < ROWS; row++)
        {
            for (int col = 0; col < COLUMNS; col++)
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
