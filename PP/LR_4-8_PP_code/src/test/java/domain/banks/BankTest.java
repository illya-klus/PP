package domain.banks;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BankTest {
    int id = 1;
    String name = "Ощад банк";
    String address = "вул. Степана Бандери 22";
    String url =  "https://www.oschadbank.ua/";
    String phoneNumber = "+380990646211";

    Bank bank = new Bank(id, name, address, url, phoneNumber);

    @Test
    public void getBankIdTest(){
        assertEquals(id, bank.getBankId());
    }
    @Test
    public void getNameTest(){
        assertEquals(name, bank.getName());
    }
    @Test
    public void getAddressTest(){
        assertEquals(address, bank.getAddress());
    }
    @Test
    public void getWeburlTest(){
        assertEquals(url, bank.getWebUrl());
    }
    @Test
    public void getPhoneNumberTest(){
        assertEquals(phoneNumber, bank.getPhoneNumber());
    }
}
