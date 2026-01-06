import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class SavingsPlanner {

    public static double calculateRequiredMonthlySavings(double savingsGoal, LocalDate targetDate) {
        if (targetDate == null || targetDate.isBefore(LocalDate.now())) {
            return 0;
        }

        long monthsBetween = ChronoUnit.MONTHS.between(LocalDate.now().withDayOfMonth(1), targetDate.withDayOfMonth(1));

        if (monthsBetween <= 0) {
            return savingsGoal;
        }

        return savingsGoal / monthsBetween;
    }

    public static double calculateRequiredMonthlySavings(double savingsGoal, int targetMonths) {
        if (targetMonths <= 0) {
            return savingsGoal;
        }
        return savingsGoal / targetMonths;
    }

    public static boolean isOnTrack(List<Transaction> transactions, double savingsGoal, LocalDate targetDate,
            int targetMonths) {
        if (savingsGoal == 0) {
            return false;
        }

        double currentBalance = FinanceManager.calculateBalance(transactions);
        double requiredMonthly;

        if (targetDate != null) {
            requiredMonthly = calculateRequiredMonthlySavings(savingsGoal, targetDate);
        } else if (targetMonths > 0) {
            requiredMonthly = calculateRequiredMonthlySavings(savingsGoal, targetMonths);
        } else {
            return false;
        }

        LocalDate now = LocalDate.now();
        double currentMonthlySavings = FinanceManager.calculateTotalIncome(transactions) -
                FinanceManager.calculateMonthlyExpenses(transactions);

        if (targetDate != null) {
            long monthsElapsed = ChronoUnit.MONTHS.between(
                    transactions.stream().map(Transaction::getDate).min(LocalDate::compareTo).orElse(now),
                    now) + 1;
            if (monthsElapsed <= 0)
                monthsElapsed = 1;
            double expectedBalance = requiredMonthly * monthsElapsed;
            return currentBalance >= expectedBalance * 0.9;
        } else {
            return currentMonthlySavings >= requiredMonthly * 0.9;
        }
    }

}
