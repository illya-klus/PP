package domain.users;

public class UserSession {
    private static final UserSession instance = new UserSession();
    private User currentUser;

    private UserSession() {}

    public static UserSession getInstance() {
        return instance;
    }

    public void login(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null when logging in.");
        }
        this.currentUser = user;
        System.out.println("User logged in: " + user.getLogin());
    }

    public void logout() {
        if (this.currentUser != null) {
            System.out.println("User logged out: " + this.currentUser.getLogin());
        }
        this.currentUser = null;
    }

    public User getCurrentUser()  {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }
}