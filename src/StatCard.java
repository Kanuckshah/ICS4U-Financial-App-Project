
import javax.swing.*;
import java.awt.*;

/**
 * A dashboard widget that displays a statistic with an icon and optional trend
 * color.
 * Features a custom-painted icon background.
 */
public class StatCard extends RoundedPanel {
    private JLabel titleLabel;
    private JLabel valueLabel;
    private String iconSymbol;

    /**
     * Creates a new statistic card.
     * 
     * @param title       The label of the statistic.
     * @param value       The value to display.
     * @param iconSymbol  The emoji/symbol for the icon.
     * @param accentColor The theme color for the icon and optional highlights.
     */
    public StatCard(String title, String value, String iconSymbol, Color accentColor) {
        super(Theme.RADIUS_MEDIUM, Theme.BACKGROUND_LIGHT);
        this.iconSymbol = iconSymbol;

        setLayout(new BorderLayout(Theme.PADDING_MEDIUM, Theme.PADDING_MEDIUM));
        setBackground(Theme.SURFACE);
        setBorder(BorderFactory.createEmptyBorder(Theme.PADDING_LARGE, Theme.PADDING_LARGE, Theme.PADDING_LARGE,
                Theme.PADDING_LARGE));

        // Icon panel with colored circle
        JPanel iconPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                // Draw colored circle
                g2.setColor(new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 30));
                g2.fillOval(0, 0, 48, 48);

                g2.setColor(accentColor);
                g2.setStroke(new BasicStroke(2));
                g2.drawOval(0, 0, 48, 48);

                // Draw Icon/Text centered
                if (iconSymbol != null && !iconSymbol.isEmpty()) {
                    g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
                    FontMetrics fm = g2.getFontMetrics();
                    int textWidth = fm.stringWidth(iconSymbol);
                    int textHeight = fm.getAscent();
                    int x = (48 - textWidth) / 2;
                    int y = (48 + textHeight) / 2 - 4; // Visual center adjustment
                    g2.drawString(iconSymbol, x, y);
                }
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(48, 48);
            }
        };
        iconPanel.setOpaque(false);

        // Title and value panel
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        titleLabel = new JLabel(title);
        titleLabel.setFont(Theme.FONT_SMALL);
        titleLabel.setForeground(Theme.TEXT_SECONDARY);
        titleLabel.setAlignmentX(LEFT_ALIGNMENT);

        valueLabel = new JLabel(value);
        valueLabel.setFont(Theme.FONT_HEADING);
        valueLabel.setForeground(Theme.TEXT_PRIMARY);
        valueLabel.setAlignmentX(LEFT_ALIGNMENT);

        textPanel.add(titleLabel);
        textPanel.add(Box.createVerticalStrut(4));
        textPanel.add(valueLabel);

        // Layout
        JPanel topPanel = new JPanel(new BorderLayout(Theme.PADDING_MEDIUM, 0));
        topPanel.setOpaque(false);
        topPanel.add(iconPanel, BorderLayout.WEST);
        topPanel.add(textPanel, BorderLayout.CENTER);

        add(topPanel, BorderLayout.CENTER);
    }

    public void setValue(String value) {
        valueLabel.setText(value);
    }

    public void setValueColor(Color color) {
        valueLabel.setForeground(color);
    }

    public String getValue() {
        return valueLabel.getText();
    }

    public JLabel getValueLabel() {
        return valueLabel;
    }
}
