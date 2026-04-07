import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.File;
import javax.imageio.ImageIO;
import java.io.IOException;


public class GridAnimation {
      Animation animation;

    private int x;        // x position of animation
    private int y;        // y position of animation

    private int width;
    private int height;

    private int dx;        // increment to move along x-axis
    private int dy;        // increment to move along y-axis

    public GridAnimation(String file , int row , int col , boolean loop) {

        animation = new Animation(loop);    // run animation once

            dx = 0;        // increment to move along x-axis
            dy = 0;    // increment to move along y-axis

        // load images from strip file

        Image stripImage = ImageManager.loadImage(file);

        int imageWidth = (int) stripImage.getWidth(null) / col;
        int imageHeight = stripImage.getHeight(null) / row;

        for (int i=0; i<row; i++) {
            for (int j=0; j<col; j++) {

                BufferedImage frameImage = new BufferedImage (imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = (Graphics2D) frameImage.getGraphics();
     
                g.drawImage(stripImage, 
                    0, 0, imageWidth, imageHeight,
                    j*imageWidth, (i*imageHeight), (j*imageWidth)+imageWidth, (i+1)*imageHeight,
                    null);

                animation.addFrame(frameImage, 100);        
            }
        }
    
    }


    public void start() {
      
        animation.start();
    }

     public void stop() {
      
        animation.stop();
    }

    
    public void update() {
        if (!animation.isStillActive())
            return;

        animation.update();
     
    }


    public void draw(Graphics2D g2, int x , int y , int w , int h) {
        if (!animation.isStillActive())
            return;

        g2.drawImage(animation.getImage(), x, y,w,h, null);
    }
      public synchronized Image getAnimationImage(){
        return animation.getImage();
    }
    
}
