package data.caches;

import data.api.APIrequester;
import domain.deposits.Deposit;

import java.util.ArrayList;
import java.util.List;

public class DepositsCache {
    private static DepositsCache instance;
    private List<Deposit> deposits = new ArrayList<>();

    private DepositsCache() {
        deposits = new ArrayList<>();
    }

    public static DepositsCache getInstance() {
        if (instance == null) {
            instance = new DepositsCache();
        }
        return instance;
    }

    public List<Deposit> getDeposits() {
        return deposits;
    }

    // Метод для підвантаження депозитів з сервера
    public List<Deposit> loadDeposits(int limit) {
        if (!deposits.isEmpty()) return deposits; // вже підвантажені

        APIrequester api = new APIrequester();
        List<Deposit> allDeposits = api.getDeposits();

        int count = Math.min(limit, allDeposits.size());
        for (int i = 0; i < count; i++) {
            deposits.add(allDeposits.get(i));
        }

        System.out.println("Підвантажено " + deposits.size() + " депозитів.");

        return this.deposits;
    }
}