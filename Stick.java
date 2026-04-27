import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Rectangle2D;
import javax.swing.JPanel;
import java.awt.Point;

public class Stick {

    private static final int WIDTH = 170;   // adjust as needed
    
    private static final int HEIGHT = 31;
    private boolean inAir = false;
    private boolean moving=false;

    private boolean playerWasOn = false;

    private int fallSpeed = 0;
    private static final int GRAVITY = 2;
    private static final int MAX_FALL_SPEED = 12;

    private int x, y,dx;
    private int startX, startY;
    private Image image;
    private TileMap tileMap;
    private GamePanel panel;

    private String position =null; 

    public Stick(GamePanel panel, int x, int y, TileMap tileMap) {
        this.panel = panel;
        this.x = x;
        this.y = y;

        this.dx = 3;

        this.startX = x;
        this.startY = y;
        this.tileMap = tileMap;

        image = ImageManager.loadImage("wooden-stick.png");

        if(panel.getLevel()==2){
            position="left";
        }
    }

    public void draw(Graphics2D g2, int offsetX) {
        g2.drawImage(image, x + offsetX, y, WIDTH, HEIGHT, null);
    }

    public Rectangle2D.Double getBounds() {
        return new Rectangle2D.Double(x, y, WIDTH, HEIGHT);
    }

    public int getX() { return x; }
    public int getY() { return y; }

   

    

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





    public boolean collidesRight() {
    int newX = x + dx;
    int offsetY = tileMap.getOffsetY();

    int checkX = newX + WIDTH; // right side

    int topTile = tileMap.pixelsToTiles(y - offsetY);
    int bottomTile = tileMap.pixelsToTiles(y - offsetY + HEIGHT - 1);
    int xTile = tileMap.pixelsToTiles(checkX);

    for (int yTile = topTile; yTile <= bottomTile; yTile++) {
        if (tileMap.getTile(xTile, yTile) != null) {
            return true;
        }
    }
    return false;
    }




    public boolean collidesLeft() {
            int newX = x - dx;
            int offsetY = tileMap.getOffsetY();

            int checkX = newX; // left side

            int topTile = tileMap.pixelsToTiles(y - offsetY);
            int bottomTile = tileMap.pixelsToTiles(y - offsetY + HEIGHT - 1);
            int xTile = tileMap.pixelsToTiles(checkX);

            for (int yTile = topTile; yTile <= bottomTile; yTile++) {
                if (tileMap.getTile(xTile, yTile) != null) {
                    return true;
                }
            }
            return false;
    }

    public void update() {

    Player player = tileMap.getPlayer();
    if(player.isOffMap()){
        player.harmfulCollision();
    }


    boolean playerOn = tileMap.getPlayer().landedOnStick();

    if (playerOn && !playerWasOn && !moving) {
        moving = true;
        
    }

    playerWasOn = playerOn;

       if (moving){

            if(position.equals("left")  ){
                    x=x+dx;
                    player.moveWithStick(dx); // move player with stick

                    if(collidesRight()){
                        position="right";
                        moving=false;
                    }
            }

        else  if(position.equals("right") ){
                    x=x-dx;
                    player.moveWithStick(-dx); // move player with stick

                    if(collidesLeft()){
                        position="left";
                        moving=false;
                    }
            }
       }
    }
                
            
    


public boolean isMoving(){return this.moving;}
public String getPosition(){return this.position;}

    public void reset() {
        x = startX;
        y = startY;
    if(panel.getLevel()==2){
        position="left";
        moving=false;
    }
    }
}