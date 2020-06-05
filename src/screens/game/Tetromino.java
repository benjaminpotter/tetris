package screens.game;

import io.Resources;

import java.awt.image.BufferedImage;
import java.util.Random;

public class Tetromino {
    public static final BufferedImage[] TILECOLOURS = { Resources.LoadImage("assets/cyan.jpg"), Resources.LoadImage("assets/purple.jpg"), Resources.LoadImage("assets/blue.jpg"), Resources.LoadImage("assets/red.jpg"), Resources.LoadImage("assets/yellow.jpg"), Resources.LoadImage("assets/orange.jpg"), Resources.LoadImage("assets/green.jpg") };


    class TetrominoShape {
        // possible tile matrices
        public boolean[][] shape;

        public int pivotX;
        public int pivotY;

        public BufferedImage img;

        TetrominoShape(boolean[][] shape, int pivotX, int pivotY) {
            this.shape = shape;
            this.pivotX = pivotX;
            this.pivotY = pivotY;

            // load random image
            //img = TILECOLOURS[new Random().nextInt(TILECOLOURS.length)];
        }
    }

    boolean[][] tShape = {
            { false, false, false, false },
            { false, false, false, false },
            { false, true, false, false },
            { true, true, true, false }
    };
    TetrominoShape tShapeObj = new TetrominoShape(tShape, 1,3);

    boolean[][] lineShape = {
            { false, false, false, false },
            { false, false, false, false },
            { false, false, false, false },
            { true, true, true, true }
    };
    TetrominoShape lineShapeObj = new TetrominoShape(lineShape, 0,3);

    boolean[][] boxShape = {
            { false, false, false, false },
            { false, false, false, false },
            { true, true, false, false },
            { true, true, false, false }
    };
    TetrominoShape boxShapeObj = new TetrominoShape(boxShape, -1,-1);

    boolean[][] lShape = {
            { false, false, false, false },
            { false, false, false, false },
            { true, false, false, false },
            { true, true, true, false }
    };
    TetrominoShape lShapeObj = new TetrominoShape(lShape, 0,3);

    boolean[][] reverselShape = {
            { false, false, false, false },
            { false, false, false, false },
            { false, false, false, true },
            { false, true, true, true }
    };
    TetrominoShape reverselShapeObj = new TetrominoShape(reverselShape, 3,3);

    boolean[][] zShape = {
            { false, false, false, false },
            { false, false, false, false },
            { true, true, false, false },
            { false, true, true, false }
    };
    TetrominoShape zShapeObj = new TetrominoShape(zShape, 1,3);

    boolean[][] reverseZShape = {
            { false, false, false, false },
            { false, false, false, false },
            { false, true, true, false },
            { true, true, false, false }
    };
    TetrominoShape reverseZShapeObj = new TetrominoShape(reverseZShape, 1,3);


    TetrominoShape[] shapes = {tShapeObj, lineShapeObj, boxShapeObj, lShapeObj, reverselShapeObj, zShapeObj, reverseZShapeObj};


    // this will store the shape of the tetromino
    public boolean[][] shape;

    // relative to the shapes (indexed from 0)
    public int pivotX;
    public int pivotY;

    public BufferedImage img;
    public int imageIndex;

    public Tetromino() {
        this.imageIndex = new Random().nextInt(TILECOLOURS.length);
        TetrominoShape parent = shapes[ new Random().nextInt(shapes.length)];
        this.shape = parent.shape;
        this.pivotX = parent.pivotX;
        this.pivotY = parent.pivotY;
        this.img = TILECOLOURS[this.imageIndex];
    }

    /*** rotateTetromino **********************************
     * performs a 90d matrix rotation on the tetromino     *
     ******************************************************/
    // rotates the tetromino
    public void rotate() {
        final int MATRIX_SIZE = 4;

        if (pivotX == -1)
            return;

        // Consider all squares one by one
        for (int x = 0; x < MATRIX_SIZE / 2; x++) {
            // Consider elements in group of 4 in
            // current square
            for (int y = x; y < MATRIX_SIZE - x - 1; y++) {
                // store current cell in temp variable
                boolean temp = shape[x][y];

                // move values from right to top
                shape[x][y] = shape[y][MATRIX_SIZE - 1 - x];

                // move values from bottom to right
                shape[y][MATRIX_SIZE - 1 - x] = shape[MATRIX_SIZE - 1 - x][MATRIX_SIZE - 1 - y];

                // move values from left to bottom
                shape[MATRIX_SIZE - 1 - x][MATRIX_SIZE - 1 - y] = shape[MATRIX_SIZE - 1 - y][x];

                // assign temp to left
                shape[MATRIX_SIZE - 1 - y][x] = temp;
            }
        }

        // move the pivot location

        double offset = ((MATRIX_SIZE - 1) / 2.0);

        double adjX = pivotX - offset;
        double adjY = pivotY - offset;

        double rotX = adjY + offset;
        double rotY = -adjX + offset;

        pivotX = (int) rotX;
        pivotY = (int) rotY;
    }
}
