import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Rectangle2D;
import javax.swing.JPanel;
import java.awt.image.BufferedImage;

public class Door {

    private static final int WIDTH = 64;   // width to draw the door
    private static final int HEIGHT = 96;  // height to draw the door

    private JPanel panel;
    private int x, y;

    private Player player;
    private Key key;

    private Animation animation;
    private boolean activated;    // true when door animation is playing
    private boolean finished;     // true when animation finished

    public Door(JPanel panel, int xPos, int yPos, Player player, Key key) {
        this.panel = panel;
        this.x = xPos;
        this.y = yPos;
        this.player = player;
        this.key = key;

        animation = new Animation(false); // non-looping

        // Load spritesheet
        Image stripImage = ImageManager.loadImage("doors/door/door-spritesheet.png");
        int frameCount = 10;
        int frameWidth = stripImage.getWidth(null) / frameCount;
        int frameHeight = stripImage.getHeight(null);

        for (int i = 0; i < frameCount; i++) {
            BufferedImage frameImage = new BufferedImage(frameWidth, frameHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = frameImage.createGraphics();
            g.drawImage(stripImage,
                    0, 0, frameWidth, frameHeight,
                    i * frameWidth, 0, (i * frameWidth) + frameWidth, frameHeight,
                    null);
            g.dispose();
            animation.addFrame(frameImage, 100);
        }

        animation.start(); // start so first frame is displayed
        animation.stop();  // stop so it doesn’t advance until triggered
        activated = false;
        finished = false;
    }

    public void update() {
        if (!activated && !finished && player.getBounds().intersects(getBoundingRectangle())) {
            if (key.isCollected()) {
                animation.start();
                activated = true;
            }
        }

        if (activated) {
            animation.update();
            if (!animation.isStillActive()) { // animation finished
                finished = true;
                activated = false;
                System.out.println("Switch to level 2");
            }
        }
    }

    public void draw(Graphics2D g2, int offsetX) {
        g2.drawImage(animation.getImage(), x + offsetX, y, WIDTH, HEIGHT, null);
    }

    public Rectangle2D.Double getBoundingRectangle() {
        return new Rectangle2D.Double(x, y, WIDTH, HEIGHT);
    }

    public int getX() { return x; }
    public int getY() { return y; }

    public int getWidth() {
        return WIDTH;
    }

    public boolean isSolid() {
        return activated;
    }

    public boolean collidesWithPlayer() {
        Rectangle2D.Double doorRect = getBoundingRectangle();
        Rectangle2D.Double playerRect = player.getBounds();

        return doorRect.intersects(playerRect);
    }

    public void reset() {
        activated = finished = false;
        animation.stop();
    }
}