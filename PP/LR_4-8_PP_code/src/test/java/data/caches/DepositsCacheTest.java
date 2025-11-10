package data.caches;

import domain.deposits.Deposit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class DepositsCacheTest {

    private DepositsCache cache;

    @BeforeEach
    public void setUp() {
        cache = DepositsCache.getInstance();
        // Очищаємо кеш перед кожним тестом
        cache.getDeposits().clear();
    }
    @Test
    public void getInstanceTest() {
        DepositsCache instance1 = DepositsCache.getInstance();
        DepositsCache instance2 = DepositsCache.getInstance();

        assertNotNull(instance1);
        assertNotNull(instance2);
        assertSame(instance1, instance2); // Перевірка Singleton
    }
    @Test
    public void getDepositsWhenEmptyTest() {
        List<Deposit> deposits = cache.getDeposits();

        assertNotNull(deposits);
        assertTrue(deposits.isEmpty());
    }
    @Test
    public void getDepositsAfterAddingTest() {
        Deposit testDeposit = createTestDeposit();
        cache.getDeposits().add(testDeposit);

        List<Deposit> deposits = cache.getDeposits();

        assertNotNull(deposits);
        assertFalse(deposits.isEmpty());
        assertEquals(1, deposits.size());
        assertEquals(testDeposit, deposits.get(0));
    }
    @Test
    public void loadDepositsWhenCacheNotEmptyTest() {
        // Додаємо тестові дані в кеш
        Deposit cachedDeposit = createTestDeposit();
        cache.getDeposits().add(cachedDeposit);

        List<Deposit> result = cache.loadDeposits(10);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(cachedDeposit, result.get(0));
    }
    @Test
    public void multipleAdditionsTest() {
        Deposit deposit1 = createTestDeposit(1, "Депозит 1", 10.0);
        Deposit deposit2 = createTestDeposit(2, "Депозит 2", 12.0);
        Deposit deposit3 = createTestDeposit(3, "Депозит 3", 15.0);

        cache.getDeposits().add(deposit1);
        cache.getDeposits().add(deposit2);
        cache.getDeposits().add(deposit3);

        List<Deposit> deposits = cache.getDeposits();

        assertEquals(3, deposits.size());
        assertEquals("Депозит 1", deposits.get(0).getName());
        assertEquals("Депозит 2", deposits.get(1).getName());
        assertEquals("Депозит 3", deposits.get(2).getName());
        assertEquals(10.0, deposits.get(0).getInterestRate());
        assertEquals(12.0, deposits.get(1).getInterestRate());
        assertEquals(15.0, deposits.get(2).getInterestRate());
    }
    @Test
    public void cachePersistenceBetweenInstancesTest() {
        // Додаємо дані в кеш
        Deposit testDeposit = createTestDeposit();
        cache.getDeposits().add(testDeposit);

        // Отримуємо новий інстанс (має бути той же самий)
        DepositsCache newInstance = DepositsCache.getInstance();

        // Перевіряємо що дані збереглися
        assertFalse(newInstance.getDeposits().isEmpty());
        assertEquals(1, newInstance.getDeposits().size());
        assertEquals(testDeposit, newInstance.getDeposits().get(0));
    }
    @Test
    public void getDepositsReturnsSameListTest() {
        List<Deposit> deposits1 = cache.getDeposits();
        List<Deposit> deposits2 = cache.getDeposits();

        assertSame(deposits1, deposits2); // Має повертати той же самий об'єкт списку
    }
    @Test
    public void addAndRemoveFromCacheTest() {
        Deposit deposit1 = createTestDeposit(1, "Депозит 1", 10.0);
        Deposit deposit2 = createTestDeposit(2, "Депозит 2", 12.0);

        // Додаємо
        cache.getDeposits().add(deposit1);
        cache.getDeposits().add(deposit2);
        assertEquals(2, cache.getDeposits().size());

        // Видаляємо один
        cache.getDeposits().remove(deposit1);
        assertEquals(1, cache.getDeposits().size());
        assertEquals(deposit2, cache.getDeposits().get(0));
    }
    @Test
    public void singletonAfterOperationsTest() {
        // Додаємо дані
        cache.getDeposits().add(createTestDeposit());

        // Перевіряємо що інстанс залишився тим же
        DepositsCache newInstance = DepositsCache.getInstance();
        assertSame(cache, newInstance);
        assertFalse(newInstance.getDeposits().isEmpty());
    }
    @Test
    public void loadDepositsWithDifferentLimitsTest() {
        // Цей тест перевіряє виклик методу loadDeposits
        // В реальних умовах він буде взаємодіяти з API
        List<Deposit> result1 = cache.loadDeposits(5);
        List<Deposit> result2 = cache.loadDeposits(10);
        List<Deposit> result3 = cache.loadDeposits(0);

        assertNotNull(result1);
        assertNotNull(result2);
        assertNotNull(result3);
        // Результат залежить від реалізації API
    }
    @Test
    public void emptyCacheAfterCreationTest() {
        DepositsCache newCache = DepositsCache.getInstance();
        assertTrue(newCache.getDeposits().isEmpty());
    }
    @Test
    public void depositPropertiesTest() {
        Deposit deposit = createTestDeposit(5, "Преміум депозит", 18.5);
        cache.getDeposits().add(deposit);

        Deposit retrieved = cache.getDeposits().get(0);

        assertEquals(5, retrieved.getDepositId());
        assertEquals("Преміум депозит", retrieved.getName());
        assertEquals(18.5, retrieved.getInterestRate());
        assertEquals(12, retrieved.getTermMonths());
        assertEquals(1000.0, retrieved.getMinAmount());
    }

    // Допоміжний метод для створення тестового депозиту
    private Deposit createTestDeposit() {
        return createTestDeposit(1, "Тестовий депозит", 10.0);
    }

    private Deposit createTestDeposit(int id, String name, double interestRate) {
        return new Deposit(
                id, 1, name, interestRate, 12, 1000.0,
                true, false, "UAH", "Опис депозиту",
                "Тест банк", "https://test.bank", "Тест адреса", "123456789"
        );
    }
}
