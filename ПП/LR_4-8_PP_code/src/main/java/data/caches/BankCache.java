package data.caches;


import data.api.APIrequester;
import domain.banks.Bank;

import java.util.ArrayList;
import java.util.List;



public class BankCache {
    private static BankCache instance = new BankCache();
    private List<Bank> banks = new ArrayList<>();

    private BankCache() {}

    public static BankCache getInstance() {
        return instance;
    }

    public List<Bank> loadAllBanks() {
        APIrequester api = new APIrequester();
        if(!banks.isEmpty()){ return banks; }

        List<Bank> responcesBanks = api.getAllBanks();

        for(Bank bank : responcesBanks)
            banks.add(bank);

        return banks;
    }

}
