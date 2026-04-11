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
    private  int NUM_TILES ;

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
    private Blade blade;


    BackgroundManager bgManager;

    //private JPanel panel;
    private GamePanel panel;
    private Dimension dimension;

    private char[][] tileTypes;

    private boolean resetting = false;
    private boolean levelCompleted=false;

    /**
        Creates a new TileMap with the specified width and
        height (in number of tiles) of the map.
    */
    public TileMap(GamePanel panel, int width, int height) {
        levelCompleted = false;

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


        if (panel.getLevel() == 1) {
        

            player = new Player (panel, this, bgManager);
            NUM_TILES=3;

            key = new Key (panel, 200, 200, player);

            door = new Door (panel, 1000, 275, player, key);

            villains = new Villain [2];
            villains[0] = new Villain(800, 280, player, 2);
        // villains[1] = new Villain(1050, 280, player, 3);
            villains[1] = new Villain(1200, 280, player, 4);

            coins = new Coin[6];
            coins[0] = new Coin(100, 294, player);
            coins[1] = new Coin(120, 294, player);
            coins[2] = new Coin(140, 294, player);
            coins[3] = new Coin(400, 70, player);
            coins[4] = new Coin(420, 70, player);
            coins[5] = new Coin(440, 70, player);

            
        int playerHeight = player.getHeight();

        int x, y;
        x = (dimension.width / 2) + TILE_SIZE;        // position player in middle of screen

    
        y = dimension.height - ((TILE_SIZE*NUM_TILES) + playerHeight);

            player.setX(x);
            player.setY(y);

        System.out.println("Player coordinates: " + x + "," + y);

        }



        if (panel.getLevel() == 2) {
            NUM_TILES=5;
            player = new Player (panel, this, bgManager);
            key = new Key (panel, 500, 200, player);
            
            door = new Door (panel, 100, 210, player, key);

            villains = new Villain [2];
            villains[0] = new Villain(1200, 200, player, 2);
       
            villains[1] = new Villain(1200, 200, player, 4);

            coins = new Coin[6];
            coins[0] = new Coin(100, 200, player);
            coins[1] = new Coin(110, 200, player);
            coins[2] = new Coin(140, 200, player);
            coins[3] = new Coin(100, 70, player);
            coins[4] = new Coin(120, 70, player);
            coins[5] = new Coin(140, 70, player);

            blade = new Blade(745, 270, player);




             
        int playerHeight = player.getHeight();

        int x, y;
        x = (dimension.width / 2) + TILE_SIZE;        // position player in middle of screen

    
        y = dimension.height - ((TILE_SIZE*NUM_TILES) + playerHeight);

            player.setX(x);
            player.setY(y);

        System.out.println("Player coordinates: " + x + "," + y);

        }

        //blade = new Blade(250, 294, player);

        sprites = new LinkedList();

        


    }


    // public void createLevelTwoEntities() {
    //  Player player = new Player (panel, this, bgManager);
    //   Key key = new Key (panel, 100, 200, player);    
    // }


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
       // blade.draw(g2, offsetX);

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

        if(blade != null){
            blade.draw(g2, offsetX);
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
        if(blade != null){
            blade.update();
        }
        
        

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
        

        if (!levelCompleted && door.collidesWithPlayer() && key.isCollected()&& door.AnimationFinished()) {
            levelCompleted = true;
            panel.endLevel();
            return;
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
        int y = dimension.height - ((TILE_SIZE * NUM_TILES) + player.getHeight());
    
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

}
