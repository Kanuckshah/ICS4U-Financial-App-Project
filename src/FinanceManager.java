import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class for performing financial calculations and data filtering.
 * Provides static methods for summing transactions, calculating balances, and
 * filtering lists.
 */
public class FinanceManager {

    /**
     * Calculates the total income from a list of transactions.
     * 
     * @param transactions List of transactions.
     * @return Total sum of income.
     */
    public static double calculateTotalIncome(List<Transaction> transactions) {
        return transactions.stream()
                .filter(t -> t instanceof Income)
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    /**
     * Calculates the total expenses from a list of transactions.
     * 
     * @param transactions List of transactions.
     * @return Total sum of expenses.
     */
    public static double calculateTotalExpenses(List<Transaction> transactions) {
        return transactions.stream()
                .filter(t -> t instanceof Expense)
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    /**
     * Calculates the net balance (Income - Expenses).
     * 
     * @param transactions List of transactions.
     * @return Net balance.
     */
    public static double calculateBalance(List<Transaction> transactions) {
        double[] totals = calculateTotals(transactions);
        return totals[0] - totals[1];
    }

    /**
     * Calculates both total income and total expenses in a single pass.
     * 
     * @param transactions List of transactions.
     * @return Array where index 0 is total income and index 1 is total expenses.
     */
    public static double[] calculateTotals(List<Transaction> transactions) {
        double income = 0.0;
        double expenses = 0.0;
        for (Transaction t : transactions) {
            if (t instanceof Income) {
                income += t.getAmount();
            } else if (t instanceof Expense) {
                expenses += t.getAmount();
            }
        }
        return new double[] { income, expenses };
    }

    /**
     * Calculates total expenses for the current month.
     * 
     * @param transactions List of transactions.
     * @return Total expenses for the current month (local time).
     */
    public static double calculateMonthlyExpenses(List<Transaction> transactions) {
        LocalDate now = LocalDate.now();
        int currentYear = now.getYear();
        int currentMonth = now.getMonthValue();
        double total = 0.0;
        for (Transaction t : transactions) {
            if (t instanceof Expense) {
                LocalDate date = t.getDate();
                if (date.getYear() == currentYear && date.getMonthValue() == currentMonth) {
                    total += t.getAmount();
                }
            }
        }
        return total;
    }

    /**
     * Checks if the user is within their monthly budget.
     * 
     * @param transactions  List of transactions.
     * @param monthlyBudget The user's monthly budget limit.
     * @return true if expenses are less than or equal to budget (or budget is 0).
     */
    public static boolean isWithinBudget(List<Transaction> transactions, double monthlyBudget) {
        if (monthlyBudget == 0) {
            return true;
        }
        return calculateMonthlyExpenses(transactions) <= monthlyBudget;
    }

    /**
     * Calculates the difference between budget and actual expenses.
     * 
     * @param transactions  List of transactions.
     * @param monthlyBudget The monthly budget.
     * @return Positive value if under budget, negative if over budget.
     */
    public static double getBudgetDifference(List<Transaction> transactions, double monthlyBudget) {
        if (monthlyBudget == 0) {
            return 0;
        }
        return calculateMonthlyExpenses(transactions) - monthlyBudget;
    }

    /**
     * Calculates savings progress as a percentage of the goal.
     * 
     * @param transactions List of transactions.
     * @param savingsGoal  The total savings goal.
     * @return Percentage (0-100) of goal reached.
     */
    public static double calculateSavingsProgress(List<Transaction> transactions, double savingsGoal) {
        if (savingsGoal == 0) {
            return 0;
        }
        double[] totals = calculateTotals(transactions);
        double balance = totals[0] - totals[1];
        if (balance <= 0) {
            return 0;
        }
        double progress = (balance / savingsGoal) * 100;
        return Math.min(progress, 100);
    }

    /**
     * Calculates the remaining amount needed to reach the savings goal.
     * 
     * @param transactions List of transactions.
     * @param savingsGoal  The total savings goal.
     * @return Amount remaining; 0 if goal is reached.
     */
    public static double getRemainingForGoal(List<Transaction> transactions, double savingsGoal) {
        double[] totals = calculateTotals(transactions);
        double balance = totals[0] - totals[1];
        double remaining = savingsGoal - balance;
        return Math.max(remaining, 0);
    }

    /**
     * Formats a double value as a currency string (e.g., "$123.45").
     * 
     * @param amount The amount to format.
     * @return Formatted currency string.
     */
    public static String formatCurrency(double amount) {
        return String.format("$%.2f", amount);
    }

    /**
     * Filters transactions by their type (Income/Expense).
     * 
     * @param transactions List of transactions.
     * @param type         The type string to match.
     * @return Filtered list of transactions.
     */
    public static List<Transaction> filterByType(List<Transaction> transactions, String type) {
        return transactions.stream()
                .filter(t -> t.getType().equalsIgnoreCase(type))
                .collect(Collectors.toList());
    }

    /**
     * Filters expenses by category.
     * 
     * @param transactions List of transactions.
     * @param category     The category to search for.
     * @return Filtered list of expense transactions.
     */
    public static List<Transaction> filterByCategory(List<Transaction> transactions, String category) {
        return transactions.stream()
                .filter(t -> t instanceof Expense && ((Expense) t).getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }

    /**
     * Filters transactions within a specific date range.
     * 
     * @param transactions List of transactions.
     * @param startDate    The start date (inclusive).
     * @param endDate      The end date (inclusive).
     * @return Filtered list of transactions.
     */
    public static List<Transaction> filterByDateRange(List<Transaction> transactions, LocalDate startDate,
            LocalDate endDate) {
        return transactions.stream()
                .filter(t -> !t.getDate().isBefore(startDate) && !t.getDate().isAfter(endDate))
                .collect(Collectors.toList());
    }

    /**
     * Calculates total income for a specific month and year.
     * 
     * @param transactions List of transactions.
     * @param year         The year.
     * @param month        The month (1-12).
     * @return Total income for that month.
     */
    public static double calculateIncomeForMonth(List<Transaction> transactions, int year, int month) {
        return transactions.stream()
                .filter(t -> t instanceof Income)
                .filter(t -> t.getDate().getYear() == year && t.getDate().getMonthValue() == month)
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    /**
     * Calculates total expenses for a specific month and year.
     * 
     * @param transactions List of transactions.
     * @param year         The year.
     * @param month        The month (1-12).
     * @return Total expenses for that month.
     */
    public static double calculateExpensesForMonth(List<Transaction> transactions, int year, int month) {
        return transactions.stream()
                .filter(t -> t instanceof Expense)
                .filter(t -> t.getDate().getYear() == year && t.getDate().getMonthValue() == month)
                .mapToDouble(Transaction::getAmount)
                .sum();
    }
}
