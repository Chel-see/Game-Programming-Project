import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Rectangle2D;
import javax.swing.JPanel;
import java.awt.Shape;
import java.awt.geom.AffineTransform;

import java.lang.Math;

public class Axe {

    private static final int WIDTH = 67; 
    private static final int HEIGHT = 95;  

   private  int pivotX ;
   private  int pivotY ;
  // private static int radius = 300;

   private int degreeRange;
   private int startDegree;
   private int endDegree;
   private int degreeChange;
   private int degree;

  
    private int x, y, startX, startY, drawX,drawY;
    private Player player;
    private Image img;
    private SoundManager soundManager;
    private boolean slashSound=false;
    


  public Axe(int xPos, int yPos, Player player) {
        this.x = xPos;
        this.y = yPos;

        this.startX = xPos;
        this.startY = yPos;

        pivotX= xPos;
        pivotY=yPos;
        this.player = player;

        degreeRange = 60;
        startDegree = -60;
        endDegree = 60;
        degreeChange = 4;

      degree = startDegree;
    
        img = ImageManager.loadImage("Additional objects/Axe.png");    
        soundManager = SoundManager.getInstance();
       soundManager.setVolume("slice", 0.85f);   
  }

  public void update () {  

    if(collidesWithPlayer()){
      if(!slashSound){
        soundManager.playSound("slice",false);
        slashSound=true;

      }
        player.harmfulCollision();
    } 
        degree += degreeChange;

    if (degree > endDegree || degree < startDegree) {
        degreeChange *= -1;
    }

    
   }



    public void draw (Graphics2D g2, int offsetX) {

     double radians = Math.toRadians(degree);

    int pivotDrawX = pivotX + offsetX;
    
     drawX = pivotX + offsetX - WIDTH / 2; // center image horizontally
     drawY = pivotY;

    // rotate around top-center of axe
    g2.rotate(radians, pivotDrawX, pivotY);

    g2.drawImage(img, drawX, drawY, WIDTH, HEIGHT, null);

  

    // reset rotation
    g2.rotate(-radians, pivotDrawX, pivotY);
    }




public boolean collidesWithPlayer() {
    Rectangle2D playerRect = player.getBounds();

    Rectangle2D rect = new Rectangle2D.Double(
        pivotX - WIDTH / 2,
        pivotY,
        WIDTH-15,
        HEIGHT-10
    );

    AffineTransform at = new AffineTransform();
    at.rotate(Math.toRadians(degree), pivotX, pivotY);

    Shape rotatedShape = at.createTransformedShape(rect);

    return rotatedShape.intersects(playerRect);
}


     


      public void reset(){
          x=startX;
          y=startY;
          degree = startDegree;
          slashSound=false;
      }
}
