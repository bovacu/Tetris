package main;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 * @author Borja Vazquez
 * @version 1.1.2
 */

public class Board extends JPanel implements Runnable{


    public static final int BOARD_WIDTH_IN_BLOCKS = 12;
    public static final int BOARD_HEIGHT_IN_BLOCKS = 23;

    private final int xOffsetBoard = 300;
    private final int yOffsetBoard = 100;

    private final int xScoreLabel = 175;
    private final int yScoreLabel = 460;
    private final int xScorePointsLabel = this.xScoreLabel + 25;
    private final int yScorePointsLabel = this.yScoreLabel + 10 + Tetromino.TETROMINO_BLOCK_SIZE;

    private final int xLevelLabel = 175;
    private final int yLevelLabel = 535;
    private final int xLevelValueLabel = this.xLevelLabel + 25;
    private final int yLevelValueLabel = this.yLevelLabel + 10 + Tetromino.TETROMINO_BLOCK_SIZE;

    private final int xLinesScoredLabel = 175;
    private final int yLinesScoredLabel = 610;
    private final int xLinesScoredValueLabel = this.xLinesScoredLabel + 25;
    private final int yLinesScoredValueLabel = this.yLinesScoredLabel + 10 + Tetromino.TETROMINO_BLOCK_SIZE;

    private JFrame frame;
    private Thread thread;
    private Tetromino tetromino;
    private Tetromino storedTetromino;
    private int boardMatrix [][];
    private int fourNextTetrominos [][][];

    private int score;
    private int level;
    private int speed;
    private int linesScored;
    private boolean alreadyPussedChangeTetromino;
    static boolean playerAlive;
    private BufferedImage background;
    private BufferedImage template;

    /**
     * Constructor to create the Board. Sets some attributes to default values, starts the Thread and loads images.
     * @param frame The main JFrame of the application
     */
    public Board(JFrame frame){
        super.setBackground(Color.BLACK);
        super.setFocusable(true);

        this.frame = frame;
        this.thread = new Thread(this);
        this.tetromino = new Tetromino();
        this.storedTetromino = null;
        this.fourNextTetrominos = assignFourTetrominos();
        this.boardMatrix = new int[Board.BOARD_HEIGHT_IN_BLOCKS][Board.BOARD_WIDTH_IN_BLOCKS];

        this.score = 0;
        this.level = 0;
        this.speed = 1000;
        this.linesScored = 0;
        this.alreadyPussedChangeTetromino = false;
        this.playerAlive = true;

        try {
            this.background = ImageIO.read(this.getClass().getResource("/backgrounds/background1.png"));
            this.template = ImageIO.read(this.getClass().getResource("/backgrounds/template.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.thread.start();
        addControlsToGame();
    }

    /**
     * Method to set the Tetrominos that will come after the current one.
     * @return a list of matrix of the four next randomly selected Tetrominos.
     */
    private int [][][] assignFourTetrominos(){
        int tetrominos [][][] = new int [4][4][4];
        tetrominos[0] = tetromino.getAllTetrominos()[new Random().nextInt(tetromino.getAllTetrominos().length)];
        tetrominos[1] = tetromino.getAllTetrominos()[new Random().nextInt(tetromino.getAllTetrominos().length)];
        tetrominos[2] = tetromino.getAllTetrominos()[new Random().nextInt(tetromino.getAllTetrominos().length)];
        tetrominos[3] = tetromino.getAllTetrominos()[new Random().nextInt(tetromino.getAllTetrominos().length)];
        return tetrominos;
    }

    /**
     * Method override from JPanel to paint everything in the game.
     * @param g references the Graphic object that paints everything.
     */
    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        paintBackground(g);
        paintFourNextTetrominos(g);
        paintStoredTetromino(g);
        paintStoredTetrominoLines(g);
        paintNextTetrominosLines(g);
        paintTexts(g);
        paintBoardMatrix(g);
        paintCurrentTetromino(g);
        paintBoardLines(g);
        Main.paintInfo(g);
        paintLoose(g);
    }

    /**
     * Method implemented from Runnable class that makes work all mechanics.
     */
    @Override
    public void run() {
        while (this.playerAlive) {
            if (!tetromino.isAlive() || tetrominoCollidesOnBottom()){
                createNewTetromino();
            }
            if(Board.playerAlive)
                tetromino.applyGravity(1);
            repaint();
            try {
                Thread.sleep(this.speed);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Method to paint the current Tetromino.
     * @param g references the Graphic object that paints everything.
     */
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

    /**
     * Method to paint the Tetrominos on the right column.
     * @param g references the Graphic object that paints everything.
     * @see #chooseColorForBlock(int)
     */
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

    /**
     * Method to paint each block of the Tetrominos attached to the Board matrix.
     * @param g references the Graphic object that paints everything.
     * @see #chooseColorForBlock(int)
     */
    private void paintStoredTetromino(Graphics g){
        int x = 150;
        int y = 125;
        if(this.storedTetromino != null)
            for(int i = 0; i < Tetromino.TETROMINO_MATRIX_SIZE; i++){
                for(int j = 0; j < Tetromino.TETROMINO_MATRIX_SIZE; j++){
                    if (this.storedTetromino.getCurrentTetrominio()[i][j] != 0) {
                        g.setColor(chooseColorForBlock(this.storedTetromino.getCurrentTetrominio()[i][j]));
                        g.fillRect(j * Tetromino.TETROMINO_BLOCK_SIZE + x +
                                        (1 - this.storedTetromino.getMatrixColumnsWithoutBlocksOnLeftHalf())
                                                * Tetromino.TETROMINO_BLOCK_SIZE
                                , i * Tetromino.TETROMINO_BLOCK_SIZE + y,
                                Tetromino.TETROMINO_BLOCK_SIZE, Tetromino.TETROMINO_BLOCK_SIZE);
                    }
                }
            }
    }

    /**
     * Method to paint the main rectangle grid lines.
     * @param g references the Graphic object that paints everything.
     */
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

    /**
     * Method to paint right rectangle grid lines of next Tetrominos.
     * @param g references the Graphic object that paints everything.
     */
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

    /**
     * Method to paint the right rectangle grid lines that stores a Tetromino.
     * @param g references the Graphic object that paints everything.
     */
    private void paintStoredTetrominoLines(Graphics g){
        g.setColor(Color.GRAY);
        int xOffsetToGoal = 150;
        int yOffsetToGoal = 150;
        int squareWidthAndHeight = 4;
        int x1 = xOffsetToGoal;
        int x2 = x1 + squareWidthAndHeight * Tetromino.TETROMINO_BLOCK_SIZE;

        for (int i = 0; i < squareWidthAndHeight; i++) {
            g.drawLine(x1, i * Tetromino.TETROMINO_BLOCK_SIZE + yOffsetToGoal, x2, i *
                    Tetromino.TETROMINO_BLOCK_SIZE + yOffsetToGoal);
        }

        int y1 = yOffsetToGoal;
        int y2 = yOffsetToGoal + squareWidthAndHeight * Tetromino.TETROMINO_BLOCK_SIZE;
        for (int i = 0; i < squareWidthAndHeight; i++) {
            g.drawLine(i * Tetromino.TETROMINO_BLOCK_SIZE + xOffsetToGoal, y1,
                    i * Tetromino.TETROMINO_BLOCK_SIZE + xOffsetToGoal, y2);
        }
    }

    /**
     * Method to paint the texts of the scene.
     * @param g references the Graphic object that paints everything.
     */
    private void paintTexts(Graphics g){
        g.setFont(Main.mainFont);
        g.setColor(Color.WHITE);

        g.drawString("SCORE", this.xScoreLabel, this.yScoreLabel);
        g.drawString(String.valueOf(this.score), this.xScorePointsLabel, this.yScorePointsLabel);

        g.drawString("LEVEL", this.xLevelLabel, this.yLevelLabel);
        g.drawString(String.valueOf(this.level), this.xLevelValueLabel, this.yLevelValueLabel);

        g.drawString("LINES", this.xLinesScoredLabel, this.yLinesScoredLabel);
        g.drawString(String.valueOf(this.linesScored), this.xLinesScoredValueLabel, this.yLinesScoredValueLabel);
    }

    /**
     * Method to paint the You Loose text.
     * @param g references the Graphic object that paints everything.
     */
    private void paintLoose(Graphics g){
        if(!Board.playerAlive){
            g.setFont(Main.loose);
            g.setColor(Color.WHITE);
            g.drawString("You Loose", (int)(Main.JFRAME_WIDTH / 2.5), Main.JFRAME_HEIGHT / 2);
        }
    }

    /**
     * Method to paint all the values of the matrix, with a specific color depending on the stored value.
     * @param g references the Graphic object that paints everything.
     * @see #chooseColorForBlock(int)
     */
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

    /**
     * Method to paint both images, the background and the template of the main rectangle, next Tetros rect and stored rect.
     * @param g references the Graphic object that paints everything.
     */
    private void paintBackground(Graphics g){
        g.drawImage(this.background, 0, 0, null);
        g.drawImage(this.template, 0, 0, null);
    }

    /**
     * Method to give a Color depending on the param.
     * @param id returns a Color depending on the id.
     * @return Returns the Color.
     */
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
    /**
     * Method to add a keyListener to the JPanel.
     * @see #spaceKeyPressed()
     * @see #rightKeyPressed()
     * @see #leftKeyPressed()
     * @see #downKeyPressed()
     * @see #upKeyPressed()
     * @see #cKeyPressed()
     */
    private void addControlsToGame(){
        super.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                // throw new UnsupportedOperationException("Not supported yet."); //To change
                // body of generated methods, choose Tools | Templates.
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if(Board.playerAlive)
                    if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                        spaceKeyPressed();
                    } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                        rightKeyPressed();
                    } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                        leftKeyPressed();
                    } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                        downKeyPressed();
                    } else if (e.getKeyCode() == KeyEvent.VK_UP){
                        upKeyPressed();
                    }else if (e.getKeyCode() == KeyEvent.VK_C){
                        cKeyPressed();
                    }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                // throw new UnsupportedOperationException("Not supported yet."); //To change
                // body of generated methods, choose Tools | Templates.
            }

        });
    }

    /**
     * Method to spin the Tetromino and ensures that on any spin, the Tetromino won't go out of the board.
     */
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

    /**
     * Method to move the Tetromino to the right and ensures it won't go out of the board on the right side.
     */
    private void rightKeyPressed(){
        int xCountingFromFirstBlockOnLeft = tetromino.getMatrixColumnsWithoutBlocksOnLeftHalf() *
                Tetromino.TETROMINO_BLOCK_SIZE + tetromino.getPosition()[0];

        if ((xCountingFromFirstBlockOnLeft < Tetromino.TETROMINO_BLOCK_SIZE * Board.BOARD_WIDTH_IN_BLOCKS -
                Tetromino.TETROMINO_BLOCK_SIZE * tetromino.numberOfBlocksFromFirstLeftBlock()) &&
                !checkIfTetrominoCollidesOnAxisX(1))
            tetromino.moveOnAxisX(1);

        repaint();
    }

    /**
     * Method to move the Tetromino to the left and ensures it won't go out of the board on the left side.
     */
    private void leftKeyPressed(){
        if ((tetromino.getPosition()[0] > 0 - Tetromino.TETROMINO_BLOCK_SIZE *
                tetromino.getMatrixColumnsWithoutBlocksOnLeftHalf()) && !checkIfTetrominoCollidesOnAxisX(-1))
            tetromino.moveOnAxisX(-1);

        repaint();
    }

    /**
     * Method to move the Tetromino faster down and ensures it won't go out of the board on bottom.
     * @see #tetrominoCollidesOnBottom()
     * @see #createNewTetromino()
     */
    private void downKeyPressed(){
        if (!tetromino.isAlive() || tetrominoCollidesOnBottom())
            createNewTetromino();

        tetromino.applyGravity(1);
        repaint();
    }

    /**
     * Method to move the Tetromino directly to the bottom.
     * @see #tetrominoCollidesOnBottom()
     * @see #createNewTetromino()
     */
    private void upKeyPressed(){
        while(!tetrominoCollidesOnBottom() && tetromino.isAlive()){
            tetromino.applyGravity(1);
        }
        createNewTetromino();
        repaint();
    }

    /**
     * Method to change the Tetromino and ensures that only one change can be done per play.
     * @see #storeTetromino()
     * @see #replaceTetromino()
     */
    private void cKeyPressed(){
        if (!this.alreadyPussedChangeTetromino){
            if (this.storedTetromino == null)
                storeTetromino();
            else
                replaceTetromino();

        }
    }

    //----------------------------------------------------------------------------------------------------------------\\

    /**
     * Method to check if the Tetromino will collide on the next move on X axis.
     * @param direction 1 if want to check right collision, -1 if want to detect left collision.
     * @return True if is going to collide, false if it's not going to collide.
     * @see #worldCoordinatesToBoardMatrixCoordinatesInBlocks(int[])
     */
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

    /**
     * Method to check if the Tetromino will collide on the row below with any block.
     * @return True if is going to collide, false if it's not going to collide.
     * @see #worldCoordinatesToBoardMatrixCoordinatesInBlocks(int[])
     */
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

    /**
     * Method to check if the Tetromino is going to go out of the board.
     * @return True if is going to go out, false if it's not.
     * @see #worldCoordinatesToBoardMatrixCoordinatesInBlocks(int[])
     */
    private boolean tetrominoOutOfBoard(){
        for(int blocksPositions [] : tetromino.positionsToAttachToBoardMatrix()){
            int positionInMatrixCoordinates [] = worldCoordinatesToBoardMatrixCoordinatesInBlocks(tetromino.getPosition());
            positionInMatrixCoordinates[1] -= (Tetromino.TETROMINO_MATRIX_SIZE - 1) - blocksPositions[1];
            if(positionInMatrixCoordinates[1] * Tetromino.TETROMINO_BLOCK_SIZE <= 0)
                return true;
        }
        return false;
    }

    //----------------------------------------------------------------------------------------------------------------\\

    /**
     * Method to create a new Tetromino.
     * @see #tetrominoOutOfBoard()
     * @see #attachTetrominoToBoardMatrix(Tetromino)
     * @see #removeRows()
     * @see #reestructarateTetrominos()
     * @see #addRetryAndMenuButton()
     */
    private void createNewTetromino(){
        if(!tetrominoOutOfBoard()){
            attachTetrominoToBoardMatrix(tetromino);
            removeRows();
            this.tetromino = new Tetromino(fourNextTetrominos[0]);
            this.alreadyPussedChangeTetromino = false;
            reestructarateTetrominos();
        }else{
            Board.playerAlive = false;
            addRetryAndMenuButton();
        }
    }

    /**
     * Method to store a tetromino for later so you can change it for your current one.
     * @see #reestructarateTetrominos()
     */
    private void storeTetromino(){
        this.storedTetromino = tetromino;
        this.alreadyPussedChangeTetromino = true;
        this.tetromino = new Tetromino(fourNextTetrominos[0]);
        reestructarateTetrominos();
    }

    /**
     * Method to change the current Tetromino with the one that is stored, if there's one stored.
     * @see #reestructarateTetrominos()
     */
    private void replaceTetromino(){
        Tetromino aux = this.tetromino;
        this.tetromino = new Tetromino(this.storedTetromino.getCurrentTetrominio());
        this.storedTetromino = aux;
        this.alreadyPussedChangeTetromino = true;
    }

    /**
     * Method to update the next Tetrominos each time a new Tetromino is created.
     */
    private void reestructarateTetrominos(){
        int tet1 [][] = fourNextTetrominos[1];
        int tet2 [][] = fourNextTetrominos[2];
        int tet3 [][] = fourNextTetrominos[3];

        fourNextTetrominos[0] = tet1;
        fourNextTetrominos[1] = tet2;
        fourNextTetrominos[2] = tet3;

        fourNextTetrominos[3] = tetromino.getAllTetrominos()[new Random().nextInt(tetromino.getAllTetrominos().length)];

    }

    /**
     * Method to convert WORLD coordinates to MATRIX coordinates.
     * @param positionInWorldCoordinates The position of the current Tetromino
     * @return An array that stores the Tetromino coordinates in MATRIX coordinates
     */
    private int[] worldCoordinatesToBoardMatrixCoordinatesInBlocks(int[] positionInWorldCoordinates){
        int [] boardMatrixBlocksCoordinates = {positionInWorldCoordinates[0] / Tetromino.TETROMINO_BLOCK_SIZE,
                (positionInWorldCoordinates[1] / Tetromino.TETROMINO_BLOCK_SIZE) - 1};
        return boardMatrixBlocksCoordinates;
    }

    /**
     * Method to put the current Tetromino's matrix's values into the board's matrix.
     * @param tetromino The current Tetromino
     * @see #worldCoordinatesToBoardMatrixCoordinatesInBlocks(int[])
     */
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

    /**
     * Method to remove rows when are full of not zeros.
     * @see #scorePoints(int)
     * @see #levelUp()
     * @see #dropRow(int)
     */
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

    /**
     * Method to get the y coordinate of each full row.
     * @return Returns an array of the row number of each full row.
     */
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

    /**
     * Method to drop all rows that are over the row that has been removed.
     * @param firstRow Y coordinate of the first row over the one removed.
     * @see #removeRows()
     * @see #dropElement(int, int)
     */
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

    /**
     * Method to drop each block as down as possible.
     * @param i The x position on the matrix.
     * @param j The y position on the matrix
     */
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

    /**
     * Method to return the score each time rows are removed.
     * @param numberOfRows The number of rows that has been removed.
     * @return The scored assign to the number of rows removed.
     */
    private int scorePoints(int numberOfRows){
        switch (numberOfRows){
            case 1 : return 40*(this.level + 1);
            case 2 : return 100*(this.level + 1);
            case 3 : return 300*(this.level + 1);
            case 4 : return 1200*(this.level + 1);
        }
        return 0;
    }

    /**
     * Method to increase the game level.
     */
    private void levelUp(){
        if(linesScored > this.level * 10 + 10){
            this.level++;
            if(this.speed >= 500){
                this.speed -= 100;
            }
        }
    }

    //----------------------------------------------------------------------------------------------------------------\\

    /**
     * Method to add the buttons of retry and return when you loose.
     */
    private void addRetryAndMenuButton(){
        int buttonsWidth = 175;
        int buttonsHeight = 65;
        int yOffset = Main.JFRAME_HEIGHT / 3;
        int spacing = (int)(buttonsHeight * 1.5f);

        JButton retry = new JButton("RETRY");
        retry.setFont(Main.mainFont);

        retry.setBounds(Main.JFRAME_WIDTH / 2 - buttonsWidth / 2, Main.JFRAME_HEIGHT / 2 + buttonsHeight - yOffset,
                buttonsWidth, buttonsHeight);
        retry.addActionListener(e -> Main.loadScene(this.frame, new Board(this.frame)));

        JButton back = new JButton("BACK");
        back.setFont(Main.mainFont);

        back.setBounds(Main.JFRAME_WIDTH / 2 - buttonsWidth / 2, retry.getY() + spacing, buttonsWidth, buttonsHeight);
        back.addActionListener(e -> Main.loadScene(this.frame, new MainMenu(this.frame)));

        super.setLayout(null);
        super.add(retry);
        super.add(back);
    }
}
