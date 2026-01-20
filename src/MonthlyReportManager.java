import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Generates reports organized by month.
 */
public class MonthlyReportManager {

    /**
     * Extracts a list of unique months present in the transaction history.
     * 
     * @param transactions List of transactions.
     * @return Sorted list of YearMonth objects.
     */
    public static List<YearMonth> getAvailableMonths(List<Transaction> transactions) {
        return transactions.stream()
                .map(t -> YearMonth.from(t.getDate()))
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Filters transactions for a specific month.
     * 
     * @param transactions List of transactions.
     * @param yearMonth    The month to filter by.
     * @return List of transactions occurring in that month.
     */
    public static List<Transaction> getTransactionsForMonth(List<Transaction> transactions, YearMonth yearMonth) {
        return transactions.stream()
                .filter(t -> YearMonth.from(t.getDate()).equals(yearMonth))
                .collect(Collectors.toList());
    }

}
