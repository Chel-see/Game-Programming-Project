import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Rectangle2D;
import javax.swing.JPanel;

import java.lang.Math;

public class Axe {

    private static final int WIDTH = 77; 
    private static final int HEIGHT = 110;  

   private  int pivotX ;
   private  int pivotY ;
  // private static int radius = 300;

   private int degreeRange;
   private int startDegree;
   private int endDegree;
   private int degreeChange;
   private int degree;

  
    private int x, y;
    private Player player;
    private Image img;
    


  public Axe(int xPos, int yPos, Player player) {
        this.x = xPos;
        this.y = yPos;
        pivotX= xPos;
        pivotY=yPos;
        this.player = player;

        degreeRange = 60;
        startDegree = -30;
        endDegree = 30;
        degreeChange = 2;

      degree = startDegree;
    
        img = ImageManager.loadImage("Additional objects/Axe.png");       
  }

  public void update () {  

    
     
        degree += degreeChange;

    if (degree > endDegree || degree < startDegree) {
        degreeChange *= -1;
    }

  
      
   }



    public void draw (Graphics2D g2, int offsetX) {

     double radians = Math.toRadians(degree);

    int pivotDrawX = pivotX + offsetX;
    
    int drawX = pivotX + offsetX - WIDTH / 2; // center image horizontally
    int drawY = pivotY;

    // rotate around top-center of axe
    g2.rotate(radians, pivotDrawX, pivotY);

    g2.drawImage(img, drawX, drawY, WIDTH, HEIGHT, null);

    // reset rotation
    g2.rotate(-radians, pivotDrawX, pivotY);
    }




  public Rectangle2D.Double getBoundingRectangle() {
        return new Rectangle2D.Double(x, y, WIDTH, HEIGHT);
    }



     public boolean collidesWithPlayer() {
        Rectangle2D.Double myRect = getBoundingRectangle();
        Rectangle2D.Double playerRect = player.getBounds();

        return myRect.intersects(playerRect);
    
      }
}
