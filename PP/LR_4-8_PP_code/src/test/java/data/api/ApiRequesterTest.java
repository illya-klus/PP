package data.api;

import domain.banks.Bank;
import domain.deposits.Deposit;
import domain.users.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class ApiRequesterTest {

    private APIrequester apiRequester;

    @BeforeEach
    public void setUp() {
        apiRequester = new APIrequester();
    }

    @Test
    public void apiRequesterCreationTest() {
        assertNotNull(apiRequester);
    }
    @Test
    public void getAllBanksTest() {
        assertDoesNotThrow(() -> {
            List<Bank> banks = apiRequester.getAllBanks();
            assertNotNull(banks);
        });
    }
    @Test
    public void getDepositsTest() {
        assertDoesNotThrow(() -> {
            List<Deposit> deposits = apiRequester.getDeposits();
            assertNotNull(deposits);
        });
    }
    @Test
    public void checkUserWithInvalidCredentialsTest() {
        assertDoesNotThrow(() -> {
            User user = apiRequester.checkUser("nonexistentuser", "wrongpassword");
            assertNull(user);
        });
    }
    @Test
    public void findBanksWithEmptyParamsTest() {
        assertDoesNotThrow(() -> {
            List<Bank> banks = apiRequester.findBanks(null, null, null, null);
            assertNotNull(banks);
        });
    }
    @Test
    public void findDepositsWithEmptyParamsTest() {
        assertDoesNotThrow(() -> {
            List<Deposit> deposits = apiRequester.findDeposits(null, null, null, null);
            assertNotNull(deposits);
        });
    }
    @Test
    public void findUserWithEmptyParamsTest() {
        assertDoesNotThrow(() -> {
            List<User> users = apiRequester.findUser(null, null, false);
            assertNotNull(users);
        });
    }
    @Test
    public void getUserDepositsWithInvalidUserIdTest() {
        assertDoesNotThrow(() -> {
            List<Deposit> deposits = apiRequester.getUserDeposits(-1);
            assertNotNull(deposits);
        });
    }
    @Test
    public void getDepositBalanceWithInvalidIdTest() {
        assertDoesNotThrow(() -> {
            double balance = apiRequester.getDepositBalance(-1);
            assertEquals(0.0, balance);
        });
    }
    @Test
    public void isDepositAlreadyOpenedForUserWithInvalidIdsTest() {
        assertDoesNotThrow(() -> {
            boolean result = apiRequester.isDepositAlreadyOpenedForUser(-1, -1);
            assertFalse(result);
        });
    }
    // Тести для методів що використовують showAlert() - обгортаємо в assertDoesNotThrow
    @Test
    public void addUserWithInvalidDataTest() {
        assertDoesNotThrow(() -> {
            boolean result = apiRequester.addUser("", "", false);
            assertFalse(result);
        });
    }
    @Test
    public void addBankWithInvalidDataTest() {
        assertDoesNotThrow(() -> {
            boolean result = apiRequester.addBank("", "", "", "");
            assertFalse(result);
        });
    }
    @Test
    public void addDepositWithInvalidDataTest() {
        assertDoesNotThrow(() -> {
            boolean result = apiRequester.addDeposit("", -1, -1.0, -1, -1.0, false, false, "");
            assertFalse(result);
        });
    }
    @Test
    public void deleteUserWithInvalidIdTest() {
        assertDoesNotThrow(() -> {
            boolean result = apiRequester.deleteUser(-1);
            assertFalse(result);
        });
    }
    @Test
    public void deleteBankWithInvalidIdTest() {
        assertDoesNotThrow(() -> {
            boolean result = apiRequester.deleteBank(-1);
            assertFalse(result);
        });
    }
    @Test
    public void deleteDepositWithInvalidIdTest() {
        assertDoesNotThrow(() -> {
            boolean result = apiRequester.deleteDeposit(-1);
            assertFalse(result);
        });
    }
    @Test
    public void closeUserDepositWithInvalidIdTest() {
        assertDoesNotThrow(() -> {
            boolean result = apiRequester.closeUserDepositById(-1);
            assertFalse(result);
        });
    }
    @Test
    public void earlyWithdrawUserDepositWithInvalidIdTest() {
        assertDoesNotThrow(() -> {
            boolean result = apiRequester.earlyWithdrawUserDeposit(-1);
            assertFalse(result);
        });
    }
    @Test
    public void openUserDepositWithInvalidIdsTest() {
        assertDoesNotThrow(() -> {
            boolean result = apiRequester.openUserDeposit(-1, -1, -1.0);
            assertFalse(result);
        });
    }
    @Test
    public void topUpUserDepositWithInvalidIdTest() {
        assertDoesNotThrow(() -> {
            boolean result = apiRequester.topUpUserDeposit(-1, 100.0);
            assertFalse(result);
        });
    }
    @Test
    public void topUpUserDepositWithNegativeAmountTest() {
        assertDoesNotThrow(() -> {
            boolean result = apiRequester.topUpUserDeposit(1, -100.0);
            assertFalse(result);
        });
    }
    // Додаткові тести для перевірки структури даних
    @Test
    public void getAllBanksReturnsValidBankObjectsTest() {
        assertDoesNotThrow(() -> {
            List<Bank> banks = apiRequester.getAllBanks();
            assertNotNull(banks);

            if (!banks.isEmpty()) {
                Bank bank = banks.get(0);
                assertNotNull(bank.getName());
                assertTrue(bank.getBankId() > 0);
            }
        });
    }
    @Test
    public void getDepositsReturnsValidDepositObjectsTest() {
        assertDoesNotThrow(() -> {
            List<Deposit> deposits = apiRequester.getDeposits();
            assertNotNull(deposits);

            if (!deposits.isEmpty()) {
                Deposit deposit = deposits.get(0);
                assertNotNull(deposit.getName());
                assertTrue(deposit.getDepositId() > 0);
            }
        });
    }
    @Test
    public void apiConnectionStabilityTest() {
        assertDoesNotThrow(() -> {
            // Багаторазово викликаємо методи для перевірки стабільності
            for (int i = 0; i < 3; i++) {
                apiRequester.getAllBanks();
                apiRequester.getDeposits();
            }
        });
    }
    @Test
    public void findDepositsWithCurrencyTest() {
        assertDoesNotThrow(() -> {
            List<Deposit> uahDeposits = apiRequester.findDeposits(null, null, null, "UAH");
            assertNotNull(uahDeposits);
        });
    }
    @Test
    public void getUserDepositsWithZeroUserIdTest() {
        assertDoesNotThrow(() -> {
            List<Deposit> deposits = apiRequester.getUserDeposits(0);
            assertNotNull(deposits);
        });
    }
    @Test
    public void getDepositBalanceWithZeroIdTest() {
        assertDoesNotThrow(() -> {
            double balance = apiRequester.getDepositBalance(0);
            assertTrue(balance >= 0);
        });
    }
    @Test
    public void topUpUserDepositWithZeroAmountTest() {
        assertDoesNotThrow(() -> {
            boolean result = apiRequester.topUpUserDeposit(1, 0.0);
            assertFalse(result);
        });
    }
}