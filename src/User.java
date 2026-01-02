import java.util.ArrayList;
import java.util.List;

public class User {
    private String username;
    private String password;
    private List<Transaction> transactions;
    private double monthlyBudget;
    private double savingsGoal;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.transactions = new ArrayList<>();
        this.monthlyBudget = 0.0;
        this.savingsGoal = 0.0;
    }

    public String getUsername() {
        return username;
    }

    public boolean validatePassword(String password) {
        return this.password.equals(password);
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }

    public double getMonthlyBudget() {
        return monthlyBudget;
    }

    public void setMonthlyBudget(double monthlyBudget) {
        if (monthlyBudget >= 0) {
            this.monthlyBudget = monthlyBudget;
        }
    }

    public double getSavingsGoal() {
        return savingsGoal;
    }

    public void setSavingsGoal(double savingsGoal) {
        if (savingsGoal >= 0) {
            this.savingsGoal = savingsGoal;
        }
    }

    public String getPassword() {
        return password;
    }
}