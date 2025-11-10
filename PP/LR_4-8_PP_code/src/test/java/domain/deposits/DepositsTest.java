package domain.deposits;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


public class DepositsTest {

    int depositId = 1;
    int bankId = 5;
    String name = "Терміновий депозит";
    double interestRate = 15.5;
    int termMonths = 12;
    double minAmount = 1000.0;
    boolean allowTopUp = true;
    boolean earlyWithdrawal = false;
    String currency = "UAH";
    String description = "Стандартний терміновий депозит";

    String bankName = "ПриватБанк";
    String bankWebUrl = "https://privatbank.ua";
    String bankAddress = "вул. Хрещатик 1";
    String bankPhoneNumber = "+380443645454";

    int openDepositId = 100;
    double moneyOnDeposit = 5000.0;
    String startDate = "2024-01-15";
    String endDate = "2025-01-15";

    // --- Тестовий об'єкт з повним конструктором ---
    Deposit deposit = new Deposit(depositId, bankId, name, interestRate, termMonths, minAmount,
            allowTopUp, earlyWithdrawal, currency, description,
            bankName, bankWebUrl, bankAddress, bankPhoneNumber,
            openDepositId, moneyOnDeposit, startDate, endDate);

    // --- Тестовий об'єкт з спрощеним конструктором ---
    Deposit simpleDeposit = new Deposit(depositId, bankId, name, interestRate, termMonths, minAmount,
            allowTopUp, earlyWithdrawal, currency, description,
            bankName, bankWebUrl, bankAddress, bankPhoneNumber);

    // --- Тести для основних полів ---
    @Test
    public void getDepositIdTest() {
        assertEquals(depositId, deposit.getDepositId());
    }
    @Test
    public void getBankIdTest() {
        assertEquals(bankId, deposit.getBankId());
    }
    @Test
    public void getNameTest() {
        assertEquals(name, deposit.getName());
    }
    @Test
    public void getInterestRateTest() {
        assertEquals(interestRate, deposit.getInterestRate());
    }
    @Test
    public void getTermMonthsTest() {
        assertEquals(termMonths, deposit.getTermMonths());
    }
    @Test
    public void getMinAmountTest() {
        assertEquals(minAmount, deposit.getMinAmount());
    }
    @Test
    public void isAllowTopUpTest() {
        assertTrue(deposit.isAllowTopUp());
    }
    @Test
    public void isEarlyWithdrawalTest() {
        assertFalse(deposit.isEarlyWithdrawal());
    }
    @Test
    public void getCurrencyTest() {
        assertEquals(currency, deposit.getCurrency());
    }
    @Test
    public void getDescriptionTest() {
        assertEquals(description, deposit.getDescription());
    }
    // --- Тести для інформації про банк ---
    @Test
    public void getBankNameTest() {
        assertEquals(bankName, deposit.getBankName());
    }
    @Test
    public void getBankWebUrlTest() {
        assertEquals(bankWebUrl, deposit.getBankWebUrl());
    }
    @Test
    public void getBankAddressTest() {
        assertEquals(bankAddress, deposit.getBankAddress());
    }
    @Test
    public void getBankPhoneNumberTest() {
        assertEquals(bankPhoneNumber, deposit.getBankPhoneNumber());
    }
    // --- Тести для полів відкритого депозиту ---
    @Test
    public void getOpenDepositIdTest() {
        assertEquals(openDepositId, deposit.getOpenDepositId());
    }
    @Test
    public void getMoneyOnDepositTest() {
        assertEquals(moneyOnDeposit, deposit.getMoneyOnDeposit());
    }
    @Test
    public void getStartDateTest() {
        assertEquals(startDate, deposit.getStartDate());
    }
    @Test
    public void getEndDateTest() {
        assertEquals(endDate, deposit.getEndDate());
    }
    // --- Тести для спрощеного конструктора ---
    @Test
    public void simpleConstructorOpenDepositIdTest() {
        assertEquals(0, simpleDeposit.getOpenDepositId());
    }
    @Test
    public void simpleConstructorMoneyOnDepositTest() {
        assertEquals(0, simpleDeposit.getMoneyOnDeposit());
    }
    @Test
    public void simpleConstructorStartDateTest() {
        assertNull(simpleDeposit.getStartDate());
    }
    @Test
    public void simpleConstructorEndDateTest() {
        assertNull(simpleDeposit.getEndDate());
    }
    // --- Тести для логічних методів ---
    @Test
    public void isClosedTest() {
        // deposit має endDate, тому він закритий
        assertTrue(deposit.isClosed());

        // simpleDeposit не має endDate, тому не закритий
        assertFalse(simpleDeposit.isClosed());
    }
    // --- Тести для сеттерів ---
    @Test
    public void setMoneyOnDepositTest() {
        double newAmount = 10000.0;
        deposit.setMoneyOnDeposit(newAmount);
        assertEquals(newAmount, deposit.getMoneyOnDeposit());
    }
    @Test
    public void setEndDateTest() {
        String newEndDate = "2024-12-31";
        deposit.setEndDate(newEndDate);
        assertEquals(newEndDate, deposit.getEndDate());
    }
    @Test
    public void setEndDateToNullTest() {
        deposit.setEndDate(null);
        assertNull(deposit.getEndDate());
        assertFalse(deposit.isClosed());
    }
}
