package domain.banks;

public class Bank {
    private int bankId;
    private String name;
    private String address;
    private String webUrl;
    private String phoneNumber;

    public Bank(int bankId, String name, String address, String webUrl, String phoneNumber) {
        this.bankId = bankId;
        this.name = name;
        this.address = address;
        this.webUrl = webUrl;
        this.phoneNumber = phoneNumber;
    }

    public int getBankId() { return bankId; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getWebUrl() { return webUrl; }
    public String getPhoneNumber() { return phoneNumber; }
}