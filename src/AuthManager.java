public class AuthManager {
    private User currentUser;

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public boolean register(String username, String password) {
        if (FileManager.userExists(username)) {
            IO.println("Username already exists. Please choose a different username.");
            return false;
        }

        if (username.trim().isEmpty()) {
            IO.println("Username cannot be empty.");
            return false;
        }

        if (password.trim().isEmpty()) {
            IO.println("Password cannot be empty.");
            return false;
        }

        User newUser = new User(username.trim(), password);

        if (FileManager.saveUser(newUser)) {
            currentUser = newUser;
            IO.println("Registration successful! You are now logged in.");
            return true;
        } else {
            IO.println("Registration failed. Please try again.");
            return false;
        }
    }

    public boolean login(String username, String password) {
        User user = FileManager.loadUser(username);

        if (user == null) {
            IO.println("User not found. Please check your username or register.");
            return false;
        }

        if (!user.validatePassword(password)) {
            IO.println("Incorrect password. Please try again.");
            return false;
        }

        currentUser = user;
        IO.println("Login successful! Welcome back, " + username + "!");
        return true;
    }

    public void logout() {
        if (currentUser != null) {
            String username = currentUser.getUsername();
            FileManager.saveUser(currentUser);
            IO.println("Data saved. Goodbye, " + username + "!");
            currentUser = null;
        }
    }
}