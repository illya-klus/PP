package domain.deposits;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

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

    // --- Метод для відображення депозиту у UI ---
    public VBox displayAsPanel() {
        VBox box = new VBox(5);
        box.setPadding(new Insets(10));
        box.setStyle("-fx-border-color: gray; -fx-border-width: 1; -fx-background-color: #f5f5f5;");

        Label lblName = new Label(name);
        lblName.setFont(new Font("Arial", 16));

        Label lblBank = new Label("Банк: " + bankName);
        Label lblRate = new Label("Відсоток: " + interestRate + "%");
        Label lblTerm = new Label("Термін: " + termMonths + " міс.");
        Label lblAmount = new Label("Мін. сума: " + minAmount + " " + currency);
        Label lblTopUp = new Label("Поповнення: " + (allowTopUp ? "Так" : "Ні"));
        Label lblEarly = new Label("Дострокове закриття: " + (earlyWithdrawal ? "Доступне" : "Немає"));
        Label lblDesc = new Label(description != null ? description : "Опис відсутній.");
        lblDesc.setWrapText(true);

        Button btnReadMore = new Button("Детальніше");
        btnReadMore.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        btnReadMore.setOnAction(e -> {
            System.out.println("Відкрито депозит: " + name + " від банку " + bankName);
            // тут можна додати детальну сторінку
        });

        box.getChildren().addAll(lblName, lblBank, lblRate, lblTerm, lblAmount, lblTopUp, lblEarly, lblDesc, btnReadMore);
        return box;
    }
}