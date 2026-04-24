
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Rectangle2D;
import javax.swing.JPanel;
import java.awt.image.BufferedImage;
import java.awt.geom.AffineTransform;
import java.lang.Math;
import java.time.Year;


public class Blade {
    private static final int WIDTH = 40; 
    private static final int HEIGHT = 40;  

    private float angle, angleChange;

    private JPanel panel;
    private int x, y, originalY, count=0;
    private Player player;
    private Image img;
    private int randomInt;
    private boolean dropping=false;
    private boolean rising =false;
    private int seconds;

    TileMap tileMap;

    private int startX , startY;
     private SoundManager soundManager;
    private boolean slashSound=false;

    


  public Blade(int xPos, int yPos,int seconds, Player player, TileMap tileMap ) {
        this.x = xPos;
        this.y = yPos;

        this.startX=xPos;
        this.startY=yPos;

        this.originalY=yPos;
        this.player = player;

        this.seconds = seconds;

        this.tileMap = tileMap;

        angle = 5;               
        angleChange = 5;
       
        img = ImageManager.loadImage("Additional objects/Saw.png"); 
        soundManager = SoundManager.getInstance();
       soundManager.setVolume("slice", 0.85f);       
  }

  public Rectangle2D.Double getBoundingRectangle() {
        return new Rectangle2D.Double(x+10, y+10, WIDTH-10, HEIGHT-10);
    }

     public boolean collidesWithPlayer() {
        Rectangle2D.Double myRect = getBoundingRectangle();
        Rectangle2D.Double playerRect = player.getBounds();

        return myRect.intersects(playerRect);
    }



    public void draw (Graphics2D g2, int offsetX) {

       
        int width, height;

        width = img.getWidth(null);        // find width of image
        height = img.getHeight(null);    // find height of image

        BufferedImage dest = new BufferedImage (width, height,
                            BufferedImage.TYPE_INT_ARGB);

            Graphics2D g2d = (Graphics2D) dest.getGraphics();

            // rotate the coordinate system of the destination image around its center
    
        AffineTransform rotation = new AffineTransform(); 
            rotation.rotate(Math.toRadians(angle*-1), width/2, height/2); 
            g2d.transform(rotation); 

            g2d.drawImage(img, 0, 0, null);    // copy in the image

            g2.drawImage(dest, x + offsetX, y+ tileMap.getOffsetY(), WIDTH, HEIGHT, null);

           g2d.dispose();
        }

    public void update() {                // modify angle of rotation
    
        angle = angle + angleChange;

        if (angle >= 360)            // reset to 5 degrees if 360 degrees reached
            angle = 5;

         if(collidesWithPlayer()){
            if(!slashSound){
            soundManager.playSound("slice",false);
            slashSound=true;
             }

            player.harmfulCollision();
         }
         drop();
   

    }

    public void drop() {

        count++;

        // WAITING STATE
        if (!dropping && !rising) {
            if (count >= seconds) { // ~3–4 seconds depending on your loop
                randomInt = (int) (Math.random() * 5) + 10;
                dropping = true;
                count = 0;
            }
        }

        // DROPPING STATE
        if (dropping) {

            int leftCol  = tileMap.pixelsToTiles(x);
            int rightCol = tileMap.pixelsToTiles(x + WIDTH - 1);
        //  int bottomRow = tileMap.pixelsToTiles(y + HEIGHT-1);
            int nextY = y + randomInt;

            int nextBottomRow = tileMap.pixelsToTiles(nextY + HEIGHT - 1);
        
        boolean tileBelowLeft  = tileMap.isTile(leftCol, nextBottomRow + 1);
        boolean tileBelowRight = tileMap.isTile(rightCol, nextBottomRow + 1);

            // if NO tile below → keep falling
            if (!tileBelowLeft && !tileBelowRight) {
                y = nextY;
            } 
            else {
                // snap exactly on top of the tile
                y = tileMap.tilesToPixels(nextBottomRow+1) - HEIGHT;

                dropping = false;
                rising = true;
            }
        }

        // RISING STATE
            else if (rising) {
                y -= 3;

                if (y <= originalY) {
                    y = originalY;
                    rising = false;
                    count = 0; // restart wait cycle
                }
            }
    }

     public void reset(){
          x=startX;
          y=startY;
          slashSound=false;
          
      }




}