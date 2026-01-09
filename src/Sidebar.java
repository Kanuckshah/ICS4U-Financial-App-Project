
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.LinkedHashMap;
import java.util.Map;

public class Sidebar extends JPanel {
    private Map<String, JButton> menuButtons;
    private JButton logoutButton;

    public Sidebar() {
        this.menuButtons = new LinkedHashMap<>();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(200, 0));
        setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.GRAY));
        setBackground(new Color(240, 240, 240));

        JLabel title = new JLabel("Menu");
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        title.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(title);
    }

    public void addMenuItem(String text, ActionListener listener) {
        JButton button = createMenuButton(text, listener);
        menuButtons.put(text, button);

        // Remove logout button if it exists
        if (logoutButton != null && logoutButton.getParent() != null) {
            remove(logoutButton);
            Component glue = null;
            for (Component c : getComponents()) {
                if (c instanceof Box.Filler) {
                    glue = c;
                    break;
                }
            }
            if (glue != null) {
                remove(glue);
            }
        }

        add(button);
    }

    public void addLogoutButton(ActionListener listener) {
        add(Box.createVerticalGlue());

        logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        logoutButton.addActionListener(listener);
        logoutButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoutButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        logoutButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        add(logoutButton);
        add(Box.createVerticalStrut(10));
    }

    private JButton createMenuButton(String text, ActionListener listener) {
        JButton button = new JButton(text);
        button.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        button.addActionListener(listener);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        return button;
    }

    public JButton getMenuButton(String text) {
        return menuButtons.get(text);
    }
}
