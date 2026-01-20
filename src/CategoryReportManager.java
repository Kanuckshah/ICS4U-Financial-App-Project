import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Generates reports based on transaction categories.
 */
public class CategoryReportManager {

    /**
     * Aggregates expenses by category.
     * 
     * @param transactions List of transactions to analyze.
     * @return A map where keys are category names and values are total amounts.
     */
    public static Map<String, Double> getCategoryBreakdown(List<Transaction> transactions) {
        Map<String, Double> categoryMap = new HashMap<>();

        for (Transaction transaction : transactions) {
            if (transaction instanceof Expense) {
                Expense expense = (Expense) transaction;
                String category = expense.getCategory();
                categoryMap.put(category, categoryMap.getOrDefault(category, 0.0) + expense.getAmount());
            }
        }

        return categoryMap;
    }

}
