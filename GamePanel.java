import javax.swing.JPanel;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import javax.swing.Timer;
import java.awt.Font;
import java.io.File;
import java.awt.FontMetrics;
import java.awt.geom.Rectangle2D;
import java.awt.event.MouseEvent;

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

    private Font gameFont;
    private boolean inTransition = false;
    private int transitionTimer = 0;

    private final int FADE_OUT_TIME = 20;
    private final int HOLD_TIME = 20;
    private final int FADE_IN_TIME = 20;
    private final int TOTAL_TRANSITION_DURATION = FADE_OUT_TIME + HOLD_TIME + FADE_IN_TIME;

    private boolean showBanner = false;
    private String bannerText = "";
    private int bannerTimer = 0;

    private Font bannerFont;

    // total frames banner is visible
    private final int BANNER_DURATION = 40;

    // animation timing
    private final int BANNER_SLIDE_TIME = 10;

    private long finalTime;
    private boolean gameOverFlag = false;

    private Image restartNormal;
    private Image restartPressed;

    private boolean restartHover = false;
    private boolean restartDown = false;

    private Rectangle2D.Double restartBounds;

    public GamePanel () {

        isRunning = false;
        isPaused = false;
        isAnimShown = false;
        isAnimPaused = false;

        soundManager = SoundManager.getInstance();

        image = new BufferedImage (600, 500, BufferedImage.TYPE_INT_ARGB);

        coinImage = ImageManager.loadImage("Animated objects/Rune-single.png");
        level = 1;
        levelChange = false;

        gameFont = loadFont("fonts/Not Jam Third Dimension 15.ttf");
        bannerFont = loadFont("fonts/Not Jam Mono Clean 13");

        restartNormal = ImageManager.loadImage("UI/restart_unpressed.png");
        restartPressed = ImageManager.loadImage("UI/restart_pressed.png");
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

        if (inTransition) {
            transitionTimer--;

            if (transitionTimer == (FADE_IN_TIME + HOLD_TIME)) {
                level++;
                //levelChange = true;
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
                showBanner("Level " + level);
            }

            if (transitionTimer <= 0) {
                inTransition = false;
            }

            return; // skip the rest of the update while in transition
        }


/*           if (levelChange) {
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
        } */

        if (showBanner) {
            bannerTimer--;
            
            if (bannerTimer <= 0) {
                showBanner = false;
            }
        }


        if (gameOver){
            System.out.println("GAME OVER"); // *** change this to the real game over method when implemented
            endGame();
        }
        
    }

    public void endLevel() {
        //level = level + 1;
        //levelChange = true;
        inTransition = true;
        transitionTimer = TOTAL_TRANSITION_DURATION;
    }


    public void gameRender() {

        // draw the game objects on the image
        Graphics2D imageContext = (Graphics2D) image.getGraphics();

        tileMap.draw(imageContext);

        hearts.draw(imageContext);

        int coinCountX = 5;
        int coinCountY = 40; // below hearts

        imageContext.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 16));

        // draw coin icon
        imageContext.drawImage(coinImage, coinCountX, coinCountY, 25, 25, null);

        // draw text
        imageContext.drawString("x " + coinCount, coinCountX + 30, coinCountY + 18);

        gameTimer.draw(imageContext, getWidth(), isPaused);

        if (isAnimShown)
            imageEffect.draw(imageContext);

        // --- TRANSITION OVERLAY-
        if (inTransition) {

            float alpha;

            if (transitionTimer > (HOLD_TIME + FADE_IN_TIME)) {
                // FADE OUT
                int t = transitionTimer - (HOLD_TIME + FADE_IN_TIME);
                alpha =  (t / (float) FADE_OUT_TIME);

            } else if (transitionTimer >= FADE_IN_TIME && transitionTimer <= (HOLD_TIME + FADE_IN_TIME)) {
                // HOLD
                alpha = 0f;

            } else {
                // FADE IN
                alpha = 1.0f - (transitionTimer / (float) FADE_IN_TIME);
            }

            // apply transparency
            BufferedImage originalCopy = new BufferedImage(
                image.getWidth(),
                image.getHeight(),
                BufferedImage.TYPE_INT_ARGB
            );
            Graphics2D g = originalCopy.createGraphics();
            g.drawImage(image, 0, 0, null);
            g.dispose();
            BufferedImage faded = applyFade(originalCopy, (int)(alpha * 255));

            // draw black background first
            imageContext.setColor(java.awt.Color.BLACK);
            imageContext.fillRect(0, 0, getWidth(), getHeight());

            // draw faded game on top
            imageContext.drawImage(faded, 0, 0, null);

            // draw text during dark phase
            if (transitionTimer >= FADE_IN_TIME && transitionTimer <= (HOLD_TIME + FADE_IN_TIME)) {
                float fontSize = getHeight() * 0.1f;
                imageContext.setFont(gameFont.deriveFont(fontSize));
                imageContext.setColor(java.awt.Color.WHITE);

                String text = "Level " + "1" + " Complete!";

                java.awt.FontMetrics fm = imageContext.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(text)) / 2;
                int y = getHeight() / 2;

                imageContext.drawString(text, x, y);
            }
        }

        if (showBanner) {

            int bannerWidth = getWidth();
            int bannerHeight = 40;
        
            int y;
            int timeLeft = bannerTimer;
        
            if (timeLeft > BANNER_DURATION - BANNER_SLIDE_TIME) {
                int t = BANNER_DURATION - timeLeft;
                float progress = t / (float) BANNER_SLIDE_TIME;
                y = (int)(-bannerHeight + progress * bannerHeight);
        
            } else if (timeLeft < BANNER_SLIDE_TIME) {
                float progress = timeLeft / (float) BANNER_SLIDE_TIME;
                y = (int)(-bannerHeight + progress * bannerHeight);
        
            } else {
                y = 0;
            }
        
            // IMPORTANT: draw onto OFFSCREEN BUFFER
            imageContext.setColor(new java.awt.Color(0, 0, 0, 180));
            imageContext.fillRect(0, y, bannerWidth, bannerHeight);
        
            imageContext.setFont(bannerFont.deriveFont(20f));
            imageContext.setColor(java.awt.Color.WHITE);
        
            FontMetrics fm = imageContext.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(bannerText)) / 2;
            int textY = y + 28;
        
            imageContext.drawString(bannerText, x, textY);
        }

        if (gameOverFlag) {
            // draw game over screen
            imageContext.setColor(new java.awt.Color(0, 0, 0, 200));
            imageContext.fillRect(0, 0, getWidth(), getHeight());

            String gameOverText = "GAME OVER";
            String timeText = "Final Time: " + gameTimer.formatTime(finalTime);

            float fontSize = getHeight() * 0.15f;
            imageContext.setFont(gameFont.deriveFont(fontSize));
            imageContext.setColor(java.awt.Color.RED);

            FontMetrics fm = imageContext.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(gameOverText)) / 2;
            int y = getHeight() / 2 - 20;

            imageContext.drawString(gameOverText, x, y);

            imageContext.setFont(gameFont.deriveFont(20f));
            fm = imageContext.getFontMetrics();
            x = (getWidth() - fm.stringWidth(timeText)) / 2;
            y += 40;

            imageContext.setFont(bannerFont.deriveFont(20f));
            FontMetrics fm2 = imageContext.getFontMetrics();
            int x2 = (getWidth() - fm2.stringWidth(timeText)) / 2;
            imageContext.setColor(java.awt.Color.WHITE);
            imageContext.drawString(timeText, x2, y);

            int btnWidth = 120;
            int btnHeight = 50;

            int btnX = (getWidth() - btnWidth) / 2;
            int btnY = getHeight() / 2 + 40;

            restartBounds = new Rectangle2D.Double(btnX, btnY, btnWidth, btnHeight);

            Image btnImage;

            if (restartDown) {
                btnImage = restartPressed;
            } else if (restartHover) {
                btnImage = restartPressed;
            } else {
                btnImage = restartNormal;
            }

            imageContext.drawImage(btnImage, btnX, btnY, btnWidth, btnHeight, null);
        }

        Graphics2D g2 = (Graphics2D) getGraphics();
        g2.drawImage(image, 0, 0, 600, 500, null);

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
                showBanner("Level " + level);
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

            //loadFont();
        }
    }


    public void startNewGame() {                // initialise and start a new game thread 
        // if (gameThread != null || !isRunning) {
        //     //soundManager.playSound ("background", true);

        //     tileManager = new TileMapManager (this);

        //     try {
        //         tileMap = tileManager.loadMap("maps/map2.txt");
        //         int w, h;
        //         w = tileMap.getWidth();
        //         h = tileMap.getHeight();
        //         System.out.println ("Width of tilemap " + w);
        //         System.out.println ("Height of tilemap " + h);
        //     }
        //     catch (Exception e) {
        //         System.out.println(e);
        //         System.exit(0);
        //     }

        //     createGameEntities();

        //     if (gameTimer == null) {
        //         gameTimer = new GameTimer();
        //     }
        //     gameTimer.start();

        //     gameThread = new Thread(this);
        //     gameThread.start();            

        // }

            if (gameThread != null) {   // remove this after testing phase
                this.level = 2;
                levelChange = true;
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

        if (!hearts.isDead()) {
            showBanner("You died! Respawning...");
        }
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
        gameTimer.pause();
        finalTime = gameTimer.getTime();
        isRunning = false;
        gameOverFlag = true;
        gameRender();
    }

    public boolean canAcceptInput() {
        return tileMap != null && !tileMap.isResetting();
    }

    public void collectCoin() {
        this.coinCount++;

        if (coinCount % 5 == 0) { // every 5 coins, gain an extra life
            hearts.extraLife();
            System.out.println("Extra life gained!");
            showBanner("+1 Life!");
        }
    }

    public int getLevel(){
        return this.level;   
    }

    private Font loadFont(String filename) {
        try {
            Font font = Font.createFont(Font.TRUETYPE_FONT, new File(filename));
            return font.deriveFont(Font.PLAIN, 24);
        } catch (Exception e){
            e.printStackTrace();
             return new Font("Arial", Font.PLAIN, 24); // fallback font
        }
    }

    private BufferedImage applyFade(BufferedImage original, int alpha) {

        int w = original.getWidth();
        int h = original.getHeight();
    
        int[] pixels = new int[w * h];
        original.getRGB(0, 0, w, h, pixels, 0, w);
    
        for (int i = 0; i < pixels.length; i++) {
    
            int a = (pixels[i] >> 24) & 255;
    
            if (a != 0) {
                int red = (pixels[i] >> 16) & 255;
                int green = (pixels[i] >> 8) & 255;
                int blue = pixels[i] & 255;
    
                pixels[i] = (alpha << 24) | (red << 16) | (green << 8) | blue;
            }
        }
    
        BufferedImage faded = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        faded.setRGB(0, 0, w, h, pixels, 0, w);
    
        return faded;
    }

    public void resetHearts(){
        hearts.reset(3);
    }

    public void showBanner(String text) {
        bannerText = text;
        bannerTimer = BANNER_DURATION;
        showBanner = true;
    }

    public void handleMouseMove(MouseEvent e) {
        if (restartBounds != null) {
            restartHover = restartBounds.contains(e.getPoint());
            gameRender(); // re-render to update button state
        }
    }

    public void handleMousePress(MouseEvent e) {
        if (gameOverFlag && restartBounds != null) {
            if (restartBounds.contains(e.getPoint())) {
                restartDown = true;
                gameRender();
            }
        }
    }
    
    public void handleMouseRelease(MouseEvent e) {
        if (gameOverFlag && restartBounds != null) {
            if (restartDown && restartBounds.contains(e.getPoint())) {
                restartGame();
            }
        }
        restartDown = false;
        gameRender();
    }

    private void restartGame() {

        // reset core state
        level = 1;
        coinCount = 0;
        gameOver = false;
        gameOverFlag = false;
    
        resetHearts();
    
        // reload map
        tileManager = new TileMapManager(this);
        try {
            tileMap = tileManager.loadMap("maps/map1.txt");
        } catch (Exception e) {
            e.printStackTrace();
        }
    
        // reset timer
        gameTimer = new GameTimer();
        gameTimer.start();
    
        // restart loop
        isRunning = true;
        gameThread = new Thread(this);
        gameThread.start();
    
        showBanner("Level 1");
    }
}