
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;

public class Villain {
    private GamePanel panel;

   private int x;
   private int y;

   //private int width = 50;
   //private int height = 37;
   private int width = 50;
   private int height = 32;

   private int dx;        // increment to move along x-axis
   private int dy;        // increment to move along y-axis


   private Player player;
   private SoundManager soundManager;
    private boolean growled=false;
  


   Animation animation;

     private int frameNumber;

     private GridAnimation currentAnim;
     private GridAnimation idle;
     private GridAnimation attack2_left;
     private GridAnimation attack3_left;
     private GridAnimation attack4_left;
     private GridAnimation attack2_right;
     private GridAnimation attack3_right;
     private GridAnimation attack4_right;
     private GridAnimation walkLeft;
     private GridAnimation walkRight;

   
    int leftBoundary;
    int rightBoundary;

    private int direction = 1; 
    private int speed = 3;
    private boolean isAttacking = false;

     

     private int startX;
     private int startY;

     private int attackType;

    public Villain(int xPos, int yPos, Player player, int anum, int leftBound, int rightBound, int speed) {
       
        x = xPos;
        y = yPos;

        startX = xPos;
        startY = yPos;
       
        leftBoundary = x-leftBound; 
        rightBoundary = x+rightBound;

        this.speed = speed;

        this.player = player;
        idle = new GridAnimation("Swamp villains/Centipede-new/Centipede_sneer.png", 1, 6, true);

        attack2_left= new GridAnimation("Swamp villains/Centipede-new/Centipede_attack2_left.png", 1, 6, true);
        attack3_left= new GridAnimation("Swamp villains/Centipede-new/Centipede_attack3_left.png", 1, 6, true);
        attack4_left= new GridAnimation("Swamp villains/Centipede-new/Centipede_attack4_left.png", 1, 4, true);
        attack2_right= new GridAnimation("Swamp villains/Centipede-new/Centipede_attack2_right.png", 1, 6, true);
        attack3_right= new GridAnimation("Swamp villains/Centipede-new/Centipede_attack3_right.png", 1, 6, true);
        attack4_right= new GridAnimation("Swamp villains/Centipede-new/Centipede_attack4_right.png", 1, 4, true);

        walkLeft= new GridAnimation("Swamp villains/Centipede-new/Centipede_walk.png", 1, 4, true);
        walkRight= new GridAnimation("Swamp villains/Centipede-new/Centipede_walkRight.png", 1, 4, true);

        this.attackType=anum;

        currentAnim = walkRight;
        currentAnim.start();

        
        soundManager = SoundManager.getInstance();
        soundManager.setVolume("growl", 0.8f);
    }

    public void update() {

      if (collidesWithPlayer()) {
        isAttacking = true;

        if(!growled){ // so it only starts once 
            soundManager.playSound("growl", false);
            growled=true;
        }

        if (player.getX() < this.x){
            setAnimation("attack_left");
        }
        else{
            setAnimation("attack_right");
        }
        // if(player.getInvincibility()) return;
    
        player.harmfulCollision();

      } else {
         isAttacking = false;
      }
   
      if (!isAttacking) {

        dx = speed * direction;
        x += dx;

        
        if (x <= leftBoundary) {
            direction = 1;
        } else if (x >= rightBoundary) {
            direction = -1;
        }

       
        if (direction == 1) {
            setAnimation("walkRight");
        } else {
            setAnimation("walkLeft");
        }
      }

    currentAnim.update();
    }

    public void setAnimation(String name){
        GridAnimation newAnim=null;
        if(name.equals("idle")){
            newAnim = idle;
        }else if(name.equals("attack_left")){
            if (attackType == 2) {
                newAnim = attack2_left;
            } else if (attackType == 3) {
                newAnim = attack3_left;
            } else if (attackType == 4) {
                newAnim = attack4_left;
            }
        }else if(name.equals("attack_right")){
            if (attackType == 2) {
                newAnim = attack2_right;
            } else if (attackType == 3) {
                newAnim = attack3_right;
            } else if (attackType == 4) {
                newAnim = attack4_right;
            }
        }
        else if(name.equals("walkLeft")){
            
                newAnim = walkLeft;

        }else if(name.equals("walkRight")){
                newAnim = walkRight;
            
        }

        if(currentAnim!= newAnim){
            currentAnim=newAnim;
            currentAnim.start();
        }

    }

    public void draw(Graphics2D g,int offsetX) {
       currentAnim.draw(g,this.x + offsetX,this.y, this.width,this.height);
    }

    public Rectangle2D.Double getBoundingRectangle() {
        //return new Rectangle2D.Double(this.x, this.y,this.width-15, this.height);
        return new Rectangle2D.Double(this.x, this.y,this.width-15, this.height);
    }

  public boolean collidesWithPlayer() {
        Rectangle2D.Double myRect = getBoundingRectangle();
        Rectangle2D.Double playerRect = player.getBounds();

        return myRect.intersects(playerRect);
    }

    public void reset() {
        // reset to original position
        x = startX;
        y = startY;
        growled=false;
    }
   
}
