
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PanelFactory {

    public interface GUIController {
        boolean login(String username, String password);

        boolean register(String username, String password);

        void logout();

        void showLogin();

        void showRegister();

        void showDashboard();

        void showAddIncome();

        void showAddExpense();

        void showTransactions();

        void showBudgetSavings();

        void showReports();

        void showMessage(String message, String title);

        void showError(String message, String title);

        void refreshAllPanels();

        User getCurrentUser();
    }

    public static JPanel createLoginPanel(GUIController gui) {
        FormPanel panel = new FormPanel("Student Finance Tracker");
        List<FormField> fields = new ArrayList<>();
        fields.add(new FormField("username", "Username:", FormField.FieldType.TEXT));
        fields.add(new FormField("password", "Password:", FormField.FieldType.PASSWORD));
        panel.addFields(fields);
        panel.addButton("Login", e -> {
            String username = panel.getFieldValue("username");
            String password = panel.getFieldValue("password");
            if (username.isEmpty() || password.isEmpty()) {
                gui.showError("Please enter both username and password.", "Login Error");
                return;
            }
            if (gui.login(username, password)) {
                panel.clearFields();
            } else {
                gui.showError("Invalid username or password.", "Login Error");
            }
        });
        panel.addButton("Register", e -> gui.showRegister());
        return panel;
    }

    public static JPanel createRegistrationPanel(GUIController gui) {
        FormPanel panel = new FormPanel("Create Account");
        List<FormField> fields = new ArrayList<>();
        fields.add(new FormField("username", "Username:", FormField.FieldType.TEXT));
        fields.add(new FormField("password", "Password:", FormField.FieldType.PASSWORD));
        fields.add(new FormField("confirmPassword", "Confirm Password:", FormField.FieldType.PASSWORD));
        panel.addFields(fields);
        panel.addButton("Register", e -> {
            String username = panel.getFieldValue("username");
            String password = panel.getFieldValue("password");
            String confirmPassword = panel.getFieldValue("confirmPassword");
            if (username.isEmpty() || password.isEmpty()) {
                gui.showError("Please fill in all fields.", "Registration Error");
                return;
            }
            if (!password.equals(confirmPassword)) {
                gui.showError("Passwords do not match.", "Registration Error");
                return;
            }
            if (gui.register(username, password)) {
                panel.clearFields();
            }
        });
        panel.addButton("Back to Login", e -> gui.showLogin());
        return panel;
    }

    public static JPanel createAddTransactionPanel(GUIController gui, boolean isIncome) {
        FormPanel panel = new FormPanel(isIncome ? "Add Income" : "Add Expense");
        List<FormField> fields = new ArrayList<>();
        fields.add(new FormField("name", "Name/Description:", FormField.FieldType.TEXT, "", true, "", 30));
        fields.add(new FormField("amount", "Amount ($):", FormField.FieldType.NUMBER, "", true, "", 30));
        fields.add(new FormField("categorySource", isIncome ? "Source:" : "Category:", FormField.FieldType.TEXT, "",
                true, "", 30));
        fields.add(new FormField("date", "Date (YYYY-MM-DD):", FormField.FieldType.DATE, LocalDate.now().toString(),
                true, "", 30));
        panel.addFields(fields);
        panel.addButton("Save", e -> {
            String name = panel.getFieldValue("name");
            String amountStr = panel.getFieldValue("amount");
            String categorySource = panel.getFieldValue("categorySource");
            String dateStr = panel.getFieldValue("date");
            if (name.isEmpty() || amountStr.isEmpty() || categorySource.isEmpty() || dateStr.isEmpty()) {
                gui.showError("Please fill in all fields.", "Validation Error");
                return;
            }
            try {
                double amount = Double.parseDouble(amountStr);
                if (amount <= 0) {
                    gui.showError("Amount must be positive.", "Validation Error");
                    return;
                }
                LocalDate date = LocalDate.parse(dateStr);
                User user = gui.getCurrentUser();
                if (user != null) {
                    Transaction transaction = isIncome ? new Income(name, amount, categorySource, date)
                            : new Expense(name, amount, categorySource, date);
                    user.addTransaction(transaction);
                    gui.refreshAllPanels();
                    gui.showMessage("Transaction added successfully!", "Success");
                    gui.showDashboard();
                }
            } catch (Exception ex) {
                gui.showError("Invalid input.", "Validation Error");
            }
        });
        panel.addButton("Cancel", e -> gui.showDashboard());
        return panel;
    }

    public static RefreshablePanel createDashboardPanel(GUIController gui) {
        ContentPanel panel = new ContentPanel("Dashboard", true);
        panel.getHeaderPanel().setVisible(false);

        // Setup sidebar
        Sidebar sidebar = panel.getSidebar();
        sidebar.addMenuItem("Dashboard", e -> gui.showDashboard());
        sidebar.addMenuItem("Add Income", e -> gui.showAddIncome());
        sidebar.addMenuItem("Add Expense", e -> gui.showAddExpense());
        sidebar.addMenuItem("Transactions", e -> gui.showTransactions());
        sidebar.addMenuItem("Budget & Savings", e -> gui.showBudgetSavings());
        sidebar.addMenuItem("Reports", e -> gui.showReports());
        sidebar.addLogoutButton(e -> {
            if (JOptionPane.showConfirmDialog(panel, "Do you want to logout?", "Logout",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                gui.logout();
            }
        });
        sidebar.selectMenuItem("Dashboard");

        // ===== NEW GRIDBAGLAYOUT =====
        JPanel dashboardContent = new JPanel(new GridBagLayout());
        dashboardContent.setOpaque(false);
        dashboardContent.setBorder(new EmptyBorder(30, 30, 30, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;

        // === 1. HEADER SECTION (Row 0) ===
        JPanel headerSection = new JPanel();
        headerSection.setLayout(new BoxLayout(headerSection, BoxLayout.Y_AXIS));
        headerSection.setOpaque(false);

        JLabel welcomeLabel = new JLabel("Welcome back!");
        welcomeLabel.setFont(Theme.FONT_TITLE);
        welcomeLabel.setForeground(Theme.TEXT_PRIMARY);
        welcomeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel dateLabel = new JLabel(LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")));
        dateLabel.setFont(Theme.FONT_BODY);
        dateLabel.setForeground(Theme.TEXT_SECONDARY);
        dateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        headerSection.add(welcomeLabel);
        headerSection.add(Box.createVerticalStrut(5));
        headerSection.add(dateLabel);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 4; // Span across all columns
        gbc.weightx = 1.0;
        gbc.weighty = 0.0; // Don't grow vertically
        dashboardContent.add(headerSection, gbc);

        // === 2. STATS CARDS (Row 1) ===
        // We want 4 cards. We can put them in a single row, 1 col each.
        StatCard balanceCard = new StatCard("Total Balance", "$0.00", "💰", Theme.PRIMARY);
        StatCard incomeCard = new StatCard("Monthly Income", "$0.00", "📈", Theme.SUCCESS);
        StatCard expensesCard = new StatCard("Monthly Expenses", "$0.00", "💸", Theme.DANGER);
        StatCard savingsCard = new StatCard("Net Savings", "$0.00", "🐷", Theme.INFO);

        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.25; // Equal width
        gbc.weighty = 0.0;
        gbc.ipady = 20; // Internal padding for height

        gbc.gridx = 0;
        dashboardContent.add(balanceCard, gbc);

        gbc.gridx = 1;
        dashboardContent.add(incomeCard, gbc);

        gbc.gridx = 2;
        dashboardContent.add(expensesCard, gbc);

        gbc.gridx = 3;
        dashboardContent.add(savingsCard, gbc);

        gbc.ipady = 0; // Reset

        // === 3. BOTTOM SECTION (Row 2) ===
        // Left: Financial Health, Right: Transactions
        // We'll give them gridwidth 2 each.

        // --- Left: Financial Health ---
        RoundedPanel healthPanel = new RoundedPanel(Theme.RADIUS_MEDIUM, null); // No border
        healthPanel.setBackground(Theme.SURFACE); // Explicitly set background
        healthPanel.setLayout(new BoxLayout(healthPanel, BoxLayout.Y_AXIS));
        healthPanel.setBorder(new EmptyBorder(25, 25, 25, 25));

        JLabel healthTitle = new JLabel("Financial Health");
        healthTitle.setFont(Theme.FONT_HEADING);
        healthTitle.setForeground(Theme.TEXT_PRIMARY);
        healthTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        healthPanel.add(healthTitle);
        healthPanel.add(Box.createVerticalStrut(20));

        // Budget
        JLabel budgetLabel = new JLabel("Monthly Budget");
        budgetLabel.setFont(Theme.FONT_SUBHEADING);
        budgetLabel.setForeground(Theme.TEXT_SECONDARY);
        budgetLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        healthPanel.add(budgetLabel);
        healthPanel.add(Box.createVerticalStrut(8));

        JProgressBar budgetProgress = new JProgressBar(0, 100);
        budgetProgress.setMaximumSize(new Dimension(Integer.MAX_VALUE, 10)); // Thinner bar
        budgetProgress.setStringPainted(false); // Clean look
        budgetProgress.setBackground(Theme.BACKGROUND);
        budgetProgress.setForeground(Theme.PRIMARY);
        budgetProgress.setAlignmentX(Component.LEFT_ALIGNMENT);
        budgetProgress.setBorderPainted(false); // Removing border for cleaner look
        healthPanel.add(budgetProgress);

        JLabel budgetStatus = new JLabel("Not set");
        budgetStatus.setFont(Theme.FONT_SMALL);
        budgetStatus.setForeground(Theme.TEXT_SECONDARY);
        budgetStatus.setAlignmentX(Component.LEFT_ALIGNMENT);
        healthPanel.add(Box.createVerticalStrut(5));
        healthPanel.add(budgetStatus);

        healthPanel.add(Box.createVerticalStrut(25));

        // Savings
        JLabel savingsLabel = new JLabel("Savings Goal");
        savingsLabel.setFont(Theme.FONT_SUBHEADING);
        savingsLabel.setForeground(Theme.TEXT_SECONDARY);
        savingsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        healthPanel.add(savingsLabel);
        healthPanel.add(Box.createVerticalStrut(8));

        JProgressBar savingsProgress = new JProgressBar(0, 100);
        savingsProgress.setMaximumSize(new Dimension(Integer.MAX_VALUE, 10));
        savingsProgress.setStringPainted(false);
        savingsProgress.setBackground(Theme.BACKGROUND);
        savingsProgress.setForeground(Theme.INFO);
        savingsProgress.setAlignmentX(Component.LEFT_ALIGNMENT);
        savingsProgress.setBorderPainted(false);
        healthPanel.add(savingsProgress);

        JLabel savingsStatus = new JLabel("Not set");
        savingsStatus.setFont(Theme.FONT_SMALL);
        savingsStatus.setForeground(Theme.TEXT_SECONDARY);
        savingsStatus.setAlignmentX(Component.LEFT_ALIGNMENT);
        healthPanel.add(Box.createVerticalStrut(5));
        healthPanel.add(savingsStatus);
        healthPanel.add(Box.createVerticalGlue());

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2; // Span 2 columns
        gbc.weightx = 0.5;
        gbc.weighty = 0.6; // Give it some vertical growth but not too much
        dashboardContent.add(healthPanel, gbc);

        // --- Right: Recent Transactions ---
        RoundedPanel transactionsPanel = new RoundedPanel(Theme.RADIUS_MEDIUM, null); // No border
        transactionsPanel.setBackground(Theme.SURFACE); // Explicitly set background
        transactionsPanel.setLayout(new BorderLayout());
        transactionsPanel.setBorder(new EmptyBorder(25, 25, 25, 25));

        JPanel transHeader = new JPanel(new BorderLayout());
        transHeader.setOpaque(false);

        JLabel transTitle = new JLabel("Recent Transactions");
        transTitle.setFont(Theme.FONT_HEADING);
        transTitle.setForeground(Theme.TEXT_PRIMARY);

        JButton viewAll = Theme.createButton("View All", false);
        viewAll.setFont(Theme.FONT_SMALL);
        viewAll.setPreferredSize(new Dimension(80, 28));
        viewAll.addActionListener(e -> gui.showTransactions());

        transHeader.add(transTitle, BorderLayout.WEST);
        transHeader.add(viewAll, BorderLayout.EAST);
        transactionsPanel.add(transHeader, BorderLayout.NORTH);

        JPanel transList = new JPanel();
        transList.setLayout(new BoxLayout(transList, BoxLayout.Y_AXIS));
        transList.setOpaque(false);

        JScrollPane transScroll = new JScrollPane(transList);
        transScroll.setBorder(null);
        transScroll.setOpaque(false);
        transScroll.getViewport().setOpaque(false);
        transScroll.getVerticalScrollBar().setUnitIncrement(16);
        transactionsPanel.add(transScroll, BorderLayout.CENTER);

        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.gridwidth = 2; // Span 2 columns
        gbc.weightx = 0.5;
        gbc.weighty = 1.0;
        dashboardContent.add(transactionsPanel, gbc);

        // Add to main scroll pane
        JScrollPane mainScroll = new JScrollPane(dashboardContent);
        mainScroll.setBorder(null);
        mainScroll.setOpaque(false);
        mainScroll.getViewport().setOpaque(false);
        mainScroll.getVerticalScrollBar().setUnitIncrement(16);

        panel.addToContent(mainScroll, BorderLayout.CENTER);

        return new RefreshablePanel(panel) {
            @Override
            public void refresh() {
                sidebar.selectMenuItem("Dashboard");
                User user = gui.getCurrentUser();
                if (user == null)
                    return;

                welcomeLabel.setText("Welcome back, " + user.getUsername() + "!");

                // Stats
                List<Transaction> allTrans = user.getTransactions();
                double[] totals = FinanceManager.calculateTotals(allTrans);
                double balance = totals[0] - totals[1];

                YearMonth now = YearMonth.now();
                List<Transaction> monthTrans = MonthlyReportManager.getTransactionsForMonth(allTrans, now);
                double monthInc = FinanceManager.calculateTotalIncome(monthTrans);
                double monthExp = FinanceManager.calculateTotalExpenses(monthTrans);
                double net = monthInc - monthExp;

                balanceCard.setValue(FinanceManager.formatCurrency(balance));
                incomeCard.setValue(FinanceManager.formatCurrency(monthInc));
                expensesCard.setValue(FinanceManager.formatCurrency(monthExp));
                savingsCard.setValue(FinanceManager.formatCurrency(net));
                savingsCard.setValueColor(net < 0 ? Theme.DANGER : Theme.TEXT_PRIMARY);

                // Budget
                double budget = user.getMonthlyBudget();
                if (budget > 0) {
                    double pct = (monthExp / budget) * 100;
                    budgetProgress.setValue((int) Math.min(pct, 100));
                    budgetStatus.setText(pct >= 100 ? "Over Budget!"
                            : "Remaining: " + FinanceManager.formatCurrency(budget - monthExp));
                    budgetStatus.setForeground(pct >= 100 ? Theme.DANGER : Theme.TEXT_SECONDARY);
                    budgetProgress.setForeground(pct >= 100 ? Theme.DANGER : Theme.PRIMARY);
                } else {
                    budgetProgress.setValue(0);
                    budgetStatus.setText("Set a budget to track progress");
                }

                // Savings
                double goal = user.getSavingsGoal();
                if (goal > 0) {
                    double pct = (balance / goal) * 100;
                    savingsProgress.setValue(balance > 0 ? (int) Math.min(pct, 100) : 0);
                    savingsStatus.setText(pct >= 100 ? "Goal Reached!"
                            : "Remaining: " + FinanceManager.formatCurrency(goal - balance));
                    savingsProgress.setForeground(Theme.INFO);
                } else {
                    savingsProgress.setValue(0);
                    savingsStatus.setText("Set a goal to track progress");
                }

                // Transactions
                transList.removeAll();
                List<Transaction> recent = allTrans.stream()
                        .sorted(Comparator.comparing(Transaction::getDate).reversed())
                        .limit(5)
                        .collect(Collectors.toList());

                if (recent.isEmpty()) {
                    JLabel empty = new JLabel("No recent transactions");
                    empty.setFont(Theme.FONT_BODY);
                    empty.setForeground(Theme.TEXT_SECONDARY);
                    empty.setAlignmentX(Component.CENTER_ALIGNMENT);
                    transList.add(Box.createVerticalStrut(30));
                    transList.add(empty);
                } else {
                    for (Transaction t : recent) {
                        transList.add(createTransactionItem(t));
                        transList.add(Box.createVerticalStrut(8));
                    }
                }
                transList.revalidate();
                transList.repaint();
            }
        };
    }

    // Helper to create list items
    private static JPanel createTransactionItem(Transaction t) {
        RoundedPanel item = new RoundedPanel(Theme.RADIUS_SMALL, Theme.BACKGROUND);
        item.setLayout(new BorderLayout(15, 0));
        item.setBorder(new EmptyBorder(10, 15, 10, 15));
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));

        JLabel icon = new JLabel(t instanceof Income ? "💰" : "💸");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));

        JPanel center = new JPanel(new GridLayout(2, 1));
        center.setOpaque(false);
        JLabel name = new JLabel(t.getName());
        name.setFont(Theme.FONT_BODY);
        name.setForeground(Theme.TEXT_PRIMARY);
        JLabel date = new JLabel(t.getDate().format(DateTimeFormatter.ofPattern("MMM d")));
        date.setFont(Theme.FONT_SMALL);
        date.setForeground(Theme.TEXT_SECONDARY);
        center.add(name);
        center.add(date);

        JLabel amt = new JLabel(FinanceManager.formatCurrency(t.getAmount()));
        amt.setFont(Theme.FONT_BUTTON);
        amt.setForeground(t instanceof Income ? Theme.SUCCESS : Theme.TEXT_PRIMARY);

        item.add(icon, BorderLayout.WEST);
        item.add(center, BorderLayout.CENTER);
        item.add(amt, BorderLayout.EAST);

        return item;
    }

    public static RefreshablePanel createBudgetSavingsPanel(GUIController gui) {
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(Theme.BACKGROUND);
        main.setBorder(
                new EmptyBorder(Theme.PADDING_LARGE, Theme.PADDING_LARGE, Theme.PADDING_LARGE, Theme.PADDING_LARGE));

        // 1. Budget Card
        RoundedPanel budgetCard = new RoundedPanel(Theme.RADIUS_MEDIUM, null);
        budgetCard.setBackground(Theme.SURFACE); // Explicitly set background
        budgetCard.setLayout(new BorderLayout());
        budgetCard.setBorder(new EmptyBorder(25, 25, 25, 25));

        JLabel budgetTitle = new JLabel("Monthly Budget");
        budgetTitle.setFont(Theme.FONT_HEADING);
        budgetTitle.setForeground(Theme.TEXT_PRIMARY);

        FormPanel budgetForm = new FormPanel(null);
        budgetForm.addFields(java.util.List.of(new FormField("budget", "Budget ($):", FormField.FieldType.NUMBER)));
        budgetForm.addButton("Save Budget", e -> {
            try {
                double val = Double.parseDouble(budgetForm.getFieldValue("budget"));
                if (val < 0) {
                    gui.showError("Positive values only", "Error");
                    return;
                }
                User u = gui.getCurrentUser();
                if (u != null) {
                    u.setMonthlyBudget(val);
                    gui.refreshAllPanels();
                    gui.showMessage("Saved", "Success");
                }
            } catch (Exception ex) {
                gui.showError("Invalid number", "Error");
            }
        });

        JProgressBar bBar = new JProgressBar(0, 100);
        bBar.setStringPainted(true);
        JLabel bStatus = new JLabel(" ");
        bStatus.setFont(Theme.FONT_SMALL);
        bStatus.setForeground(Theme.TEXT_SECONDARY);

        JPanel bBottom = new JPanel(new BorderLayout());
        bBottom.setOpaque(false);
        bBottom.add(bStatus, BorderLayout.NORTH);
        bBottom.add(bBar, BorderLayout.CENTER);

        budgetCard.add(budgetTitle, BorderLayout.NORTH);
        budgetCard.add(budgetForm, BorderLayout.CENTER);
        budgetCard.add(bBottom, BorderLayout.SOUTH);

        // 2. Savings Card
        RoundedPanel savingsCard = new RoundedPanel(Theme.RADIUS_MEDIUM, null);
        savingsCard.setBackground(Theme.SURFACE); // Explicitly set background
        savingsCard.setLayout(new BorderLayout());
        savingsCard.setBorder(new EmptyBorder(25, 25, 25, 25));

        JLabel savingsTitle = new JLabel("Savings Goal");
        savingsTitle.setFont(Theme.FONT_HEADING);
        savingsTitle.setForeground(Theme.TEXT_PRIMARY);

        FormPanel savingsForm = new FormPanel(null);
        savingsForm.addFields(java.util.List.of(
                new FormField("goal", "Goal ($):", FormField.FieldType.NUMBER),
                new FormField("date", "Target Date (YYYY-MM-DD):", FormField.FieldType.DATE, "", false, "Optional",
                        20)));
        savingsForm.addButton("Save Goal", e -> {
            try {
                double val = Double.parseDouble(savingsForm.getFieldValue("goal"));
                String dStr = savingsForm.getFieldValue("date");
                if (val < 0) {
                    gui.showError("Positive values only", "Error");
                    return;
                }
                User u = gui.getCurrentUser();
                if (u != null) {
                    u.setSavingsGoal(val);
                    if (!dStr.isEmpty())
                        u.setSavingsTargetDate(LocalDate.parse(dStr));
                    else
                        u.setSavingsTargetDate(null);
                    gui.refreshAllPanels();
                    gui.showMessage("Saved", "Success");
                }
            } catch (Exception ex) {
                gui.showError("Invalid input", "Error");
            }
        });

        JProgressBar sBar = new JProgressBar(0, 100);
        sBar.setStringPainted(true);
        JLabel sStatus = new JLabel(" ");
        sStatus.setFont(Theme.FONT_SMALL);
        sStatus.setForeground(Theme.TEXT_SECONDARY);

        JPanel sBottom = new JPanel(new BorderLayout());
        sBottom.setOpaque(false);
        sBottom.add(sStatus, BorderLayout.NORTH);
        sBottom.add(sBar, BorderLayout.CENTER);

        savingsCard.add(savingsTitle, BorderLayout.NORTH);
        savingsCard.add(savingsForm, BorderLayout.CENTER);
        savingsCard.add(sBottom, BorderLayout.SOUTH);

        // Layout Config
        JPanel content = new JPanel(new GridLayout(2, 1, 0, 20));
        content.setOpaque(false);
        content.add(budgetCard);
        content.add(savingsCard);

        JButton back = Theme.createButton("Back", false);
        back.addActionListener(e -> gui.showDashboard());
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.LEFT));
        footer.setBackground(Theme.BACKGROUND);
        footer.add(back);

        main.add(content, BorderLayout.CENTER);
        main.add(footer, BorderLayout.SOUTH);

        return new RefreshablePanel(main) {
            @Override
            public void refresh() {
                User u = gui.getCurrentUser();
                if (u == null)
                    return;

                // Budget populate
                double b = u.getMonthlyBudget();
                budgetForm.setFieldValue("budget", b > 0 ? String.format("%.2f", b) : "");
                if (b > 0) {
                    double exp = FinanceManager.calculateMonthlyExpenses(u.getTransactions());
                    double pct = (exp / b) * 100;
                    bBar.setValue((int) Math.min(pct, 100));
                    bBar.setString(String.format("%.1f%%", pct));
                    bBar.setForeground(pct >= 100 ? Theme.DANGER : Theme.PRIMARY);
                    bStatus.setText(
                            pct >= 100 ? "Over Budget" : "Remaining: " + FinanceManager.formatCurrency(b - exp));
                } else {
                    bBar.setValue(0);
                    bBar.setString("Not Set");
                }

                // Savings populate
                double g = u.getSavingsGoal();
                savingsForm.setFieldValue("goal", g > 0 ? String.format("%.2f", g) : "");
                LocalDate td = u.getSavingsTargetDate();
                savingsForm.setFieldValue("date", td != null ? td.toString() : "");

                if (g > 0) {
                    double bal = FinanceManager.calculateBalance(u.getTransactions());
                    double pct = (bal / g) * 100;
                    sBar.setValue(bal > 0 ? (int) Math.min(pct, 100) : 0);
                    sBar.setString(String.format("%.1f%%", Math.max(0, pct)));
                    sBar.setForeground(Theme.INFO);
                    sStatus.setText("Current: " + FinanceManager.formatCurrency(bal));
                } else {
                    sBar.setValue(0);
                    sBar.setString("Not set");
                }
            }
        };
    }

    public static RefreshablePanel createTransactionHistoryPanel(GUIController gui) {
        JPanel mainConfig = new JPanel(new BorderLayout());
        mainConfig.setBackground(Theme.BACKGROUND);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Theme.BACKGROUND);
        header.setBorder(
                new EmptyBorder(Theme.PADDING_LARGE, Theme.PADDING_LARGE, Theme.PADDING_SMALL, Theme.PADDING_LARGE));

        JLabel title = new JLabel("Transaction History");
        title.setFont(Theme.FONT_TITLE);
        title.setForeground(Theme.TEXT_PRIMARY);

        JButton backBtn = Theme.createButton("Back to Dashboard", false); // Secondary button
        backBtn.addActionListener(e -> gui.showDashboard());

        header.add(title, BorderLayout.WEST);
        header.add(backBtn, BorderLayout.EAST);
        mainConfig.add(header, BorderLayout.NORTH);

        // Content List
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setOpaque(false);
        listPanel.setBorder(new EmptyBorder(0, Theme.PADDING_LARGE, Theme.PADDING_LARGE, Theme.PADDING_LARGE));

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        mainConfig.add(scrollPane, BorderLayout.CENTER);

        return new RefreshablePanel(mainConfig) {
            @Override
            public void refresh() {
                listPanel.removeAll();
                User user = gui.getCurrentUser();
                if (user == null)
                    return;

                List<Transaction> transactions = user.getTransactions().stream()
                        .sorted(Comparator.comparing(Transaction::getDate).reversed())
                        .collect(Collectors.toList());

                if (transactions.isEmpty()) {
                    JLabel empty = new JLabel("No transactions found.");
                    empty.setFont(Theme.FONT_HEADING);
                    empty.setForeground(Theme.TEXT_SECONDARY);
                    empty.setAlignmentX(Component.CENTER_ALIGNMENT);
                    listPanel.add(Box.createVerticalStrut(50));
                    listPanel.add(empty);
                } else {
                    for (Transaction t : transactions) {
                        listPanel.add(createFullTransactionItem(t, gui, () -> {
                            // On delete callback
                            user.removeTransaction(t);
                            gui.refreshAllPanels();
                        }));
                        listPanel.add(Box.createVerticalStrut(10));
                    }
                }
                listPanel.revalidate();
                listPanel.repaint();
            }
        };
    }

    // Helper for full width transaction item with actions
    private static JPanel createFullTransactionItem(Transaction t, GUIController gui, Runnable onDelete) {
        RoundedPanel item = new RoundedPanel(Theme.RADIUS_SMALL, null);
        item.setBackground(Theme.SURFACE);
        item.setLayout(new BorderLayout(15, 0));
        item.setBorder(new EmptyBorder(15, 20, 15, 20));
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        // Left: Icon + Type
        JLabel icon = new JLabel(t instanceof Income ? "💰" : "💸");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));

        JPanel left = new JPanel(new GridLayout(2, 1));
        left.setOpaque(false);
        JLabel name = new JLabel(t.getName());
        name.setFont(Theme.FONT_HEADING);
        name.setForeground(Theme.TEXT_PRIMARY);
        JLabel type = new JLabel(t instanceof Income ? "Income" : "Expense");
        type.setFont(Theme.FONT_SMALL);
        type.setForeground(Theme.TEXT_SECONDARY);
        left.add(name);
        left.add(type);

        JPanel farLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        farLeft.setOpaque(false);
        farLeft.add(icon);
        farLeft.add(left);

        // Center: Date + Category
        JPanel center = new JPanel(new GridLayout(2, 1));
        center.setOpaque(false);
        JLabel date = new JLabel("Date: " + t.getDate().toString());
        date.setFont(Theme.FONT_BODY);
        date.setForeground(Theme.TEXT_PRIMARY);
        String catSrc = t instanceof Income ? ((Income) t).getSource() : ((Expense) t).getCategory();
        JLabel category = new JLabel(t instanceof Income ? "Source: " + catSrc : "Category: " + catSrc);
        category.setFont(Theme.FONT_SMALL);
        category.setForeground(Theme.TEXT_SECONDARY);
        center.add(date);
        center.add(category);

        // Right: Amount + Delete
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        right.setOpaque(false);

        JLabel amount = new JLabel(FinanceManager.formatCurrency(t.getAmount()));
        amount.setFont(Theme.FONT_HEADING);
        amount.setForeground(t instanceof Income ? Theme.SUCCESS : Theme.TEXT_PRIMARY);

        JButton deleteBtn = new JButton("🗑️");
        deleteBtn.setBorderPainted(false);
        deleteBtn.setContentAreaFilled(false);
        deleteBtn.setFocusPainted(false);
        deleteBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        deleteBtn.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(item, "Delete this transaction?", "Confirm Delete",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                onDelete.run();
            }
        });

        right.add(amount);
        right.add(deleteBtn);

        item.add(farLeft, BorderLayout.WEST);
        item.add(center, BorderLayout.CENTER);
        item.add(right, BorderLayout.EAST);

        return item;
    }

    public static RefreshablePanel createReportsPanel(GUIController gui) {
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(Theme.BACKGROUND);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(
                new EmptyBorder(Theme.PADDING_LARGE, Theme.PADDING_LARGE, Theme.PADDING_SMALL, Theme.PADDING_LARGE));
        header.setBackground(Theme.BACKGROUND);

        JLabel title = new JLabel("Financial Reports");
        title.setFont(Theme.FONT_TITLE);
        title.setForeground(Theme.TEXT_PRIMARY);

        JButton backBtn = Theme.createButton("Back to Dashboard", false);
        backBtn.addActionListener(e -> gui.showDashboard());

        header.add(title, BorderLayout.WEST);
        header.add(backBtn, BorderLayout.EAST);
        main.add(header, BorderLayout.NORTH);

        // Scrollable Content
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(0, Theme.PADDING_LARGE, Theme.PADDING_LARGE, Theme.PADDING_LARGE));
        content.setBackground(Theme.BACKGROUND);

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        main.add(scroll, BorderLayout.CENTER);

        return new RefreshablePanel(main) {
            @Override
            public void refresh() {
                content.removeAll();
                User user = gui.getCurrentUser();
                if (user == null)
                    return;

                List<Transaction> transactions = user.getTransactions();
                double income = FinanceManager.calculateTotalIncome(transactions);
                double expenses = FinanceManager.calculateTotalExpenses(transactions);
                double savings = income - expenses;

                // 1. Overview Card
                RoundedPanel overview = new RoundedPanel(Theme.RADIUS_MEDIUM, null);
                overview.setBackground(Theme.SURFACE);
                overview.setLayout(new GridLayout(1, 3, 20, 0));
                overview.setBorder(new EmptyBorder(25, 25, 25, 25));
                overview.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

                overview.add(createReportStat("Total Income", FinanceManager.formatCurrency(income), Theme.SUCCESS));
                overview.add(createReportStat("Total Expenses", FinanceManager.formatCurrency(expenses), Theme.DANGER));
                overview.add(createReportStat("Net Savings", FinanceManager.formatCurrency(savings),
                        savings >= 0 ? Theme.INFO : Theme.DANGER));

                content.add(overview);
                content.add(Box.createVerticalStrut(30));

                // 2. Category Breakdown
                JLabel catTitle = new JLabel("Expense Breakdown by Category");
                catTitle.setFont(Theme.FONT_HEADING);
                catTitle.setForeground(Theme.TEXT_PRIMARY);
                catTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
                content.add(catTitle);
                content.add(Box.createVerticalStrut(15));

                Map<String, Double> categories = CategoryReportManager.getCategoryBreakdown(transactions);
                if (categories.isEmpty()) {
                    JLabel empty = new JLabel("No expenses recorded.");
                    empty.setFont(Theme.FONT_BODY);
                    empty.setForeground(Theme.TEXT_SECONDARY);
                    content.add(empty);
                } else {
                    // Sort by amount descending
                    categories.entrySet().stream()
                            .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                            .forEach(entry -> {
                                double amt = entry.getValue();
                                double pct = (expenses > 0) ? (amt / expenses) * 100 : 0;
                                content.add(createCategoryBar(entry.getKey(), amt, pct));
                                content.add(Box.createVerticalStrut(15));
                            });
                }

                content.revalidate();
                content.repaint();
            }
        };
    }

    private static JPanel createReportStat(String label, String value, Color color) {
        JPanel p = new JPanel(new GridLayout(2, 1));
        p.setOpaque(false);
        JLabel l = new JLabel(label);
        l.setFont(Theme.FONT_SMALL);
        l.setForeground(Theme.TEXT_SECONDARY);
        JLabel v = new JLabel(value);
        v.setFont(Theme.FONT_TITLE); // Reusing title font for big numbers
        v.setForeground(color);
        p.add(l);
        p.add(v);
        return p;
    }

    private static JPanel createCategoryBar(String category, double amount, double percentage) {
        JPanel panel = new JPanel(new BorderLayout(10, 5));
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        JLabel name = new JLabel(category);
        name.setFont(Theme.FONT_BODY);
        name.setForeground(Theme.TEXT_PRIMARY);

        JLabel val = new JLabel(String.format("%s (%.1f%%)", FinanceManager.formatCurrency(amount), percentage));
        val.setFont(Theme.FONT_BODY);
        val.setForeground(Theme.TEXT_PRIMARY);

        top.add(name, BorderLayout.WEST);
        top.add(val, BorderLayout.EAST);

        JProgressBar bar = new JProgressBar(0, 100);
        bar.setValue((int) percentage);
        bar.setStringPainted(false);
        bar.setBackground(Theme.SURFACE);
        bar.setForeground(Theme.PRIMARY);
        bar.setBorderPainted(false);
        bar.setPreferredSize(new Dimension(100, 8));

        panel.add(top, BorderLayout.CENTER);
        panel.add(bar, BorderLayout.SOUTH);

        return panel;
    }

    public static abstract class RefreshablePanel extends JPanel {
        public RefreshablePanel(JPanel panel) {
            setLayout(new BorderLayout());
            add(panel, BorderLayout.CENTER);
        }

        public abstract void refresh();
    }
}
