package org.mateolamthi;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class GameBoard extends JPanel implements ActionListener {

    // Board dimensions
    private final int BOARD_WIDTH = 600;   // Width of the game board in pixels
    private final int BOARD_HEIGHT = 600;  // Height of the game board in pixels

    // Size of each game unit (dot)
    private final int UNIT_SIZE = 20;      // Size of each snake segment and apple in pixels

    // Total possible units on the board
    private final int TOTAL_UNITS = (BOARD_WIDTH * BOARD_HEIGHT) / (UNIT_SIZE * UNIT_SIZE);
    // Total number of segments that could fit on the board

    // Maximum position for random apple placement
    private final int RANDOM_POSITION = (BOARD_WIDTH / UNIT_SIZE) - 1; // Used to calculate random apple position

    // Game speed (delay between moves)
    private int gameSpeed = 140; // Delay in milliseconds between moves (lower = faster)

    // Arrays to hold the x and y coordinates of snake parts
    private final int snakeX[] = new int[TOTAL_UNITS]; // X-coordinates for the snake's body segments
    private final int snakeY[] = new int[TOTAL_UNITS]; // Y-coordinates for the snake's body segments

    // Current size of the snake
    private int snakeLength; // Number of snake segments

    // Coordinates of the apple
    private int appleX; // X-coordinate of the apple
    private int appleY; // Y-coordinate of the apple

    // Game level and score
    private int currentLevel; // Tracks the current level
    private int applesEaten;  // Tracks the number of apples eaten (used for leveling up)

    // Direction flags (only one can be true at a time)
    private boolean movingLeft = false;
    private boolean movingRight = true;
    private boolean movingUp = false;
    private boolean movingDown = false;
    private boolean inGame = true;  // Flag indicating if the game is still active
    private boolean isPaused = false; // Flag indicating if the game is paused

    // Timer and images
    private Timer gameTimer; // Timer that controls the game's speed
    private Image bodyPart;  // Image for the snake's body segment
    private Image appleImage; // Image for the apple
    private Image headImage; // Image for the snake's head

    // Reference to the parent Snake class (main window)
    private Snake parent; // Used to update elements in the main game window

    // Constructor that initializes the game board
    public GameBoard(Snake parent) {
        this.parent = parent; // Store reference to the parent window
        initializeBoard();    // Set up the game board
    }

    // Method to set up the game board
    private void initializeBoard() {
        addKeyListener(new KeyHandler()); // Add a key listener to capture keyboard input
        setBackground(Color.black);       // Set the background color of the board to black
        setFocusable(true);               // Make the panel focusable so it can receive keyboard events
        setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT)); // Set the size of the game board

        loadImages();  // Load the images for the snake and apple
        startGame();   // Start the game
    }

    // Method to load the images for the snake and apple
    private void loadImages() {
        // Load images for the snake's body, apple, and head from the resources folder
        ImageIcon bodyIcon = new ImageIcon("src/main/java/org/resources/dot.png");
        bodyPart = bodyIcon.getImage();

        ImageIcon appleIcon = new ImageIcon("src/main/java/org/resources/apple.png");
        appleImage = appleIcon.getImage();

        ImageIcon headIcon = new ImageIcon("src/main/java/org/resources/head.png");
        headImage = headIcon.getImage();
    }

    // Method to start the game (reset all variables and start the timer)
    private void startGame() {
        snakeLength = 3;  // Initial length of the snake is 3 segments

        // Set initial positions of the snake parts
        for (int i = 0; i < snakeLength; i++) {
            snakeX[i] = 100 - i * UNIT_SIZE; // Position each segment behind the head
            snakeY[i] = 100;                 // Keep the snake on the same Y-axis
        }

        placeApple();  // Place the first apple on the board

        currentLevel = 1;  // Set the starting level to 1
        applesEaten = 0;   // Reset the apple counter

        // Initialize the game timer with the current game speed
        gameTimer = new Timer(gameSpeed, this);
        gameTimer.start();  // Start the timer, which triggers the game loop
    }

    // Method to reset the game (can be called to restart)
    public void resetGame() {
        snakeLength = 3;  // Reset snake length

        // Reset direction flags
        movingLeft = false;
        movingRight = true;
        movingUp = false;
        movingDown = false;

        // Reset positions of the snake segments
        for (int i = 0; i < snakeLength; i++) {
            snakeX[i] = 100 - i * UNIT_SIZE; // Reset positions similar to start
            snakeY[i] = 100;
        }

        placeApple();  // Re-place the apple on the board

        currentLevel = 1;  // Reset to level 1
        applesEaten = 0;   // Reset apples eaten counter

        gameSpeed = 140;   // Reset the game speed
        gameTimer.setDelay(gameSpeed); // Update the timer delay
        gameTimer.restart();           // Restart the timer

        isPaused = false;  // Reset pause flag
        inGame = true;     // Set the game to active
    }

    // Method to toggle between pause and resume
    public void togglePause() {
        if (isPaused) {
            resumeGame();  // If paused, resume the game
        } else {
            pauseGame();   // If not paused, pause the game
        }
    }

    // Method to check if the game is currently paused
    public boolean isPaused() {
        return isPaused;
    }

    // Method to pause the game
    private void pauseGame() {
        isPaused = true;    // Set pause flag
        gameTimer.stop();   // Stop the game timer
    }

    // Method to resume the game
    private void resumeGame() {
        isPaused = false;   // Clear pause flag
        gameTimer.start();  // Restart the game timer
    }

    // Method to render the game components on the board
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);  // Call the superclass's method to ensure proper painting
        renderGame(g);            // Render the game elements
    }

    // Method to render the game (snake, apple, etc.)
    private void renderGame(Graphics g) {
        if (inGame) {
            // Draw the apple on the board
            g.drawImage(appleImage, appleX, appleY, this);

            // Draw the snake segments
            for (int i = 0; i < snakeLength; i++) {
                if (i == 0) {
                    g.drawImage(headImage, snakeX[i], snakeY[i], this); // Draw the head
                } else {
                    g.drawImage(bodyPart, snakeX[i], snakeY[i], this);  // Draw the body segments
                }
            }

            Toolkit.getDefaultToolkit().sync(); // Ensure smooth rendering
        } else {
            showGameOver(g);  // If the game is over, display the game over message
        }
    }

    // Method to display the game over screen
    private void showGameOver(Graphics g) {
        String message = "Game Over"; // Default message for game over
        if (currentLevel == 10) {
            message = "You Win!"; // If the player wins by reaching level 10
        }

        Font font = new Font("Helvetica", Font.BOLD, 14);  // Font for the message
        FontMetrics metrics = getFontMetrics(font);        // Font metrics for centering text

        g.setColor(Color.white); // Set the font color to white
        g.setFont(font);         // Set the font
        g.drawString(message, (BOARD_WIDTH - metrics.stringWidth(message)) / 2, BOARD_HEIGHT / 2); // Draw message at center
    }

    // Method to check if the snake has collided with the apple
    private void checkAppleCollision() {
        // Check if the snake's head has the same coordinates as the apple
        if ((snakeX[0] == appleX) && (snakeY[0] == appleY)) {
            snakeLength++;    // Increase the snake's length
            applesEaten++;    // Increment the apples eaten count
            placeApple();     // Place a new apple

            // Level up every 5 apples eaten, until level 10
            if (applesEaten % 5 == 0 && currentLevel < 10) {
                currentLevel++; // Increase the level
                JOptionPane.showMessageDialog(this, "Level Up! You are now at Level " + currentLevel); // Show level up message

                // Increase the game speed as the level increases
                if (currentLevel > 1 && gameSpeed > 40) { // Ensure the game doesn't get too fast
                    gameSpeed -= 10; // Decrease the delay to increase speed
                    gameTimer.setDelay(gameSpeed); // Update the timer delay
                }
                parent.updateLevel(currentLevel); // Update the level label in the parent window
            } else if (currentLevel == 10) {
                JOptionPane.showMessageDialog(this, "You Win!"); // Show win message
                inGame = false; // End the game
                gameTimer.stop(); // Stop the timer
            }
        }
    }

    // Method to move the snake on the board
    private void moveSnake() {
        // Move the body of the snake (shift positions forward)
        for (int i = snakeLength; i > 0; i--) {
            snakeX[i] = snakeX[i - 1]; // Move each body part to the position of the part ahead of it
            snakeY[i] = snakeY[i - 1];
        }

        // Move the snake's head based on the current direction
        if (movingLeft) {
            snakeX[0] -= UNIT_SIZE; // Move left
        }

        if (movingRight) {
            snakeX[0] += UNIT_SIZE; // Move right
        }

        if (movingUp) {
            snakeY[0] -= UNIT_SIZE; // Move up
        }

        if (movingDown) {
            snakeY[0] += UNIT_SIZE; // Move down
        }
    }

    // Method to check for collisions with the walls or itself
    private void checkCollisions() {
        // Check collision with the snake's body (if the head touches any part of the body)
        for (int i = snakeLength; i > 0; i--) {
            if ((i > 4) && (snakeX[0] == snakeX[i]) && (snakeY[0] == snakeY[i])) {
                inGame = false; // End the game
            }
        }

        // Check for collisions with the borders
        if (snakeY[0] >= BOARD_HEIGHT || snakeY[0] < 0 || snakeX[0] >= BOARD_WIDTH || snakeX[0] < 0) {
            inGame = false; // End the game if the snake goes out of bounds
        }

        // Stop the timer if the game ends
        if (!inGame) {
            gameTimer.stop();
        }
    }

    // Method to randomly place an apple on the board
    private void placeApple() {
        int randomPosition = (int) (Math.random() * RANDOM_POSITION); // Generate a random X position for the apple
        appleX = randomPosition * UNIT_SIZE;

        randomPosition = (int) (Math.random() * RANDOM_POSITION); // Generate a random Y position for the apple
        appleY = randomPosition * UNIT_SIZE;
    }

    // Action performed method (called by the timer on every tick)
    @Override
    public void actionPerformed(ActionEvent e) {
        if (inGame && !isPaused) {  // If the game is active and not paused
            checkAppleCollision();  // Check if the snake has eaten an apple
            checkCollisions();      // Check for collisions
            moveSnake();            // Move the snake
        }
        repaint();  // Repaint the board to update the graphics
    }

    // Inner class to handle key events
    private class KeyHandler extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode(); // Get the key code of the pressed key

            // Check for arrow key inputs to control snake movement
            if ((key == KeyEvent.VK_LEFT) && (!movingRight)) {
                movingLeft = true;
                movingUp = false;
                movingDown = false;
            }

            if ((key == KeyEvent.VK_RIGHT) && (!movingLeft)) {
                movingRight = true;
                movingUp = false;
                movingDown = false;
            }

            if ((key == KeyEvent.VK_UP) && (!movingDown)) {
                movingUp = true;
                movingRight = false;
                movingLeft = false;
            }

            if ((key == KeyEvent.VK_DOWN) && (!movingUp)) {
                movingDown = true;
                movingRight = false;
                movingLeft = false;
            }

            // Optional: Allow pausing with the 'P' key
            if (key == KeyEvent.VK_P) {
                togglePause(); // Toggle between pause and resume
                // Update the pause button text in the parent frame
                if (isPaused) {
                    parent.pauseButton.setText("Resume");
                } else {
                    parent.pauseButton.setText("Pause");
                }
            }
        }
    }
}
