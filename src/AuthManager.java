/**
 * Manages user authentication, including login, registration, and session
 * management.
 */
public class AuthManager {
    private User currentUser;

    /**
     * Retrieves the currently logged-in user.
     * 
     * @return The current User object, or null if no user is logged in.
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Checks if a user is currently logged in.
     * 
     * @return true if a user is logged in, false otherwise.
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }

    /**
     * Result of a registration attempt.
     */
    public enum RegistrationResult {
        SUCCESS,
        USERNAME_TAKEN,
        INVALID_INPUT
    }

    /**
     * Attempts to register a new user.
     * 
     * @param username The desired username (must be unique).
     * @param password The desired password (non-empty).
     * @return RegistrationResult indicating success or specific failure reason.
     */
    public RegistrationResult register(String username, String password) {
        if (username.trim().isEmpty() || password.trim().isEmpty()) {
            return RegistrationResult.INVALID_INPUT;
        }

        if (FileManager.userExists(username)) {
            return RegistrationResult.USERNAME_TAKEN;
        }

        User newUser = new User(username.trim(), password);

        if (FileManager.saveUser(newUser)) {
            currentUser = newUser;
            return RegistrationResult.SUCCESS;
        }
        return RegistrationResult.INVALID_INPUT; // Fallback for save error
    }

    /**
     * Attempts to log in an existing user.
     * 
     * @param username The username.
     * @param password The password.
     * @return true if credentials are valid, false otherwise.
     */
    public boolean login(String username, String password) {
        User user = FileManager.loadUser(username);

        if (user == null || !user.validatePassword(password)) {
            return false;
        }

        currentUser = user;
        return true;
    }

    /**
     * Logs out the current user and saves their data.
     */
    public void logout() {
        if (currentUser != null) {
            FileManager.saveUser(currentUser);
            currentUser = null;
        }
    }
}
