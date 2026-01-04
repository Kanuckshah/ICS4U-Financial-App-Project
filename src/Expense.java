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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public static Expense fromFileString(String fileString) {
        String[] parts = fileString.split("\\|");
        if (parts.length >= 5) {
            String name = parts[1];
            double amount = Double.parseDouble(parts[2]);
            String category = parts[3];
            LocalDate date = LocalDate.parse(parts[4]);
            return new Expense(name, amount, category, date);
        }
        return null;
    }
}