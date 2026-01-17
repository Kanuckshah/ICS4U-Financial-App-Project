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
            return false;
        }
        
        if (username.trim().isEmpty() || password.trim().isEmpty()) {
            return false;
        }
        
        User newUser = new User(username.trim(), password);
        
        if (FileManager.saveUser(newUser)) {
            currentUser = newUser;
            return true;
        }
        return false;
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
