public class FinanceApp {
    private AuthManager auth = new AuthManager();

    public void start() {
        IO.println("=== Student Finance Tracker ===");
        if (!auth.isLoggedIn()) {
            String u = IO.readLine("Username: ");
            String p = IO.readLine("Password: ");
            auth.login(u, p);
        }
    }
}