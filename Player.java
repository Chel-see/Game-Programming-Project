//import java.awt.Dimension;
import java.awt.Graphics2D;

import java.awt.geom.Rectangle2D;
import javax.swing.JPanel;
// import javax.swing.JPanel;
// import java.awt.Image;
// import javax.swing.ImageIcon;
import java.awt.Point;

public class Player {            

   private static final int DX = 8;    // amount of X pixels to move in one keystroke
   private static final int DY = 32;    // amount of Y pixels to move in one keystroke

   private static final int TILE_SIZE = 32;

   private JPanel panel;        // reference to the JFrame on which player is drawn
   private TileMap tileMap;
   private BackgroundManager bgManager;

   private int x;            // x-position of player's sprite
   private int y;            // y-position of player's sprite

   Graphics2D g2;
   //private Dimension dimension;

   //private Image playerImage, playerLeftImage, playerRightImage;

   private boolean jumping;
   private int timeElapsed;
   private int startY;

   private boolean goingUp;
   private boolean goingDown;

   private boolean inAir;
   private int initialVelocity;
   private int startAir;
   private int width;
   private int height;

   private GridAnimation idle;
   private GridAnimation walkRight;
   private GridAnimation walkLeft;
  // private GridAnimation death;
  // private GridAnimation jump;

   private GridAnimation currentAnim;

   public Player (JPanel panel, TileMap t, BackgroundManager b) {
      this.panel = panel;

      tileMap = t;            // tile map on which the player's sprite is displayed
      bgManager = b;            // instance of BackgroundManager

      goingUp = goingDown = false;
      inAir = false;

      this.width=40;
      this.height=45;

      idle = new GridAnimation("Warrior/Idle.png", 1, 6, true);
      walkRight= new GridAnimation("Warrior/WalkRight.png", 1, 8, true);
      walkLeft= new GridAnimation("Warrior/WalkLeft.png", 1, 8, true);
      //death = new GridAnimation("Warrior/Dead.png", 1, 4, false);

     // jump = new GridAnimation("Warrior/Jump.png", 1, 5, true);
      
      currentAnim=idle;
      currentAnim.start();
   
   }


   
    public void setAnimation(String name){
        GridAnimation newAnim=null;
        if(name.equals("idle")){
            newAnim = idle;
   
        }else if(name.equals("walkRight")){
                newAnim = walkRight;
            
        }
        else if(name.equals("walkLeft")){
                newAnim = walkLeft;
            
        }
      //   else if(name.equals("jump")){
      //       newAnim = jump;
      //   }
      //   else if(name.equals("death")){
      //       newAnim = death;
      //   }

        if(currentAnim!= newAnim){
            currentAnim=newAnim;
            currentAnim.start();
        }

    }


   // public Point collidesWithTile(int newX, int newY) {

            // int playerWidth = playerImage.getWidth(null);
            // int offsetY = tileMap.getOffsetY();
      // int xTile = tileMap.pixelsToTiles(newX);
      // int yTile = tileMap.pixelsToTiles(newY - offsetY);
    

      // if (tileMap.getTile(xTile, yTile) != null) {
            // Point tilePos = new Point (xTile, yTile);
          // return tilePos;
      // }
      // else {
        // return null;
      // }
   // }
   public Point collidesWithTile(int newX, int newY) {  //cater for left and right collisions mainly 

     
      
      int offsetY = tileMap.getOffsetY();
      
      int xTile = tileMap.pixelsToTiles(newX);
      // checks the point of both the x and y axis for collisions . 
      int yTileFrom = tileMap.pixelsToTiles(newY - offsetY+2); // added 2 so it wont detect the floor and be stuck in place
      int yTileTo = tileMap.pixelsToTiles(newY - offsetY + height-2);

      for (int yTile = yTileFrom; yTile <= yTileTo; yTile++) {
        if (tileMap.getTile(xTile, yTile) != null) {
            return new Point(xTile, yTile);
        }
    }
        return null;
      
   }


   public Point collidesWithTileDown (int newX, int newY) {

            int offsetY = tileMap.getOffsetY();
            
      int xTile = tileMap.pixelsToTiles(newX);
      int yTileFrom = tileMap.pixelsToTiles(y - offsetY);
      int yTileTo = tileMap.pixelsToTiles(newY - offsetY + height);

      for (int yTile=yTileFrom; yTile<=yTileTo; yTile++) {
        if (tileMap.getTile(xTile, yTile) != null) {
                Point tilePos = new Point (xTile, yTile);
              return tilePos;
          }
        else {
            if (tileMap.getTile(xTile+1, yTile) != null) {
                int leftSide = (xTile + 1) * TILE_SIZE;
                if (newX + width > leftSide) {
                    Point tilePos = new Point (xTile+1, yTile);
                    return tilePos;
                    }
            }
        }
      }

      return null;
   }


   public Point collidesWithTileUp (int newX, int newY) {

     

            int offsetY = tileMap.getOffsetY();
      int xTile = tileMap.pixelsToTiles(newX);

      int yTileFrom = tileMap.pixelsToTiles(y - offsetY);
      int yTileTo = tileMap.pixelsToTiles(newY - offsetY);
     
      for (int yTile=yTileFrom; yTile>=yTileTo; yTile--) {
        if (tileMap.getTile(xTile, yTile) != null) {
                Point tilePos = new Point (xTile, yTile);
              return tilePos;
        }
        else {
            if (tileMap.getTile(xTile+1, yTile) != null) {
                int leftSide = (xTile + 1) * TILE_SIZE;
                if (newX + width > leftSide) {
                    Point tilePos = new Point (xTile+1, yTile);
                    return tilePos;
                    }
            }
        }
                    
      }

      return null;
   }
 
/*

   public Point collidesWithTile(int newX, int newY) {

     int playerWidth = playerImage.getWidth(null);
     int playerHeight = playerImage.getHeight(null);

           int fromX = Math.min (x, newX);
     int fromY = Math.min (y, newY);
     int toX = Math.max (x, newX);
     int toY = Math.max (y, newY);

     int fromTileX = tileMap.pixelsToTiles (fromX);
     int fromTileY = tileMap.pixelsToTiles (fromY);
     int toTileX = tileMap.pixelsToTiles (toX + playerWidth - 1);
     int toTileY = tileMap.pixelsToTiles (toY + playerHeight - 1);

     for (int x=fromTileX; x<=toTileX; x++) {
        for (int y=fromTileY; y<=toTileY; y++) {
            if (tileMap.getTile(x, y) != null) {
                Point tilePos = new Point (x, y);
                return tilePos;
            }
        }
     }
    
     return null;
   }
*/


   public synchronized void move (int direction) {

      int newX = x;
      Point tilePos = null;

      if (!panel.isVisible ()) return;
      
      if (direction == 1) {        // move left
      setAnimation("walkLeft");
            newX = x - DX;
         if (newX < 0) {
         x = 0;
         return;
         }
         
         tilePos = collidesWithTile(newX, y);
        

         if (tilePos == null && tileMap.getDoor().isSolid()) {
            if (tileMap.getDoor().collidesWithPlayer()) {

               // block movement on right side of door
              // x = tileMap.getDoor().getX() + tileMap.getDoor().getWidth();
                newX=newX-0;
               return;
            }
         }
      } 
      else                
      if (direction == 2) {        // move right
         setAnimation("walkRight");
         
            newX = x + DX;

               int tileMapWidth = tileMap.getWidthPixels();

         if (newX + width >= tileMapWidth) {
            x = tileMapWidth - width;
            return;
         }

         tilePos = collidesWithTile(newX+width, y);
         if (tilePos == null && tileMap.getDoor().isSolid()) {
            if (tileMap.getDoor().collidesWithPlayer()) {
      
               // block movement on left side of door
               //x = tileMap.getDoor().getX() - playerWidth;
               // x = tileMap.getDoor().getX() - playerWidth;
              newX=newX+0;
               return;
            }
         }           
      }
      else                // jump
      if (direction == 3 && !jumping) {     
          jump();
      return;
      }
      else if (direction == 0) {
          setAnimation("idle");
      }
    
      if (tilePos != null) {  
         if (direction == 1) {
            System.out.println (": Collision going left");
            x = ((int) tilePos.getX() + 1) * TILE_SIZE;       // keep flush with right side of tile
         }
         else
         if (direction == 2) {
            System.out.println (": Collision going right");
            
            x = ( ((int) tilePos.getX() ) * TILE_SIZE- width); // keep flush with left side of tile eg 15x32=448, 448-32=416 but the player can move an extra amount 
         }
     }
      else {
          if (direction == 1) {
          x = newX;
          bgManager.moveLeft();
          }
      else
      if (direction == 2) {
          x = newX;
          bgManager.moveRight();
         }

          if (isInAir()) {
          System.out.println("In the air. Starting to fall.");
          if (direction == 1) {                // make adjustment for falling on left side of tile
                
          x = x - width + DX;
          }
          fall();
          }
      }
   }


   public boolean isInAir() {  // no longer uses collideswithTile, checks feet directly

     

      Point tilePos;

      if (!jumping && !inAir) {   
        
          int offsetY = tileMap.getOffsetY();
          int leftTile  = tileMap.pixelsToTiles(x);
         int rightTile = tileMap.pixelsToTiles(x + width - 1);

        int yTile = tileMap.pixelsToTiles(y - offsetY + height + 1);

        // ONLY check directly below both feet
        if (tileMap.getTile(leftTile, yTile) == null &&
            tileMap.getTile(rightTile, yTile) == null) {
            return true;
        } 
    }
        

      return false;
   }


   private void fall() {

      jumping = false;
      inAir = true;
      timeElapsed = 0;

      goingUp = false;
      goingDown = true;

      startY = y;
      initialVelocity = 0;
   }


   public void jump () {  
      //setAnimation("jump");

      if (!panel.isVisible ()) return;

      if (inAir || jumping) return;

      jumping = true;
      timeElapsed = 0;

      goingUp = true;
      goingDown = false;

      startY = y;
      initialVelocity = 70;
   }


   public void update () {
      currentAnim.update();

      int distance = 0;
      int newY = 0;

      timeElapsed++;

      if (jumping || inAir) {
       distance = (int) (initialVelocity * timeElapsed - 
                             4.9 * timeElapsed * timeElapsed);
       newY = startY - distance;

       if (newY > y && goingUp) {
        goingUp = false;
           goingDown = true;
       }

       if (goingUp) {
        Point tilePos = collidesWithTileUp (x, newY);    
           if (tilePos != null) {                // hits a tile going up
               System.out.println ("Jumping: Collision Going Up!");

                    int offsetY = tileMap.getOffsetY();
            int topTileY = ((int) tilePos.getY()) * TILE_SIZE + offsetY;
            int bottomTileY = topTileY + TILE_SIZE;

               y = bottomTileY;
               fall();
        }
           else {
            y = newY;
            System.out.println ("Jumping: No collision.");
           }
            }
        else
        if (goingDown) {            
        Point tilePos = collidesWithTileDown (x, newY);    
           if (tilePos != null) {                // hits a tile going up
            int offsetY = tileMap.getOffsetY();
            int tileTopY = ((int) tilePos.getY()) * TILE_SIZE + offsetY;

            // snap player to top of tile (same as normal landing)
            y = tileTopY - height;

            // THEN check water
            if (checkWaterCollision(tilePos)) {
               return;
            }

            System.out.println ("Jumping: Collision Going Down!");
             
            goingDown = false;

                      //int offsetY = tileMap.getOffsetY();
            int topTileY = ((int) tilePos.getY()) * TILE_SIZE + offsetY;

                y = topTileY - height;
              jumping = false;
            inAir = false;
           }
           else {
            y = newY;
            System.out.println ("Jumping: No collision.");
           }
       }
      }
   }


   public void moveUp () {

      if (!panel.isVisible ()) return;

      y = y - DY;
   }


   public int getX() {
      return x;
   }


   public void setX(int x) {
      this.x = x;
   }


   public int getY() {
      return y;
   }
   public int getHeight() {
      return this.height;
   }
   public int getWidth() {
      return this.width;
   }



   public void setY(int y) {
      this.y = y;
   }


   // public Image getImage() {
   //    return playerImage;
   // }

   public void draw(Graphics2D g2, int offsetX) {
         currentAnim.draw(g2,x+offsetX,y,width,height);
   }

   public Rectangle2D.Double getBounds() {
      return new Rectangle2D.Double(x, y, width,height);
  }

  private boolean checkWaterCollision(Point tilePos) {

      if (tilePos == null || tileMap.isResetting()) return false;

      if (tileMap.isWaterTile(
            (int) tilePos.getX(),
            (int) tilePos.getY())) {

         System.out.println("Player stepped on water!");

         tileMap.handlePlayerDeath();

         return true;
      }

      return false;
   }

   public void villainCollision() {
      tileMap.handlePlayerDeath();
   }
}