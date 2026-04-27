import java.awt.Graphics2D;
import java.awt.Image;

public class Helicopter {

    private Image image;
    private int x, y;
    int width = 220, height = 90; // dimensions for drawing

    public Helicopter(int x, int y) {
        this.image = ImageManager.loadImage("Additional objects/helicopter.png");
        this.x = x;
        this.y = y;
    }

    public void update(int phase) {
        // phase controls behavior
        if (phase == 0) {
            x -= 5; // move left
        } else if (phase == 1) {
            y += 4; // descend
        }
    }

    public void draw(Graphics2D g, int offsetX) {
        g.drawImage(image, x + offsetX, y, width, height, null);
    }

    public int getX() { return x; }
    public int getY() { return y; }
}
