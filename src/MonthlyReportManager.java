
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

public class MonthlyReportManager {

    public static List<YearMonth> getAvailableMonths(List<Transaction> transactions) {
        return transactions.stream()
                .map(t -> YearMonth.from(t.getDate()))
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    public static List<Transaction> getTransactionsForMonth(List<Transaction> transactions, YearMonth yearMonth) {
        return transactions.stream()
                .filter(t -> YearMonth.from(t.getDate()).equals(yearMonth))
                .collect(Collectors.toList());
    }

}
