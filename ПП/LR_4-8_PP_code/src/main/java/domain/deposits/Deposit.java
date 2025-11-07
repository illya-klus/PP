package domain.deposits;


import data.api.APIrequester;

public class Deposit {

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
    private String bankName;
    private String bankWebUrl;
    private String bankAddress;
    private String bankPhoneNumber;

    public APIrequester api = new APIrequester();

    // --- Конструктор ---
    public Deposit(int depositId, int bankId, String name, double interestRate, int termMonths, double minAmount,
                   boolean allowTopUp, boolean earlyWithdrawal, String currency, String description,
                   String bankName, String bankWebUrl, String bankAddress, String bankPhoneNumber) {

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
    }

    // --- Геттери ---
    public String getDescription() {return this.description;}
    public int getDepositId() { return depositId; }
    public String getName() { return name; }
    public double getInterestRate() { return interestRate; }
    public int getTermMonths() { return termMonths; }
    public double getMinAmount() { return minAmount; }
    public boolean isAllowTopUp() { return allowTopUp; }
    public boolean isEarlyWithdrawal() { return earlyWithdrawal; }
    public String getCurrency() { return currency; }
    public String getBankName() { return bankName; }

    // --- Логічні методи (запити до API) ---

    /** Закриває депозит, встановлюючи end_date = CURRENT_DATE */
    public void closeDeposit(int userId) {
        api.closeUserDepositById(depositId);
        System.out.println("Депозит закрито: " + name);
    }

    /** Поповнює депозит на певну суму */
    public void topUp(int userId, double amount) {
        if (!allowTopUp) {
            System.out.println("❌ Цей депозит не дозволяє поповнення");
            return;
        }
        api.topUpUserDeposit(depositId, amount);
        System.out.println("Поповнено депозит " + name + " на " + amount + " " + currency);
    }


    /** Знімає достроково кошти (якщо дозволено) */
    public void earlyWithdraw(int userId) {
        if (!earlyWithdrawal) {
            System.out.println("❌ Дострокове зняття не дозволено для цього депозиту.");
            return;
        }
        api.earlyWithdrawUserDeposit(depositId);
        System.out.println("💸 Кошти достроково знято з депозиту " + name);
    }


}