
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class TablePanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JPanel toolbarPanel;
    private JPanel topPanel;
    private List<JButton> actionButtons;

    public TablePanel(String[] columnNames, boolean editable) {
        this.actionButtons = new ArrayList<>();

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Top panel for filters/controls
        topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(Color.WHITE);
        add(topPanel, BorderLayout.NORTH);

        // Table
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return editable;
            }
        };

        table = new JTable(tableModel);
        table.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        table.setRowHeight(25);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Toolbar for action buttons
        toolbarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        toolbarPanel.setBackground(Color.WHITE);
        add(toolbarPanel, BorderLayout.SOUTH);
    }

    public TablePanel addActionButton(String label, ActionListener listener) {
        JButton button = new JButton(label);
        button.addActionListener(listener);
        actionButtons.add(button);
        toolbarPanel.add(button);
        return this;
    }

    public TablePanel addTopControl(Component component) {
        topPanel.add(component);
        return this;
    }

    public void addRow(Object[] rowData) {
        tableModel.addRow(rowData);
    }

    public void clearRows() {
        tableModel.setRowCount(0);
    }

    public int getSelectedRow() {
        return table.getSelectedRow();
    }

    public Object getValueAt(int row, int column) {
        return tableModel.getValueAt(row, column);
    }

    public void setValueAt(Object value, int row, int column) {
        tableModel.setValueAt(value, row, column);
    }

    public void removeRow(int row) {
        tableModel.removeRow(row);
    }

    public int getRowCount() {
        return tableModel.getRowCount();
    }

    public JTable getTable() {
        return table;
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    public JPanel getToolbarPanel() {
        return toolbarPanel;
    }

    public JPanel getTopPanel() {
        return topPanel;
    }
}
