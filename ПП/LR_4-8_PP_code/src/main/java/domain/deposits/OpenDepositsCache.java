package domain.deposits;

import data.APIrequester;
import domain.users.User;
import domain.users.UserSession;

import java.util.ArrayList;
import java.util.List;

public class OpenDepositsCache {
    private static OpenDepositsCache instance;
    private List<Deposit> openDeposits = new ArrayList<>();

    private OpenDepositsCache() {
        openDeposits = new ArrayList<>();
    }

    public static OpenDepositsCache getInstance() {
        if (instance == null) {
            instance = new OpenDepositsCache();
        }
        return instance;
    }

    public List<Deposit> getOpenDeposits() {
        return openDeposits;
    }

    /**
     * Підвантажує відкриті депозити користувача з сервера через API.
     * Якщо кеш не порожній — повертає збережені дані.
     */
    public List<Deposit> loadOpenDeposits() {
        if (!openDeposits.isEmpty()) {
            return openDeposits; // вже є в кеші
        }

        User currentUser = UserSession.getInstance().getCurrentUser();
        if (currentUser == null) {
            System.out.println("❌ Неможливо завантажити відкриті депозити — користувач не авторизований.");
            return new ArrayList<>();
        }

        APIrequester api = new APIrequester();
        List<Deposit> userDeposits = api.getUserDeposits(currentUser.getUserId());

        if (userDeposits != null) {
            openDeposits.addAll(userDeposits);
            System.out.println("✅ Підвантажено " + openDeposits.size() + " відкритих депозитів користувача.");
        } else {
            System.out.println("⚠️ Не вдалося отримати відкриті депозити з API.");
        }

        return openDeposits;
    }

    /**
     * Очищує кеш відкритих депозитів (наприклад, при виході користувача).
     */
    public void clear() {
        openDeposits.clear();
        System.out.println("🧹 Кеш відкритих депозитів очищено.");
    }
}
