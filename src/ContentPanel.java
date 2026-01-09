
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
        setBackground(Color.WHITE);

        JPanel mainLayout = new JPanel(new BorderLayout());

        if (includeSidebar) {
            sidebar = new Sidebar();
            mainLayout.add(sidebar, BorderLayout.WEST);
        }

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header
        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);

        titleLabel = new JLabel(title);
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        subtitleLabel = new JLabel("");
        subtitleLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        subtitleLabel.setForeground(Color.DARK_GRAY);
        subtitleLabel.setVisible(false);
        headerPanel.add(subtitleLabel, BorderLayout.EAST);

        contentPanel.add(headerPanel, BorderLayout.NORTH);

        // Content area
        contentArea = new JPanel();
        contentArea.setBackground(Color.WHITE);
        contentPanel.add(contentArea, BorderLayout.CENTER);

        // Footer
        footerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        footerPanel.setBackground(Color.WHITE);
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
        StatCard card = new StatCard(title, value, backgroundColor);
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
