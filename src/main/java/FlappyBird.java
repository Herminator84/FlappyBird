
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import javax.sound.sampled.*;
import java.io.IOException;

public class FlappyBird extends JPanel implements ActionListener {

        // Game constants
        private static final int WIDTH = 800;
        private static final int HEIGHT = 600;
        private static final int BIRD_SIZE = 50;
        private static final int PIPE_WIDTH = 80;
        private static final int PIPE_GAP = 200;
        private static final int PIPE_SPEED = 5;
        private static final int GRAVITY = 1;
        private static final int JUMP_STRENGTH = -15;

        // Bird properties
        private int birdY = HEIGHT / 2;
        private int birdVelocity = 0;

        // Pipes
        private List<Rectangle> pipes;

        // Game state
        private Timer timer;
        private boolean gameOver = false;

        public FlappyBird() {
            // Create the window
            JFrame frame = new JFrame("Flappy Bird");
            frame.setSize(WIDTH, HEIGHT);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(this);
            frame.setVisible(true);
            frame.setResizable(false);

            // Set up game objects
            pipes = new ArrayList<>();
            addPipes(); // Add initial pipes

            // Timer for the game loop (60 FPS)
            timer = new Timer(1000 / 60, this);
            timer.start();

            // Key listener to control bird
            frame.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                        if (!gameOver) {
                            birdVelocity = JUMP_STRENGTH; // Jump when space is pressed
                        } else {
                            // Reset the game if it's over and space is pressed again
                            resetGame();
                        }
                    }
                }
            });
        }

        // Add pipes to the game at random heights
        private void addPipes() {
            int pipeHeight = 100 + (int) (Math.random() * 300);  // Random height for the top pipe
            pipes.add(new Rectangle(WIDTH, 0, PIPE_WIDTH, pipeHeight));  // Top pipe
            pipes.add(new Rectangle(WIDTH, pipeHeight + PIPE_GAP, PIPE_WIDTH, HEIGHT - pipeHeight - PIPE_GAP));  // Bottom pipe
        }

        // Reset game state when restarting
        private void resetGame() {
            birdY = HEIGHT / 2;
            birdVelocity = 0;
            pipes.clear();
            addPipes();
            gameOver = false;
        }

        // Game loop
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!gameOver) {
                birdVelocity += GRAVITY; // Apply gravity to the bird
                birdY += birdVelocity;   // Update bird's Y position

                // Prevent the bird from falling through the ground
                if (birdY > HEIGHT - BIRD_SIZE) {
                    birdY = HEIGHT - BIRD_SIZE;
                    birdVelocity = 0;
                    gameOver = true;
                }

                // Move pipes to the left
                for (Rectangle pipe : pipes) {
                    pipe.x -= PIPE_SPEED;
                }

                // Check for pipe collisions or off-screen pipes
                if (pipes.get(0).x + PIPE_WIDTH < 0) {
                    pipes.remove(0);  // Remove top pipe
                    pipes.remove(0);  // Remove bottom pipe
                    addPipes();       // Add new pipes
                }

                // Collision detection with pipes
                for (Rectangle pipe : pipes) {
                    if (new Rectangle(100, birdY, BIRD_SIZE, BIRD_SIZE).intersects(pipe)) {
                        gameOver = true;
                    }
                }
            }

            repaint();  // Redraw the screen with the updated positions
        }

        // Paint the game elements on the screen
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            // Draw background
            g.setColor(Color.CYAN);
            g.fillRect(0, 0, WIDTH, HEIGHT);

            // Draw the bird
            g.setColor(Color.YELLOW);
            g.fillOval(100, birdY, BIRD_SIZE, BIRD_SIZE);

            // Draw the pipes
            g.setColor(Color.GREEN);
            for (Rectangle pipe : pipes) {
                g.fillRect(pipe.x, pipe.y, pipe.width, pipe.height);
            }

            // If game over, display text
            if (gameOver) {
                g.setColor(Color.RED);
                g.setFont(new Font("Arial", Font.BOLD, 50));
                g.drawString("Game Over", WIDTH / 3, HEIGHT / 2);
                g.setFont(new Font("Arial", Font.PLAIN, 30));
                g.drawString("Press SPACE to Restart", WIDTH / 3 - 50, HEIGHT / 2 + 50);
            }
        }



        public class SoundManager {
            private Clip backgroundClip; // To hold the background music clip

            // Method to play the background music
            public void playBackgroundMusic(String musicFile) {
                try {
                    // Load the audio input stream
                    AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(getClass().getResource("/Sound/mixkit-game-level-music-689.wav"));

                    backgroundClip = AudioSystem.getClip(); // Get a clip resource
                    backgroundClip.open(audioInputStream); // Open the audio input stream
                    backgroundClip.loop(Clip.LOOP_CONTINUOUSLY); // Loop the music continuously
                    backgroundClip.start(); // Start playing the sound
                } catch (UnsupportedAudioFileException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (LineUnavailableException e) {
                    e.printStackTrace();
                }
            }

            // Method to stop the background music
            public void stopBackgroundMusic() {
                if (backgroundClip != null && backgroundClip.isRunning()) {
                    backgroundClip.stop(); // Stop the music
                }
            }

            public  void main(String[] args) {
                SoundManager soundManager = new SoundManager();
                soundManager.playBackgroundMusic("mixkit-game-level-music-689.wav"); // Play the background music
            }
        }



        // Main method to start the game
        public static void main(String[] args) {
            new FlappyBird();

        }



    }


















