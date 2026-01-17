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
    
    public String getSource() {
        return source;
    }
    
    public void setSource(String source) {
        this.source = source;
    }
    
    public static Income fromFileString(String fileString) {
        String[] parts = fileString.split("\\|");
        if (parts.length >= 5) {
            String name = parts[1];
            double amount = Double.parseDouble(parts[2]);
            String source = parts[3];
            LocalDate date = LocalDate.parse(parts[4]);
            return new Income(name, amount, source, date);
        }
        return null;
    }
}
