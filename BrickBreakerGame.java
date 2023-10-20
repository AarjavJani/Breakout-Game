import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class BrickBreakerGame extends JPanel implements ActionListener
{
    private int ballX, ballY; // Ball's position
    private int ballSpeedX, ballSpeedY; // Ball's speed
    private int paddleX; // Paddle's position
    private int score;
    private int brickRowCount = 1;
    private int brickColumnCount = 2;
    private int brickWidth;
    private int brickHeight;
    private int[][] bricks;
    private boolean gameOver; // Flag to indicate the game is over
    private static int maxwidth;
    private static boolean resetRequested; // Flag to indicate a reset is requested

    public BrickBreakerGame()
    {
        // Initialize game variables
        ballX = 100;
        ballY = 100;
        ballSpeedX = 2;
        ballSpeedY = 2;
        paddleX = 150;
        score = 0;
        gameOver = false;
        resetRequested = false;

        // Initialize bricks
        brickWidth = 50;
        brickHeight = 20;
        bricks = new int[brickRowCount][brickColumnCount];
        for (int i = 0; i < brickRowCount; i++)
        {
            for (int j = 0; j < brickColumnCount; j++)
            {
                bricks[i][j] = 1; // 1 means the brick is active
            }
        }

        // Start a timer to update the game state
        Timer timer = new Timer(5, this);
        timer.start();
    }

    public void actionPerformed(ActionEvent e)
    {
        if (gameOver && resetRequested)
        {
            // Reset the game when a reset is requested
            resetGame();
            return;
        }

        if (gameOver)
        {
            return; // Don't update if the game is over
        }

        // Update ball's position
        ballX += ballSpeedX;
        ballY += ballSpeedY;

        // Check if the ball went below the line
        if (ballY >= 600)
        {
            gameOver = true;
        }

        // Ball-wall collisions
        if (ballX <= 0 || ballX >= maxwidth - 50)
        {
            ballSpeedX = -ballSpeedX;
        }
        if (ballY <= 0)
        {
            ballSpeedY = -ballSpeedY;
        }

        // Ball-paddle collision
        if (ballY + 20 >= 500 && ballX + 20 >= paddleX && ballX <= paddleX + 60)
        {
            ballSpeedY = -ballSpeedY;
        }

        // Ball-brick collisions
        for (int i = 0; i < brickRowCount; i++)
        {
            for (int j = 0; j < brickColumnCount; j++)
            {
                if (bricks[i][j] == 1)
                {
                    int brickX = j * brickWidth;
                    int brickY = i * brickHeight;
                    if (ballX >= brickX && ballX <= brickX + brickWidth && ballY >= brickY && ballY <= brickY + brickHeight)
                    {
                        bricks[i][j] = 0; // Mark the brick as inactive
                        ballSpeedY = -ballSpeedY;
                        score++; // Increment score
                    }
                }
            }
        }

        // Check if all bricks are destroyed
        boolean allBricksDestroyed = true;
        for (int i = 0; i < brickRowCount; i++)
        {
            for (int j = 0; j < brickColumnCount; j++)
            {
                if (bricks[i][j] == 1)
                {
                    allBricksDestroyed = false;
                    break;
                }
            }
        }

        if (allBricksDestroyed)
        {
            gameOver = true;
        }

        // Repaint the game panel
        repaint();
    }

    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
		
		// Set the background color to black
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, getWidth(), getHeight());

        // Draw the bricks
        for (int i = 0; i < brickRowCount; i++)
        {
            for (int j = 0; j < brickColumnCount; j++)
            {
                if (bricks[i][j] == 1)
                {
                    g.setColor(Color.BLUE);
                    g.fillRect(j * brickWidth, i * brickHeight, brickWidth, brickHeight);
                }
            }
        }

        // Draw the ball
        g.setColor(Color.RED);
        g.fillOval(ballX, ballY, 20, 20);

        // Draw the paddle
        g.setColor(Color.GREEN);
        g.fillRect(paddleX, 500, 60, 10);

        // Draw the score
        g.setColor(Color.WHITE);
        g.drawString("Score: " + score, 10, 10);

        // Game over message and reset prompt
        if (gameOver)
        {
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 36));
            g.drawString("Game Over! Your Score:"+score, 100, 200);
            g.drawString("Press any key to continue.", 100, 300);
        }
    }

    // Method to reset the game
    private void resetGame()
    {
        ballX = 100;
        ballY = 100;
        ballSpeedX = 2;
        ballSpeedY = 2;
        paddleX = 150;
        score = 0;

        // Initialize bricks
        for (int i = 0; i < brickRowCount; i++)
        {
            for (int j = 0; j < brickColumnCount; j++)
            {
                bricks[i][j] = 1; // Reset all bricks
            }
        }

        gameOver = false;
        resetRequested = false;
    }

    public static void main(String[] args)
    {
        JFrame frame = new JFrame("Brick Breaker Game");
        BrickBreakerGame game = new BrickBreakerGame();
        frame.add(game);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH); // Maximize the frame
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        maxwidth = frame.getWidth();

        // Listen for keyboard input to move the paddle and request reset
        frame.addKeyListener(new KeyAdapter()
        {
            public void keyPressed(KeyEvent e)
            {
                if (game.gameOver)
                {
                    if (resetRequested)
                    {
                        game.resetGame(); // Reset the game when any key is pressed after a game over
                    }
                    else
                    {
                        resetRequested = true; // Set the reset request flag
                    }
                }
                else
                {
                    resetRequested = false; // Reset the reset request flag if the game is not over
                }

                if (e.getKeyCode() == KeyEvent.VK_LEFT && game.paddleX > 0)
                {
                    game.paddleX -= 40;
                }
                if (e.getKeyCode() == KeyEvent.VK_RIGHT && game.paddleX < maxwidth - 100)
                {
                    game.paddleX += 40;
                }
            }
        });

        frame.setFocusable(true);
        frame.setFocusTraversalKeysEnabled(false);
    }
}
