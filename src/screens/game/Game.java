package screens.game;

import application.Application;
import audio.AudioPlayer;
import io.Resources;
import screens.Screen;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

public class Game extends Screen {

    final int TILE_SIZE = 20; // 20x20
    final int BOARD_WIDTH = 10;
    final int BOARD_HEIGHT = 20;

    //JFrame window; // game window
    JPanel game; // game pane

    JPanel leftPanel;
    JPanel rightPanel;
    JLabel scoreLabel;

    boolean isRunning;

    boolean[][] staticTiles = new boolean[10][20];
    int[][] tileColours = new int[10][20];
    int score;

    Tetromino activeTetromino;
    int x = 4;
    int y = -4;

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
        leftPanel.setLayout(new BorderLayout());

        scoreLabel = new JLabel(String.valueOf(score), SwingConstants.CENTER);
        leftPanel.add(scoreLabel, BorderLayout.CENTER);

        add(leftPanel, BorderLayout.WEST);

        rightPanel = new JPanel();
        rightPanel.setPreferredSize(new Dimension(5 * TILE_SIZE, 20 * TILE_SIZE));
        rightPanel.setLayout(new BorderLayout());

        JButton back = new JButton("Back");
        back.addActionListener(e -> {
            Application.instance.loadScreen(0);
            AudioPlayer.stopAudio();
        });
        //rightPanel.add(back, BorderLayout.CENTER);

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
                            g.drawImage(Tetromino.TILECOLOURS[ tileColours[i][j] ], (i) * TILE_SIZE, (j) * TILE_SIZE, null);
                            //drawRect((i) * TILE_SIZE, (j) * TILE_SIZE, TILE_SIZE, TILE_SIZE);
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

    /*** onApplicationFocused *****************************
     * start the game                                      *
     ******************************************************/
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
    }

    /*** runGame ******************************************
     * start the audio track.  enter the game loop, a way  *
     * of keeping track of time in the game world          *
     ******************************************************/
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

            update(deltaTime);
            render();
        }

        cleanUp();
    }

    /*** cleanUp ******************************************
     * when the user has died and the death screen should  *
     * be shown, reset the game so they may start again    *
     * also, save the highscore                            *
     ******************************************************/
    void cleanUp() {

        if (score > Resources.LoadInt("highscore.txt")) {
            Resources.SaveInt(score, "highscore.txt");
        }

        // should've made game a separate object and just instantiated a new one but oh well
        score = 0;
        activeTetromino = new Tetromino();
        staticTiles = new boolean[BOARD_WIDTH][BOARD_HEIGHT];
        tileColours = new int[BOARD_WIDTH][BOARD_HEIGHT];

        Application.instance.loadScreen(2);
    }

    /*** checkColumns *************************************
     * check a specified row for any tiles                 *
     ******************************************************/
    boolean checkColumns (int row) {
        for (int i = 0; i < BOARD_WIDTH; i++) {
            if (!staticTiles[i][row]) {
                return false;
            }
        }

        return true;
    }

    /*** eraseRow *****************************************
     * remove a specified row of tiles                     *
     ******************************************************/
    void eraseRow(int row) {
        for (int i = 0; i < BOARD_WIDTH; i++) {
            staticTiles[i][row] = false;
        }
    }

    /*** shift ********************************************
     * shift all rows starting with the row specified down *
     * one tile                                            *
     ******************************************************/
    void shift(int row) {
        for (int i = 0; i < BOARD_WIDTH; i++) {
            boolean last = staticTiles[i][0];
            int lastColour = tileColours[i][0];
            for (int j = 1; j <= row; j++) {
                boolean tile = staticTiles[i][j];
                staticTiles[i][j] = last;
                last = tile;

                int colour = tileColours[i][j];
                tileColours[i][j] = lastColour;
                lastColour = colour;
            }
        }
    }

    /*** updateScore **************************************
     * update the current score label display on the window*
     ******************************************************/
    void updateScore() {
        scoreLabel.setText(String.valueOf(score));
    }

    /*** checkBoard ***************************************
     * checks whether there are any full rows of tiles     *
     * if there are, award the player points and clear the *
     * row                                                 *
     ******************************************************/
    void checkBoard () {
        int[] rowsToCheck = new int[20];
        for (int i = BOARD_HEIGHT - 1; i >= 0; i--) {

            if (checkColumns(i)) {
                // need to erase the row
                eraseRow(i);
                rowsToCheck[i] = i;

                score += 100;
                updateScore();
            } else {
                rowsToCheck[i] = -1;
            }
        }

        for (int i = 0; i < rowsToCheck.length; i++) {
            if (rowsToCheck[i] != -1) {
                shift(i); // i and rows to check at i are equal
            }
        }
    }

    /*** makeTetrominoStatic ******************************
     * When a tetromino makes a valid collision (i.e not   *
     * off the side of the board) make all the tiles in    *
     * the tetromino static.                               *
     ******************************************************/
    void makeTetrominoStatic() {
        for(int i = 0; i < activeTetromino.shape.length; i++) {
            for (int j = 0; j < activeTetromino.shape[i].length; j++) {
                if (activeTetromino.shape[j][i]) {
                    staticTiles[(x + i - activeTetromino.pivotX)][y + j - activeTetromino.pivotY] = true;
                    tileColours[(x + i - activeTetromino.pivotX)][y + j - activeTetromino.pivotY] = activeTetromino.imageIndex;
                }
            }
        }

        // check if there are any rows
        checkBoard();

        activeTetromino = new Tetromino();
    }

    /*** collides ****************************************
     * check if the active tetromino collides with anything*
     * called anytime the tetromino attempts to move       *
     ******************************************************/
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

            if (tileY < 0) {
                if (staticTiles[tileX][0])
                    died();
            } else {

                // if the y is greater than the height of the board
                if (tileY + 1 > 19 || staticTiles[tileX][tileY + 1]) {
                    return true;
                }
            }
        }

        return false;
    }

    /*** moveTetromino ************************************
     * moves the active tetromino a specified number of     *
     * tiles.  the movement vector these two components     *
     * make is expected to be unit length                   *
     ******************************************************/
    void moveTetromino(int xMove, int yMove) {
        for (int i = 0; i < activeTetromino.shape.length; i++) {
            for (int j = 0; j < activeTetromino.shape[i].length; j++) {
                // if there is a tile
                if (activeTetromino.shape[i][j]){
                    // check if the movement will make this tiles position invalid
                    int target = x + j - activeTetromino.pivotX + xMove;

                    if (target > BOARD_WIDTH - 1 || target < 0) {
                        return;
                    }
                }
            }
        }

        x += xMove;
        y += yMove;

        if(collides()) {
            makeTetrominoStatic();
            x = 4;
            y = -5;
        }
    }


    /*** update *******************************************
     * called every frame draw. updates game logic.        *
     ******************************************************/
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

    /*** render *******************************************
     * requests for the jpanel to be repainted             *
     ******************************************************/
    void render() {
        game.repaint(); // repaint the game panel
    }

    /*** died *********************************************
     * makes the run state false                           *
     ******************************************************/
    void died() {
        isRunning = false;
    }
}
