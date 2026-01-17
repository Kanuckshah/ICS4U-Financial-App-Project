
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ContentPanel extends JPanel {
    private JPanel headerPanel;
    private JPanel contentArea;
    private JPanel footerPanel;
    private Sidebar sidebar;
    private JLabel titleLabel;
    private JLabel subtitleLabel;
    private List<StatCard> statCards;

    public ContentPanel(String title, boolean includeSidebar) {
        this.statCards = new ArrayList<>();

        setLayout(new BorderLayout());
        setBackground(Theme.BACKGROUND);

        JPanel mainLayout = new JPanel(new BorderLayout());

        if (includeSidebar) {
            sidebar = new Sidebar();
            mainLayout.add(sidebar, BorderLayout.WEST);
        }

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Theme.BACKGROUND);
        // No padding - let dashboard control its own spacing
        contentPanel.setBorder(null);

        // Header
        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Theme.BACKGROUND);

        titleLabel = new JLabel(title);
        titleLabel.setFont(Theme.FONT_TITLE);
        titleLabel.setForeground(Theme.TEXT_PRIMARY);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        subtitleLabel = new JLabel("");
        subtitleLabel.setFont(Theme.FONT_BODY);
        subtitleLabel.setForeground(Theme.WARNING);
        subtitleLabel.setVisible(false);
        headerPanel.add(subtitleLabel, BorderLayout.EAST);

        contentPanel.add(headerPanel, BorderLayout.NORTH);

        // Content area
        contentArea = new JPanel();
        contentArea.setBackground(Theme.BACKGROUND);
        contentPanel.add(contentArea, BorderLayout.CENTER);

        // Footer
        footerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        footerPanel.setBackground(Theme.BACKGROUND);
        contentPanel.add(footerPanel, BorderLayout.SOUTH);

        mainLayout.add(contentPanel, BorderLayout.CENTER);
        add(mainLayout, BorderLayout.CENTER);
    }

    public Sidebar getSidebar() {
        return sidebar;
    }

    public void setSubtitle(String subtitle) {
        subtitleLabel.setText(subtitle);
        subtitleLabel.setVisible(subtitle != null && !subtitle.isEmpty());
    }

    public void setContentLayout(LayoutManager layout) {
        contentArea.setLayout(layout);
    }

    public void addToContent(Component component) {
        contentArea.add(component);
    }

    public void addToContent(Component component, Object constraints) {
        contentArea.add(component, constraints);
    }

    public void addToFooter(Component component) {
        footerPanel.add(component);
    }

    public void clearContent() {
        contentArea.removeAll();
        statCards.clear();
    }

    public StatCard addStatCard(String title, String value, Color backgroundColor) {
        StatCard card = new StatCard(title, value, "📊", backgroundColor); // Default icon
        statCards.add(card);
        return card;
    }

    public List<StatCard> getStatCards() {
        return statCards;
    }

    public JPanel getContentArea() {
        return contentArea;
    }

    public JPanel getHeaderPanel() {
        return headerPanel;
    }

    public JPanel getFooterPanel() {
        return footerPanel;
    }

    public void setTitle(String title) {
        titleLabel.setText(title);
    }
}
