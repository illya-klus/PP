package domain.users;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

public class UserSessionTest {

    private UserSession session;
    private User testUser;
    private User adminUser;

    @BeforeEach
    public void setUp() {
        session = UserSession.getInstance();
        session.logout(); // Очищаємо сесію перед кожним тестом

        testUser = new User(1, "john_doe", "password123", false);
        adminUser = new User(2, "admin", "adminpass", true);
    }

    @Test
    public void getInstanceTest() {
        UserSession instance1 = UserSession.getInstance();
        UserSession instance2 = UserSession.getInstance();

        assertNotNull(instance1);
        assertNotNull(instance2);
        assertSame(instance1, instance2); // Перевірка що це Singleton
    }
    @Test
    public void loginTest() {
        session.login(testUser);

        assertTrue(session.isLoggedIn());
        assertEquals(testUser, session.getCurrentUser());
        assertEquals("john_doe", session.getCurrentUser().getLogin());
    }
    @Test
    public void loginWithAdminUserTest() {
        session.login(adminUser);

        assertTrue(session.isLoggedIn());
        assertEquals(adminUser, session.getCurrentUser());
        assertTrue(session.getCurrentUser().isAdmin());
    }
    @Test
    public void loginWithNullUserTest() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> session.login(null)
        );

        assertEquals("User cannot be null when logging in.", exception.getMessage());
        assertFalse(session.isLoggedIn());
        assertNull(session.getCurrentUser());
    }
    @Test
    public void logoutTest() {
        session.login(testUser);
        assertTrue(session.isLoggedIn());

        session.logout();

        assertFalse(session.isLoggedIn());
        assertNull(session.getCurrentUser());
    }
    @Test
    public void logoutWhenNotLoggedInTest() {
        assertFalse(session.isLoggedIn());

        // Не повинно бути помилки при logout без користувача
        assertDoesNotThrow(() -> session.logout());
        assertFalse(session.isLoggedIn());
        assertNull(session.getCurrentUser());
    }
    @Test
    public void isLoggedInTest() {
        assertFalse(session.isLoggedIn());

        session.login(testUser);
        assertTrue(session.isLoggedIn());

        session.logout();
        assertFalse(session.isLoggedIn());
    }
    @Test
    public void getCurrentUserWhenLoggedInTest() {
        session.login(testUser);

        User currentUser = session.getCurrentUser();
        assertNotNull(currentUser);
        assertEquals(testUser.getUserId(), currentUser.getUserId());
        assertEquals(testUser.getLogin(), currentUser.getLogin());
    }
    @Test
    public void getCurrentUserWhenNotLoggedInTest() {
        assertNull(session.getCurrentUser());
    }
    @Test
    public void multipleLoginLogoutTest() {
        // Перший логін
        session.login(testUser);
        assertEquals(testUser, session.getCurrentUser());

        // Логаут
        session.logout();
        assertNull(session.getCurrentUser());

        // Логін іншого користувача
        session.login(adminUser);
        assertEquals(adminUser, session.getCurrentUser());

        // Ще один логаут
        session.logout();
        assertNull(session.getCurrentUser());
    }
    @Test
    public void sessionPersistenceTest() {
        session.login(testUser);

        // Отримуємо новий інстанс (має бути той же самий)
        UserSession newInstance = UserSession.getInstance();

        // Перевіряємо що користувач зберігся
        assertEquals(testUser, newInstance.getCurrentUser());
        assertTrue(newInstance.isLoggedIn());
    }
}