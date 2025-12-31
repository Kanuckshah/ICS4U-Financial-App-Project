import java.util.List;

public class FinanceManager {
    public static double calculateBalance(List<Transaction> transactions) {
        double balance = 0;
        for (Transaction t : transactions) {
            if (t instanceof Income)
                balance += t.getAmount();
            else
                balance -= t.getAmount();
        }
        return balance;
    }

    public static String formatCurrency(double amount) {
        return String.format("$%.2f", amount);
    }
}