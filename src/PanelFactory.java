
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

    // Interface to access FinanceGUI methods without circular dependency
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

        panel.addButton("Login", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
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
            }
        });

        panel.addButton("Register", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gui.showRegister();
            }
        });

        return panel;
    }

    public static JPanel createRegistrationPanel(GUIController gui) {
        FormPanel panel = new FormPanel("Create Account");

        List<FormField> fields = new ArrayList<>();
        fields.add(new FormField("username", "Username:", FormField.FieldType.TEXT));
        fields.add(new FormField("password", "Password:", FormField.FieldType.PASSWORD));
        fields.add(new FormField("confirmPassword", "Confirm Password:", FormField.FieldType.PASSWORD));

        panel.addFields(fields);

        panel.addButton("Register", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
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
            }
        });

        panel.addButton("Back to Login", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gui.showLogin();
            }
        });

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

        panel.addButton("Save", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = panel.getFieldValue("name");
                String amountStr = panel.getFieldValue("amount");
                String categorySource = panel.getFieldValue("categorySource");
                String dateStr = panel.getFieldValue("date");

                if (name.isEmpty() || amountStr.isEmpty() || categorySource.isEmpty() || dateStr.isEmpty()) {
                    gui.showError("Please fill in all fields.", "Validation Error");
                    return;
                }

                double amount;
                try {
                    amount = Double.parseDouble(amountStr);
                    if (amount <= 0) {
                        gui.showError("Amount must be positive.", "Validation Error");
                        return;
                    }
                } catch (NumberFormatException ex) {
                    gui.showError("Invalid amount format.", "Validation Error");
                    return;
                }

                LocalDate date;
                try {
                    date = LocalDate.parse(dateStr);
                } catch (DateTimeParseException ex) {
                    gui.showError("Invalid date format. Use YYYY-MM-DD.", "Validation Error");
                    return;
                }

                User user = gui.getCurrentUser();
                if (user == null)
                    return;

                Transaction transaction;
                if (isIncome) {
                    transaction = new Income(name, amount, categorySource, date);
                } else {
                    transaction = new Expense(name, amount, categorySource, date);
                }

                user.addTransaction(transaction);
                gui.refreshAllPanels();
                gui.showMessage("Transaction added successfully!", "Success");
                gui.showDashboard();
            }
        });

        panel.addButton("Cancel", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gui.showDashboard();
            }
        });

        return panel;
    }

    public static RefreshablePanel createDashboardPanel(GUIController gui) {
        ContentPanel panel = new ContentPanel("Dashboard", true);

        // Setup sidebar
        Sidebar sidebar = panel.getSidebar();
        sidebar.addMenuItem("Dashboard", e -> gui.showDashboard());
        sidebar.addMenuItem("Add Income", e -> gui.showAddIncome());
        sidebar.addMenuItem("Add Expense", e -> gui.showAddExpense());
        sidebar.addMenuItem("Transactions", e -> gui.showTransactions());
        sidebar.addMenuItem("Budget & Savings", e -> gui.showBudgetSavings());
        sidebar.addMenuItem("Reports", e -> gui.showReports());
        sidebar.addLogoutButton(e -> {
            int response = JOptionPane.showConfirmDialog(
                    panel,
                    "Do you want to logout and save?",
                    "Logout",
                    JOptionPane.YES_NO_OPTION);
            if (response == JOptionPane.YES_OPTION) {
                gui.logout();
            }
        });

        // Setup content layout
        panel.setContentLayout(new BorderLayout());

        JPanel statsPanel = new JPanel(new GridLayout(2, 3, 20, 20));
        statsPanel.setBackground(Color.WHITE);

        StatCard balanceCard = panel.addStatCard("Current Balance", "$0.00", Color.WHITE);
        StatCard incomeCard = panel.addStatCard("Total Income", "$0.00", Color.WHITE);
        StatCard expensesCard = panel.addStatCard("Total Expenses", "$0.00", Color.WHITE);
        StatCard budgetCard = panel.addStatCard("Budget Status", "Not Set", Color.WHITE);
        StatCard savingsCard = panel.addStatCard("Savings Goal", "Not Set", Color.WHITE);

        statsPanel.add(balanceCard);
        statsPanel.add(incomeCard);
        statsPanel.add(expensesCard);
        statsPanel.add(budgetCard);
        statsPanel.add(savingsCard);

        JPanel budgetProgressPanel = new JPanel(new BorderLayout());
        budgetProgressPanel.setBorder(BorderFactory.createTitledBorder("Monthly Budget Usage"));
        budgetProgressPanel.setBackground(Color.WHITE);
        JProgressBar budgetProgressBar = new JProgressBar(0, 100);
        budgetProgressBar.setStringPainted(true);
        budgetProgressBar.setString("0%");
        budgetProgressPanel.add(budgetProgressBar, BorderLayout.CENTER);
        statsPanel.add(budgetProgressPanel);

        panel.addToContent(statsPanel, BorderLayout.CENTER);

        JPanel savingsPanel = new JPanel(new BorderLayout());
        savingsPanel.setBorder(BorderFactory.createTitledBorder("Savings Goal Progress"));
        savingsPanel.setBackground(Color.WHITE);
        JProgressBar savingsProgressBar = new JProgressBar(0, 100);
        savingsProgressBar.setStringPainted(true);
        savingsProgressBar.setString("0%");
        savingsPanel.add(savingsProgressBar, BorderLayout.CENTER);

        panel.addToContent(savingsPanel, BorderLayout.SOUTH);

        return new RefreshablePanel(panel) {
            @Override
            public void refresh() {
                User user = gui.getCurrentUser();
                if (user == null)
                    return;

                List<Transaction> transactions = user.getTransactions();
                double[] totals = FinanceManager.calculateTotals(transactions);
                double income = totals[0];
                double expenses = totals[1];
                double balance = income - expenses;

                balanceCard.setValue(FinanceManager.formatCurrency(balance));
                incomeCard.setValue(FinanceManager.formatCurrency(income));
                expensesCard.setValue(FinanceManager.formatCurrency(expenses));

                double monthlyBudget = user.getMonthlyBudget();
                if (monthlyBudget > 0) {
                    double monthlyExpenses = FinanceManager.calculateMonthlyExpenses(transactions);
                    double percentage = (monthlyExpenses / monthlyBudget) * 100;

                    budgetProgressBar.setValue((int) Math.min(percentage, 100));
                    budgetProgressBar.setString(String.format("%.1f%%", percentage));

                    if (percentage >= 100) {
                        budgetCard.setValue("Over Budget");
                        panel.setSubtitle("⚠️ OVER BUDGET");
                    } else if (percentage >= 90) {
                        budgetCard.setValue("Critical (90%+)");
                        panel.setSubtitle("⚠️ Warning: 90% of budget used");
                    } else if (percentage >= 75) {
                        budgetCard.setValue("Caution (75%+)");
                        panel.setSubtitle("⚠️ Caution: 75% of budget used");
                    } else {
                        budgetCard.setValue("Within Budget");
                        panel.setSubtitle("");
                    }
                } else {
                    budgetProgressBar.setValue(0);
                    budgetProgressBar.setString("Not Set");
                    budgetCard.setValue("Not Set");
                    panel.setSubtitle("");
                }

                double savingsGoal = user.getSavingsGoal();
                if (savingsGoal > 0) {
                    double progress = FinanceManager.calculateSavingsProgress(transactions, savingsGoal);
                    savingsProgressBar.setValue((int) Math.min(progress, 100));
                    savingsProgressBar.setString(String.format("%.1f%%", progress));
                    savingsCard.setValue(FinanceManager.formatCurrency(savingsGoal));
                } else {
                    savingsProgressBar.setValue(0);
                    savingsProgressBar.setString("Not Set");
                    savingsCard.setValue("Not Set");
                }
            }
        };
    }

    public static RefreshablePanel createBudgetSavingsPanel(GUIController gui) {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Budget Section
        JPanel budgetSection = new JPanel(new BorderLayout());
        budgetSection.setBorder(BorderFactory.createTitledBorder("Monthly Budget"));
        budgetSection.setBackground(Color.WHITE);

        FormPanel budgetForm = new FormPanel(null);
        List<FormField> budgetFields = new ArrayList<>();
        budgetFields.add(new FormField("budget", "Monthly Budget ($):", FormField.FieldType.NUMBER));
        budgetForm.addFields(budgetFields);

        JLabel budgetStatusLabel = new JLabel("");
        budgetStatusLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        JProgressBar budgetProgressBar = new JProgressBar(0, 100);
        budgetProgressBar.setStringPainted(true);
        budgetProgressBar.setString("Not Set");

        JPanel budgetStatusPanel = new JPanel(new BorderLayout());
        budgetStatusPanel.setBackground(Color.WHITE);
        budgetStatusPanel.add(budgetStatusLabel, BorderLayout.NORTH);
        budgetStatusPanel.add(budgetProgressBar, BorderLayout.CENTER);

        budgetForm.addButton("Save Budget", e -> {
            String budgetStr = budgetForm.getFieldValue("budget");
            if (budgetStr.isEmpty()) {
                gui.showError("Please enter a budget amount.", "Validation Error");
                return;
            }

            try {
                double budget = Double.parseDouble(budgetStr);
                if (budget < 0) {
                    gui.showError("Budget cannot be negative.", "Validation Error");
                    return;
                }

                User user = gui.getCurrentUser();
                if (user != null) {
                    user.setMonthlyBudget(budget);
                    gui.showMessage("Budget saved successfully!", "Success");
                    gui.refreshAllPanels();
                }
            } catch (NumberFormatException ex) {
                gui.showError("Invalid budget amount.", "Validation Error");
            }
        });

        JPanel budgetContent = new JPanel(new BorderLayout());
        budgetContent.setBackground(Color.WHITE);
        budgetContent.add(budgetForm, BorderLayout.NORTH);
        budgetContent.add(budgetStatusPanel, BorderLayout.CENTER);

        budgetSection.add(budgetContent, BorderLayout.CENTER);

        JPanel budgetButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        budgetButtonPanel.setBackground(Color.WHITE);
        JButton backButton = new JButton("Back to Dashboard");
        backButton.addActionListener(e -> gui.showDashboard());
        budgetButtonPanel.add(backButton);
        budgetSection.add(budgetButtonPanel, BorderLayout.SOUTH);

        // Savings Section
        JPanel savingsSection = new JPanel(new BorderLayout());
        savingsSection.setBorder(BorderFactory.createTitledBorder("Savings Goal"));
        savingsSection.setBackground(Color.WHITE);

        FormPanel savingsForm = new FormPanel(null);
        List<FormField> savingsFields = new ArrayList<>();
        savingsFields.add(new FormField("savingsGoal", "Savings Goal ($):", FormField.FieldType.NUMBER));
        savingsFields.add(new FormField("targetDate", "Target Date (YYYY-MM-DD):", FormField.FieldType.DATE, "", false,
                "Leave empty to use months instead", 20));
        savingsFields.add(new FormField("targetMonths", "Target Months:", FormField.FieldType.NUMBER, "", false,
                "Leave empty to use date instead", 20));
        savingsForm.addFields(savingsFields);

        JLabel savingsStatusLabel = new JLabel("");
        savingsStatusLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        JProgressBar savingsProgressBar = new JProgressBar(0, 100);
        savingsProgressBar.setStringPainted(true);
        savingsProgressBar.setString("Not Set");

        JPanel savingsStatusPanel = new JPanel(new BorderLayout());
        savingsStatusPanel.setBackground(Color.WHITE);
        savingsStatusPanel.add(savingsStatusLabel, BorderLayout.NORTH);
        savingsStatusPanel.add(savingsProgressBar, BorderLayout.CENTER);

        savingsForm.addButton("Save Goal", e -> {
            String goalStr = savingsForm.getFieldValue("savingsGoal");
            if (goalStr.isEmpty()) {
                gui.showError("Please enter a savings goal amount.", "Validation Error");
                return;
            }

            try {
                double goal = Double.parseDouble(goalStr);
                if (goal < 0) {
                    gui.showError("Savings goal cannot be negative.", "Validation Error");
                    return;
                }

                User user = gui.getCurrentUser();
                if (user == null)
                    return;

                user.setSavingsGoal(goal);

                String dateStr = savingsForm.getFieldValue("targetDate");
                String monthsStr = savingsForm.getFieldValue("targetMonths");

                if (!dateStr.isEmpty()) {
                    try {
                        LocalDate targetDate = LocalDate.parse(dateStr);
                        user.setSavingsTargetDate(targetDate);
                        user.setSavingsTargetMonths(0);
                    } catch (DateTimeParseException ex) {
                        gui.showError("Invalid date format. Use YYYY-MM-DD.", "Validation Error");
                        return;
                    }
                } else if (!monthsStr.isEmpty()) {
                    try {
                        int months = Integer.parseInt(monthsStr);
                        if (months <= 0) {
                            gui.showError("Target months must be positive.", "Validation Error");
                            return;
                        }
                        user.setSavingsTargetMonths(months);
                        user.setSavingsTargetDate(null);
                    } catch (NumberFormatException ex) {
                        gui.showError("Invalid number of months.", "Validation Error");
                        return;
                    }
                } else {
                    user.setSavingsTargetDate(null);
                    user.setSavingsTargetMonths(0);
                }

                gui.showMessage("Savings goal saved successfully!", "Success");
                gui.refreshAllPanels();
            } catch (NumberFormatException ex) {
                gui.showError("Invalid savings goal amount.", "Validation Error");
            }
        });

        JPanel savingsContent = new JPanel(new BorderLayout());
        savingsContent.setBackground(Color.WHITE);
        savingsContent.add(savingsForm, BorderLayout.NORTH);
        savingsContent.add(savingsStatusPanel, BorderLayout.CENTER);

        savingsSection.add(savingsContent, BorderLayout.CENTER);

        mainPanel.add(budgetSection, BorderLayout.NORTH);
        mainPanel.add(savingsSection, BorderLayout.CENTER);

        return new RefreshablePanel(mainPanel) {
            @Override
            public void refresh() {
                User user = gui.getCurrentUser();
                if (user == null)
                    return;

                // Update budget
                double monthlyBudget = user.getMonthlyBudget();
                if (monthlyBudget > 0) {
                    budgetForm.setFieldValue("budget", String.format("%.2f", monthlyBudget));

                    List<Transaction> transactions = user.getTransactions();
                    double monthlyExpenses = FinanceManager.calculateMonthlyExpenses(transactions);
                    double percentage = (monthlyBudget > 0) ? (monthlyExpenses / monthlyBudget) * 100 : 0;

                    budgetProgressBar.setValue((int) Math.min(percentage, 100));
                    budgetProgressBar.setString(String.format("%.1f%%", percentage));

                    if (percentage >= 100) {
                        budgetStatusLabel.setText("⚠️ OVER BUDGET - Over by "
                                + FinanceManager.formatCurrency(monthlyExpenses - monthlyBudget));
                    } else if (percentage >= 90) {
                        budgetStatusLabel.setText("⚠️ WARNING: 90% of budget used - "
                                + FinanceManager.formatCurrency(monthlyBudget - monthlyExpenses) + " remaining");
                    } else if (percentage >= 75) {
                        budgetStatusLabel.setText("⚠️ CAUTION: 75% of budget used - "
                                + FinanceManager.formatCurrency(monthlyBudget - monthlyExpenses) + " remaining");
                    } else {
                        budgetStatusLabel.setText("Within Budget - "
                                + FinanceManager.formatCurrency(monthlyBudget - monthlyExpenses) + " remaining");
                    }
                } else {
                    budgetForm.setFieldValue("budget", "");
                    budgetProgressBar.setValue(0);
                    budgetProgressBar.setString("Not Set");
                    budgetStatusLabel.setText("");
                }

                // Update savings
                double savingsGoal = user.getSavingsGoal();
                if (savingsGoal > 0) {
                    savingsForm.setFieldValue("savingsGoal", String.format("%.2f", savingsGoal));

                    LocalDate targetDate = user.getSavingsTargetDate();
                    if (targetDate != null) {
                        savingsForm.setFieldValue("targetDate", targetDate.toString());
                        savingsForm.setFieldValue("targetMonths", "");
                    } else {
                        savingsForm.setFieldValue("targetDate", "");
                        int targetMonths = user.getSavingsTargetMonths();
                        if (targetMonths > 0) {
                            savingsForm.setFieldValue("targetMonths", String.valueOf(targetMonths));
                        } else {
                            savingsForm.setFieldValue("targetMonths", "");
                        }
                    }

                    List<Transaction> transactions = user.getTransactions();
                    double progress = FinanceManager.calculateSavingsProgress(transactions, savingsGoal);
                    double balance = FinanceManager.calculateBalance(transactions);

                    savingsProgressBar.setValue((int) Math.min(progress, 100));
                    savingsProgressBar.setString(String.format("%.1f%%", progress));

                    if (targetDate != null) {
                        double requiredMonthly = SavingsPlanner.calculateRequiredMonthlySavings(savingsGoal,
                                targetDate);
                        boolean onTrack = SavingsPlanner.isOnTrack(transactions, savingsGoal, targetDate, 0);
                        savingsStatusLabel.setText("Target Date: " + targetDate.toString() + " | Required Monthly: " +
                                FinanceManager.formatCurrency(requiredMonthly) + " | Status: "
                                + (onTrack ? "On Track ✓" : "Behind ✗"));
                    } else {
                        int targetMonths = user.getSavingsTargetMonths();
                        if (targetMonths > 0) {
                            double requiredMonthly = SavingsPlanner.calculateRequiredMonthlySavings(savingsGoal,
                                    targetMonths);
                            boolean onTrack = SavingsPlanner.isOnTrack(transactions, savingsGoal, null, targetMonths);
                            savingsStatusLabel.setText("Target Months: " + targetMonths + " | Required Monthly: " +
                                    FinanceManager.formatCurrency(requiredMonthly) + " | Status: "
                                    + (onTrack ? "On Track ✓" : "Behind ✗"));
                        } else {
                            savingsStatusLabel.setText("Current Balance: " + FinanceManager.formatCurrency(balance) +
                                    " | Remaining: " + FinanceManager.formatCurrency(
                                            FinanceManager.getRemainingForGoal(transactions, savingsGoal)));
                        }
                    }
                } else {
                    savingsForm.setFieldValue("savingsGoal", "");
                    savingsForm.setFieldValue("targetDate", "");
                    savingsForm.setFieldValue("targetMonths", "");
                    savingsProgressBar.setValue(0);
                    savingsProgressBar.setString("Not Set");
                    savingsStatusLabel.setText("");
                }
            }
        };
    }

    public static RefreshablePanel createTransactionHistoryPanel(GUIController gui) {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        String[] columns = { "Date", "Type", "Name", "Amount", "Category/Source" };
        TablePanel tablePanel = new TablePanel(columns, false);

        tablePanel.addActionButton("Edit", e -> {
            int selectedRow = tablePanel.getSelectedRow();
            if (selectedRow == -1) {
                gui.showError("Please select a transaction to edit.", "No Selection");
                return;
            }

            User user = gui.getCurrentUser();
            if (user == null)
                return;

            List<Transaction> transactions = user.getTransactions();
            List<Transaction> sortedTransactions = transactions.stream()
                    .sorted(Comparator.comparing(Transaction::getDate).reversed())
                    .collect(Collectors.toList());

            if (selectedRow >= sortedTransactions.size())
                return;

            Transaction transaction = sortedTransactions.get(selectedRow);
            showEditDialog(gui, transaction, transactions.indexOf(transaction), mainPanel);
        });

        tablePanel.addActionButton("Delete", e -> {
            int selectedRow = tablePanel.getSelectedRow();
            if (selectedRow == -1) {
                gui.showError("Please select a transaction to delete.", "No Selection");
                return;
            }

            User user = gui.getCurrentUser();
            if (user == null)
                return;

            int confirm = JOptionPane.showConfirmDialog(
                    mainPanel,
                    "Are you sure you want to delete this transaction?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                List<Transaction> transactions = user.getTransactions();
                List<Transaction> sortedTransactions = transactions.stream()
                        .sorted(Comparator.comparing(Transaction::getDate).reversed())
                        .collect(Collectors.toList());

                if (selectedRow >= sortedTransactions.size())
                    return;

                Transaction transaction = sortedTransactions.get(selectedRow);
                user.removeTransaction(transaction);
                gui.refreshAllPanels();
                gui.showMessage("Transaction deleted successfully!", "Success");
            }
        });

        tablePanel.addActionButton("Sort by Date", e -> gui.refreshAllPanels());
        tablePanel.addActionButton("Sort by Amount", e -> gui.refreshAllPanels());
        tablePanel.addActionButton("Back to Dashboard", e -> gui.showDashboard());

        mainPanel.add(tablePanel, BorderLayout.CENTER);

        return new RefreshablePanel(mainPanel) {
            @Override
            public void refresh() {
                User user = gui.getCurrentUser();
                if (user == null)
                    return;

                List<Transaction> transactions = user.getTransactions();
                List<Transaction> sortedTransactions = transactions.stream()
                        .sorted(Comparator.comparing(Transaction::getDate).reversed())
                        .collect(Collectors.toList());

                tablePanel.clearRows();

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                for (Transaction t : sortedTransactions) {
                    String type = (t instanceof Income) ? "Income" : "Expense";
                    String categorySource = (t instanceof Income) ? ((Income) t).getSource()
                            : ((Expense) t).getCategory();

                    Object[] row = {
                            t.getDate().format(formatter),
                            type,
                            t.getName(),
                            FinanceManager.formatCurrency(t.getAmount()),
                            categorySource
                    };
                    tablePanel.addRow(row);
                }
            }
        };
    }

    private static void showEditDialog(GUIController gui, Transaction transaction, int index, JPanel parent) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(parent), "Edit Transaction", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(parent);

        FormPanel editForm = new FormPanel(null);
        List<FormField> fields = new ArrayList<>();
        fields.add(new FormField("name", "Name:", FormField.FieldType.TEXT, transaction.getName()));
        fields.add(new FormField("amount", "Amount:", FormField.FieldType.NUMBER,
                String.valueOf(transaction.getAmount())));

        boolean isIncome = transaction instanceof Income;
        String categorySource = isIncome ? ((Income) transaction).getSource() : ((Expense) transaction).getCategory();
        fields.add(new FormField("categorySource", isIncome ? "Source:" : "Category:", FormField.FieldType.TEXT,
                categorySource));
        fields.add(new FormField("date", "Date (YYYY-MM-DD):", FormField.FieldType.DATE,
                transaction.getDate().toString()));

        editForm.addFields(fields);

        editForm.addButton("Save", e -> {
            String name = editForm.getFieldValue("name");
            String amountStr = editForm.getFieldValue("amount");
            String catSrc = editForm.getFieldValue("categorySource");
            String dateStr = editForm.getFieldValue("date");

            if (name.isEmpty() || amountStr.isEmpty() || catSrc.isEmpty() || dateStr.isEmpty()) {
                gui.showError("Please fill in all fields.", "Validation Error");
                return;
            }

            try {
                double amount = Double.parseDouble(amountStr);
                LocalDate date = LocalDate.parse(dateStr);

                if (amount <= 0) {
                    gui.showError("Amount must be positive.", "Validation Error");
                    return;
                }

                User user = gui.getCurrentUser();
                if (user == null)
                    return;

                Transaction newTransaction;
                if (isIncome) {
                    newTransaction = new Income(name, amount, catSrc, date);
                } else {
                    newTransaction = new Expense(name, amount, catSrc, date);
                }

                user.getTransactions().set(index, newTransaction);
                gui.refreshAllPanels();
                gui.showMessage("Transaction updated successfully!", "Success");
                dialog.dispose();
            } catch (Exception ex) {
                gui.showError("Invalid input: " + ex.getMessage(), "Validation Error");
            }
        });

        editForm.addButton("Cancel", e -> dialog.dispose());

        dialog.add(editForm, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    public static RefreshablePanel createReportsPanel(GUIController gui) {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Monthly Report Section
        JPanel monthlyPanel = new JPanel(new BorderLayout());
        monthlyPanel.setBorder(BorderFactory.createTitledBorder("Monthly Report"));
        monthlyPanel.setBackground(Color.WHITE);

        JPanel monthSelectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        monthSelectionPanel.setBackground(Color.WHITE);
        monthSelectionPanel.add(new JLabel("Select Month:"));

        JComboBox<String> monthComboBox = new JComboBox<>();
        monthComboBox.addItem("Current Month");
        monthSelectionPanel.add(monthComboBox);

        monthlyPanel.add(monthSelectionPanel, BorderLayout.NORTH);

        String[] monthlyColumns = { "Income", "Expenses", "Net Balance", "Transactions" };
        TablePanel monthlyTable = new TablePanel(monthlyColumns, false);
        monthlyPanel.add(monthlyTable, BorderLayout.CENTER);

        // Category Breakdown Section
        JPanel categoryPanel = new JPanel(new BorderLayout());
        categoryPanel.setBorder(BorderFactory.createTitledBorder("Category Spending Breakdown"));
        categoryPanel.setBackground(Color.WHITE);

        String[] categoryColumns = { "Category", "Amount", "Percentage" };
        TablePanel categoryTable = new TablePanel(categoryColumns, false);
        categoryPanel.add(categoryTable, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(Color.WHITE);
        JButton backButton = new JButton("Back to Dashboard");
        backButton.addActionListener(e -> gui.showDashboard());
        buttonPanel.add(backButton);

        JButton refreshButton = new JButton("Refresh");
        monthSelectionPanel.add(refreshButton);

        mainPanel.add(monthlyPanel, BorderLayout.NORTH);
        mainPanel.add(categoryPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        RefreshablePanel panel = new RefreshablePanel(mainPanel) {
            @Override
            public void refresh() {
                User user = gui.getCurrentUser();
                if (user == null)
                    return;

                // Update month list
                List<YearMonth> availableMonths = MonthlyReportManager.getAvailableMonths(user.getTransactions());
                monthComboBox.removeAllItems();
                monthComboBox.addItem("Current Month");
                for (YearMonth month : availableMonths) {
                    monthComboBox.addItem(month.toString());
                }

                // Update monthly report
                String selected = (String) monthComboBox.getSelectedItem();
                YearMonth yearMonth = "Current Month".equals(selected) ? YearMonth.now() : YearMonth.parse(selected);

                List<Transaction> monthlyTransactions = MonthlyReportManager
                        .getTransactionsForMonth(user.getTransactions(), yearMonth);
                double income = FinanceManager.calculateTotalIncome(monthlyTransactions);
                double expenses = FinanceManager.calculateTotalExpenses(monthlyTransactions);
                double netBalance = income - expenses;

                monthlyTable.clearRows();
                Object[] row = {
                        FinanceManager.formatCurrency(income),
                        FinanceManager.formatCurrency(expenses),
                        FinanceManager.formatCurrency(netBalance),
                        monthlyTransactions.size()
                };
                monthlyTable.addRow(row);

                // Update category breakdown
                Map<String, Double> categoryMap = CategoryReportManager.getCategoryBreakdown(user.getTransactions());
                double totalExpenses = FinanceManager.calculateTotalExpenses(user.getTransactions());

                categoryTable.clearRows();
                List<Map.Entry<String, Double>> sortedCategories = categoryMap.entrySet().stream()
                        .sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()))
                        .collect(Collectors.toList());

                for (Map.Entry<String, Double> entry : sortedCategories) {
                    double amount = entry.getValue();
                    double percentage = totalExpenses > 0 ? (amount / totalExpenses) * 100 : 0;
                    Object[] catRow = {
                            entry.getKey(),
                            FinanceManager.formatCurrency(amount),
                            String.format("%.1f%%", percentage)
                    };
                    categoryTable.addRow(catRow);
                }
            }
        };

        monthComboBox.addActionListener(e -> panel.refresh());
        refreshButton.addActionListener(e -> panel.refresh());

        return panel;
    }

    // Helper class to make panels refreshable
    public static abstract class RefreshablePanel extends JPanel {
        private JPanel wrappedPanel;

        public RefreshablePanel(JPanel panel) {
            this.wrappedPanel = panel;
            setLayout(new BorderLayout());
            add(panel, BorderLayout.CENTER);
        }

        public abstract void refresh();
    }
}
