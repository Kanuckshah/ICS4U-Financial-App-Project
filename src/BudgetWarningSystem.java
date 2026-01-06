import java.util.List;

public class BudgetWarningSystem {

    public static boolean isBudgetWarningNeeded(List<Transaction> transactions, double monthlyBudget) {
        if (monthlyBudget == 0) {
            return false;
        }
        double monthlyExpenses = FinanceManager.calculateMonthlyExpenses(transactions);
        double percentageUsed = (monthlyExpenses / monthlyBudget) * 100;
        return percentageUsed >= 75;
    }
}
