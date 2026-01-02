import java.util.Scanner;

public class IO {
    private static Scanner scanner = new Scanner(System.in);

    public static void println() {
        System.out.println();
    }

    public static void println(String message) {
        System.out.println(message);
    }

    public static void print(String message) {
        System.out.print(message);
    }

    public static String readLine() {
        return scanner.nextLine();
    }

    public static String readLine(String prompt) {
        print(prompt);
        return scanner.nextLine();
    }

    public static double readDouble(String prompt) {
        while (true) {
            try {
                print(prompt);
                String input = scanner.nextLine();
                double value = Double.parseDouble(input);
                return value;
            } catch (NumberFormatException e) {
                println("Invalid input. Please enter a valid number.");
            }
        }
    }

    public static double readPositiveDouble(String prompt) {
        while (true) {
            double value = readDouble(prompt);
            if (value > 0) {
                return value;
            }
            println("Please enter a positive number.");
        }
    }

    public static double readNonNegativeDouble(String prompt) {
        while (true) {
            double value = readDouble(prompt);
            if (value >= 0) {
                return value;
            }
            println("Please enter a non-negative number.");
        }
    }

    public static int readInt(String prompt) {
        while (true) {
            try {
                print(prompt);
                String input = scanner.nextLine();
                int value = Integer.parseInt(input);
                return value;
            } catch (NumberFormatException e) {
                println("Invalid input. Please enter a valid integer.");
            }
        }
    }

    public static void printSeparator() {
        println("--------------------------------------");
    }
}