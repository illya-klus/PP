package data.caches;

import domain.banks.Bank;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class BankCacheTest {

    private BankCache cache;

    @BeforeEach
    public void setUp() {
        cache = BankCache.getInstance();
        // Очищаємо кеш перед кожним тестом
        cache.getBanks().clear();
    }
    @Test
    public void getInstanceTest() {
        BankCache instance1 = BankCache.getInstance();
        BankCache instance2 = BankCache.getInstance();

        assertNotNull(instance1);
        assertNotNull(instance2);
        assertSame(instance1, instance2); // Перевірка Singleton
    }
    @Test
    public void loadAllBanksWhenCacheEmptyTest() {
        List<Bank> result = cache.loadAllBanks();

        assertNotNull(result);
        // Результат залежить від API, але метод не має падати
    }
    @Test
    public void loadAllBanksWhenCacheNotEmptyTest() {
        // Додаємо тестові дані в кеш
        Bank testBank = createTestBank();
        cache.getBanks().add(testBank);

        List<Bank> result = cache.loadAllBanks();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(testBank, result.get(0));
    }
    @Test
    public void banksListInitializationTest() {
        List<Bank> banks = cache.getBanks();

        assertNotNull(banks);
        assertTrue(banks.isEmpty());
    }
    @Test
    public void addBanksManuallyTest() {
        Bank bank1 = createTestBank(1, "Банк 1", "Адреса 1", "url1", "тел1");
        Bank bank2 = createTestBank(2, "Банк 2", "Адреса 2", "url2", "тел2");

        cache.getBanks().add(bank1);
        cache.getBanks().add(bank2);

        List<Bank> banks = cache.getBanks();

        assertEquals(2, banks.size());
        assertEquals("Банк 1", banks.get(0).getName());
        assertEquals("Банк 2", banks.get(1).getName());
    }
    @Test
    public void cachePersistenceBetweenInstancesTest() {
        // Додаємо дані в кеш
        Bank testBank = createTestBank();
        cache.getBanks().add(testBank);

        // Отримуємо новий інстанс (має бути той же самий)
        BankCache newInstance = BankCache.getInstance();

        // Перевіряємо що дані збереглися
        assertFalse(newInstance.getBanks().isEmpty());
        assertEquals(1, newInstance.getBanks().size());
        assertEquals(testBank, newInstance.getBanks().get(0));
    }
    @Test
    public void multipleBanksAdditionTest() {
        // Додаємо кілька банків
        for (int i = 1; i <= 5; i++) {
            Bank bank = createTestBank(i, "Банк " + i, "Адреса " + i, "url" + i, "тел" + i);
            cache.getBanks().add(bank);
        }

        assertEquals(5, cache.getBanks().size());

        // Перевіряємо дані
        assertEquals("Банк 1", cache.getBanks().get(0).getName());
        assertEquals("Банк 3", cache.getBanks().get(2).getName());
        assertEquals("Банк 5", cache.getBanks().get(4).getName());
        assertEquals("url2", cache.getBanks().get(1).getWebUrl());
        assertEquals("тел4", cache.getBanks().get(3).getPhoneNumber());
    }
    @Test
    public void bankPropertiesTest() {
        Bank bank = createTestBank(10, "ПриватБанк", "вул. Хрещатик 1", "https://privatbank.ua", "+380443636363");
        cache.getBanks().add(bank);

        Bank retrieved = cache.getBanks().get(0);

        assertEquals(10, retrieved.getBankId());
        assertEquals("ПриватБанк", retrieved.getName());
        assertEquals("вул. Хрещатик 1", retrieved.getAddress());
        assertEquals("https://privatbank.ua", retrieved.getWebUrl());
        assertEquals("+380443636363", retrieved.getPhoneNumber());
    }
    @Test
    public void loadAllBanksMultipleCallsTest() {
        // Перший виклик
        List<Bank> result1 = cache.loadAllBanks();
        assertNotNull(result1);

        // Другий виклик - має повернути той же список
        List<Bank> result2 = cache.loadAllBanks();
        assertNotNull(result2);

        // Третій виклик
        List<Bank> result3 = cache.loadAllBanks();
        assertNotNull(result3);

        // Всі виклики мають повертати той же самий об'єкт списку
        assertSame(result1, result2);
        assertSame(result2, result3);
    }
    @Test
    public void emptyCacheAfterClearTest() {
        // Додаємо дані
        cache.getBanks().add(createTestBank());
        assertFalse(cache.getBanks().isEmpty());

        // Очищаємо
        cache.getBanks().clear();
        assertTrue(cache.getBanks().isEmpty());

        // Перевіряємо що loadAllBanks все ще працює
        List<Bank> result = cache.loadAllBanks();
        assertNotNull(result);
    }
    @Test
    public void singletonInstanceConsistencyTest() {
        BankCache instance1 = BankCache.getInstance();
        instance1.getBanks().add(createTestBank(1, "Банк А", "Адреса А", "urlA", "телА"));

        BankCache instance2 = BankCache.getInstance();
        instance2.getBanks().add(createTestBank(2, "Банк Б", "Адреса Б", "urlБ", "телБ"));

        // Обидва інстанси мають бути одним і тим же об'єктом
        assertSame(instance1, instance2);

        // Дані мають бути спільними
        assertEquals(2, instance1.getBanks().size());
        assertEquals(2, instance2.getBanks().size());
        assertEquals("Банк А", instance1.getBanks().get(0).getName());
        assertEquals("Банк Б", instance1.getBanks().get(1).getName());
    }

    // Допоміжний метод для створення тестового банку
    private Bank createTestBank() {
        return createTestBank(1, "Тест банк", "Тест адреса", "https://test.bank", "123456789");
    }

    private Bank createTestBank(int id, String name, String address, String webUrl, String phoneNumber) {
        return new Bank(id, name, address, webUrl, phoneNumber);
    }
}