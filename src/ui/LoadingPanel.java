package ui;

import javax.swing.*;
import java.awt.*;

/**
 * A minimalist loading screen that draws three animated dots
 * in the center of the component.
 */
public class LoadingPanel extends JPanel {

    private int dotCount = 0;
    private boolean isRunning = true;

    public LoadingPanel() {
        // Makes the panel transparent so it doesn't have a solid background
        setOpaque(false);

        // Animation thread to cycle the number of dots
        Runnable r = () -> {
            try {
                while (isRunning) {
                    // Cycle dotCount: 0 -> 1 -> 2 -> 3 -> 0...
                    dotCount = (dotCount + 1) % 4;

                    // Request a redraw of the component
                    repaint();

                    Thread.sleep(500);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };
        new Thread(r).start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Use Graphics2D for smoother circles (anti-aliasing)
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        // Dot settings
        int dotSize = 10;
        int spacing = 15;
        g2d.setColor(Color.WHITE);

        // Calculate the starting X position to keep the 3 dots centered
        // Total width = (3 dots * size) + (2 spaces * spacing)
        int totalWidth = (3 * dotSize) + (2 * spacing);
        int startX = (width - totalWidth) / 2;
        int startY = (height - dotSize) / 2;

        // Draw the dots based on the current animation state
        for (int i = 0; i < dotCount; i++) {
            int x = startX + (i * (dotSize + spacing));
            g2d.fillOval(x, startY, dotSize, dotSize);
        }
    }

    /**
     * Stops the animation thread.
     */
    public void stop() {
        this.isRunning = false;
    }
}