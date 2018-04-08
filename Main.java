package main;

import javax.swing.*;
import java.awt.*;

public class Main {

    static final int JFRAME_WIDTH = 900;
    static  final int JFRAME_HEIGHT = 795;

    public static void main(String args[]){
        JFrame frame = new JFrame("Basic Game");
        Board board;
        board = new Board();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(board);
        frame.setPreferredSize(new Dimension(JFRAME_WIDTH, JFRAME_HEIGHT));
        centerWindow(frame);
        frame.pack();
        //frame.setResizable(false);
        frame.setVisible(true);
        board.requestFocus();
    }

    public static void centerWindow(JFrame f){
        Dimension windowSize = f.getPreferredSize();
        Dimension pos = Toolkit.getDefaultToolkit().getScreenSize();

        int dx = (pos.width / 2) - (windowSize.width / 2);
        int dy = (pos.height / 2) - (windowSize.height / 2);
        f.setLocation(dx, dy);

    }
}
