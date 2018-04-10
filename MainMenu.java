package main;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * @author Borja Vazquez
 * @version 1.1.2
 */

public class MainMenu extends JPanel {
    private BufferedImage background;
    private JFrame frame;

    private JButton play;
    private JButton highscores;
    private JButton options;

    private int buttonsWidth;
    private int buttonsHeight;

    private int yOffset;
    private int spacing;

    public MainMenu(JFrame frame){
        super.setLayout(null);
        this.buttonsWidth = 175;
        this.buttonsHeight = 65;
        this.yOffset = Main.JFRAME_HEIGHT / 3;
        this.spacing = (int)(this.buttonsHeight * 1.5f);
        this.frame = frame;
        this.play = new JButton("PLAY");
        this.play.setFont(Main.mainFont);
        this.highscores = new JButton("HIGH SCORES");
        this.highscores.setFont(Main.mainFont);
        this.options = new JButton("OPTIONS");
        this.options.setFont(Main.mainFont);

        this.play.setBounds(Main.JFRAME_WIDTH / 2 - this.buttonsWidth / 2, Main.JFRAME_HEIGHT / 2 +
                        this.buttonsHeight - this.yOffset,
                this.buttonsWidth, this.buttonsHeight);

        this.highscores.setBounds(Main.JFRAME_WIDTH / 2 - this.buttonsWidth / 2, this.play.getY() + this.spacing,
                this.buttonsWidth, this.buttonsHeight);

        this.options.setBounds(Main.JFRAME_WIDTH / 2 - this.buttonsWidth / 2, this.highscores.getY() + this.spacing,
                this.buttonsWidth, this.buttonsHeight);

        this.play.addActionListener(e -> Main.loadScene(this.frame, new Board(this.frame)));
        this.play.addActionListener(e -> Main.loadScene(this.frame, new HighScores()));
        this.play.addActionListener(e -> Main.loadScene(this.frame, new Options()));

        super.add(this.play);
        super.add(this.highscores);
        super.add(this.options);

        try {
            background = ImageIO.read(this.getClass().getResource("/backgrounds/background1.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        g.drawImage(this.background, 0, 0, null);
        Main.paintInfo(g);
    }
}
