
import javax.swing.*;
import java.awt.*;

public class StatCard extends JPanel {
    private JLabel titleLabel;
    private JLabel valueLabel;

    public StatCard(String title, String value, Color backgroundColor) {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        setBackground(backgroundColor);

        titleLabel = new JLabel(title);
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        titleLabel.setForeground(Color.DARK_GRAY);

        valueLabel = new JLabel(value);
        valueLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
        valueLabel.setForeground(Color.BLACK);

        add(titleLabel, BorderLayout.NORTH);
        add(valueLabel, BorderLayout.CENTER);
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
