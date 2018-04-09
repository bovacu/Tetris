package main;

import java.awt.*;
import java.util.Random;

/**
    IF YOU WANT TO ADD CUSTOM TETROMINOS YOU HAVE TO:
    1. THE TETROMINO (FOR NOW) MUST BE 4x4
        1.1. ADD IT WITH THE DEFAULT POSITION TO THE MATRIX
        1.2. ADD THE ROTATIONS WITH A NEW METHOD AND ADD IT TO SPIN TETROMINO
        1.3. ADD THE COLOR TO THE CHOOSECOLORFORTETROMINO() METHOD
        1.4. IN BOARD.JAVA, ADD ALSO THE COLOR TO THE CHOOSECOLORFORBLOCK() METHOD
 **/

public class Tetromino {
    public static final int TETROMINO_BLOCK_SIZE = 25;
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

    public Tetromino(){
        this.currentTetrominio = this.allTetrominios[new Random().nextInt(this.allTetrominios.length)];
        this.id = idGetter();
        this.spin = 0;
        this.xPos = 150;
        this.yPos = (Tetromino.TETROMINO_MATRIX_SIZE - getMatrixRowsWithoutBlocksOnUpHalf() - 1) * Tetromino.TETROMINO_BLOCK_SIZE;
        chooseColorForTetrominio();
    }

    public Tetromino(int tetromino [][]){
        this.currentTetrominio = tetromino;
        this.id = idGetter();
        this.spin = 0;
        this.xPos = 150;
        this.yPos = (Tetromino.TETROMINO_MATRIX_SIZE - getMatrixRowsWithoutBlocksOnUpHalf() - 1) * Tetromino.TETROMINO_BLOCK_SIZE;
        chooseColorForTetrominio();
    }

    public int [][][] getAllTetrominos(){
        return this.allTetrominios;
    }

    private int idGetter(){
        for(int i = 0; i < Tetromino.TETROMINO_MATRIX_SIZE; i++){
            for(int j = 0; j < Tetromino.TETROMINO_MATRIX_SIZE; j++){
                if (isBlock(i, j))
                    return this.currentTetrominio[i][j];
            }
        }
        return 0;
    }

    public int [][] getCurrentTetrominio(){
        return this.currentTetrominio;
    }

    public int [] getPosition(){
        return new int[]{this.xPos, this.yPos};
    }

    public Color getColor(){
        return this.color;
    }

    public int getId(){
        return this.id;
    }

    public void setPosition(int [] position){
        this.xPos = position[0];
        this.yPos = position[1];
    }

    public boolean isBlock(int i, int j){
        return (currentTetrominio[i][j] != 0);
    }

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

    public void applyGravity(int speed) {
        this.yPos += Tetromino.TETROMINO_BLOCK_SIZE * speed;
    }

    public void moveOnAxisX(int direction){
        this.xPos += Tetromino.TETROMINO_BLOCK_SIZE * direction;
    }

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

    public int getNumberOfBlocksInTetrominio(){
        int bloques = 0;
        for (int j = 0; j < Tetromino.TETROMINO_MATRIX_SIZE; j++)
            for(int i = 0; i < Tetromino.TETROMINO_MATRIX_SIZE; i++)
                if(isBlock(i, j))
                    bloques++;
        return bloques;
    }

    public boolean isAlive(){
        return (this.yPos - Tetromino.TETROMINO_BLOCK_SIZE * getMatrixRowsWithoutBlocksOnDownHalf()
                < Board.BOARD_HEIGHT_IN_BLOCKS * Tetromino.TETROMINO_BLOCK_SIZE);
    }

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

    public int numberOfBlocksFromFirstLeftBlock(){
        int blocks = 2 - getMatrixColumnsWithoutBlocksOnLeftHalf();
        blocks += 2 - getMatrixColumnsWithoutBlocksOnRightHalf();
        return blocks;
    }
}
