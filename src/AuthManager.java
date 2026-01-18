public class AuthManager {
    private User currentUser;

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public enum RegistrationResult {
        SUCCESS,
        USERNAME_TAKEN,
        INVALID_INPUT
    }

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

    public boolean login(String username, String password) {
        User user = FileManager.loadUser(username);

        if (user == null || !user.validatePassword(password)) {
            return false;
        }

        currentUser = user;
        return true;
    }

    public void logout() {
        if (currentUser != null) {
            FileManager.saveUser(currentUser);
            currentUser = null;
        }
    }
}
