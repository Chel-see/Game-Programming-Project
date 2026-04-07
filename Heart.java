import java.awt.Graphics2D;
import java.awt.Image;

public class Heart {

    private Image fullHeart;
    private Image emptyHeart;

    private int lives;
    private int maxLives;

    public Heart(int maxLives) {
        this.maxLives = maxLives;
        this.lives = maxLives;

        fullHeart = ImageManager.loadImage("Heart/heart-full.png");
        emptyHeart = ImageManager.loadImage("Heart/heart-empty.png");
    }

    public void draw(Graphics2D g2) {

        int x = 5;   // starting position
        int y = 5;

        int drawWidth = 30;   // width of each heart
        int drawHeight = 26;  // height of each heart
        int spacing = drawWidth + 1;

        for (int i = 0; i < maxLives; i++) {
            Image heartImg;

            if (i < lives) {
                heartImg = fullHeart;
            } else {
                heartImg = emptyHeart;
            }

            g2.drawImage(heartImg, x + (i * spacing), y, drawWidth, drawHeight, null);
        }
    }

    public void loseLife() {
        if (lives > 0) {
            lives--;
        }
    }

    public int getLives() {
        return lives;
    }

    public boolean isDead() {
        return lives <= 0;
    }
}
