import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a user of the application.
 * Stores personal information, financial goals, and a history of transactions.
 */
public class User {
    private String username;
    private String password;
    private List<Transaction> transactions;
    private double monthlyBudget;
    private double savingsGoal;
    private LocalDate savingsTargetDate;
    private int savingsTargetMonths;

    /**
     * Creates a new User with the specified credentials.
     * 
     * @param username The unique username.
     * @param password The user's password.
     */
    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.transactions = new ArrayList<>();
        this.monthlyBudget = 0.0;
        this.savingsGoal = 0.0;
        this.savingsTargetDate = null;
        this.savingsTargetMonths = 0;
    }

    public String getUsername() {
        return username;
    }

    /**
     * Validates if the provided password matches the user's password.
     * 
     * @param password The password to check.
     * @return true if the password matches, false otherwise.
     */
    public boolean validatePassword(String password) {
        return this.password.equals(password);
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    /**
     * Adds a new transaction to the user's history.
     * 
     * @param transaction The transaction to add.
     */
    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }

    public boolean removeTransaction(int index) {
        if (index >= 0 && index < transactions.size()) {
            transactions.remove(index);
            return true;
        }
        return false;
    }

    public boolean removeTransaction(Transaction transaction) {
        return transactions.remove(transaction);
    }

    public Transaction getTransaction(int index) {
        if (index >= 0 && index < transactions.size()) {
            return transactions.get(index);
        }
        return null;
    }

    public int getTransactionCount() {
        return transactions.size();
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

    public LocalDate getSavingsTargetDate() {
        return savingsTargetDate;
    }

    public void setSavingsTargetDate(LocalDate savingsTargetDate) {
        this.savingsTargetDate = savingsTargetDate;
    }

    public int getSavingsTargetMonths() {
        return savingsTargetMonths;
    }

    public void setSavingsTargetMonths(int savingsTargetMonths) {
        if (savingsTargetMonths >= 0) {
            this.savingsTargetMonths = savingsTargetMonths;
        }
    }

    public String getPassword() {
        return password;
    }
}
