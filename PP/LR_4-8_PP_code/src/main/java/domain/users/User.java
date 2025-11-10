package domain.users;

public class User {
    private final int userId;
    private final String login;
    private final boolean isAdmin;
    private String password = "";

    public User(int userId, String login, boolean isAdmin) {
        this.userId = userId;
        this.login = login;
        this.isAdmin = isAdmin;
    }

    public User(int userId, String login, String password, boolean isAdmin) {
        this.userId = userId;
        this.login = login;
        this.isAdmin = isAdmin;
        this.password = password;
    }

    public String getPassword() {
        return password;
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