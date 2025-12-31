import java.io.*;
import java.util.ArrayList;

public class FileManager {
    private static final String DATA_DIR = "user_data/";

    public static boolean saveUser(User user) {
        new File(DATA_DIR).mkdirs();
        try (PrintWriter writer = new PrintWriter(new FileWriter(DATA_DIR + user.getUsername() + ".txt"))) {
            writer.println("USERNAME:" + user.getUsername());
            writer.println("PASSWORD:" + user.getPassword());
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}