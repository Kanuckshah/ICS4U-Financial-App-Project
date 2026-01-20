import javax.swing.*;
import java.awt.*;

/**
 * A custom JPanel that supports rounded corners and optional border color.
 * Uses anti-aliasing for smooth rendering.
 */
public class RoundedPanel extends JPanel {
    private int radius;
    private Color borderColor;

    /**
     * Creates a rounded panel with the specified radius and transparent background.
     * 
     * @param radius The corner radius in pixels.
     */
    public RoundedPanel(int radius) {
        this(radius, null);
    }

    /**
     * Creates a rounded panel with the specified radius and border color.
     * 
     * @param radius      The corner radius in pixels.
     * @param borderColor The color of the border (null for no border).
     */
    public RoundedPanel(int radius, Color borderColor) {
        this.radius = radius;
        this.borderColor = borderColor;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        // Draw rounded background
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);

        g2.dispose();
        super.paintComponent(g);
    }

    @Override
    protected void paintBorder(Graphics g) {
        if (borderColor != null) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Draw a slightly thicker border for better visibility
            g2.setStroke(new BasicStroke(1.5f));
            g2.setColor(borderColor);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);

            g2.dispose();
        }
    }
}
