import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class FinanceGUI implements PanelFactory.GUIController {
    private JFrame mainFrame;
    private AuthManager authManager;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private User currentUser;

    // Panel cache for lazy loading
    private Map<String, JPanel> panelCache;

    public FinanceGUI() {
        this.authManager = new AuthManager();
        this.currentUser = null;
        this.panelCache = new HashMap<>();
        initializeGUI();
    }

    private void initializeGUI() {
        mainFrame = new JFrame("Student Finance Tracker v2.0");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(1000, 700);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setResizable(true);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Create panels using factory
        panelCache.put("LOGIN", PanelFactory.createLoginPanel(this));
        panelCache.put("REGISTER", PanelFactory.createRegistrationPanel(this));
        panelCache.put("DASHBOARD", PanelFactory.createDashboardPanel(this));
        panelCache.put("ADD_INCOME", PanelFactory.createAddTransactionPanel(this, true));
        panelCache.put("ADD_EXPENSE", PanelFactory.createAddTransactionPanel(this, false));
        panelCache.put("TRANSACTIONS", PanelFactory.createTransactionHistoryPanel(this));
        panelCache.put("BUDGET_SAVINGS", PanelFactory.createBudgetSavingsPanel(this));
        panelCache.put("REPORTS", PanelFactory.createReportsPanel(this));

        // Add panels to card layout
        for (Map.Entry<String, JPanel> entry : panelCache.entrySet()) {
            mainPanel.add(entry.getValue(), entry.getKey());
        }

        mainFrame.add(mainPanel);
        showLogin();
    }

    @Override
    public void showLogin() {
        cardLayout.show(mainPanel, "LOGIN");
        refreshPanel("LOGIN");
    }

    @Override
    public void showRegister() {
        cardLayout.show(mainPanel, "REGISTER");
        refreshPanel("REGISTER");
    }

    @Override
    public void showDashboard() {
        if (currentUser != null) {
            refreshPanel("DASHBOARD");
            cardLayout.show(mainPanel, "DASHBOARD");
        }
    }

    @Override
    public void showAddIncome() {
        if (currentUser != null) {
            refreshPanel("ADD_INCOME");
            cardLayout.show(mainPanel, "ADD_INCOME");
        }
    }

    @Override
    public void showAddExpense() {
        if (currentUser != null) {
            refreshPanel("ADD_EXPENSE");
            cardLayout.show(mainPanel, "ADD_EXPENSE");
        }
    }

    @Override
    public void showTransactions() {
        if (currentUser != null) {
            refreshPanel("TRANSACTIONS");
            cardLayout.show(mainPanel, "TRANSACTIONS");
        }
    }

    @Override
    public void showBudgetSavings() {
        if (currentUser != null) {
            refreshPanel("BUDGET_SAVINGS");
            cardLayout.show(mainPanel, "BUDGET_SAVINGS");
        }
    }

    @Override
    public void showReports() {
        if (currentUser != null) {
            refreshPanel("REPORTS");
            cardLayout.show(mainPanel, "REPORTS");
        }
    }

    @Override
    public boolean login(String username, String password) {
        if (authManager.login(username, password)) {
            currentUser = authManager.getCurrentUser();
            showDashboard();
            return true;
        }
        return false;
    }

    @Override
    public boolean register(String username, String password) {
        if (authManager.register(username, password)) {
            currentUser = authManager.getCurrentUser();
            showDashboard();
            return true;
        }
        return false;
    }

    @Override
    public void logout() {
        if (currentUser != null) {
            authManager.logout();
            currentUser = null;
            showLogin();
        }
    }

    @Override
    public User getCurrentUser() {
        return currentUser;
    }

    @Override
    public void refreshAllPanels() {
        if (currentUser != null) {
            refreshPanel("DASHBOARD");
            refreshPanel("TRANSACTIONS");
            refreshPanel("BUDGET_SAVINGS");
            refreshPanel("REPORTS");
        }
    }

    private void refreshPanel(String panelName) {
        JPanel panel = panelCache.get(panelName);
        if (panel instanceof PanelFactory.RefreshablePanel) {
            ((PanelFactory.RefreshablePanel) panel).refresh();
        }
    }

    public void display() {
        mainFrame.setVisible(true);
    }

    @Override
    public void showMessage(String message, String title) {
        JOptionPane.showMessageDialog(mainFrame, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void showError(String message, String title) {
        JOptionPane.showMessageDialog(mainFrame, message, title, JOptionPane.ERROR_MESSAGE);
    }
}
