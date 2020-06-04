package screens.game;

import audio.AudioPlayer;
import screens.Screen;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

public class Game extends Screen {

    final int TILE_SIZE = 20; // 20x20

    //JFrame window; // game window
    JPanel game; // game pane

    JPanel leftPanel;
    JPanel rightPanel;

    boolean isRunning;

    boolean[][] staticTiles = new boolean[10][24];

    Tetromino activeTetromino;
    int x = 5;
    int y = 10;

    public Game(JFrame window) {
        super(window);

        isRunning = false;

        setPreferredSize(new Dimension(400, 400));
        setLayout(new BorderLayout());

        JPanel gameHolder = new JPanel();
        gameHolder.setPreferredSize(new Dimension( 10 * TILE_SIZE, 20 * TILE_SIZE ));
        gameHolder.setBorder(  BorderFactory.createLineBorder(Color.black, 1) ); // wrap the game panel in a border
        gameHolder.setLayout(new GridLayout(0, 1)); // this will make the game panel fill this panel
        add(gameHolder, BorderLayout.CENTER);

        leftPanel = new JPanel();
        leftPanel.setPreferredSize(new Dimension(5 * TILE_SIZE, 20 * TILE_SIZE));
        add(leftPanel, BorderLayout.WEST);

        rightPanel = new JPanel();
        rightPanel.setPreferredSize(new Dimension(5 * TILE_SIZE, 20 * TILE_SIZE));
        add(rightPanel, BorderLayout.EAST);

        activeTetromino = new Tetromino();

        // rendering
        game = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                for (int i = 0; i < activeTetromino.shape.length; i++) {
                    for(int j = 0; j < activeTetromino.shape[i].length; j++) {

                        if (i == activeTetromino.pivotX && j == activeTetromino.pivotY) {
                            // this is the pivot tile
                            g.setColor(Color.red);
                        } else {
                            g.setColor(Color.black);
                        }

                        // if this tile on the shape is meant to be drawn
                        if (activeTetromino.shape[j][i]) {
                            // draw this tile
                            g.drawImage(activeTetromino.img, (x + i - activeTetromino.pivotX) * TILE_SIZE, (y + j - activeTetromino.pivotY) * TILE_SIZE, null);
                        }
                    }
                }

                // draw static tiles
                for(int i = 0; i < staticTiles.length; i++) {
                    for (int j = 0; j < staticTiles[i].length; j++) {
                        if (staticTiles[i][j]) {
                            g.drawRect((i) * TILE_SIZE, (j) * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                        }
                    }
                }
            }
        };
        gameHolder.add(game);

        // input management
        window.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {

                // space key or down arrow
                if (e.getKeyCode() == 32 || e.getKeyCode() == 40) {
                    // hard drop
                    moveTetromino(0, 1);
                }

                // right arrow
                if (e.getKeyCode() == 39) {
                    moveTetromino(1, 0);
                }

                // left arrow
                if (e.getKeyCode() == 37) {
                    moveTetromino(-1, 0);
                }

                // up arrow
                if(e.getKeyCode() == 38){
                    activeTetromino.rotate();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
    }

    @Override
    public void onApplicationFocused() {
        super.onApplicationFocused();

        Thread gameThread = new Thread() {
            @Override
            public void run() {
                super.run();

                runGame();
            }
        };

        gameThread.start();

        //runGame();
    }

    void runGame () {
        isRunning = true;

        // start playing some audio
        try {
            AudioPlayer.playAudio("assets/music.wav");
        }

        // cant find audio file
        catch(UnsupportedAudioFileException unsupportedAudioFileException) {
            unsupportedAudioFileException.printStackTrace();
        }

        // err loading audio file
        catch(IOException ioException) {
            ioException.printStackTrace();
        }

        catch (LineUnavailableException lineUnavailableException) {
            lineUnavailableException.printStackTrace();
        }
        // create the game loop
        long time = System.nanoTime();
        while(isRunning) {

            // calculate time between frames
            long timeThisFrame = System.nanoTime();
            double deltaTime = (timeThisFrame - time) / 1000000000.0;
            time = timeThisFrame;

            handleInput();
            update(deltaTime);
            render();
        }
    }

    void handleInput() { }

    boolean checkColumns (int row) {
        for (int i = 0; i < staticTiles[row].length; i++) {
            System.out.println(staticTiles[row][i]);
            if (!staticTiles[row][i]) {
                return false;
            }


        }

        return true;
    }

    void eraseRow(int row) {
        for (int i = 0; i < staticTiles[row].length; i++) {
            staticTiles[row][i] = false;
        }
    }

    void checkBoard () {
        for (int i = 0; i < staticTiles.length; i++) {
            if (checkColumns(i)) {
                // need to erase the row
                eraseRow(i);
            }
            System.out.println(i);
        }
    }

    void makeTetrominoStatic() {
        for(int i = 0; i < activeTetromino.shape.length; i++) {
            for (int j = 0; j < activeTetromino.shape[i].length; j++) {
                if (activeTetromino.shape[j][i]) {
                    staticTiles[(x + i - activeTetromino.pivotX)][y + j - activeTetromino.pivotY] = true;
                }
            }
        }

        // check if there are any rows
        checkBoard();

        activeTetromino = new Tetromino();
    }

    boolean collides() {
        // for each column
        for (int i = 0; i < activeTetromino.shape.length; i++) {
            int lowest = -1;

            for (int j = 0; j < activeTetromino.shape[i].length; j++) {
                if (activeTetromino.shape[j][i]) {
                    lowest = j;
                }
            }

            // no tile in this column
            if (lowest == -1)
                continue;

            // if the lowest was to increase by 1 on y, would it collide?
            int tileX = x + i - activeTetromino.pivotX;
            int tileY = y + lowest - activeTetromino.pivotY;

            // if the y is greater than the height of the board
            if (tileY + 1 > 19 || staticTiles[tileX][tileY + 1]) {
                return true;
            }
        }

        return false;
    }

    void moveTetromino(int xMove, int yMove) {
        x += xMove;
        y += yMove;

        if(collides()) {
            makeTetrominoStatic();
            x = 4;
            y = 1;
        }
    }

    double timePassed = 0;
    void update(double deltaTime) {
        // update tiles
        //x += 1 * deltaTime;

        if (timePassed > 1) {
            moveTetromino(0, 1);
            timePassed = 0;
        }

        // does active tile collide with anything?

        timePassed += 1 * deltaTime;
    }

    void render() {
        game.repaint(); // repaint the game panel
    }

    public static void main(String[] args) {
        //new screens.game.Game().run();
    }
}
