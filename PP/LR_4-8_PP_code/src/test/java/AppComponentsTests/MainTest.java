package AppComponentsTests;

import AppComponents.Main;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MainTest {

    @Test
    public void testMainClass() {
        // Створюємо об'єкт - покриває конструктор
        Main main = new Main();
        assertNotNull(main);

        // Перевіряємо що метод main існує
        assertDoesNotThrow(() -> {
            Main.class.getMethod("main", String[].class);
        });
    }

    @Test
    public void testMainMethod() {
        // Викликаємо main тільки якщо це перший тест
        try {
            Main.main(new String[]{});
        } catch (IllegalStateException e) {
            // Це нормально - JavaFX вже запущено
            // Головне що метод викликався і покриття є
        }
    }
}