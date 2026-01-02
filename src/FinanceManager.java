import java.time.LocalDate;
import java.util.List;

public class FinanceManager {

    public static double calculateTotalIncome(List<Transaction> transactions) {
        return transactions.stream()
                .filter(t -> t instanceof Income)
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    public static double calculateTotalExpenses(List<Transaction> transactions) {
        return transactions.stream()
                .filter(t -> t instanceof Expense)
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    public static double calculateBalance(List<Transaction> transactions) {
        return calculateTotalIncome(transactions) - calculateTotalExpenses(transactions);
    }

    public static double calculateMonthlyExpenses(List<Transaction> transactions) {
        LocalDate now = LocalDate.now();
        return transactions.stream()
                .filter(t -> t instanceof Expense)
                .filter(t -> t.getDate().getYear() == now.getYear() &&
                        t.getDate().getMonth() == now.getMonth())
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    public static boolean isWithinBudget(List<Transaction> transactions, double monthlyBudget) {
        if (monthlyBudget == 0) {
            return true;
        }
        return calculateMonthlyExpenses(transactions) <= monthlyBudget;
    }

    public static double getBudgetDifference(List<Transaction> transactions, double monthlyBudget) {
        if (monthlyBudget == 0) {
            return 0;
        }
        return calculateMonthlyExpenses(transactions) - monthlyBudget;
    }

    public static double calculateSavingsProgress(List<Transaction> transactions, double savingsGoal) {
        if (savingsGoal == 0) {
            return 0;
        }
        double balance = calculateBalance(transactions);
        if (balance <= 0) {
            return 0;
        }
        double progress = (balance / savingsGoal) * 100;
        return Math.min(progress, 100);
    }

    public static double getRemainingForGoal(List<Transaction> transactions, double savingsGoal) {
        double balance = calculateBalance(transactions);
        double remaining = savingsGoal - balance;
        return Math.max(remaining, 0);
    }

    public static String formatCurrency(double amount) {
        return String.format("$%.2f", amount);
    }
}