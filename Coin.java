import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Random;
import javax.swing.JPanel;
import java.awt.Image;
import java.awt.Graphics2D;

public class Coin {
    private JPanel panel;

   private int x;
   private int y;

   private int width=25;
   private int height=25;

   private GridAnimation spin;
   private boolean fading = false;

   private int alpha = 255;
   

   private Player player;

    public Coin(int xPos, int yPos, Player player) {
    x = xPos;
    y = yPos;
    this.player = player;
    spin = new GridAnimation("Animated objects/Rune.png", 1, 4, true);
    spin.start();

    }


    public void update (){
        spin.update();

        if (collidesWithPlayer()) {
            fading = true;
            spin.stop();
        }
        if(fading){
            alpha -= 15;
            if(alpha < 0){
                alpha = 0;
            }       
        }
        if (alpha <= 0){
            fading = false;  
        }

    }



    public void draw(Graphics2D g2,int offsetX) {

      Image img = spin.getAnimationImage();
      BufferedImage original = new BufferedImage(
            img.getWidth(null),
            img.getHeight(null),
            BufferedImage.TYPE_INT_ARGB
        );
    
        Graphics2D g = original.createGraphics();
        g.drawImage(img, 0, 0, null);
        g.dispose();

        int w = original.getWidth();
        int h = original.getHeight();

        int[] pixels = new int[w * h];
        original.getRGB(0, 0, w, h, pixels, 0, w);

         for(int i = 0; i < pixels.length; i++){
    
            int a = (pixels[i] >> 24) & 255;
            int red = (pixels[i] >> 16) & 255;
            int green = (pixels[i] >> 8) & 255;
            int blue = pixels[i] & 255;
    
            if(a != 0){
                pixels[i] = (alpha << 24) | (red << 16) | (green << 8) | blue;
            }
        }
    
        BufferedImage copy = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        copy.setRGB(0, 0, w, h, pixels, 0, w);


       g2.drawImage(copy,this.x + offsetX,this.y, this.width,this.height,null);
    }




    public Rectangle2D.Double getBoundingRectangle() {
        return new Rectangle2D.Double(this.x, this.y,this.width, this.height);
    }

  public boolean collidesWithPlayer() {
        Rectangle2D.Double myRect = getBoundingRectangle();
        Rectangle2D.Double playerRect = player.getBounds();

        return myRect.intersects(playerRect);
    }
    
    public boolean getFading(){return this.fading;}
    public int getAlpha(){return this.alpha;}

}





