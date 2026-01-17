import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

/**
 * Modern ULTRA-PREMIUM Dark Financial Theme
 * Uses deep navy/black backgrounds with vibrant emerald accents.
 */
public class Theme {
    // === COLOR PALETTE: "Midnight Ledger" ===

    // Primary Accents (Emerald Green - Vibrant & Expensive)
    public static final Color PRIMARY = new Color(16, 185, 129); // Emerald-500
    public static final Color PRIMARY_DARK = new Color(5, 150, 105); // Emerald-600
    public static final Color PRIMARY_GLOW = new Color(16, 185, 129, 60); // Glow effect

    // Backgrounds (Deep Rich Navy/Black)
    public static final Color BACKGROUND = new Color(10, 14, 23); // Almost black navy
    public static final Color BACKGROUND_LIGHT = new Color(17, 24, 39); // Gray-900 equivalent
    public static final Color SURFACE = new Color(31, 41, 55); // Gray-800 equivalent (Cards)
    public static final Color SURFACE_HIGHLIGHT = new Color(55, 65, 81);// Gray-700 (Hover/Input)

    // Text (High Contrast)
    public static final Color TEXT_PRIMARY = new Color(249, 250, 251); // Gray-50
    public static final Color TEXT_SECONDARY = new Color(156, 163, 175); // Gray-400
    public static final Color TEXT_DARK = new Color(17, 24, 39); // For light buttons

    // Semantic Colors
    public static final Color SUCCESS = new Color(34, 197, 94); // Green
    public static final Color WARNING = new Color(245, 158, 11); // Amber
    public static final Color DANGER = new Color(239, 68, 68); // Red
    public static final Color INFO = new Color(59, 130, 246); // Blue

    // Shadows
    public static final Color SHADOW = new Color(0, 0, 0, 120);

    // Fonts (Clean, Modern, Expensive)
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 32);
    public static final Font FONT_HEADING = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font FONT_SUBHEADING = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 14);

    // Geometry
    public static final int PADDING_SMALL = 10;
    public static final int PADDING_MEDIUM = 20;
    public static final int PADDING_LARGE = 30;
    public static final int PADDING_XL = 32; // Restored for compatibility
    public static final int PADDING_XXL = 48; // Restored for compatibility

    public static final int RADIUS_SMALL = 8;
    public static final int RADIUS_MEDIUM = 20; // Smooth curves
    public static final int RADIUS_LARGE = 30;

    /**
     * Create a modern rounded button
     */
    public static JButton createButton(String text, boolean isPrimary) {
        Color bg = isPrimary ? PRIMARY : SURFACE_HIGHLIGHT;
        Color hover = isPrimary ? PRIMARY_DARK : new Color(75, 85, 99);
        Color fg = isPrimary ? Color.WHITE : TEXT_PRIMARY;

        RoundedButton button = new RoundedButton(text, bg, hover, RADIUS_SMALL);
        button.setFont(FONT_BUTTON);
        button.setForeground(fg);
        return button;
    }

    /**
     * Style a text field
     */
    public static void styleTextField(JTextField field) {
        field.setFont(FONT_BODY);
        field.setForeground(TEXT_PRIMARY);
        field.setBackground(SURFACE_HIGHLIGHT);
        field.setCaretColor(PRIMARY);
        field.setOpaque(true);
        field.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        // Custom painting for border could be added here or via wrapper
    }
}
