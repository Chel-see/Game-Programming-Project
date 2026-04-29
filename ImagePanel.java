import javax.swing.JPanel;
import java.awt.Image;
import javax.swing.ImageIcon;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.Color;

class ImagePanel extends JPanel {
    private Image image;
    private boolean showImage = false;

    public ImagePanel(String path) {
        image = new ImageIcon(path).getImage();
        setPreferredSize(new Dimension(600, 100)); // adjust as needed

        if (image == null) {
            System.out.println("Image failed to load!");
        }
        setBackground(Color.GRAY);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    
        if (showImage && image != null) {
            int imgW = image.getWidth(this);
            int imgH = image.getHeight(this);
    
            int x = (getWidth() - imgW) / 2;
            int y = (getHeight() - imgH) / 2;
    
            g.drawImage(image, x, y, this);
        }
    }

    public void setShowImage(boolean value) {
        showImage = value;
        repaint();
    }
}