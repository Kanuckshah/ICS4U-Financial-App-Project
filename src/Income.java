import java.time.LocalDate;

public class Income extends Transaction {
    private String source;

    public Income(String name, double amount, String source, LocalDate date) {
        super(name, amount, date);
        this.source = source;
    }

    @Override
    public String getType() {
        return "Income";
    }

    @Override
    public String getCategoryOrSource() {
        return source;
    }
}