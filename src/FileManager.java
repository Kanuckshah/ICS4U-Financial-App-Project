import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FileManager {
    private static final String DATA_DIR = "user_data/";

    private static void ensureDataDirectory() {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    private static String getUserFilePath(String username) {
        return DATA_DIR + username + ".txt";
    }

    public static boolean saveUser(User user) {
        ensureDataDirectory();
        String filePath = getUserFilePath(user.getUsername());

        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.println("USERNAME:" + user.getUsername());
            writer.println("PASSWORD:" + user.getPassword());
            writer.println("BUDGET:" + user.getMonthlyBudget());
            writer.println("SAVINGS_GOAL:" + user.getSavingsGoal());
            if (user.getSavingsTargetDate() != null) {
                writer.println("SAVINGS_TARGET_DATE:" + user.getSavingsTargetDate().toString());
            }
            if (user.getSavingsTargetMonths() > 0) {
                writer.println("SAVINGS_TARGET_MONTHS:" + user.getSavingsTargetMonths());
            }
            writer.println("TRANSACTIONS_START");

            for (Transaction transaction : user.getTransactions()) {
                writer.println(transaction.formatForFile());
            }

            writer.println("TRANSACTIONS_END");
            return true;
        } catch (IOException e) {
            System.err.println("Error saving user data: " + e.getMessage());
            return false;
        }
    }

    public static User loadUser(String username) {
        String filePath = getUserFilePath(username);
        File file = new File(filePath);

        if (!file.exists()) {
            return null;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            String password = null;
            double budget = 0.0;
            double savingsGoal = 0.0;
            LocalDate savingsTargetDate = null;
            int savingsTargetMonths = 0;
            List<Transaction> transactions = new ArrayList<>();
            boolean inTransactions = false;

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("USERNAME:")) {
                } else if (line.startsWith("PASSWORD:")) {
                    password = line.substring("PASSWORD:".length());
                } else if (line.startsWith("BUDGET:")) {
                    budget = Double.parseDouble(line.substring("BUDGET:".length()));
                } else if (line.startsWith("SAVINGS_GOAL:")) {
                    savingsGoal = Double.parseDouble(line.substring("SAVINGS_GOAL:".length()));
                } else if (line.startsWith("SAVINGS_TARGET_DATE:")) {
                    try {
                        savingsTargetDate = LocalDate.parse(line.substring("SAVINGS_TARGET_DATE:".length()));
                    } catch (Exception e) {
                    }
                } else if (line.startsWith("SAVINGS_TARGET_MONTHS:")) {
                    try {
                        savingsTargetMonths = Integer.parseInt(line.substring("SAVINGS_TARGET_MONTHS:".length()));
                    } catch (Exception e) {
                    }
                } else if (line.equals("TRANSACTIONS_START")) {
                    inTransactions = true;
                } else if (line.equals("TRANSACTIONS_END")) {
                    inTransactions = false;
                } else if (inTransactions && !line.trim().isEmpty()) {
                    Transaction transaction = parseTransaction(line);
                    if (transaction != null) {
                        transactions.add(transaction);
                    }
                }
            }

            if (password != null) {
                User user = new User(username, password);
                user.setMonthlyBudget(budget);
                user.setSavingsGoal(savingsGoal);
                if (savingsTargetDate != null) {
                    user.setSavingsTargetDate(savingsTargetDate);
                }
                if (savingsTargetMonths > 0) {
                    user.setSavingsTargetMonths(savingsTargetMonths);
                }
                for (Transaction transaction : transactions) {
                    user.addTransaction(transaction);
                }
                return user;
            }
        } catch (IOException e) {
            System.err.println("Error loading user data: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Error parsing user data: " + e.getMessage());
        }

        return null;
    }

    private static Transaction parseTransaction(String line) {
        if (line.startsWith("Income|")) {
            return Income.fromFileString(line);
        } else if (line.startsWith("Expense|")) {
            return Expense.fromFileString(line);
        }
        return null;
    }

    public static boolean userExists(String username) {
        String filePath = getUserFilePath(username);
        File file = new File(filePath);
        return file.exists();
    }
}