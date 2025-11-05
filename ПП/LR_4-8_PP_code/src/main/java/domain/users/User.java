package domain.users;

public class User {
    private final int userId;
    private final String login;
    private final boolean isAdmin;

    public User(int userId, String login, boolean isAdmin) {
        this.userId = userId;
        this.login = login;
        this.isAdmin = isAdmin;
    }

    public int getUserId() {
        return userId;
    }

    public String getLogin() {
        return login;
    }

    public boolean isAdmin() {
        return isAdmin;
    }
}