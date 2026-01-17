import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoryReportManager {
    
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
