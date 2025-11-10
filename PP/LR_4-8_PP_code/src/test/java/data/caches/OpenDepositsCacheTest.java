package data.caches;


import domain.deposits.Deposit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class OpenDepositsCacheTest {

    private OpenDepositsCache cache;

    @BeforeEach
    public void setUp() {
        cache = OpenDepositsCache.getInstance();
        cache.clear(); // Очищаємо перед кожним тестом
    }

    @Test
    public void getInstanceTest() {
        OpenDepositsCache instance1 = OpenDepositsCache.getInstance();
        OpenDepositsCache instance2 = OpenDepositsCache.getInstance();

        assertNotNull(instance1);
        assertNotNull(instance2);
        assertSame(instance1, instance2); // Перевірка Singleton
    }
    @Test
    public void getOpenDepositsWhenEmptyTest() {
        List<Deposit> deposits = cache.getOpenDeposits();

        assertNotNull(deposits);
        assertTrue(deposits.isEmpty());
    }
    @Test
    public void getOpenDepositsAfterAddingTest() {
        Deposit testDeposit = createTestDeposit();
        cache.getOpenDeposits().add(testDeposit);

        List<Deposit> deposits = cache.getOpenDeposits();

        assertNotNull(deposits);
        assertFalse(deposits.isEmpty());
        assertEquals(1, deposits.size());
        assertEquals(testDeposit, deposits.get(0));
    }
    @Test
    public void clearTest() {
        // Додаємо тестові дані
        Deposit testDeposit = createTestDeposit();
        cache.getOpenDeposits().add(testDeposit);

        assertFalse(cache.getOpenDeposits().isEmpty());

        cache.clear();

        assertTrue(cache.getOpenDeposits().isEmpty());
    }
    @Test
    public void clearEmptyCacheTest() {
        assertTrue(cache.getOpenDeposits().isEmpty());

        // Не повинно бути помилки при очищенні пустого кешу
        assertDoesNotThrow(() -> cache.clear());
        assertTrue(cache.getOpenDeposits().isEmpty());
    }
    @Test
    public void loadOpenDepositsWhenCacheNotEmptyTest() {
        // Додаємо тестові дані в кеш
        Deposit cachedDeposit = createTestDeposit();
        cache.getOpenDeposits().add(cachedDeposit);

        List<Deposit> result = cache.loadOpenDeposits();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(cachedDeposit, result.get(0));
    }
    @Test
    public void multipleAdditionsTest() {
        Deposit deposit1 = createTestDeposit(1, "Депозит 1");
        Deposit deposit2 = createTestDeposit(2, "Депозит 2");
        Deposit deposit3 = createTestDeposit(3, "Депозит 3");

        cache.getOpenDeposits().add(deposit1);
        cache.getOpenDeposits().add(deposit2);
        cache.getOpenDeposits().add(deposit3);

        List<Deposit> deposits = cache.getOpenDeposits();

        assertEquals(3, deposits.size());
        assertEquals("Депозит 1", deposits.get(0).getName());
        assertEquals("Депозит 2", deposits.get(1).getName());
        assertEquals("Депозит 3", deposits.get(2).getName());
    }
    @Test
    public void cachePersistenceBetweenInstancesTest() {
        // Додаємо дані в кеш
        Deposit testDeposit = createTestDeposit();
        cache.getOpenDeposits().add(testDeposit);

        // Отримуємо новий інстанс (має бути той же самий)
        OpenDepositsCache newInstance = OpenDepositsCache.getInstance();

        // Перевіряємо що дані збереглися
        assertFalse(newInstance.getOpenDeposits().isEmpty());
        assertEquals(1, newInstance.getOpenDeposits().size());
        assertEquals(testDeposit, newInstance.getOpenDeposits().get(0));
    }
    @Test
    public void getOpenDepositsReturnsSameListTest() {
        List<Deposit> deposits1 = cache.getOpenDeposits();
        List<Deposit> deposits2 = cache.getOpenDeposits();

        assertSame(deposits1, deposits2); // Має повертати той же самий об'єкт списку
    }
    @Test
    public void addAndRemoveFromCacheTest() {
        Deposit deposit1 = createTestDeposit(1, "Депозит 1");
        Deposit deposit2 = createTestDeposit(2, "Депозит 2");

        // Додаємо
        cache.getOpenDeposits().add(deposit1);
        cache.getOpenDeposits().add(deposit2);
        assertEquals(2, cache.getOpenDeposits().size());

        // Видаляємо один
        cache.getOpenDeposits().remove(deposit1);
        assertEquals(1, cache.getOpenDeposits().size());
        assertEquals(deposit2, cache.getOpenDeposits().get(0));

        // Очищаємо всі
        cache.clear();
        assertTrue(cache.getOpenDeposits().isEmpty());
    }
    @Test
    public void singletonAfterClearTest() {
        // Додаємо дані
        cache.getOpenDeposits().add(createTestDeposit());

        // Очищаємо
        cache.clear();

        // Перевіряємо що інстанс залишився тим же
        OpenDepositsCache newInstance = OpenDepositsCache.getInstance();
        assertSame(cache, newInstance);
        assertTrue(newInstance.getOpenDeposits().isEmpty());
    }

    // Допоміжний метод для створення тестового депозиту
    private Deposit createTestDeposit() {
        return createTestDeposit(1, "Тестовий депозит");
    }

    private Deposit createTestDeposit(int id, String name) {
        return new Deposit(
                id, 1, name, 10.0, 12, 1000.0,
                true, false, "UAH", "Опис депозиту",
                "Тест банк", "https://test.bank", "Тест адреса", "123456789",
                100, 5000.0, "2024-01-01", "2024-12-31"
        );
    }
}
