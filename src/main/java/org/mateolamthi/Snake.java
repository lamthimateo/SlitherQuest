package org.mateolamthi;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.event.*;

public class Snake extends JFrame {

    private GameBoard gameBoard;
    private JPanel headerPanel;
    private JLabel levelLabel;
    private JButton restartButton;
    public JButton pauseButton;

    public Snake() {
        initUI();
    }

    private void initUI() {
        setTitle("Snake");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Set layout to BorderLayout
        setLayout(new BorderLayout());

        // Initialize game board
        gameBoard = new GameBoard(this);

        // Initialize header panel
        headerPanel = new JPanel();

        // Initialize level label
        levelLabel = new JLabel("Level: 1");

        // Initialize restart button
        restartButton = new JButton("Restart");
        restartButton.setFocusable(false);
        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gameBoard.resetGame();
                // Reset level label
                levelLabel.setText("Level: 1");
                // Reset pause button text
                pauseButton.setText("Pause");
            }
        });

        // Initialize pause button
        pauseButton = new JButton("Pause");
        pauseButton.setFocusable(false);
        pauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gameBoard.togglePause();
                if (gameBoard.isPaused()) {
                    pauseButton.setText("Resume");
                } else {
                    pauseButton.setText("Pause");
                }
                // Ensure the game board regains focus for key events
                gameBoard.requestFocusInWindow();
            }
        });

        // Add level label, pause button, and restart button to header panel
        headerPanel.add(levelLabel);
        headerPanel.add(pauseButton);
        headerPanel.add(restartButton);

        // Add header panel to the NORTH
        add(headerPanel, BorderLayout.NORTH);

        // Add game board to the CENTER
        add(gameBoard, BorderLayout.CENTER);

        // Adjust frame settings
        setResizable(false);
        pack();
        setLocationRelativeTo(null);
    }

    // Method to update the level label
    public void updateLevel(int level) {
        levelLabel.setText("Level: " + level);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            JFrame ex = new Snake();
            ex.setVisible(true);
        });
    }
}
