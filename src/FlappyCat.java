import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class FlappyCat extends JPanel implements ActionListener, KeyListener {
//    int boardWidth = 360;
//    int boardHeight = 640;

    int boardWidth = 800;
    int boardHeight = 600;

    //images
    Image backgroundImg;
    Image catImg;
    Image[] topPipeImgs = new Image[3]; // Array to store different top pipe images
    Image[] bottomPipeImgs = new Image[3];

    //cat class
    int catX = boardWidth/8;
    int catY = boardWidth/2;
    int catWidth = 60;
    int catHeight = 30;

    class Cat {
        int x = catX;
        int y = catY;
        int width = catWidth;
        int height = catHeight;
        Image img;

        Cat(Image img) {
            this.img = img;
        }
    }

    //pipe class
    int pipeX = boardWidth;
    int pipeY = 0;
    int pipeWidth = 64;  //scaled by 1/6
    int pipeHeight = 512;

    class Pipe {
        int x = pipeX;
        int y = pipeY;
        int width = pipeWidth;
        int height = pipeHeight;
        Image img;
        boolean passed = false;

        Pipe(Image img) {
            this.img = img;
        }
    }

    //game logic
    Cat cat;
    int velocityX = -4; //move pipes to the left speed (simulates cat moving right)
    int velocityY = 0; //move cat up/down speed.
    int gravity = 1;

    ArrayList<Pipe> pipes;
    Random random = new Random();

    Timer gameLoop;
    Timer placePipeTimer;
    boolean gameOver = false;
    double score = 0;

    FlappyCat() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        // setBackground(Color.blue);
        setFocusable(true);
        addKeyListener(this);

        //load images
        backgroundImg = new ImageIcon(getClass().getResource("/image/flappybirdbg.png")).getImage();
        catImg = new ImageIcon(getClass().getResource("/image/flappycat.png")).getImage();

        //topPipeImg = new ImageIcon(getClass().getResource("/image/toppipe.png")).getImage();
        topPipeImgs[0] = new ImageIcon(getClass().getResource("/image/toppipe.png")).getImage();
        topPipeImgs[1] = new ImageIcon(getClass().getResource("/image/toppipe.png")).getImage();
        topPipeImgs[2] = new ImageIcon(getClass().getResource("/image/toppipe.png")).getImage();

        //bottomPipeImg = new ImageIcon(getClass().getResource("/image/bottompipe.png")).getImage();
        bottomPipeImgs[0] = new ImageIcon(getClass().getResource("/image/bottompipe.png")).getImage();
        bottomPipeImgs[1] = new ImageIcon(getClass().getResource("/image/bottompipe.png")).getImage();
        bottomPipeImgs[2] = new ImageIcon(getClass().getResource("/image/bottompipe.png")).getImage();

        //cat
        cat = new Cat(catImg);
        pipes = new ArrayList<Pipe>();

        //place pipes timer
        placePipeTimer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Code to be executed
                placePipes();
            }
        });
        placePipeTimer.start();

        //game timer
        gameLoop = new Timer(1800/60, this); //how long it takes to start timer, milliseconds gone between frames
        gameLoop.start();

        addKeyListener(this); // เพิ่ม KeyListener ให้กับ JFrame
        setFocusable(true);
        requestFocus(); // ขอให้ JFrame ได้รับการโฟกัสเมื่อเปิดแอปพลิเคชัน

        // Create restart button
        JButton restartButton = new JButton("Restart");
        restartButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                restartGame();
            }
        });
        restartButton.setFocusable(false); // Ensure button doesn't steal focus from the panel

        // Add restart button to a panel at the bottom of the frame
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(restartButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void restartGame() {
        // Reset game conditions
        cat.y = catY;
        velocityY = 0;
        pipes.clear();
        gameOver = false;
        score = 0;

        // Restart timers
        gameLoop.start();
        placePipeTimer.start();

        // Request focus for the panel to ensure key events are captured
        requestFocus();
    }

    public void placePipes() {
        int randomPipeY = (int) (pipeY - pipeHeight / 4 - Math.random() * (pipeHeight / 2));
        int openingSpace = boardHeight / 4;

        // ปรับเพิ่มความยากโดยการสุ่มความสูงของท่อและระยะห่างระหว่างท่อ
        int randomHeightOffset = random.nextInt(boardHeight / 4); // สุ่มความสูงเพิ่มเติมสำหรับท่อ
        int randomOpeningOffset = random.nextInt(boardHeight / 4); // สุ่มระยะห่างของช่องระหว่างท่อ

        Pipe topPipe = new Pipe(topPipeImgs[random.nextInt(3)]);
        topPipe.y = randomPipeY + randomHeightOffset; // เพิ่มความสูงสุ่ม
        pipes.add(topPipe);

        Pipe bottomPipe = new Pipe(bottomPipeImgs[random.nextInt(3)]);
        bottomPipe.y = topPipe.y + pipeHeight + openingSpace + randomOpeningOffset; // เพิ่มระยะห่างสุ่ม
        pipes.add(bottomPipe);
    }

    public void paint(Graphics g) {
        super.paint(g);
        draw(g);
    }

    public void draw(Graphics g) {
        //background
        //g.drawImage(backgroundImg, 0, 0, this.boardWidth, this.boardHeight, null);

        int width = getWidth(); // Get the actual width of the panel
        int height = getHeight(); // Get the actual height of the panel

        g.drawImage(backgroundImg, 0, 0, width, height, null);

        //cat
        g.drawImage(catImg, cat.x, cat.y, cat.width, cat.height, null);
        //pipes
        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }

        //score
        g.setColor(Color.white);

        g.setFont(new Font("Arial", Font.BOLD,  32));
        if (gameOver) {
            g.setColor(Color.red);
            g.setFont(new Font("Arial", Font.BOLD , 32));
            g.drawString("Game Over: " + String.valueOf((int) score), 10, 35);
        }
        else {
            g.drawString(String.valueOf((int) score), 10, 35);
        }
    }

    public void move() {
        int width = getWidth();
        int height = getHeight();

        //cat
        velocityY += gravity;
        cat.y += velocityY;
        cat.y = Math.max(cat.y, 0); //apply gravity to current cat.y, limit the cat.y to top of the canvas

        //pipes
        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            pipe.x += velocityX;

            if (!pipe.passed && cat.x > pipe.x + pipe.width) {
                score += 0.5; //0.5 because there are 2 pipes! so 0.5*2 = 1, 1 for each set of pipes
                pipe.passed = true;
            }

            if (collision(cat, pipe)) {
                gameOver = true;
            }
        }

        if (cat.y > height) {
            gameOver = true;
        }
    }

    boolean collision(Cat a, Pipe b) {
        return a.x < b.x + b.width &&   //a's top left corner doesn't reach b's top right corner
                a.x + a.width > b.x &&   //a's top right corner passes b's top left corner
                a.y < b.y + b.height &&  //a's top left corner doesn't reach b's bottom left corner
                a.y + a.height > b.y;    //a's bottom left corner passes b's top left corner
    }

    @Override
    public void actionPerformed(ActionEvent e) { //called every x milliseconds by gameLoop timer
        move();
        repaint();
        if (gameOver) {
            placePipeTimer.stop();
            gameLoop.stop();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (!gameOver && e.getKeyCode() == KeyEvent.VK_SPACE) {
                velocityY = -9; // สูงสุดความเร็วที่แมวกระโดด

            if (gameOver) {
                cat.y = catY;
                velocityY = 0;
                pipes.clear();
                gameOver = false;
                score = 0;
                gameLoop.start();
                placePipeTimer.start();
            }
        }
    }

    //not needed
    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}
}