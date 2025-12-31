import java.time.LocalDate;

public class Expense extends Transaction {
    private String category;

    public Expense(String name, double amount, String category, LocalDate date) {
        super(name, amount, date);
        this.category = category;
    }

    @Override
    public String getType() {
        return "Expense";
    }

    @Override
    public String getCategoryOrSource() {
        return category;
    }
}