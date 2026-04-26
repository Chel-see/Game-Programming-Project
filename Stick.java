import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Rectangle2D;
import javax.swing.JPanel;
import java.awt.Point;

public class Stick {

    private static final int WIDTH = 170;   // adjust as needed
    private static final int HEIGHT = 32;
    private boolean inAir = false;
    private int fallSpeed = 0;
    private static final int GRAVITY = 2;
    private static final int MAX_FALL_SPEED = 12;

    private int x, y;
    private int startX, startY;
    private Image image;
    private TileMap tileMap;
    private JPanel panel;

    public Stick(JPanel panel, int x, int y, TileMap tileMap) {
        this.panel = panel;
        this.x = x;
        this.y = y;
        this.startX = x;
        this.startY = y;
        this.tileMap = tileMap;

        image = ImageManager.loadImage("wooden-stick.png");
    }

    public void draw(Graphics2D g2, int offsetX) {
        g2.drawImage(image, x + offsetX, y, WIDTH, HEIGHT, null);
    }

    public Rectangle2D.Double getBounds() {
        return new Rectangle2D.Double(x, y, WIDTH, HEIGHT);
    }

    public int getX() { return x; }
    public int getY() { return y; }


private Point collidesWithTileDown(int newY) {

    int offsetY = tileMap.getOffsetY();

    int leftTile = TileMap.pixelsToTiles(x);
    int rightTile = TileMap.pixelsToTiles(x + WIDTH - 1);

    int yTile = TileMap.pixelsToTiles(newY - offsetY + HEIGHT);

    // check both feet
    if (tileMap.getTile(leftTile, yTile) != null) {
        return new Point(leftTile, yTile);
    }

    if (tileMap.getTile(rightTile, yTile) != null) {
        return new Point(rightTile, yTile);
    }

    return null;
}

private boolean isInAir() {

    int offsetY = tileMap.getOffsetY();

    int leftTile = TileMap.pixelsToTiles(x);
    int rightTile = TileMap.pixelsToTiles(x + WIDTH - 1);
    int yTile = TileMap.pixelsToTiles(y - offsetY + HEIGHT + 1);

    return (tileMap.getTile(leftTile, yTile) == null &&
            tileMap.getTile(rightTile, yTile) == null);
}


    public boolean move(int dx) {
        int newX = x + dx;

        int checkX;
        if (dx > 0) {
            checkX = newX + WIDTH;
        } else {
            checkX = newX;
        }

        int topTile = TileMap.pixelsToTiles(y - tileMap.getOffsetY());
        int bottomTile = TileMap.pixelsToTiles(y - tileMap.getOffsetY() + HEIGHT - 1);
        int xTile = TileMap.pixelsToTiles(checkX);

        for (int yTile = topTile; yTile <= bottomTile; yTile++) {
            if (tileMap.getTile(xTile, yTile) != null) {
                if (dx > 0) {
                    newX = (xTile * 32) - WIDTH; // align to left of tile
                } else {
                    newX = ((xTile + 1) * 32); // align to right of tile
                }
                return false; // blocked
            }
        }

        x = newX;
        return true;
    }


public void update() {

    if (isInAir()) {
        inAir = true;
    }

    if (inAir) {

        fallSpeed += GRAVITY;
        if (fallSpeed > MAX_FALL_SPEED) {
            fallSpeed = MAX_FALL_SPEED;
        }

        int newY = y + fallSpeed;

        Point tilePos = collidesWithTileDown(newY);

        if (tilePos != null) {
            // LAND
            int offsetY = tileMap.getOffsetY();
            int tileTopY = ((int) tilePos.getY()) * 32 + offsetY;

            y = tileTopY - HEIGHT;

            inAir = false;
            fallSpeed = 0;

        } else {
            y = newY;
        }
    }
}




    public void reset() {
        x = startX;
        y = startY;
    }
}