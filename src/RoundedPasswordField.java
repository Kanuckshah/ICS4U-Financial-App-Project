import javax.swing.*;
import java.awt.*;

/**
 * /**
 * A custom JPasswordField with rounded corners and focus-state styling.
 */
public class RoundedPasswordField extends JPasswordField {
    private int radius;
    private Color borderColor;
    private Color focusBorderColor;
    private boolean isFocused = false;

    /**
     * Creates a rounded password field.
     * 
     * @param columns The number of columns to calculate preferred width.
     * @param radius  The corner radius in pixels.
     */
    public RoundedPasswordField(int columns, int radius) {
        super(columns);
        this.radius = radius;
        this.borderColor = Theme.BACKGROUND_LIGHT;
        this.focusBorderColor = Theme.PRIMARY;

        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                isFocused = true;
                repaint();
            }

            public void focusLost(java.awt.event.FocusEvent evt) {
                isFocused = false;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw rounded background
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);

        g2.dispose();
        super.paintComponent(g);
    }

    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw rounded border
        g2.setColor(isFocused ? focusBorderColor : borderColor);
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, radius, radius);

        g2.dispose();
    }
}
