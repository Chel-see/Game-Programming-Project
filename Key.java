import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import javax.swing.JPanel;
import java.util.Random;
import java.awt.Image;
import java.awt.image.BufferedImage;

public class Key {

   private JPanel panel;

   private int x;
   private int y;

   private int width;
   private int height;

   private int dx;        // increment to move along x-axis
   private int dy;        // increment to move along y-axis

   private Random random;

   private Player player;
   private SoundManager soundManager;
  
   private boolean keyColSound=false;

   Animation animation;

   private int frameNumber;

   private boolean collected;

   private double scaleImage;

   private int startX;
   private int startY;

   public Key (JPanel p, int xPos, int yPos, Player player) {
        panel = p;

        random = new Random();

        scaleImage = 2.0;

        x = xPos;
        y = yPos;

        startX = xPos;
        startY = yPos;

        dx = 20;
        dy = 20;

        this.player = player;

        animation = new Animation(true); // true = looping

        Image stripImage = ImageManager.loadImage("Key2.png");

        int frameCount = 7;

        int imageWidth = stripImage.getWidth(null) / frameCount;
        int imageHeight = stripImage.getHeight(null);

        for (int i = 0; i < frameCount; i++) {

            BufferedImage frameImage = new BufferedImage(
                imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);

            Graphics2D g = frameImage.createGraphics();

            g.drawImage(stripImage,
                0, 0, imageWidth, imageHeight,
                i * imageWidth, 0,
                (i + 1) * imageWidth, imageHeight,
                null);

            g.dispose();

            animation.addFrame(frameImage, 100);
        }

        width = (int) (imageWidth * scaleImage);
        height = (int) (imageHeight * scaleImage);


        frameNumber = 0;

        collected = false;
        animation.start();
        soundManager = SoundManager.getInstance();
        soundManager.setVolume("key", 0.85f);
   }


   public void draw (Graphics2D g2, int offsetX) {

        g2.drawImage(animation.getImage(), x + offsetX, y, width, height, null);

   }

   public void update() {
        animation.update();
        move();
    }


    public void move() {
    
        if (!panel.isVisible()) return;
        if (!collected) return;

        int targetX = player.getX() + 8;
        int targetY = player.getY() - height - 3;

        x += (targetX - x) * 0.35;
        y += (targetY - y) * 0.35;

        y += Math.sin(System.currentTimeMillis() * 0.005) * 2;
    }


    public Rectangle2D.Double getBoundingRectangle() {
        return new Rectangle2D.Double (x, y, width, height);
    }

    public boolean isCollected() {
        return collected;
    }

    public void collect() {
        collected = true;
        if(!keyColSound){
            soundManager.playSound("key", false);
            keyColSound = true;
        }
    }

    public void reset() {
        collected = false;
        keyColSound = false;
        x = startX;
        y = startY;
       
    }
}
