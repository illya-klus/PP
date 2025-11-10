package domain.deposits;

import data.api.APIrequester;

public class Deposit {

    // --- Основні поля з таблиці deposits ---
    private int depositId;
    private int bankId;
    private String name;
    private double interestRate;
    private int termMonths;
    private double minAmount;
    private boolean allowTopUp;
    private boolean earlyWithdrawal;
    private String currency;
    private String description;

    // --- Інформація про банк ---
    private String bankName;
    private String bankWebUrl;
    private String bankAddress;
    private String bankPhoneNumber;

    // --- Додаткові поля для відкритих депозитів ---
    private int openDepositId;      // opendeposits.opendepositid
    private double moneyOnDeposit;  // opendeposits.moneyondeposit
    private String startDate;       // opendeposits.startdate
    private String endDate;         // opendeposits.enddate

    public APIrequester api = new APIrequester();

    // --- Конструктор повний (з відкритими депозитами) ---
    public Deposit(int depositId, int bankId, String name, double interestRate, int termMonths, double minAmount,
                   boolean allowTopUp, boolean earlyWithdrawal, String currency, String description,
                   String bankName, String bankWebUrl, String bankAddress, String bankPhoneNumber,
                   int openDepositId, double moneyOnDeposit, String startDate, String endDate) {

        this.depositId = depositId;
        this.bankId = bankId;
        this.name = name;
        this.interestRate = interestRate;
        this.termMonths = termMonths;
        this.minAmount = minAmount;
        this.allowTopUp = allowTopUp;
        this.earlyWithdrawal = earlyWithdrawal;
        this.currency = currency;
        this.description = description;

        this.bankName = bankName;
        this.bankWebUrl = bankWebUrl;
        this.bankAddress = bankAddress;
        this.bankPhoneNumber = bankPhoneNumber;

        this.openDepositId = openDepositId;
        this.moneyOnDeposit = moneyOnDeposit;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // --- Спрощений конструктор (для загальних депозитів, без відкриття) ---
    public Deposit(int depositId, int bankId, String name, double interestRate, int termMonths, double minAmount,
                   boolean allowTopUp, boolean earlyWithdrawal, String currency, String description,
                   String bankName, String bankWebUrl, String bankAddress, String bankPhoneNumber) {

        this(depositId, bankId, name, interestRate, termMonths, minAmount,
                allowTopUp, earlyWithdrawal, currency, description,
                bankName, bankWebUrl, bankAddress, bankPhoneNumber,
                0, 0, null, null);
    }

    // --- Геттери ---
    public int getDepositId() { return depositId; }
    public int getBankId() { return bankId; }
    public String getName() { return name; }
    public double getInterestRate() { return interestRate; }
    public int getTermMonths() { return termMonths; }
    public double getMinAmount() { return minAmount; }
    public boolean isAllowTopUp() { return allowTopUp; }
    public boolean isEarlyWithdrawal() { return earlyWithdrawal; }
    public String getCurrency() { return currency; }
    public String getDescription() { return description; }

    public String getBankName() { return bankName; }
    public String getBankWebUrl() { return bankWebUrl; }
    public String getBankAddress() { return bankAddress; }
    public String getBankPhoneNumber() { return bankPhoneNumber; }

    public int getOpenDepositId() { return openDepositId; }
    public double getMoneyOnDeposit() { return moneyOnDeposit; }
    public String getStartDate() { return startDate; }
    public String getEndDate() { return endDate; }

    public boolean isClosed() { return endDate != null; }

    // --- Сеттери (на випадок оновлення з API) ---
    public void setMoneyOnDeposit(double moneyOnDeposit) { this.moneyOnDeposit = moneyOnDeposit; }
    public void setEndDate(String endDate) { this.endDate = endDate; }

    // --- Логічні методи (робота з API) ---

    /** Закриває відкритий депозит */
    public void closeDeposit() {
        if (openDepositId == 0) {
            System.out.println("❌ Неможливо закрити: депозит не відкритий.");
            return;
        }
        api.closeUserDepositById(openDepositId);
        System.out.println("✅ Депозит \"" + name + "\" закрито.");
    }

    /** Поповнює відкритий депозит */
    public void topUp(double amount) {
        if (!allowTopUp) {
            System.out.println("❌ Цей депозит не дозволяє поповнення.");
            return;
        }
        if (openDepositId == 0) {
            System.out.println("❌ Неможливо поповнити: депозит не відкритий.");
            return;
        }
        api.topUpUserDeposit(openDepositId, amount);
        System.out.println("💰 Поповнено депозит \"" + name + "\" на " + amount + " " + currency);
    }

    /** Дострокове зняття коштів (якщо дозволено) */
    public void earlyWithdraw() {
        if (!earlyWithdrawal) {
            System.out.println("❌ Дострокове зняття не дозволено.");
            return;
        }
        if (openDepositId == 0) {
            System.out.println("❌ Неможливо виконати зняття: депозит не відкритий.");
            return;
        }
        api.earlyWithdrawUserDeposit(openDepositId);
        System.out.println("💸 Кошти достроково знято з депозиту \"" + name + "\".");
    }
}