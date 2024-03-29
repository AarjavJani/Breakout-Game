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
    private int brickRowCount = 5;
    private int brickColumnCount = 27;
    private int brickWidth;
    private int brickHeight;
    private int[][] bricks;
    private boolean gameOver; // Flag to indicate the game is over
    private static int maxwidth;
    private static boolean resetRequested; // Flag to indicate a reset is requested
	private boolean gamePaused;

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
		gamePaused = false;

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
		if (gamePaused)
		{
			return; // Don't update if the game is paused
		}
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
        if (ballY + 20 == 500 && ballX + 20 >= paddleX && ballX <= paddleX + 60)
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
		// Game Pause Message
		if (gamePaused)
		{
			// Display a message when the game is paused
			g.setColor(Color.RED);
			g.setFont(new Font("Arial", Font.BOLD, 20));
			g.drawString("Game Paused. Press Esc to Resume.", 100, 200);
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
        try
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
		    		// Pause Game
		    		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) // Check if user entered 'Esc'
		    		{
		    			if (game.gameOver)
		    			{
		    				game.resetGame(); // Reset the game even when "Esc" is pressed after a game over
		    			}
		    			else{
		    				// Toggle the game pause if Game is not Over
		    				game.gamePaused = !game.gamePaused;
		    			}
		    			game.repaint(); // Repaint to show/hide the message
		    		}
		    		if (game.gamePaused)
		    		{
		    			return; // Don't process other keys when the game is paused
		    		}
    
		    		// Game Over and Reset
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
    
		    		// Moving the paddle to Left
                    if (e.getKeyCode() == KeyEvent.VK_LEFT && game.paddleX > 0)
                    {
                        game.paddleX -= 40;
                    }
		    		// Moving the paddle to Left
                    if (e.getKeyCode() == KeyEvent.VK_RIGHT && game.paddleX < maxwidth - 100)
                    {
                        game.paddleX += 40;
                    }
                }
            });

            frame.setFocusable(true);
            frame.setFocusTraversalKeysEnabled(false);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            // Occurs if nested-loops in arrays have some mistake like OOB
            System.out.print("Error: Array index out of bounds.");
            e.printStackTrace();
        }
        catch (NullPointerException e)
        {
            // Occurs if all objects are not properly initialized before using them
            System.out.print("Error: Object not properly initialized.");
            e.printStackTrace();
        }
        catch (IllegalArgumentException e)
        {
            // Occurs if an illegal or inappropriate arguments are passed to a method
            System.out.print("Error: Illegal argument passed to method.");
            e.printStackTrace();
        }
        catch (IllegalStateException e)
        {
            // Occurs if an operation is performed at an illegal or inappropriate time
            System.out.print("Error: Operation performed at illegal time.");
            e.printStackTrace();
        }
        catch (HeadlessException e)
        {
            // Occurs if graphics operations ar eperformed on a system without a GUI or screen
            System.out.print("Error: System does not have a Screen/GUI.");
            e.printStackTrace();
        }
    }
}