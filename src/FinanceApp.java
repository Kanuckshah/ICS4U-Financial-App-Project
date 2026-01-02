import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class FinanceApp {
    private AuthManager authManager;

    public FinanceApp() {
        this.authManager = new AuthManager();
    }

    public void start() {
        IO.println("======================================");
        IO.println("  Student Finance Tracker App");
        IO.println("======================================");
        IO.println();

        while (true) {
            if (!authManager.isLoggedIn()) {
                showAuthMenu();
            } else {
                showMainMenu();
            }
        }
    }

    private void showAuthMenu() {
        IO.println("=== Authentication ===");
        IO.println("1. Register");
        IO.println("2. Login");
        IO.println("3. Exit");
        IO.printSeparator();

        String choice = IO.readLine("Enter your choice: ");

        switch (choice) {
            case "1":
                handleRegistration();
                break;
            case "2":
                handleLogin();
                break;
            case "3":
                IO.println("Thank you for using Student Finance Tracker!");
                System.exit(0);
                break;
            default:
                IO.println("Invalid choice. Please try again.");
        }
        IO.println();
    }

    private void handleRegistration() {
        IO.printSeparator();
        IO.println("=== Registration ===");
        String username = IO.readLine("Enter username: ");
        String password = IO.readLine("Enter password: ");
        authManager.register(username, password);
    }

    private void handleLogin() {
        IO.printSeparator();
        IO.println("=== Login ===");
        String username = IO.readLine("Enter username: ");
        String password = IO.readLine("Enter password: ");
        authManager.login(username, password);
    }

    private void showMainMenu() {
        IO.printSeparator();
        IO.println("=== Main Menu ===");
        IO.println("1. Add Income");
        IO.println("2. Add Expense");
        IO.println("3. Set Monthly Budget");
        IO.println("4. Set Savings Goal");
        IO.println("5. View Transaction History");
        IO.println("6. View Financial Summary");
        IO.println("7. Logout and Save");
        IO.printSeparator();

        String choice = IO.readLine("Enter your choice: ");
        IO.println();

        switch (choice) {
            case "1":
                handleAddIncome();
                break;
            case "2":
                handleAddExpense();
                break;
            case "3":
                handleSetBudget();
                break;
            case "4":
                handleSetSavingsGoal();
                break;
            case "5":
                handleViewTransactionHistory();
                break;
            case "6":
                handleViewFinancialSummary();
                break;
            case "7":
                handleLogout();
                break;
            default:
                IO.println("Invalid choice. Please try again.");
        }
        IO.println();
    }

    private void handleAddIncome() {
        IO.printSeparator();
        IO.println("=== Add Income ===");

        String name = IO.readLine("Enter income name/description: ");
        double amount = IO.readPositiveDouble("Enter amount: $");
        String source = IO.readLine("Enter source (job, event, gift, etc.): ");
        LocalDate date = readDate("Enter date (YYYY-MM-DD) or press Enter for today: ");

        Income income = new Income(name, amount, source, date);
        authManager.getCurrentUser().addTransaction(income);

        IO.println("Income added successfully!");
    }

    private void handleAddExpense() {
        IO.printSeparator();
        IO.println("=== Add Expense ===");

        String name = IO.readLine("Enter expense name/description: ");
        double amount = IO.readPositiveDouble("Enter amount: $");
        String category = IO.readLine("Enter category (food, transport, events, personal, etc.): ");
        LocalDate date = readDate("Enter date (YYYY-MM-DD) or press Enter for today: ");

        Expense expense = new Expense(name, amount, category, date);
        authManager.getCurrentUser().addTransaction(expense);

        IO.println("Expense added successfully!");
    }

    private void handleSetBudget() {
        IO.printSeparator();
        IO.println("=== Set Monthly Budget ===");

        double budget = IO.readNonNegativeDouble("Enter monthly budget: $");
        authManager.getCurrentUser().setMonthlyBudget(budget);

        IO.println("Monthly budget set to " + FinanceManager.formatCurrency(budget));
    }

    private void handleSetSavingsGoal() {
        IO.printSeparator();
        IO.println("=== Set Savings Goal ===");

        double goal = IO.readNonNegativeDouble("Enter savings goal: $");
        authManager.getCurrentUser().setSavingsGoal(goal);

        IO.println("Savings goal set to " + FinanceManager.formatCurrency(goal));
    }

    private void handleViewTransactionHistory() {
        IO.printSeparator();
        IO.println("=== Transaction History ===");

        List<Transaction> transactions = authManager.getCurrentUser().getTransactions();

        if (transactions.isEmpty()) {
            IO.println("No transactions found.");
            return;
        }

        List<Transaction> sortedTransactions = transactions.stream()
                .sorted(Comparator.comparing(Transaction::getDate).reversed())
                .collect(Collectors.toList());

        IO.println(String.format("%-10s | %-20s | %-12s | %-15s | %s",
                "Type", "Name", "Amount", "Category/Source", "Date"));
        IO.println("----------------------------------------------------------------------------");

        for (Transaction transaction : sortedTransactions) {
            IO.println(transaction.formatForDisplay());
        }

        IO.println("----------------------------------------------------------------------------");
        IO.println("Total transactions: " + transactions.size());
    }

    private void handleViewFinancialSummary() {
        IO.printSeparator();
        IO.println("=== Financial Summary ===");

        User user = authManager.getCurrentUser();
        List<Transaction> transactions = user.getTransactions();

        double totalIncome = FinanceManager.calculateTotalIncome(transactions);
        double totalExpenses = FinanceManager.calculateTotalExpenses(transactions);
        double balance = FinanceManager.calculateBalance(transactions);

        IO.println("Total Income:     " + FinanceManager.formatCurrency(totalIncome));
        IO.println("Total Expenses:   " + FinanceManager.formatCurrency(totalExpenses));
        IO.println("Current Balance:  " + FinanceManager.formatCurrency(balance));
        IO.println();

        double monthlyBudget = user.getMonthlyBudget();
        if (monthlyBudget > 0) {
            double monthlyExpenses = FinanceManager.calculateMonthlyExpenses(transactions);
            double budgetDifference = FinanceManager.getBudgetDifference(transactions, monthlyBudget);
            boolean withinBudget = FinanceManager.isWithinBudget(transactions, monthlyBudget);

            IO.println("=== Budget Status ===");
            IO.println("Monthly Budget:      " + FinanceManager.formatCurrency(monthlyBudget));
            IO.println("Monthly Expenses:    " + FinanceManager.formatCurrency(monthlyExpenses));

            if (withinBudget) {
                IO.println("Status:              Within Budget ✓");
                IO.println("Remaining:           " + FinanceManager.formatCurrency(-budgetDifference));
            } else {
                IO.println("Status:              Over Budget ✗");
                IO.println("Over by:             " + FinanceManager.formatCurrency(budgetDifference));
            }
            IO.println();
        } else {
            IO.println("Monthly Budget: Not set");
            IO.println();
        }

        double savingsGoal = user.getSavingsGoal();
        if (savingsGoal > 0) {
            double progress = FinanceManager.calculateSavingsProgress(transactions, savingsGoal);
            double remaining = FinanceManager.getRemainingForGoal(transactions, savingsGoal);

            IO.println("=== Savings Goal ===");
            IO.println("Savings Goal:        " + FinanceManager.formatCurrency(savingsGoal));
            IO.println("Current Balance:     " + FinanceManager.formatCurrency(balance));
            IO.println("Progress:            " + String.format("%.1f%%", progress));

            if (remaining > 0) {
                IO.println("Remaining:           " + FinanceManager.formatCurrency(remaining));
            } else {
                IO.println("Status:              Goal Achieved! ✓");
            }
        } else {
            IO.println("Savings Goal: Not set");
        }
    }

    private void handleLogout() {
        authManager.logout();
    }

    private LocalDate readDate(String prompt) {
        while (true) {
            String input = IO.readLine(prompt);

            if (input.trim().isEmpty()) {
                return LocalDate.now();
            }

            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                return LocalDate.parse(input.trim(), formatter);
            } catch (DateTimeParseException e) {
                IO.println("Invalid date format. Please use YYYY-MM-DD or press Enter for today.");
            }
        }
    }
}