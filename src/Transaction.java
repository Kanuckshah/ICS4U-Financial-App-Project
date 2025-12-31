import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public abstract class Transaction {
    protected String name;
    protected double amount;
    protected LocalDate date;

    public Transaction(String name, double amount, LocalDate date) {
        this.name = name;
        this.amount = amount;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public abstract String getType();

    public abstract String getCategoryOrSource();

    public String formatForDisplay() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return String.format("%-10s | %-20s | $%-10.2f | %-15s | %s",
                getType(), name, amount, getCategoryOrSource(), date.format(formatter));
    }
}