import java.util.Scanner;

public class IO {
    private static Scanner scanner = new Scanner(System.in);

    public static void println(String m) {
        System.out.println(m);
    }

    public static void print(String m) {
        System.out.print(m);
    }

    public static String readLine(String prompt) {
        print(prompt);
        return scanner.nextLine();
    }
}