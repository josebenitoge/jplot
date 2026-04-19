package ui;



import model.Config;

import javax.swing.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class ChartFrame extends JFrame {
    private Timer resizeTimer;
    private LoadingPanel loading;
    private ChartPanel plotter;

    public ChartFrame(Config info) {
        loading = new LoadingPanel();
        plotter = new ChartPanel(loading, info);
        setContentPane(plotter);
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                handleResizeEvent();
            }
        });
        plotter.fillMatrix(getWidth(), getHeight());
    }

    private void handleResizeEvent() {
        if (resizeTimer != null && resizeTimer.isRunning()) {
            resizeTimer.stop();
        }
        // Show loading state immediately
        plotter.setLoading(true);
        plotter.repaint();

        // Delay the matrix recreation until dragging stops
        resizeTimer = new Timer(300, e -> {
            plotter.setLoading(false);
            plotter.repaint();
        });
        resizeTimer.setRepeats(false);
        resizeTimer.start();
    }
}
