package com.shpp.p2p.cs.okurylyk.assignment4;


import acm.graphics.*;
import acm.util.RandomGenerator;
import com.shpp.cs.a.graphics.WindowProgram;

import java.awt.*;
import java.awt.event.MouseEvent;


public class Breakout extends WindowProgram {

    /**
     * Width and height of application window in pixels
     */
    public static final int APPLICATION_WIDTH = 400;
    public static final int APPLICATION_HEIGHT = 600;

    /**
     * Dimensions of game board (usually the same)
     */
    private static final int WIDTH = APPLICATION_WIDTH;
    private static final int HEIGHT = APPLICATION_HEIGHT;

    /**
     * Dimensions of the paddle
     */
    private static final int PADDLE_WIDTH = 60;
    private static final int PADDLE_HEIGHT = 10;

    /**
     * Offset of the paddle up from the bottom
     */
    private static final int PADDLE_Y_OFFSET = 30;

    /**
     * Number of bricks per row
     */
    private static final int NBRICKS_PER_ROW = 10;

    /**
     * Number of rows of bricks
     */
    private static final int NBRICK_ROWS = 10;

    /**
     * Number of all bricks
     */
    private static final int ALL_BRICKS = NBRICK_ROWS * NBRICKS_PER_ROW;

    /**
     * Separation between bricks
     */
    private static final int BRICK_SEP = 4;

    /**
     * Width of a brick
     */
    private static final int BRICK_WIDTH =
            (WIDTH - (NBRICKS_PER_ROW - 1) * BRICK_SEP) / NBRICKS_PER_ROW;

    /**
     * Height of a brick
     */
    private static final int BRICK_HEIGHT = 8;

    /**
     * Radius of the ball in pixels
     */
    private static final int BALL_RADIUS = 10;

    /**
     * Offset of the top brick row from the top
     */
    private static final int BRICK_Y_OFFSET = 70;

    /**
     * Number of turns
     */
    private static final int NTURNS = 3;

    /**
     * Time between frames
     */
    private static final double PAUSE_TIME = 1000 / 70.0;


    private GRoundRect paddle; // create a paddle
    private GOval ball; // create a ball
    private double vx, vy; // ball velocity in X and Y coordinates
    private int crashedBricks = 0; // counter of crashed bricks
    private int usedAttempts = 0; // attempts that have already used (counter)

    /**
     * Method run() consist from two main methods, one that create an interface of a game and another that start
     * this game.
     */
    public void run() {
        buildInterface();
        startGame();
    }

    /**
     * We start our game from preparation and when user is ready ball starts move. User plays until he/she win
     * or lose the game.
     */
    private void startGame() {
        gamePreparation();
        playTillLoseOrWin();
    }

    /**
     * This method makes a game. Also it shows to user some info, when game end, or something happened.
     */
    private void playTillLoseOrWin() {
        while (usedAttempts != NTURNS) {
            moveBall();
            takeGameInfo();
            if (crashedBricks == ALL_BRICKS) {
                makeWinLabel();
                break;
            }
        }
    }

    /**
     * Adding to the screen win label
     */
    private void makeWinLabel() {
        GLabel label = new GLabel("You WIN!");
        label.setFont(new Font("MV Boli", Font.PLAIN, (50)));
        label.setVisible(true);
        label.setColor(Color.GREEN);
        label.setLocation((WIDTH - label.getWidth()) / 2.0, HEIGHT / 2.0 - label.getHeight());
        add(label);
    }

    /**
     * If ball crashed, this method check your number of turns and set ball to the start position.
     */
    private void takeGameInfo() {
        if (!ballNotCrashed()) {
            usedAttempts++;
            ball.setLocation(WIDTH / 2.0 - BALL_RADIUS, HEIGHT / 2.0 - BALL_RADIUS);
            checkAttempts();
        }
    }

    /**
     * If you crashed ball three times there will be lose label on the screen, otherwise it will show
     * you a number of attempts that you have.
     */
    private void checkAttempts() {
        if (usedAttempts < NTURNS) {
            showAttemptsBalance();
        } else {
            makeLoseLabel();
        }
    }

    /**
     * Create a lose label on the screen.
     */
    private void makeLoseLabel() {
        GLabel label = new GLabel("You LOSE!");
        label.setFont(new Font("MV Boli", Font.PLAIN, (30)));
        label.setVisible(true);
        label.setColor(Color.RED);
        label.setLocation((WIDTH - label.getWidth()) / 2.0, HEIGHT / 2.0 - label.getHeight());
        add(label);
    }

    /**
     * Show how many attempts you have.
     */
    private void showAttemptsBalance() {
        GLabel attemptsLabel = makeInfoLabel();
        waitForClick();
        remove(attemptsLabel);
    }

    /**
     * Here I make settings of a label.
     *
     * @return label that shows how many attempts you have.
     */
    private GLabel makeInfoLabel() {
        GLabel label = new GLabel("You have " + (NTURNS - usedAttempts) + " attempts, click to resume.");
        label.setFont(new Font("Cambria", Font.PLAIN, (25)));
        label.setVisible(true);
        label.setLocation((WIDTH - label.getWidth()) / 2.0, HEIGHT / 2.0 - label.getHeight());
        add(label);
        return label;
    }

    /**
     * Create a ball on the screen and greeting user. When user click a mouse the game will start.
     */
    private void gamePreparation() {
        GLabel startLabel = makeStartLabel();
        buildBall();
        waitForClick();
        remove(startLabel);
    }

    /**
     * Here I make settings of start label
     *
     * @return label that shows greeting.
     */
    private GLabel makeStartLabel() {
        GLabel label = new GLabel("Click mouse to start");
        label.setFont(new Font("Cambria", Font.PLAIN, (30)));
        label.setVisible(true);
        label.setLocation((WIDTH - label.getWidth()) / 2.0, HEIGHT / 2.0 - label.getHeight());
        add(label);
        return label;
    }

    /**
     * Create interface of a game.
     */
    private void buildInterface() {
        buildBricksWall(NBRICKS_PER_ROW, NBRICK_ROWS);
        buildPaddle();
        addMouseListeners();
    }

    /**
     * Build bricks with gives parameters. And also set color that it needs to each row of brick wall.
     *
     * @param cols number of column.
     * @param rows number of rows.
     */
    private void buildBricksWall(int cols, int rows) {
        for (int i = 0; i < NBRICKS_PER_ROW; i++) {
            for (int j = 0; j < NBRICK_ROWS; j++) {
                double x = i * (BRICK_WIDTH + BRICK_SEP);
                double y = (j * (BRICK_HEIGHT + BRICK_SEP)) + BRICK_Y_OFFSET;
                GRect brick = new GRect(x, y, BRICK_WIDTH, BRICK_HEIGHT);
                brick.setFilled(true);
                brick.setFillColor(colorSetter(j));
                add(brick);
            }
        }
    }

    /**
     * Build ball and put him to the start position in the center of the screen.
     *
     */
    private void buildBall() {
        ball = new GOval(getWidth() / 2.0 - BALL_RADIUS, getHeight() / 2.0 - BALL_RADIUS,
                BALL_RADIUS, BALL_RADIUS);
        ball.setFilled(true);
        ball.setColor(Color.BLACK);
        add(ball);
    }

    /**
     * Move ball until game end with pause time of 70 frames per sec. Also here is a random generator that starts
     * moving ball with random vx direction from 1 to 3 and negative or positive direction with probability 50%.
     */
    private void moveBall() {
        RandomGenerator rgen = RandomGenerator.getInstance();
        vx = rgen.nextDouble(1.0, 3.0);
        if (rgen.nextBoolean(0.5))
            vx = -vx;
        vy = 3.0;
        while (ballNotCrashed() && crashedBricks != ALL_BRICKS) {
            ball.move(vx, vy);
            pause(PAUSE_TIME);
            moveTillCollision();
        }
    }

    /**
     * Check a ball when he moves if there is collision with some objects or a wall. When it is a wall, the ball
     * just bounced, when object is a paddle it is also bounced and when object is not a paddle and not a null
     * it means that it is a brick, so ball must crashed the brick and bounced down.
     */
    private void moveTillCollision() {
        GObject object = getCollidingObject(ball, BALL_RADIUS * 2, BALL_RADIUS * 2);
        if (ball.getX() > getWidth() - (BALL_RADIUS * 2) || ball.getX() < 0) {
            vx = -vx;
        } else if (ball.getY() < 0) {
            vy = -vy;
        } else if (object == paddle) {
            vy = -vy;
        } else if (object != null) {
            crashedBricks++;
            remove(object);
            vy = -vy;

        }
    }

    /**
     * When ball Y coordinate is higher than a bottom of a window, that means that ball is crashed.
     *
     * @return true when not crashed and false when crashed.
     */
    private boolean ballNotCrashed() {
        return ball.getY() < getHeight() - (BALL_RADIUS * 2);
    }

    /**
     * Builds a paddle and add on the screen with correct location.
     */
    private void buildPaddle() {
        paddle = new GRoundRect((getWidth() - PADDLE_WIDTH) / 2.0, getHeight() - PADDLE_Y_OFFSET,
                PADDLE_WIDTH, PADDLE_HEIGHT);
        paddle.setFilled(true);
        paddle.setColor(Color.BLACK);
        add(paddle);
    }

    /**
     * Add mouse Event.
     *
     * @param me gives location of the mouse.
     */
    public void mouseMoved(MouseEvent me) {
        if (me.getX() < (getWidth() - PADDLE_WIDTH) && me.getX() > 0) {
            paddle.setLocation(me.getX(), getHeight() - PADDLE_Y_OFFSET);
        } else if (me.getX() > (getWidth() - PADDLE_WIDTH)) {
            paddle.setLocation(getWidth() - PADDLE_WIDTH, getHeight() - PADDLE_Y_OFFSET);
        } else if (me.getX() < 0) {
            paddle.setLocation(0, getHeight() - PADDLE_Y_OFFSET);
        }

    }

    /**
     * Set different colour of the brick row.
     *
     * @param i the present number of brick row.
     * @return Colour.
     */
    private Color colorSetter(int i) {
        if (i < 2) {
            return Color.RED;
        } else if (i < 4) {
            return Color.ORANGE;
        } else if (i < 6) {
            return Color.YELLOW;
        } else if (i < 8) {
            return Color.GREEN;
        } else
            return Color.CYAN;
    }

    /**
     * Check if object colliding with another object and return this object.
     *
     * @param obj       object that we discovered.
     * @param objWidth  width of this object.
     * @param objHeight height of this object.
     * @return object the collided to discovered object or null if there is no one.
     */
    private GObject getCollidingObject(GObject obj, int objWidth, int objHeight) {
        if (getElementAt(obj.getX(), obj.getY()) != null) {
            return getElementAt(obj.getX(), obj.getY());
        } else if (getElementAt(obj.getX() + objWidth, obj.getY()) != null) {
            return getElementAt(obj.getX() + objWidth, obj.getY());
        } else if (getElementAt(obj.getX(), obj.getY() + objHeight) != null) {
            return getElementAt(obj.getX(), obj.getY() + (objHeight));
        } else if (getElementAt(obj.getX() + objWidth, obj.getY() + objHeight) != null) {
            return getElementAt(obj.getX() + objWidth, obj.getY() + objHeight);
        } else
            return null;

    }
}
