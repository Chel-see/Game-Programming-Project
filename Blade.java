import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Rectangle2D;
import javax.swing.JPanel;
import java.awt.image.BufferedImage;
import java.awt.geom.AffineTransform;
import java.lang.Math;


public class Blade {
    private static final int WIDTH = 40; 
    private static final int HEIGHT = 40;  

    private float angle, angleChange;

    private JPanel panel;
    private int x, y;
    private Player player;
    private Image img;
    


  public Blade(int xPos, int yPos, Player player) {
        this.x = xPos;
        this.y = yPos;
        this.player = player;
        angle = 5;               
        angleChange = 2;
       
        img = ImageManager.loadImage("Additional objects/Saw.png");       
  }

  public Rectangle2D.Double getBoundingRectangle() {
        return new Rectangle2D.Double(x, y, WIDTH-10, HEIGHT-10);
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

            g2.drawImage(dest, x + offsetX, y, WIDTH, HEIGHT, null);

           g2d.dispose();
        }

    public void update() {                // modify angle of rotation
    
        angle = angle + angleChange;

        if (angle >= 360)            // reset to 5 degrees if 360 degrees reached
            angle = 5;

            if(collidesWithPlayer()){
            System.out.println("Player Collided with Blade");

            }

    }

    public void move (){}




}
