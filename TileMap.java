import java.awt.Image;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.LinkedList;
import java.util.Iterator;
import javax.swing.JPanel;

/**
    The TileMap class contains the data for a tile-based
    map, including Sprites. Each tile is a reference to an
    Image. Images are used multiple times in the tile map.
    map.
*/

public class TileMap {

    private static final int TILE_SIZE = 32;
   private static final int TILE_SIZE_BITS = 6;

    private Image[][] tiles;
    private int screenWidth, screenHeight;
    private int mapWidth, mapHeight;
    private int offsetY;

    private LinkedList sprites;

    private Player player;
    private Key key;
    private Door door;
    private Villain [] villains ; 
    private Coin [] coins;

    BackgroundManager bgManager;

    //private JPanel panel;
    private GamePanel panel;
    private Dimension dimension;

    private char[][] tileTypes;

    private boolean resetting = false;

    private Stick stick;

    /**
        Creates a new TileMap with the specified width and
        height (in number of tiles) of the map.
    */
    public TileMap(GamePanel panel, int width, int height) {

    this.panel = panel;
    dimension = panel.getSize();

    screenWidth = dimension.width;
    screenHeight = dimension.height;

    System.out.println ("Width: " + screenWidth);
    System.out.println ("Height: " + screenHeight);


    mapWidth = width;
    mapHeight = height;

        // get the y offset to draw all sprites and tiles

           offsetY = screenHeight - tilesToPixels(mapHeight);
    System.out.println("offsetY: " + offsetY);

    bgManager = new BackgroundManager (panel, 12);

    tiles = new Image[mapWidth][mapHeight];
    tileTypes = new char[mapWidth][mapHeight];

    this.stick = new Stick(panel, 730, 65, this); // choose position

    player = new Player (panel, this, bgManager);

    key = new Key (panel, 1820, 180, player);

    door = new Door (panel, 1350, 275, player, key);

    

    villains = new Villain [3];
    villains[0] = new Villain(850, 322, player, 2, 55, 410, 3);
    villains[1] = new Villain(1420, 322, player, 3, 0, 100, 8);
    villains[2] = new Villain(12, 226, player, 4, 12, 110, 2);

    coins = new Coin[5];
    coins[0] = new Coin(0, 102, player);
    coins[1] = new Coin(720, 230, player);
    coins[2] = new Coin(198, 130, player);
    coins[3] = new Coin(280, 230, player);
    coins[4] = new Coin(995, 40, player);

    sprites = new LinkedList();

    

    int playerHeight = player.getHeight();

    int x, y;
    x = (dimension.width / 2) + TILE_SIZE;        // position player in middle of screen

    //x = 192;                    // position player in 'random' location
    y = dimension.height - ((TILE_SIZE*3) + playerHeight);

        player.setX(x);
        player.setY(y);

    System.out.println("Player coordinates: " + x + "," + y);

    }


    /**
        Gets the width of this TileMap (number of pixels across).
    */
    public int getWidthPixels() {
    return tilesToPixels(mapWidth);
    }


    /**
        Gets the width of this TileMap (number of tiles across).
    */
    public int getWidth() {
        return mapWidth;
    }


    /**
        Gets the height of this TileMap (number of tiles down).
    */
    public int getHeight() {
        return mapHeight;
    }


    public int getOffsetY() {
    return offsetY;
    }

    /**
        Gets the tile at the specified location. Returns null if
        no tile is at the location or if the location is out of
        bounds.
    */
    public Image getTile(int x, int y) {
        if (x < 0 || x >= mapWidth ||
            y < 0 || y >= mapHeight)
        {
            return null;
        }
        else {
            return tiles[x][y];
        }
    }


    /**
        Sets the tile at the specified location.
    */
    public void setTile(int x, int y, Image tile, char type) {
        tiles[x][y] = tile;
        tileTypes[x][y] = type;
    }


    /**
        Gets an Iterator of all the Sprites in this map,
        excluding the player Sprite.
    */

    public Iterator getSprites() {
        return sprites.iterator();
    }

    /**
        Class method to convert a pixel position to a tile position.
    */

    public static int pixelsToTiles(float pixels) {
        return pixelsToTiles(Math.round(pixels));
    }


    /**
        Class method to convert a pixel position to a tile position.
    */

    public static int pixelsToTiles(int pixels) {
        return (int)Math.floor((float)pixels / TILE_SIZE);
    }


    /**
        Class method to convert a tile position to a pixel position.
    */

    public static int tilesToPixels(int numTiles) {
        return numTiles * TILE_SIZE;
    }

    /**
        Draws the specified TileMap.
    */
    public void draw(Graphics2D g2)
    {
        int mapWidthPixels = tilesToPixels(mapWidth);

        // get the scrolling position of the map
        // based on player's position

        int offsetX = screenWidth / 2 -
            Math.round(player.getX()) - TILE_SIZE;
        offsetX = Math.min(offsetX, 0);
        offsetX = Math.max(offsetX, screenWidth - mapWidthPixels);

/*
        // draw black background, if needed
        if (background == null ||
            screenHeight > background.getHeight(null))
        {
            g.setColor(Color.black);
            g.fillRect(0, 0, screenWidth, screenHeight);
        }
*/
    // draw the background first

    bgManager.draw (g2);

        // draw the visible tiles

        int firstTileX = pixelsToTiles(-offsetX);
        int lastTileX = firstTileX + pixelsToTiles(screenWidth) + 1;
        for (int y=0; y<mapHeight; y++) {
            for (int x=firstTileX; x <= lastTileX; x++) {
                Image image = getTile(x, y);
                if (image != null) {
                    g2.drawImage(image,
                        tilesToPixels(x) + offsetX,
                        tilesToPixels(y) + offsetY,
                        null);
                }
            }
        }

        // draw door
        door.draw(g2, offsetX);

        // draw stick
        stick.draw(g2, offsetX);

        // draw player

       // g2.drawImage(player.getImage(),Math.round(player.getX()) + offsetX,Math.round(player.getY()) + offsetY, player.getWidth(), player.getHeight(), null);

       player.draw(g2, offsetX);
        // draw key
        key.draw(g2, offsetX);

        // draw villain
        for(int i=0; i< villains.length; i++){
        villains[i].draw(g2,offsetX);
        }

        for(int i=0; i< coins.length; i++){
         coins[i].draw(g2, offsetX);
        }
     

        

/*
        // draw sprites
        Iterator i = map.getSprites();
        while (i.hasNext()) {
            Sprite sprite = (Sprite)i.next();
            int x = Math.round(sprite.getX()) + offsetX;
            int y = Math.round(sprite.getY()) + offsetY;
            g.drawImage(sprite.getImage(), x, y, null);

            // wake up the creature when it's on screen
            if (sprite instanceof Creature &&
                x >= 0 && x < screenWidth)
            {
                ((Creature)sprite).wakeUp();
            }
        }
*/

    }


    public void moveLeft() {
    int x, y;
    x = player.getX();
    y = player.getY();

    String mess = "Going left. x = " + x + " y = " + y;
    System.out.println(mess);

    player.move(1);

    }


    public void moveRight() {
    int x, y;
    x = player.getX();
    y = player.getY();

    String mess = "Going right. x = " + x + " y = " + y;
    System.out.println(mess);

    player.move(2);

    }

    public void moveNowhere(){

        player.move(0);
    }


    public void jump() {
    int x, y;
    x = player.getX();
    y = player.getY();

    String mess = "Jumping. x = " + x + " y = " + y;
    System.out.println(mess);

    player.move(3);

    }


    public void update() {
        player.update();
        key.update();
        door.update();
        

        for(int i=0; i< villains.length; i++){
           villains[i].update();
        }

        for(int i=0; i< coins.length; i++){
           boolean collectedNow = coins[i].update();

           if (collectedNow){
                panel.collectCoin();
           }
        }
      

        if (!key.isCollected() && player.getBounds().intersects(key.getBoundingRectangle())) {
            key.collect();
            System.out.println("Key collected!");
        }
    }

    public Door getDoor() {
        return door;
    }

    public boolean isWaterTile(int x, int y) {
        if (x < 0 || x >= mapWidth || y < 0 || y >= mapHeight) {
            return false;
        }
    
        char t = tileTypes[x][y];
    
        return (t == 'J' || t == 'K' || t == 'L' || t == 'M');
    }

    public void loseLife(){
        panel.loseLife();
    }

    public void resetPlayer() {

    
        int x = (dimension.width / 2) + TILE_SIZE;
        int y = dimension.height - ((TILE_SIZE * 3) + player.getHeight());
    
        player.setX(x);
        player.setY(y);
    
        System.out.println("Player reset to start");
    }

    public boolean isResetting(){
        return resetting;
    }

    public void handlePlayerDeath() {

        if (resetting) return;
    
        resetting = true;
    
        loseLife();
    
            // If player still has lives → respawn
    if (!panel.isPlayerDead()) {

        panel.respawnDelay(() -> {

            resetPlayer();
            key.reset();
            door.reset();
            stick.reset();

            for (int i = 0; i < coins.length; i++) {
                coins[i].reset();
            }

            for (int i = 0; i < villains.length; i++) {
                villains[i].reset();
            }

            resetting = false;

            System.out.println("Map reset");
        });

    } else {
        // FINAL LIFE LOST → do NOTHING here
        // GamePanel will handle game over rendering
        panel.gameOver(true);

        resetting = false; // allow rendering to continue normally
    }
    }

    public Stick getStick() {
        return this.stick;
    }

}
