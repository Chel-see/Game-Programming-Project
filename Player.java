
import java.awt.Graphics2D;

import java.awt.geom.Rectangle2D;
import javax.swing.JPanel;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.Image;

public class Player {            

   private static final int DX = 8;    // amount of X pixels to move in one keystroke
   private static final int DY = 32;    // amount of Y pixels to move in one keystroke

   private static final int TILE_SIZE = 32;

   //private JPanel panel;        // reference to the JFrame on which player is drawn
   private GamePanel panel;        // reference to the GamePanel on which player is drawn
   private TileMap tileMap;
   private BackgroundManager bgManager;

   private int x;            // x-position of player's sprite
   private int y;            // y-position of player's sprite

   Graphics2D g2;

   private boolean jumping;
   private int timeElapsed;
   private int startY;

   private boolean goingUp;
   private boolean goingDown;
   private boolean playerIsDead=false;

   private boolean landedOnStick=false;

   private boolean inAir;
   private boolean splashPlayed=false;
   private boolean deathAnimFinished=false;
 

   private int initialVelocity;
   private int startAir;
   private int width;
   private int height;

   private GridAnimation idle;
   private GridAnimation walkRight;
   private GridAnimation walkLeft;
   private GridAnimation death;
  // private GridAnimation jump;

   private GridAnimation currentAnim;

   private Stick stick;
   private SoundManager soundManager;

   public Player (GamePanel panel, TileMap t, BackgroundManager b) {
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
      death = new GridAnimation("Warrior/Dead.png", 1, 4, false);

     // jump = new GridAnimation("Warrior/Jump.png", 1, 5, true);
      
      currentAnim=idle;
      currentAnim.start();

      stick = tileMap.getStick();
      soundManager = SoundManager.getInstance();
      soundManager.setVolume("splash", 0.85f);
   
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
        else if(name.equals("death")){
            newAnim = death;
        }

        if(currentAnim!= newAnim){
            currentAnim=newAnim;
            currentAnim.start();
        }

    }

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

         int playerWidth = getWidth();
         int playerHeight = getHeight();
         int offsetY = tileMap.getOffsetY();
         
         int xTile = tileMap.pixelsToTiles(newX);
         int yTileFrom = tileMap.pixelsToTiles(y - offsetY);
         int yTileTo = tileMap.pixelsToTiles(newY - offsetY + playerHeight);
     
         // First, check if landing on a crate - if yes, return null (no tile collision)
         Rectangle2D.Double futureBounds = new Rectangle2D.Double(newX, newY, playerWidth, playerHeight);

         // ADD NULL CHECK HERE
         if (stick != null && futureBounds.intersects(stick.getBounds())) {
            // Check if landing ON TOP of stick
            if (y + getHeight() <= stick.getY() + 5) {
               return null; // Return null to prevent tile collision handling
            }
         }
     
         // Only check tile collisions if not landing on crate
         for (int yTile = yTileFrom; yTile <= yTileTo; yTile++) {
             if (tileMap.getTile(xTile, yTile) != null) {
                 Point tilePos = new Point(xTile, yTile);
                 return tilePos;
             }
             else {
                 if (tileMap.getTile(xTile+1, yTile) != null) {
                     int leftSide = (xTile + 1) * TILE_SIZE;
                     if (newX + playerWidth > leftSide) {
                         Point tilePos = new Point(xTile+1, yTile);
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
 



   public synchronized void move (int direction) {
      if (playerIsDead) return;  // stops all movement 

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

         if (stick.getBounds().intersects(new Rectangle2D.Double(newX, y, width, height))) {
            boolean moved = stick.move(-DX);
            if (!moved) {
                return; // can't push → stop player
            }
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

         // ADD THIS STICK PUSHING CODE FOR RIGHT MOVEMENT
         if (stick != null && stick.getBounds().intersects(new Rectangle2D.Double(newX, y, width, height))) {
            boolean moved = stick.move(DX);  // Positive DX for right push
            if (!moved) {
               return; // can't push → stop player
            }
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
         if(!playerIsDead){
           setAnimation("idle");
         }

          
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
          if (direction == 1 ) {
          x = newX;
          bgManager.moveLeft();
          }
      else
      if (direction == 2 ) {
          x = newX;
          bgManager.moveRight();
         }

          if (isInAir()) {
          System.out.println("In the air. Starting to fall.");

       
          fall();
          }
      }
   }




      public boolean isInAir() {
         if (!jumping && !inAir) {   
             int offsetY = tileMap.getOffsetY();
             int leftTile = tileMap.pixelsToTiles(x);
             int rightTile = tileMap.pixelsToTiles(x + width - 1);
             int yTile = tileMap.pixelsToTiles(y - offsetY + height + 1);
             
             // Check tiles below
             if (tileMap.getTile(leftTile, yTile) == null &&
                 tileMap.getTile(rightTile, yTile) == null) {
                 
                 // Also check if standing on stick
                 Rectangle2D.Double feetBounds = new Rectangle2D.Double(x, y + height, width, 2);
                 if (stick != null && feetBounds.intersects(stick.getBounds())) {
                     return false; // Standing on stick
                 }
                 return true; // In air
             }
         }
         return false;
     }




private void checkStandingOnWater() {

    if (panel.isInvincible() || playerIsDead) return;

    int offsetY = tileMap.getOffsetY();

    int leftTile = tileMap.pixelsToTiles(x);
    int rightTile = tileMap.pixelsToTiles(x + width - 1);
    int yTile = tileMap.pixelsToTiles(y - offsetY + height);

    boolean onWater =
        tileMap.isWaterTile(leftTile, yTile) ||
        tileMap.isWaterTile(rightTile, yTile);

    if (onWater) {

        if (!splashPlayed) {
            soundManager.playSound("splash", false);
            splashPlayed = true;
        }

        harmfulCollision();

    } else {
        splashPlayed = false;
    }
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
      //initialVelocity = 70;
      initialVelocity = 55;
   }


   public void update () {
      currentAnim.update();


      if (playerIsDead) {

        // wait until death animation finishes
        if (!currentAnim.isStillActive() && !deathAnimFinished) {
            tileMap.handlePlayerDeath();
            deathAnimFinished = true;
        }

        return; 
      }
      checkStandingOnWater();

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
            if (newY < 0) {
                y = 0;
                fall();
                return;
            }

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
            Point tilePos = collidesWithTileDown(x, newY);
            
            if (tilePos != null) {
                // Landed on tile
                int offsetY = tileMap.getOffsetY();
                int tileTopY = ((int) tilePos.getY()) * TILE_SIZE + offsetY;
                y = tileTopY - getHeight();
                
                if (checkWaterCollision(tilePos)) {
                  if(!panel.isInvincible()){
                     if(!splashPlayed ){
                        soundManager.playSound("splash", false);
                        splashPlayed=true;
                     }
                     harmfulCollision();
                  }  
                }
                splashPlayed=false;
                System.out.println("Jumping: Collision Going Down!");
                goingDown = false;
                jumping = false;
                inAir = false;
            }
            else {
                // Check for crate landing (since collidesWithTileDown returned null for crates)
                landedOnStick = false;
                
               Rectangle2D.Double futureBounds = new Rectangle2D.Double(x, newY, getWidth(), getHeight());
                        
               if (futureBounds.intersects(stick.getBounds())) {
                  if (y + getHeight() <= stick.getY() + 10) {
                     y = stick.getY() - getHeight();
                     landedOnStick = true;
                     goingDown = false;
                     jumping = false;
                     inAir = false;
                     System.out.println("Landed on stick");
                     //return;
                  }
                }
                
                if (!landedOnStick) {
                    y = newY;
                    System.out.println("Jumping: No collision.");
                }
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
      if (playerIsDead && !currentAnim.isStillActive()) {
        g2.drawImage(currentAnim.getAnimationImage(), x+offsetX, y, width, height, null);
        return;
      }
     
         //currentAnim.draw(g2,x+offsetX,y,width,height);

         BufferedImage frame = (BufferedImage) currentAnim.getAnimationImage();

         if (panel.isInvincible()){
            //invincible=true;
            BufferedImage faded = applyFade(frame, 100); // increase alpha value for less transparency
            g2.drawImage(faded, x + offsetX, y, width, height, null);
         }
         else {
            //invincible=false;
            g2.drawImage(frame, x + offsetX, y, width, height, null);
         }
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


         return true;
      }

      return false;
   }

   public void harmfulCollision() {
      if(panel.isInvincible()) return;

      if (playerIsDead) return;   // so it does repeatedly trigger death if already dead

      playerIsDead = true;
      deathAnimFinished = false;

      setAnimation("death");     
      
   }

   public void playerDied() {
      playerIsDead = true;
   }

   public void playerLiving(){
      playerIsDead=false;
      deathAnimFinished = false;
      splashPlayed = false;

    setAnimation("idle"); // reset animation
   }
   

   private BufferedImage applyFade(BufferedImage img, int alpha) {

      int w = img.getWidth(null);
      int h = img.getHeight(null);
  
      BufferedImage buffered = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
      Graphics2D g = buffered.createGraphics();
      g.drawImage(img, 0, 0, null);
      g.dispose();
  
      int[] pixels = new int[w * h];
      buffered.getRGB(0, 0, w, h, pixels, 0, w);
  
      for (int i = 0; i < pixels.length; i++) {
  
         int a = (pixels[i] >> 24) & 255;

         if (a != 0) {
            int red = (pixels[i] >> 16) & 255;
            int green = (pixels[i] >> 8) & 255;
            int blue = pixels[i] & 255;

            pixels[i] = (alpha << 24) | (red << 16) | (green << 8) | blue;
         }
      }
  
      BufferedImage faded = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
      faded.setRGB(0, 0, w, h, pixels, 0, w);
  
      return faded;
  }


  public boolean landedOnStick() {
      return this.landedOnStick;
  }


  public void moveWithStick(int dx) {
   if (!landedOnStick) return;
      int newX = x + dx;

    Point tilePos;

    if (dx > 0) {
        tilePos = collidesWithTile(newX + width, y); // right side
        if (tilePos != null) {
            // snap to left side of tile
            x = ((int) tilePos.getX()) * TILE_SIZE - width;
            return;
        }
    } else {
        tilePos = collidesWithTile(newX, y); // left side
        if (tilePos != null) {
            // snap to right side of tile
            x = ((int) tilePos.getX() + 1) * TILE_SIZE;
            return;
        }
    }

    // no collision → move freely
    x = newX;
   }



public boolean isOffMap(){
  
    int mapHeightPixels = tileMap.getHeight() * TILE_SIZE;
    return this.y  >= mapHeightPixels;

 
}




}