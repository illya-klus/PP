package domain.users;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


public class UserTest {
    int userId = 1;
    String login = "john_doe";
    String password = "securepassword123";
    boolean isAdmin = false;

    // Тестовий об'єкт з паролем
    User userWithPassword = new User(userId, login, password, isAdmin);

    // Тестовий об'єкт без пароля
    User userWithoutPassword = new User(userId, login, isAdmin);

    @Test
    public void getUserIdTest() {
        assertEquals(userId, userWithPassword.getUserId());
        assertEquals(userId, userWithoutPassword.getUserId());
    }

    @Test
    public void getLoginTest() {
        assertEquals(login, userWithPassword.getLogin());
        assertEquals(login, userWithoutPassword.getLogin());
    }

    @Test
    public void isAdminTest() {
        assertFalse(userWithPassword.isAdmin());
        assertFalse(userWithoutPassword.isAdmin());
    }

    @Test
    public void getPasswordWithPasswordTest() {
        assertEquals(password, userWithPassword.getPassword());
    }

    @Test
    public void getPasswordWithoutPasswordTest() {
        assertEquals("", userWithoutPassword.getPassword());
    }

    @Test
    public void userWithAdminTest() {
        User adminUser = new User(2, "admin", "adminpass", true);
        assertTrue(adminUser.isAdmin());
    }

    @Test
    public void userEqualityTest() {
        User user1 = new User(1, "user1", false);
        User user2 = new User(1, "user1", false);
        User user3 = new User(2, "user2", true);

        // Перевірка що однакові userId, login, isAdmin
        assertEquals(user1.getUserId(), user2.getUserId());
        assertEquals(user1.getLogin(), user2.getLogin());
        assertEquals(user1.isAdmin(), user2.isAdmin());

        // Перевірка що різні користувачі
        assertNotEquals(user1.getUserId(), user3.getUserId());
    }

    @Test
    public void userWithDifferentPasswordsTest() {
        User user1 = new User(1, "user", "pass1", false);
        User user2 = new User(1, "user", "pass2", false);

        assertEquals(user1.getUserId(), user2.getUserId());
        assertEquals(user1.getLogin(), user2.getLogin());
        assertNotEquals(user1.getPassword(), user2.getPassword());
    }
}