package main;

import java.awt.*;
import java.util.Random;

/**
 * @author Borja Vazquez
 * @version 1.1.2

 *   <p>IF YOU WANT TO ADD CUSTOM TETROMINOS YOU HAVE TO: (THE TETROMINO (FOR NOW) MUST BE 4x4)
 * <p>    1.1. ADD IT WITH THE DEFAULT POSITION TO THE ALLTETROMINOS ATTRIBUTE
 * <p>    1.2. ADD THE ROTATIONS WITH A NEW METHOD AND ADD IT TO SPIN TETROMINO
 * <p>    1.3. ADD THE COLOR TO THE CHOOSECOLORFORTETROMINO() METHOD
 * <p>    1.4. IN BOARD.JAVA, ADD ALSO THE COLOR TO THE CHOOSECOLORFORBLOCK() METHOD
 */

public class Tetromino {
    /**
     * By default 25, the width and height of each block
     */
    public static final int TETROMINO_BLOCK_SIZE = 25;
    /**
     * By default 4, the width and height the matrix
     */
    public static final int TETROMINO_MATRIX_SIZE = 4; // If this value is changed, every matrix MUST be changed to a TETROMINIO_MATRIX_SIZE x TETROMINIO_MATRIX_SIZE matrix

    private final int allTetrominios[][][] = {
            {{0,0,0,0},     //
             {0,0,0,0},     //
             {1,1,0,0},     // OO
             {1,1,0,0}},    // OO

            {{0,2,0,0},     // I
             {0,2,0,0},     // I
             {0,2,0,0},     // I
             {0,2,0,0}},    // I

            {{0,0,0,0},     //
             {0,0,0,0},     //
             {0,3,3,0},     //  SS
             {3,3,0,0}},    // SS

            {{0,0,0,0},     //
             {0,0,0,0},     //
             {4,4,0,0},     // ZZ
             {0,4,4,0}},    //  ZZ

            {{0,0,0,0},     //
             {0,5,0,0},     // L
             {0,5,0,0},     // L
             {0,5,5,0}},    // LL

            {{0,0,0,0},     //
             {0,0,6,0},     //  J
             {0,0,6,0},     //  J
             {0,6,6,0}},    // JJ

            {{0,0,0,0},     //
             {0,7,0,0},     //  T
             {7,7,7,0},     // TTT
             {0,0,0,0}}     //
    };

    private int currentTetrominio [][];
    private Color color;
    private int id;
    private int spin;
    private int xPos; // The (0, 0) of these two variables is the top left square of the board,
    private int yPos; // not the top left corner of the JFrame.

    /**
     * Constructor with no parameters. Generates first Tetromino of the game.
     */
    public Tetromino(){
        this.currentTetrominio = this.allTetrominios[new Random().nextInt(this.allTetrominios.length)];
        this.id = idGetter();
        this.spin = 0;
        this.xPos = 150;
        this.yPos = (getMatrixRowsWithoutBlocksOnDownHalf() - 1) * Tetromino.TETROMINO_BLOCK_SIZE;
        chooseColorForTetrominio();
    }

    /**
     * Constructor that takes the matrix of a Tetromino. It is used always except for the first Tetromino.
     * @param tetromino The matrix with default rotation of a Tetromino.
     */
    public Tetromino(int tetromino [][]){
        this.currentTetrominio = tetromino;
        this.id = idGetter();
        this.spin = 0;
        this.xPos = 150;
        this.yPos = (getMatrixRowsWithoutBlocksOnDownHalf() - 1) * Tetromino.TETROMINO_BLOCK_SIZE;
        chooseColorForTetrominio();
    }

    /**
     * Method to get the list of all default Tetrominos.
     * @return Returns the list of matrix of all the default Tetrominos.
     */
    public int [][][] getAllTetrominos(){
        return this.allTetrominios;
    }

    /**
     * Method to set which id the Tetromino will have, getting the first none zero value of the matrix
     * @return Returns the number to be set to the id
     */
    private int idGetter(){
        for(int i = 0; i < Tetromino.TETROMINO_MATRIX_SIZE; i++){
            for(int j = 0; j < Tetromino.TETROMINO_MATRIX_SIZE; j++){
                if (isBlock(i, j))
                    return this.currentTetrominio[i][j];
            }
        }
        return 0;
    }

    /**
     * Method to get the matrix of the current Tetromino.
     * @return Returns the matrix of the Tetromino.
     */
    public int [][] getCurrentTetrominio(){
        return this.currentTetrominio;
    }

    /**
     * Method to get the Tetromino's position in WORLD coordinates.
     * @return Returns the position of the Tetromino in WORLD coordinates in an array, [0] = x, [1] = y.
     */
    public int [] getPosition(){
        return new int[]{this.xPos, this.yPos};
    }

    /**
     * Method to get the Tetromino's color.
     * @return Returns the color of the Tetromino.
     */
    public Color getColor(){
        return this.color;
    }

    /**
     * Method to get the Tetromino's id.
     * @return Returns the id of the Tetromino.
     */
    public int getId(){
        return this.id;
    }

    /**
     * Method to set the Tetromino's position in WORLD coordinates.
     * @param position 2d array, [0] = x, [1] = y.
     */
    public void setPosition(int [] position){
        this.xPos = position[0];
        this.yPos = position[1];
    }

    /**
     * Method to check if a value of the matrix is block or not.
     * @param i The y in MATRIX coordinates.
     * @param j The x in MATRIX coordinates.
     * @return Returns true if the value in i, j is not zero. Otherwise returns false.
     */
    public boolean isBlock(int i, int j){
        return (currentTetrominio[i][j] != 0);
    }

    /**
     * Method to spin the Tetromino to a specific rotation depending on the rotation attribute.
     */
    public void spinTetrominio(){
        if (this.spin > 3) {
            this.spin = 0;
        }

        switch (this.id){
            case 2 : spinI(); break;
            case 3 : spinS(); break;
            case 4 : spinZ(); break;
            case 5 : spinL(); break;
            case 6 : spinJ(); break;
            case 7 : spinT(); break;
        }
        spin++;
    }

    /**
     * Method to spin the Tetromino with I shape.
     */
    private void spinI(){
        if (this.spin == 0) {
            this.currentTetrominio = new int[][]{{0,0,0,0},
                                                 {0,0,0,0},
                                                 {2,2,2,2},
                                                 {0,0,0,0}};
        } else if (this.spin == 1) {
            this.currentTetrominio = new int[][]{{0,0,2,0},
                                                 {0,0,2,0},
                                                 {0,0,2,0},
                                                 {0,0,2,0}};
        } else if (this.spin == 2) {
            this.currentTetrominio = new int[][]{{0,0,0,0},
                                                 {2,2,2,2},
                                                 {0,0,0,0},
                                                 {0,0,0,0}};
        } else {
            this.currentTetrominio = this.allTetrominios[1];
        }
    }

    /**
     * Method to spin the Tetromino with S shape.
     */
    private void spinS(){
        if (this.spin == 0) {
            this.currentTetrominio = new int[][]{{0,0,0,0},
                                                 {3,0,0,0},
                                                 {3,3,0,0},
                                                 {0,3,0,0}};
        } else if (this.spin == 1) {
            this.currentTetrominio = new int[][]{{0,0,0,0},
                                                 {0,3,3,0},
                                                 {3,3,0,0},
                                                 {0,0,0,0}};
        } else if (this.spin == 2) {
            this.currentTetrominio = new int[][]{{0,0,0,0},
                                                 {0,3,0,0},
                                                 {0,3,3,0},
                                                 {0,0,3,0}};
        } else {
            this.currentTetrominio = this.allTetrominios[2];
        }
    }

    /**
     * Method to spin the Tetromino with Z shape.
     */
    private void spinZ(){
        if (this.spin == 0) {
            this.currentTetrominio = new int[][]{
                    {0,0,0,0},
                    {0,4,0,0},
                    {4,4,0,0},
                    {4,0,0,0}};
        } else if (this.spin == 1) {
            this.currentTetrominio = new int[][]{
                    {0,0,0,0},
                    {4,4,0,0},
                    {0,4,4,0},
                    {0,0,0,0}};
        } else if (this.spin == 2) {
            this.currentTetrominio = new int[][]{
                    {0,0,0,0},
                    {0,0,4,0},
                    {0,4,4,0},
                    {0,4,0,0}};
        } else {
            this.currentTetrominio = this.allTetrominios[3];
        }
    }

    /**
     * Method to spin the Tetromino with L shape.
     */
    private void spinL(){
        if (this.spin == 0) {
            this.currentTetrominio = new int[][]{
                    {0,0,0,0},
                    {0,0,0,0},
                    {5,5,5,0},
                    {5,0,0,0}};
        } else if (this.spin == 1) {
            this.currentTetrominio = new int[][]{
                    {0,0,0,0},
                    {5,5,0,0},
                    {0,5,0,0},
                    {0,5,0,0}};
        } else if (this.spin == 2) {
            this.currentTetrominio = new int[][]{
                    {0,0,0,0},
                    {0,0,5,0},
                    {5,5,5,0},
                    {0,0,0,0}};
        } else {
            this.currentTetrominio = this.allTetrominios[4];
        }
    }

    /**
     * Method to spin the Tetromino with J shape.
     */
    private void spinJ(){
        if (this.spin == 0) {
            this.currentTetrominio = new int[][]{
                    {0,0,0,0},
                    {0,6,0,0},
                    {0,6,6,6},
                    {0,0,0,0}};
        } else if (this.spin == 1) {
            this.currentTetrominio = new int[][]{
                    {0,0,0,0},
                    {0,0,6,6},
                    {0,0,6,0},
                    {0,0,6,0}};
        } else if (this.spin == 2) {
            this.currentTetrominio = new int[][]{
                    {0,0,0,0},
                    {0,0,0,0},
                    {0,6,6,6},
                    {0,0,0,6}};
        } else {
            this.currentTetrominio = this.allTetrominios[5];
        }
    }

    /**
     * Method to spin the Tetromino with T shape.
     */
    private void spinT(){
        if (this.spin == 0) {
            this.currentTetrominio = new int[][]{
                    {0,0,0,0},
                    {0,7,0,0},
                    {0,7,7,0},
                    {0,7,0,0}};
        } else if (this.spin == 1) {
            this.currentTetrominio = new int[][]{
                    {0,0,0,0},
                    {0,0,0,0},
                    {7,7,7,0},
                    {0,7,0,0}};
        } else if (this.spin == 2) {
            this.currentTetrominio = new int[][]{
                    {0,0,0,0},
                    {0,7,0,0},
                    {7,7,0,0},
                    {0,7,0,0}};
        } else {
            this.currentTetrominio = this.allTetrominios[6];
        }
    }

    /**
     * Method to set the color of the Tetromino depending on the id attribute.
     */
    private void chooseColorForTetrominio(){
        switch (this.id){
            case 1 : this.color = Color.YELLOW;  break;
            case 2 : this.color = Color.CYAN;    break;
            case 3 : this.color = Color.RED;     break;
            case 4 : this.color = Color.GREEN;   break;
            case 5 : this.color = Color.ORANGE;  break;
            case 6 : this.color = Color.BLUE;    break;
            case 7 : this.color = Color.PINK;    break;
        }
    }

    /**
     * Method to make the Tetromino fall one row each tick of time (depends on the speed attribute).
     * @param speed By default set to 1. It's the number of rows to fall per tick.
     */
    public void applyGravity(int speed) {
        this.yPos += Tetromino.TETROMINO_BLOCK_SIZE * speed;
    }

    /**
     * Method to move the Tetromino to the right or left.
     * @param direction must be 1 (if want to move to the right) or -1 (if want to move to the left)
     */
    public void moveOnAxisX(int direction){
        this.xPos += Tetromino.TETROMINO_BLOCK_SIZE * direction;
    }

    /**
     * Method to get how much columns are there without any blocks, but just on the right side starting from middle.
     * @return Returns the number of columns that satisfy the condition.
     * @see #isBlock(int, int)
     */
    public int getMatrixColumnsWithoutBlocksOnRightHalf() {
        boolean add = true;
        int positionsWithoutBlocks = 0;

        for (int j = (Tetromino.TETROMINO_MATRIX_SIZE / 2); j < Tetromino.TETROMINO_MATRIX_SIZE; j++) {
            for(int i = 0; i < Tetromino.TETROMINO_MATRIX_SIZE; i++)
                if(isBlock(i, j))
                    add = false;
            if (add)
                positionsWithoutBlocks++;
            add = true;
        }
        return positionsWithoutBlocks;
    }

    /**
     * Method to get how much columns are there without any blocks, but just on the left side starting from middle.
     * @return Returns the number of columns that satisfy the condition.
     * @see #isBlock(int, int)
     */
    public int getMatrixColumnsWithoutBlocksOnLeftHalf() {
        boolean add = true;
        int positionsWithoutBlocks = 0;

        for (int j = 0; j < Tetromino.TETROMINO_MATRIX_SIZE - (Tetromino.TETROMINO_MATRIX_SIZE / 2); j++) {
            for(int i = 0; i < Tetromino.TETROMINO_MATRIX_SIZE; i++)
                if(isBlock(i, j))
                    add = false;
            if (add)
                positionsWithoutBlocks++;
            add = true;
        }
        return positionsWithoutBlocks;
    }

    /**
     * Method to get how much rows are there without any blocks, but just on the bottom side starting from middle.
     * @return Returns the number of rows that satisfy the condition.
     * @see #isBlock(int, int)
     */
    public int getMatrixRowsWithoutBlocksOnDownHalf(){
        boolean add = true;
        int positionsWithoutBlocks = 0;

        for (int i = Tetromino.TETROMINO_MATRIX_SIZE - 1; i > 1; i--) {
            for(int j = 0; j < Tetromino.TETROMINO_MATRIX_SIZE; j++)
                if(isBlock(i, j))
                    add = false;
            if (add)
                positionsWithoutBlocks++;
            add = true;
        }
        return positionsWithoutBlocks;
    }

    /**
     * Method to get how much rows are there without any blocks, but just on the up side starting from middle.
     * @return Returns the number of rows that satisfy the condition.
     * @see #isBlock(int, int)
     */
    public int getMatrixRowsWithoutBlocksOnUpHalf(){
        boolean add = true;
        int positionsWithoutBlocks = 0;

        for (int i = 0; i < Tetromino.TETROMINO_MATRIX_SIZE - (Tetromino.TETROMINO_MATRIX_SIZE / 2); i++) {
            for(int j = 0; j < Tetromino.TETROMINO_MATRIX_SIZE; j++)
                if(isBlock(i, j))
                    add = false;
            if (add)
                positionsWithoutBlocks++;
            add = true;
        }
        return positionsWithoutBlocks;
    }

    /**
     * Method to get how many blocks the Tetromino has.
     * @return Returns the number of blocks.
     * @see #isBlock(int, int)
     */
    public int getNumberOfBlocksInTetrominio(){
        int bloques = 0;
        for (int j = 0; j < Tetromino.TETROMINO_MATRIX_SIZE; j++)
            for(int i = 0; i < Tetromino.TETROMINO_MATRIX_SIZE; i++)
                if(isBlock(i, j))
                    bloques++;
        return bloques;
    }

    /**
     * Method to check if the player can still play or not.
     * @return Returns true if no block is lower or equals to zero on the y axis. Otherwise return false.
     */
    public boolean isAlive(){
        return (this.yPos - Tetromino.TETROMINO_BLOCK_SIZE * getMatrixRowsWithoutBlocksOnDownHalf()
                < Board.BOARD_HEIGHT_IN_BLOCKS * Tetromino.TETROMINO_BLOCK_SIZE);
    }

    /**
     * Method to get the position of each block of the Tetromino in MATRIX coordinates.
     * @return Returns the list of positions of each block in MATRIX coordinates.
     * @see #isBlock(int, int)
     * @see #getNumberOfBlocksInTetrominio()
     */
    public int[][] positionsToAttachToBoardMatrix(){
        int blocks = getNumberOfBlocksInTetrominio();
        int positions [][] = new int[blocks][2];
        int iPos = 0;
        for(int i = 0; i < Tetromino.TETROMINO_MATRIX_SIZE; i++)
            for(int j = 0; j < Tetromino.TETROMINO_MATRIX_SIZE; j++)
                if(isBlock(i, j)){
                    positions[iPos][0] = j;
                    positions[iPos][1] = i;
                    iPos++;
                }
        return positions;
    }

    /**
     * Method to get how many blocks the Tetromino has since the first block.
     * @return Returns the number blocks since the first one, not having in count how many empty columns are on the left side.
     * @see #getMatrixColumnsWithoutBlocksOnLeftHalf()
     * @see #getMatrixColumnsWithoutBlocksOnRightHalf()
     */
    public int numberOfBlocksFromFirstLeftBlock(){
        int blocks = 2 - getMatrixColumnsWithoutBlocksOnLeftHalf();
        blocks += 2 - getMatrixColumnsWithoutBlocksOnRightHalf();
        return blocks;
    }
}
