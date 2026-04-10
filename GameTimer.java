import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Font;

public class GameTimer {

    private long startTime;
    private long elapsedTime;
    private boolean running;

    private long bestTime = Long.MAX_VALUE;

    public GameTimer() {
        elapsedTime = 0;
        running = false;
    }

    public void start() {
        if (!running) {
            startTime = System.currentTimeMillis();
            running = true;
        }
    }

    public void pause() {
        if (running) {
            elapsedTime += System.currentTimeMillis() - startTime;
            running = false;
        }
    }

    public void reset() {
        elapsedTime = 0;
        running = false;
    }

    public long getTime() {
        if (running) {
            return elapsedTime + (System.currentTimeMillis() - startTime);
        }
        return elapsedTime;
    }

    public void updateBestTime() {
        long current = getTime();
        if (current < bestTime) {
            bestTime = current;
        }
    }

    private String formatTime(long timeMillis) {
        long seconds = timeMillis / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;

        return String.format("%02d:%02d", minutes, seconds);
    }

    public void draw(Graphics2D g2, int panelWidth, boolean paused) {

        if (paused) {
            long blink = (System.currentTimeMillis() / 500); // Blink every 500ms
            if (blink % 2 == 0) return;
            g2.setColor(Color.YELLOW);
        } else {
            g2.setColor(Color.WHITE);
        }

        g2.setFont(new Font("Arial", Font.BOLD, 16));

        String timeText = "Time: " + formatTime(getTime());

        int x = panelWidth - 100;
        int y = 20;

        g2.drawString(timeText, x, y);
    }
}
