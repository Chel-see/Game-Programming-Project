import java.awt.Image;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.LinkedList;
import java.util.Iterator;
import javax.swing.JPanel;


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
    private Blade [] blades;
    private Axe axe;


    BackgroundManager bgManager;

    //private JPanel panel;
    private GamePanel panel;
    private Dimension dimension;

    private char[][] tileTypes;

    private boolean resetting = false;
    private boolean levelCompleted=false;

    private Stick stick;

    private Helicopter helicopter;
    private int cutscenePhase = 0;
    private int cutsceneTimer = 0;

   

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

         
            offsetY = screenHeight - tilesToPixels(mapHeight);
        System.out.println("offsetY: " + offsetY);

        bgManager = new BackgroundManager (panel, 12);

        tiles = new Image[mapWidth][mapHeight];
        tileTypes = new char[mapWidth][mapHeight];


        if (panel.getLevel() == 1) {
        

            NUM_TILES=3;

            this.stick = new Stick(panel, 730, 66, this); // choose position

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


            int playerHeight = player.getHeight();

                int x, y;
                x = (dimension.width / 2) + TILE_SIZE;        // position player in middle of screen

            
                y = dimension.height - ((TILE_SIZE*NUM_TILES) + playerHeight);

            player.setX(x);
            player.setY(y);

        System.out.println("Player coordinates: " + x + "," + y);

        }



        if (panel.getLevel() == 2) {
            NUM_TILES=4;

            this.stick = new Stick(panel, 1410, 230, this); // choose position

            player = new Player (panel, this, bgManager);
            key = new Key (panel, 550, 270 ,  player);
            
            door = new Door (panel, 4170, 14, player, key);

            villains = new Villain [2];
             villains[0] = new Villain(2704, 355, player, 2, 0, 180, 2);
             villains[1] = new Villain(3870, 322, player, 3, 120, 100, 6);
           

            coins = new Coin[5];
            coins[0] = new Coin(84, 188, player);
            coins[1] = new Coin(824, 285, player);
            coins[2] = new Coin(1877, 125, player);
            coins[3] = new Coin(2445, 149, player);
            coins[4] = new Coin(3123, 213, player);

            blades = new Blade[3];
            blades[0] = new Blade(620, 190, 100, player,this);
            blades[1] = new Blade(730, 190, 40, player,this);
            blades[2] = new Blade(230,  80, 40, player,this);
            
            axe=new Axe(3470,96,player);

            panel.resetHearts();
            panel.resetCoinCount();


        int playerHeight = player.getHeight();

        int x, y;
        x = (dimension.width / 2) + TILE_SIZE;        // position player in middle of screen

    
        y = dimension.height - ((TILE_SIZE*NUM_TILES) + playerHeight);

            player.setX(x);
            player.setY(y);

        System.out.println("Player coordinates: " + x + "," + y);

        }


        if (panel.getLevel() == 3){
            NUM_TILES = 3;

            player = new Player (panel, this, bgManager);
            helicopter = new Helicopter(500, 50);  // start off screen (right)

            // position player on ground
            int playerHeight = player.getHeight();
            int x = 100;
            int y = dimension.height - ((TILE_SIZE * NUM_TILES) + playerHeight);

            player.setX(x);
            player.setY(y);

        }

       

        sprites = new LinkedList();

        


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

    public boolean isTile(int col, int row) {
    if (col < 0 || col >= mapWidth || row < 0 || row >= mapHeight) {
        return false;
    }
    return tiles[col][row] != null;
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

    

        int offsetX = screenWidth / 2 -
            Math.round(player.getX()) - TILE_SIZE;
        offsetX = Math.min(offsetX, 0);
        offsetX = Math.max(offsetX, screenWidth - mapWidthPixels);



    bgManager.draw (g2);



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

        if (panel.getLevel() == 3){
            helicopter.draw(g2, offsetX);
            System.out.println("Helicopter X: " + helicopter.getX());

            player.draw(g2, offsetX);
            return;
        }

      
        door.draw(g2, offsetX);
      

      
        if (stick!=null){
            stick.draw(g2, offsetX);
        }

        
       player.draw(g2, offsetX);
      
       key.draw(g2, offsetX);

        for(int i=0; i< villains.length; i++){
        villains[i].draw(g2,offsetX);
        }

        for(int i=0; i< coins.length; i++){
         coins[i].draw(g2, offsetX);
        }

        if(blades != null){
            for(int i=0; i< blades.length; i++){
                blades[i].draw(g2, offsetX);
            }
            
        }

        if(axe != null){
            axe.draw(g2, offsetX);
        }

       

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

        if (panel.getLevel() == 3){
            panel.success(true);
            cutsceneTimer++;

            // Phase 0: helicopter flies in from right
            if (cutscenePhase == 0) {
                helicopter.update(0);

                if (helicopter.getX() < 300) {
                    cutscenePhase = 1;
                }
            }
            // Phase 1: helicopter descends
            else if (cutscenePhase == 1) {
                helicopter.update(1);

                if (helicopter.getY() >= 260) {
                    cutscenePhase = 2;
                }
            }
            // Phase 2: player walks to helicopter
            else if (cutscenePhase == 2){
                player.setAnimation("walkRight");
                player.setX(player.getX() + 5);

                if (player.getX() >= helicopter.getX() - 25) {
                    player.setAnimation("idle");
                    cutscenePhase = 3;
                }
            }
            // Phase 3: end
            else if (cutscenePhase == 3){
                panel.end();
            }

            return;
        }

        player.update();
        key.update();
        door.update();

        if(stick != null && panel.getLevel() == 2){
            stick.update();
        }

        if(blades != null){
            for(int i=0; i< blades.length; i++){
                blades[i].update();
            }
        }

        if(axe != null){
            axe.update();
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

        player.playerLiving();
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

        if (panel.isInvincible()) {
            System.out.println("Player is invincible, no life lost.");
            return;
        }

        if (resetting) return;
    
        resetting = true;
    
        loseLife();
    
        
    if (!panel.isPlayerDead()) {
        

        panel.respawnDelay(() -> {
            player.playerLiving();
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
            if(blades != null){
                for (int i = 0; i < blades.length; i++) {
                    blades[i].reset();
                }
            }
            if(axe != null){
                axe.reset();
            }

            resetting = false;

            System.out.println("Map reset");
        });

    } else {
     
        panel.gameOver(true);

        resetting = false; 
    }
    }

    public Stick getStick() {
        return this.stick;
    }
    public Player getPlayer(){
        return this.player;
    }

}
