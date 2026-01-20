
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The main navigation sidebar.
 * Manages menu buttons, selection state, and logout integration.
 * Wraps buttons in custom logic for hover/selected aesthetics tailored to the
 * dark theme.
 */
public class Sidebar extends JPanel {
    private Map<String, JButton> menuButtons;
    private JButton logoutButton;
    private JButton selectedButton;

    // Theme Colors
    private static final Color BG_COLOR = new Color(2, 44, 34); // Very Dark Emerald (Almost Black/Green)
    private static final Color HOVER_COLOR = new Color(6, 78, 59); // Dark Emerald for Hover
    private static final Color SELECTED_COLOR = new Color(5, 150, 105); // Bright Emerald for Selection
    private static final Color TEXT_COLOR = new Color(209, 250, 229); // Mint Cream
    private static final Color DANGER_COLOR = new Color(239, 68, 68); // Red

    public Sidebar() {
        this.menuButtons = new LinkedHashMap<>();
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(260, 0));
        setBackground(BG_COLOR);

        // --- Header Section ---
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(BG_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(30, 20, 30, 20));

        // Icon
        JLabel iconLabel = new JLabel("💎");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Title
        JLabel titleLabel = new JLabel("Finance Tracker");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(iconLabel);
        headerPanel.add(Box.createVerticalStrut(10));
        headerPanel.add(titleLabel);

        add(headerPanel, BorderLayout.NORTH);

        // --- Menu Section ---
        JPanel menuContainer = new JPanel();
        menuContainer.setLayout(new BoxLayout(menuContainer, BoxLayout.Y_AXIS));
        menuContainer.setBackground(BG_COLOR);
        menuContainer.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15)); // consistent padding

        add(menuContainer, BorderLayout.CENTER);
    }

    /**
     * Adds a navigation menu item.
     * 
     * @param text     The button label (e.g., "Dashboard").
     * @param listener The action to perform when clicked.
     */
    public void addMenuItem(String text, ActionListener listener) {
        JButton button = createMenuButton(text, listener);
        menuButtons.put(text, button);

        // Find the center panel to add the button
        JPanel menuContainer = (JPanel) ((BorderLayout) getLayout()).getLayoutComponent(BorderLayout.CENTER);
        if (menuContainer != null) {
            menuContainer.add(button);
            menuContainer.add(Box.createVerticalStrut(8)); // Spacing between items
        }
    }

    /**
     * Adds a logout button to the bottom of the sidebar.
     * 
     * @param listener The action to perform (e.g., show confirmation).
     */
    public void addLogoutButton(ActionListener listener) {
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(BG_COLOR);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 30, 20));

        logoutButton = new RoundedButton("🚪 Logout", new Color(254, 226, 226), new Color(254, 202, 202), 10);
        logoutButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        logoutButton.setForeground(DANGER_COLOR);
        logoutButton.setPreferredSize(new Dimension(0, 45)); // Full width
        logoutButton.addActionListener(listener);

        footerPanel.add(logoutButton, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);
    }

    private JButton createMenuButton(String text, ActionListener listener) {
        String icon = getIconForMenuItem(text);

        // We use a JPanel inside the button or custom painting, but standard JButton
        // with strict layout is robust
        JButton button = new JButton();
        button.setLayout(new BorderLayout());
        button.setBackground(BG_COLOR);
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false); // We will paint background manually or via listener
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        // Custom internal components for perfect alignment
        JPanel internalPanel = new JPanel(new BorderLayout(15, 0)); // 15px gap between icon and text
        internalPanel.setOpaque(false);

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        iconLabel.setForeground(TEXT_COLOR);

        JLabel textLabel = new JLabel(text);
        textLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        textLabel.setForeground(TEXT_COLOR);

        internalPanel.add(iconLabel, BorderLayout.WEST);
        internalPanel.add(textLabel, BorderLayout.CENTER);

        button.add(internalPanel);

        // Interaction Logic
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (button != selectedButton) {
                    button.setBackground(HOVER_COLOR);
                    button.setContentAreaFilled(true);
                    button.setOpaque(true);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (button != selectedButton) {
                    button.setContentAreaFilled(false);
                    button.setOpaque(false);
                }
            }
        });

        button.addActionListener(e -> {
            // Reset old selection
            if (selectedButton != null) {
                selectedButton.setContentAreaFilled(false);
                selectedButton.setOpaque(false);
                updateButtonColors(selectedButton, TEXT_COLOR);
            }

            // Set new selection
            selectedButton = button;
            button.setBackground(SELECTED_COLOR);
            button.setContentAreaFilled(true);
            button.setOpaque(true);
            updateButtonColors(button, Color.WHITE);

            listener.actionPerformed(e);
        });

        return button;
    }

    private void updateButtonColors(JButton button, Color color) {
        // Helper to update labels inside the button
        if (button.getComponentCount() > 0 && button.getComponent(0) instanceof Container) {
            Container c = (Container) button.getComponent(0);
            for (Component comp : c.getComponents()) {
                if (comp instanceof JLabel) {
                    comp.setForeground(color);
                }
            }
        }
    }

    private String getIconForMenuItem(String text) {
        switch (text) {
            case "Dashboard":
                return "📊";
            case "Add Income":
                return "💰";
            case "Add Expense":
                return "💸";
            case "Transactions":
                return "📝";
            case "Budget & Savings":
                return "🎯";
            case "Reports":
                return "📈";
            default:
                return "•";
        }
    }

    /**
     * Programmatically selects a menu item (e.g., on initial load).
     * Updates the visual state of the button.
     * 
     * @param text The label of the menu item to select.
     */
    public void selectMenuItem(String text) {
        JButton button = menuButtons.get(text);
        if (button != null) {
            // Reset old selection
            if (selectedButton != null) {
                selectedButton.setContentAreaFilled(false);
                selectedButton.setOpaque(false);
                updateButtonColors(selectedButton, TEXT_COLOR);
            }

            // Set new selection
            selectedButton = button;
            button.setBackground(SELECTED_COLOR);
            button.setContentAreaFilled(true);
            button.setOpaque(true);
            updateButtonColors(button, Color.WHITE);
        }
    }

    public JButton getMenuButton(String text) {
        return menuButtons.get(text);
    }
}
