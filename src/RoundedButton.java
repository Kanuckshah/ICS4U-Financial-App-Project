import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * /**
 * A custom JButton featuring rounded corners, hover effects, and modern
 * styling.
 */
public class RoundedButton extends JButton {
    private Color backgroundColor;
    private Color hoverColor;
    private int radius;
    private boolean isHovered = false;

    /**
     * Creates a new rounded button.
     * 
     * @param text       The button text.
     * @param bgColor    The normal background color.
     * @param hoverColor The background color when hovered.
     * @param radius     The corner radius in pixels.
     */
    public RoundedButton(String text, Color bgColor, Color hoverColor, int radius) {
        super(text);
        this.backgroundColor = bgColor;
        this.hoverColor = hoverColor;
        this.radius = radius;

        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                isHovered = true;
                repaint();
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                isHovered = false;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw rounded background
        g2.setColor(isHovered ? hoverColor : backgroundColor);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);

        g2.dispose();
        super.paintComponent(g);
    }

    @Override
    protected void paintBorder(Graphics g) {
        // No border painting needed
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension size = super.getPreferredSize();
        size.width += 56; // Horizontal padding
        size.height += 28; // Vertical padding
        return size;
    }
}
