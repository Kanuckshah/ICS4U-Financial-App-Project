public class AuthManager {
    private User currentUser;

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public void login(String u, String p) {
        currentUser = new User(u, p); // Simplified for stage 3
        IO.println("Welcome, " + u);
    }

    public User getCurrentUser() {
        return currentUser;
    }
}