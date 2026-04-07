
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;

public class Villain {
    private JPanel panel;

   private int x;
   private int y;

   private int width=70;
   private int height=75;

   private int dx;        // increment to move along x-axis
   private int dy;        // increment to move along y-axis


   private Player player;
   private SoundManager soundManager;
  
   private boolean soundPlayed;

   Animation animation;

   private int frameNumber;

   private GridAnimation currentAnim;
    private GridAnimation idle;
    private GridAnimation attack;
     private GridAnimation attack2;
     private GridAnimation attack3;
     private GridAnimation attack4;


    public Villain(int xPos, int yPos, Player player, int anum) {
        x = xPos;
        y = yPos;
        this.player = player;
        idle = new GridAnimation("Swamp villains/Centipede/Centipede_sneer.png", 1, 6, true);

        attack2= new GridAnimation("Swamp villains/Centipede/Centipede_attack2.png", 1, 6, true);
        attack3= new GridAnimation("Swamp villains/Centipede/Centipede_attack3.png", 1, 6, true);
        attack4= new GridAnimation("Swamp villains/Centipede/Centipede_attack4.png", 1, 4, true);

        if (anum == 2) {
            attack = attack2;
        } else if (anum == 3) {
            attack = attack3;
        } else if (anum == 4) {
            attack = attack4;
        }

        currentAnim = idle;
        currentAnim.start();
    }

    public void update() {

        if(collidesWithPlayer()){
            setAnimation("attack");
           
        }else{
            setAnimation("idle");
        }
        currentAnim.update();
    }

    public void setAnimation(String name){
        GridAnimation newAnim=null;
        if(name.equals("idle")){
            newAnim = idle;
        }else if(name.equals("attack")){
            newAnim = attack;
      
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
        return new Rectangle2D.Double(this.x, this.y,this.width, this.height);
    }

  public boolean collidesWithPlayer() {
        Rectangle2D.Double myRect = getBoundingRectangle();
        Rectangle2D.Double playerRect = player.getBounds();

        return myRect.intersects(playerRect);
    }

   
}
