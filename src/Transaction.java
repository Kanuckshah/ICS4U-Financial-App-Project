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
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setAmount(double amount) {
        if (amount > 0) {
            this.amount = amount;
        }
    }
    
    public void setDate(LocalDate date) {
        this.date = date;
    }
    
    public abstract String getType();
    
    public abstract String getCategoryOrSource();
    
    public String formatForDisplay() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return String.format("%-10s | %-20s | $%-10.2f | %-15s | %s",
            getType(),
            name,
            amount,
            getCategoryOrSource(),
            date.format(formatter));
    }
    
    public String formatForFile() {
        return String.format("%s|%s|%.2f|%s|%s",
            getType(),
            name,
            amount,
            getCategoryOrSource(),
            date.toString());
    }
}
