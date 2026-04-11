import javax.swing.JPanel;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import javax.swing.Timer;

/**
   A component that displays all the game entities
*/

public class GamePanel extends JPanel
               implements Runnable {

    private SoundManager soundManager;

    private boolean isRunning;
    private boolean isPaused;

    private Thread gameThread;

    private BufferedImage image;
     private Image backgroundImage;

  
    private volatile boolean isAnimShown;
    private volatile boolean isAnimPaused;

    private ImageEffect imageEffect;        // sprite demonstrating an image effect

    TileMapManager tileManager;
    TileMap    tileMap;

    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private boolean jumpPressed = false;

    private boolean jumpWasPressed = false;

    private Heart hearts;

    private boolean gameOver = false;
    

    private GameTimer gameTimer;

    private int coinCount = 0;
    private Image coinImage;

    private boolean levelChange;
    private int level;

    public GamePanel () {

        isRunning = false;
        isPaused = false;
        isAnimShown = false;
        isAnimPaused = false;

        soundManager = SoundManager.getInstance();

        image = new BufferedImage (600, 500, BufferedImage.TYPE_INT_RGB);

        coinImage = ImageManager.loadImage("Animated objects/Rune-single.png");
        level = 1;
        levelChange = false;
    }


    public void createGameEntities() {
    
        imageEffect = new ImageEffect (this);
    }


    public void run () {
        try {
            isRunning = true;
            while (isRunning) {
                if (!isPaused)
                    gameUpdate();
                gameRender();
                Thread.sleep (50);    
            }
        }
        catch(InterruptedException e) {}
    }


    public void gameUpdate() {

        processInput(); // player movements

        tileMap.update();

        if (!isPaused && isAnimShown)
           
        imageEffect.update();


          if (levelChange) {
                levelChange = false;
                tileManager = new TileMapManager (this);

            try {
                String filename = "maps/map" + level + ".txt";
                tileMap = tileManager.loadMap(filename) ;
         }
            catch (Exception e) {        // no more maps: terminate game
                gameOver = true;
                System.out.println(e);
              
                return;
            }
        }




        if (gameOver){
            System.out.println("GAME OVER"); // *** change this to the real game over method when implemented
            endGame();
        }
        
    }

    public void endLevel() {
        level = level + 1;
        levelChange = true;
    }


    public void gameRender() {

        // draw the game objects on the image

        Graphics2D imageContext = (Graphics2D) image.getGraphics();

        tileMap.draw (imageContext);

        hearts.draw(imageContext);

        int coinCountX = 5;
        int coinCountY = 40; // below hearts

        imageContext.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 16));

        // draw coin icon
        imageContext.drawImage(coinImage, coinCountX,coinCountY, 25, 25, null);

        // draw text
        imageContext.drawString("x " + coinCount, coinCountX + 30, coinCountY + 18);

        gameTimer.draw(imageContext, getWidth(), isPaused);

        if (isAnimShown)
                 // draw the animation

        imageEffect.draw(imageContext);            // draw the image effect

        Graphics2D g2 = (Graphics2D) getGraphics();    // get the graphics context for the panel
        g2.drawImage(image, 0, 0, 600, 500, null);    // draw the image on the graphics context

        imageContext.dispose();
    }


    public void startGame() {                // initialise and start the game thread 

        if (gameThread == null) {
            //soundManager.playSound ("background", true);

            tileManager = new TileMapManager (this);

            try {
                tileMap = tileManager.loadMap("maps/map1.txt");
                int w, h;
                w = tileMap.getWidth();
                h = tileMap.getHeight();
                System.out.println ("Width of tilemap " + w);
                System.out.println ("Height of tilemap " + h);
            }
            catch (Exception e) {
                System.out.println(e);
                System.exit(0);
            }

            createGameEntities();

            hearts = new Heart(3);

            gameTimer = new GameTimer();
            gameTimer.start();

            gameThread = new Thread(this);
            gameThread.start();
        }
    }


    public void startNewGame() {                // initialise and start a new game thread 
        if (gameThread != null || !isRunning) {
            //soundManager.playSound ("background", true);

            tileManager = new TileMapManager (this);

            try {
                tileMap = tileManager.loadMap("maps/map2.txt");
                int w, h;
                w = tileMap.getWidth();
                h = tileMap.getHeight();
                System.out.println ("Width of tilemap " + w);
                System.out.println ("Height of tilemap " + h);
            }
            catch (Exception e) {
                System.out.println(e);
                System.exit(0);
            }

            createGameEntities();

            if (gameTimer == null) {
                gameTimer = new GameTimer();
            }
            gameTimer.start();

            gameThread = new Thread(this);
            gameThread.start();            

        }
    }


    public void pauseGame() {                // pause the game (don't update game entities)
        if (isRunning) {
            if (isPaused){
                isPaused = false;
                gameTimer.start(); // resume timer when unpausing
            }
            else{
                isPaused = true;
                gameTimer.pause(); // pause timer when pausing
            }

          
        }
    }


    public void endGame() {                    // end the game thread
        isRunning = false;
        //soundManager.stopClip ("background");
    }

    
    public void moveLeft() {
        tileMap.moveLeft();
    }


    public void moveRight() {
        tileMap.moveRight();
    }


    public void jump() {
        tileMap.jump();
    }

    
    public void showAnimation() {
        isAnimShown = true;
       
        
    }

    public void setLeftPressed(boolean value) {
        leftPressed = value;
    }
    
    public void setRightPressed(boolean value) {
        rightPressed = value;
    }
    
    public void setJumpPressed(boolean value) {
        jumpPressed = value;
    }

    private void processInput() {
        if (leftPressed) {
            tileMap.moveLeft();
        }
        if (rightPressed) {
            tileMap.moveRight();
        }
        if (jumpPressed && !jumpWasPressed) {
            tileMap.jump();
        }
        if(!leftPressed && !rightPressed) {
            tileMap.moveNowhere();
        }

        jumpWasPressed = jumpPressed;
    }

    public void loseLife() {
        hearts.loseLife();
        this.coinCount = 0;
    }

    public boolean isPlayerDead() {
        return hearts.isDead();
    }

    public void respawnDelay(Runnable afterDelay) {
        new javax.swing.Timer(2000, e -> {
            afterDelay.run(); // call the actual reset after 2 seconds
            ((javax.swing.Timer)e.getSource()).stop(); // stop timer
        }).start();
    }

    public void gameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public boolean canAcceptInput() {
        return tileMap != null && !tileMap.isResetting();
    }

    public void collectCoin() {
        this.coinCount++;
    }

    public int getLevel(){
        return this.level;   
    }
}