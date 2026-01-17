import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

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
        double[] totals = calculateTotals(transactions);
        return totals[0] - totals[1];
    }
    
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
        return new double[]{income, expenses};
    }
    
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
        double[] totals = calculateTotals(transactions);
        double balance = totals[0] - totals[1];
        if (balance <= 0) {
            return 0;
        }
        double progress = (balance / savingsGoal) * 100;
        return Math.min(progress, 100);
    }
    
    public static double getRemainingForGoal(List<Transaction> transactions, double savingsGoal) {
        double[] totals = calculateTotals(transactions);
        double balance = totals[0] - totals[1];
        double remaining = savingsGoal - balance;
        return Math.max(remaining, 0);
    }
    
    public static String formatCurrency(double amount) {
        return String.format("$%.2f", amount);
    }
    
    public static List<Transaction> filterByType(List<Transaction> transactions, String type) {
        return transactions.stream()
            .filter(t -> t.getType().equalsIgnoreCase(type))
            .collect(Collectors.toList());
    }
    
    public static List<Transaction> filterByCategory(List<Transaction> transactions, String category) {
        return transactions.stream()
            .filter(t -> t instanceof Expense && ((Expense) t).getCategory().equalsIgnoreCase(category))
            .collect(Collectors.toList());
    }
    
    public static List<Transaction> filterByDateRange(List<Transaction> transactions, LocalDate startDate, LocalDate endDate) {
        return transactions.stream()
            .filter(t -> !t.getDate().isBefore(startDate) && !t.getDate().isAfter(endDate))
            .collect(Collectors.toList());
    }
    
    public static double calculateIncomeForMonth(List<Transaction> transactions, int year, int month) {
        return transactions.stream()
            .filter(t -> t instanceof Income)
            .filter(t -> t.getDate().getYear() == year && t.getDate().getMonthValue() == month)
            .mapToDouble(Transaction::getAmount)
            .sum();
    }
    
    public static double calculateExpensesForMonth(List<Transaction> transactions, int year, int month) {
        return transactions.stream()
            .filter(t -> t instanceof Expense)
            .filter(t -> t.getDate().getYear() == year && t.getDate().getMonthValue() == month)
            .mapToDouble(Transaction::getAmount)
            .sum();
    }
}
