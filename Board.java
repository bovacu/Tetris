package main;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class Board extends JPanel implements Runnable{
    public static final int BOARD_WIDTH_IN_BLOCKS = 12;
    public static final int BOARD_HEIGHT_IN_BLOCKS = 23;

    private final int xOffsetBoard = 300;
    private final int yOffsetBoard = 100;

    private final int xScoreLabel = 170;
    private final int yScoreLabel = 460;
    private final int xScorePointsLabel = this.xScoreLabel + 25;
    private final int yScorePointsLabel = this.yScoreLabel + 10 + Tetromino.TETROMINO_BLOCK_SIZE;

    private final int xLevelLabel = 175;
    private final int yLevelLabel = 535;
    private final int xLevelValueLabel = this.xLevelLabel + 25;
    private final int yLevelValueLabel = this.yLevelLabel + 10 + Tetromino.TETROMINO_BLOCK_SIZE;

    private final int xLinesScoredLabel = 175;
    private final int yLinesScoredLabel = 610;
    private final int xLinesScoredValueLabel = this.xLinesScoredLabel + 20;
    private final int yLinesScoredValueLabel = this.yLinesScoredLabel + 10 + Tetromino.TETROMINO_BLOCK_SIZE;

    private Thread thread;
    private Tetromino tetromino;
    private Font font;
    private int boardMatrix [][];
    private int fourNextTetrominos [][][];

    private int score;
    private int level;
    private int speed;
    private int linesScored;
    private BufferedImage background;

    public Board(){
        super.setBackground(Color.BLACK);
        super.setFocusable(true);

        this.thread = new Thread(this);
        this.tetromino = new Tetromino();
        this.fourNextTetrominos = asignFourTetrominos();
        this.font = new Font("Dialog", Font.PLAIN, 20);
        this.boardMatrix = new int[Board.BOARD_HEIGHT_IN_BLOCKS][Board.BOARD_WIDTH_IN_BLOCKS];

        this.score = 0;
        this.level = 0;
        this.speed = 1000;
        this.linesScored = 0;

        try {
            background = ImageIO.read(new File("src/backgrounds/background3.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }


        this.thread.start();
        addControlsToGame();
    }

    private int [][][] asignFourTetrominos(){
        int tetrominos [][][] = new int [4][4][4];
        tetrominos[0] = tetromino.getAllTetrominos()[new Random().nextInt(tetromino.getAllTetrominos().length)];
        tetrominos[1] = tetromino.getAllTetrominos()[new Random().nextInt(tetromino.getAllTetrominos().length)];
        tetrominos[2] = tetromino.getAllTetrominos()[new Random().nextInt(tetromino.getAllTetrominos().length)];
        tetrominos[3] = tetromino.getAllTetrominos()[new Random().nextInt(tetromino.getAllTetrominos().length)];
        return tetrominos;
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        paintBoard(g);
        paintBackground(g);
        paintFourNextTetrominos(g);
        paintNextTetrominosLines(g);
        paintTexts(g);
        paintBoardMatrix(g);
        paintCurrentTetromino(g);
        paintBoardLines(g);
    }

    @Override
    public void run() {
        while (true) {
            if (!tetromino.isAlive() || tetrominoCollidesOnBottom())
                createNewTetromino();
            tetromino.applyGravity(1);
            repaint();
            try {
                Thread.sleep(this.speed);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void paintBoard(Graphics g){
        g.setColor(Color.BLACK);
        g.fillRect(this.xOffsetBoard, this.yOffsetBoard, Board.BOARD_WIDTH_IN_BLOCKS * Tetromino.TETROMINO_BLOCK_SIZE,
                Board.BOARD_HEIGHT_IN_BLOCKS * Tetromino.TETROMINO_BLOCK_SIZE);
    }

    private void paintCurrentTetromino(Graphics g){
        for (int i = 0; i < Tetromino.TETROMINO_MATRIX_SIZE; i++) {
            for (int j = 0; j < Tetromino.TETROMINO_MATRIX_SIZE; j++) {
                if (tetromino.isBlock(i, j)) {
                    g.setColor(tetromino.getColor());
                    g.fillRect(j * Tetromino.TETROMINO_BLOCK_SIZE + tetromino.getPosition()[0] + this.xOffsetBoard, i * Tetromino.TETROMINO_BLOCK_SIZE
                            + tetromino.getPosition()[1], Tetromino.TETROMINO_BLOCK_SIZE, Tetromino.TETROMINO_BLOCK_SIZE);
                }
            }
        }
    }

    private void paintFourNextTetrominos(Graphics g){
        int x = 650;
        int y = 150;

        for(int tetromino_[][] : this.fourNextTetrominos){
            for(int i = 0; i < Tetromino.TETROMINO_MATRIX_SIZE; i++){
                for(int j = 0; j < Tetromino.TETROMINO_MATRIX_SIZE; j++){
                    if (tetromino_[i][j] != 0) {
                        g.setColor(chooseColorForBlock(tetromino_[i][j]));
                        g.fillRect(j * Tetromino.TETROMINO_BLOCK_SIZE + x, i * Tetromino.TETROMINO_BLOCK_SIZE + y,
                                Tetromino.TETROMINO_BLOCK_SIZE, Tetromino.TETROMINO_BLOCK_SIZE);
                    }
                }
            }
            y += 125;
        }
    }

    private void paintBoardLines(Graphics g){
        g.setColor(Color.GRAY);
        for (int i = 0; i < Board.BOARD_HEIGHT_IN_BLOCKS; i++) {
            g.drawLine(this.xOffsetBoard, i * Tetromino.TETROMINO_BLOCK_SIZE + this.yOffsetBoard,
                    2 * Tetromino.TETROMINO_BLOCK_SIZE * Board.BOARD_WIDTH_IN_BLOCKS, i * Tetromino.TETROMINO_BLOCK_SIZE + this.yOffsetBoard);
        }

        for (int i = 0; i < Board.BOARD_WIDTH_IN_BLOCKS; i++) {
            g.drawLine(this.xOffsetBoard + i * Tetromino.TETROMINO_BLOCK_SIZE, this.yOffsetBoard,
                    i * Tetromino.TETROMINO_BLOCK_SIZE + this.xOffsetBoard, Tetromino.TETROMINO_BLOCK_SIZE * Board.BOARD_HEIGHT_IN_BLOCKS + this.yOffsetBoard);
        }
    }

    private void paintNextTetrominosLines(Graphics g){
        g.setColor(Color.GRAY);
        int x1 = this.xOffsetBoard + Board.BOARD_WIDTH_IN_BLOCKS * Tetromino.TETROMINO_BLOCK_SIZE +
                Tetromino.TETROMINO_BLOCK_SIZE * 2;
        int x2 = x1 + 4 * Tetromino.TETROMINO_BLOCK_SIZE;
        for (int i = 0; i < Board.BOARD_HEIGHT_IN_BLOCKS - 3; i++) {
            g.drawLine(x1, i * Tetromino.TETROMINO_BLOCK_SIZE + 125, x2, i * Tetromino.TETROMINO_BLOCK_SIZE + 125);
        }

        int nextTetrominosBoardWidth = 4;
        for (int i = 0; i < nextTetrominosBoardWidth; i++) {
            g.drawLine(x1 + Tetromino.TETROMINO_BLOCK_SIZE * i, Tetromino.TETROMINO_BLOCK_SIZE + this.yOffsetBoard,
                    x1 + Tetromino.TETROMINO_BLOCK_SIZE * i, this.yOffsetBoard
                            + (Board.BOARD_HEIGHT_IN_BLOCKS - 2) * Tetromino.TETROMINO_BLOCK_SIZE);
        }
    }

    private void paintTexts(Graphics g){
        g.setFont(this.font);
        g.setColor(Color.WHITE);

        g.drawString("SCORE", this.xScoreLabel, this.yScoreLabel);
        g.drawString(String.valueOf(this.score), this.xScorePointsLabel, this.yScorePointsLabel);

        g.drawString("LEVEL", this.xLevelLabel, this.yLevelLabel);
        g.drawString(String.valueOf(this.level), this.xLevelValueLabel, this.yLevelValueLabel);

        g.drawString("LINES", this.xLinesScoredLabel, this.yLinesScoredLabel);
        g.drawString(String.valueOf(this.linesScored), this.xLinesScoredValueLabel, this.yLinesScoredValueLabel);
    }

    private void paintBoardMatrix(Graphics g){
        for (int i = 0; i < Board.BOARD_HEIGHT_IN_BLOCKS; i++)
            for(int j = 0; j < Board.BOARD_WIDTH_IN_BLOCKS; j++){
                if(boardMatrix[i][j] != 0){
                    g.setColor(chooseColorForBlock(boardMatrix[i][j]));
                    g.fillRect(j *Tetromino.TETROMINO_BLOCK_SIZE + this.xOffsetBoard, i * Tetromino.TETROMINO_BLOCK_SIZE + this.yOffsetBoard
                            , Tetromino.TETROMINO_BLOCK_SIZE, Tetromino.TETROMINO_BLOCK_SIZE);
                }
            }
    }

    private void paintBackground(Graphics g){
        g.drawImage(this.background, 0, 0, null);
    }

    private Color chooseColorForBlock(int id){
        switch (id){
            case 1 : return Color.YELLOW;
            case 2 : return Color.CYAN;
            case 3 : return Color.RED;
            case 4 : return Color.GREEN;
            case 5 : return Color.ORANGE;
            case 6 : return Color.BLUE;
            case 7 : return Color.PINK;
            default: return null;
        }
    }

    //----------------------------------------------------------------------------------------------------------------\\

    private void addControlsToGame(){
        super.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                // throw new UnsupportedOperationException("Not supported yet."); //To change
                // body of generated methods, choose Tools | Templates.
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    spaceKeyPressed();
                } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    rightKeyPressed();
                } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    leftKeyPressed();
                } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    downKeyPressed();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                // throw new UnsupportedOperationException("Not supported yet."); //To change
                // body of generated methods, choose Tools | Templates.
            }

        });
    }

    private void spaceKeyPressed(){
        tetromino.spinTetrominio();
        int xCountingFromFirstBlockOnLeft = tetromino.getMatrixColumnsWithoutBlocksOnLeftHalf() *
                Tetromino.TETROMINO_BLOCK_SIZE + tetromino.getPosition()[0];
        if (xCountingFromFirstBlockOnLeft < 0)
            tetromino.setPosition(new int[]{0, tetromino.getPosition()[1]});

        else if (xCountingFromFirstBlockOnLeft + Tetromino.TETROMINO_BLOCK_SIZE *
                tetromino.numberOfBlocksFromFirstLeftBlock() > Tetromino.TETROMINO_BLOCK_SIZE *
                Board.BOARD_WIDTH_IN_BLOCKS){

            int dif = (xCountingFromFirstBlockOnLeft + Tetromino.TETROMINO_BLOCK_SIZE *
                    tetromino.numberOfBlocksFromFirstLeftBlock()) -
                    (Board.BOARD_WIDTH_IN_BLOCKS * Tetromino.TETROMINO_BLOCK_SIZE);
            tetromino.setPosition(new int[]{tetromino.getPosition()[0] - dif, tetromino.getPosition()[1]});
        }

        repaint();
    }

    private void rightKeyPressed(){
        int xCountingFromFirstBlockOnLeft = tetromino.getMatrixColumnsWithoutBlocksOnLeftHalf() *
                Tetromino.TETROMINO_BLOCK_SIZE + tetromino.getPosition()[0];

        if ((xCountingFromFirstBlockOnLeft < Tetromino.TETROMINO_BLOCK_SIZE * Board.BOARD_WIDTH_IN_BLOCKS -
                Tetromino.TETROMINO_BLOCK_SIZE * tetromino.numberOfBlocksFromFirstLeftBlock()) &&
                !checkIfTetrominoCollidesOnAxisX(1))
            tetromino.moveOnAxisX(1);

        repaint();
    }

    private void leftKeyPressed(){
        if ((tetromino.getPosition()[0] > 0 - Tetromino.TETROMINO_BLOCK_SIZE *
                tetromino.getMatrixColumnsWithoutBlocksOnLeftHalf()) && !checkIfTetrominoCollidesOnAxisX(-1))
            tetromino.moveOnAxisX(-1);

        repaint();
    }

    private void downKeyPressed(){
        if (!tetromino.isAlive() || tetrominoCollidesOnBottom())
            createNewTetromino();

        tetromino.applyGravity(1);
        repaint();
    }

    //----------------------------------------------------------------------------------------------------------------\\

    private boolean checkIfTetrominoCollidesOnAxisX(int direction){
        for(int blocksPositions [] : tetromino.positionsToAttachToBoardMatrix()){
            int positionInMatrixCoordinates [] = worldCoordinatesToBoardMatrixCoordinatesInBlocks(tetromino.getPosition());
            positionInMatrixCoordinates[0] += blocksPositions[0];
            positionInMatrixCoordinates[1] -= (Tetromino.TETROMINO_MATRIX_SIZE - 1) - blocksPositions[1];
            if(positionInMatrixCoordinates[0] > 0 && positionInMatrixCoordinates[0] < (Board.BOARD_WIDTH_IN_BLOCKS - 1)
                    && (positionInMatrixCoordinates[0] + direction) >= 0 && (positionInMatrixCoordinates[1]) >= 0) {
                if (boardMatrix[positionInMatrixCoordinates[1]][positionInMatrixCoordinates[0] + direction] != 0) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean tetrominoCollidesOnBottom(){
        for(int blocksPositions [] : tetromino.positionsToAttachToBoardMatrix()){
            int positionInMatrixCoordinates [] = worldCoordinatesToBoardMatrixCoordinatesInBlocks(tetromino.getPosition());
            positionInMatrixCoordinates[0] += blocksPositions[0];
            positionInMatrixCoordinates[1] -= (Tetromino.TETROMINO_MATRIX_SIZE - 1) - blocksPositions[1];
            if(positionInMatrixCoordinates[1] < (Board.BOARD_HEIGHT_IN_BLOCKS - 1) && (positionInMatrixCoordinates[1] + 1) >= 0) {
                if (this.boardMatrix[positionInMatrixCoordinates[1] + 1][positionInMatrixCoordinates[0]] != 0) {
                    return true;
                }
            }
        }
        return false;
    }

    //----------------------------------------------------------------------------------------------------------------\\

    private void createNewTetromino(){
        attachTetrominoToBoardMatrix(tetromino);
        removeRows();
        this.tetromino = new Tetromino(fourNextTetrominos[0]);
        reestructarateTetrominos();
    }

    private void reestructarateTetrominos(){
        int tet1 [][] = fourNextTetrominos[1];
        int tet2 [][] = fourNextTetrominos[2];
        int tet3 [][] = fourNextTetrominos[3];

        fourNextTetrominos[0] = tet1;
        fourNextTetrominos[1] = tet2;
        fourNextTetrominos[2] = tet3;

        fourNextTetrominos[3] = tetromino.getAllTetrominos()[new Random().nextInt(tetromino.getAllTetrominos().length)];

    }

    private int[] worldCoordinatesToBoardMatrixCoordinatesInBlocks(int[] positionInWorldCoordinates){
        int [] boardMatrixBlocksCoordinates = {positionInWorldCoordinates[0] / Tetromino.TETROMINO_BLOCK_SIZE,
                (positionInWorldCoordinates[1] / Tetromino.TETROMINO_BLOCK_SIZE) - 1};
        return boardMatrixBlocksCoordinates;
    }

    private void attachTetrominoToBoardMatrix(Tetromino tetromino){
        int tetrominoPosition [] = {tetromino.getPosition()[0], tetromino.getPosition()[1]};
        int blocksPositions [][] = tetromino.positionsToAttachToBoardMatrix();
        for(int[] rows : blocksPositions){
            int positionsInBoardMatrixBlocksCoordinates [] = worldCoordinatesToBoardMatrixCoordinatesInBlocks(tetrominoPosition);
            positionsInBoardMatrixBlocksCoordinates[0] += rows[0];
            positionsInBoardMatrixBlocksCoordinates[1] -= (Tetromino.TETROMINO_MATRIX_SIZE - 1) - rows[1];
            boardMatrix[positionsInBoardMatrixBlocksCoordinates[1]][positionsInBoardMatrixBlocksCoordinates[0]] = tetromino.getId();
        }
    }

    //----------------------------------------------------------------------------------------------------------------\\

    private void removeRows(){
        int filledRows [] = iPositionOfFilledRows();
        int firstRow = 0;
        if(filledRows.length > 0){
            firstRow = filledRows[0];
            this.score += scorePoints(filledRows.length);
            linesScored += filledRows.length;
            levelUp();
            for(int i : filledRows){
                for(int j = 0; j < Board.BOARD_WIDTH_IN_BLOCKS; j++){
                    this.boardMatrix[i][j] = 0;
                }
            }
            dropRow(firstRow);
        }
    }

    private int[] iPositionOfFilledRows(){
        ArrayList<Integer> rows = new ArrayList<>();
        for(int i = 0; i < Board.BOARD_HEIGHT_IN_BLOCKS; i++){
            boolean add = true;
            for(int j = 0; j < Board.BOARD_WIDTH_IN_BLOCKS; j++){
                if(this.boardMatrix[i][j] == 0)
                    add = false;
            }
            if(add)
                rows.add(i);
        }
        int arr [] = new int[rows.size()];
        for(int i = 0; i < rows.size(); i++){
            arr[i] = rows.get(i);
        }
        return arr;
    }

    private void dropRow(int firstRow){
        firstRow--;
        boolean noTetrominoOver = true;
        for(int i = firstRow; i > 0; i--){
            for (int j = 0; j < Board.BOARD_WIDTH_IN_BLOCKS; j++){
                if (this.boardMatrix[i][j] != 0)
                    noTetrominoOver = false;
                dropElement(i, j);
            }
            if (noTetrominoOver)
                break;
        }
        removeRows();
    }

    private void dropElement(int i, int j){
        int block = this.boardMatrix[i][j];
        while(this.boardMatrix[i + 1][j] == 0){
            this.boardMatrix[i][j] = 0;
            this.boardMatrix[i + 1][j] = block;
            if(i < Board.BOARD_HEIGHT_IN_BLOCKS - 2)
                i++;
            else
                break;
        }
    }

    //----------------------------------------------------------------------------------------------------------------\\

    private int scorePoints(int numberOfRows){
        switch (numberOfRows){
            case 1 : return 40*(this.level + 1);
            case 2 : return 100*(this.level + 1);
            case 3 : return 300*(this.level + 1);
            case 4 : return 1200*(this.level + 1);
        }
        return 0;
    }

    private void levelUp(){
        if(linesScored > this.level * 10 + 10){
            this.level++;
            if(this.speed >= 500){
                this.speed -= 100;
            }
        }
    }
}
