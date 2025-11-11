package data.api;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import domain.banks.Bank;
import domain.deposits.Deposit;
import domain.users.User;
import domain.users.UserSession;
import org.json.JSONArray;
import org.json.JSONObject;

import javafx.scene.control.*;
import javafx.scene.layout.*;

public class APIrequester {

    // Єдиний базовий URL проєкту
    private static final String BASE_URL = "https://wxynvayzgtsrwpkqehvk.supabase.co/rest/v1/";
    // Анонімний ключ доступу (public anon key з Supabase)
    private static final String API_KEY =
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Ind4eW52YXl6Z3Rzcndwa3FlaHZrIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjIxOTQ3MTAsImV4cCI6MjA3Nzc3MDcxMH0.AuQapcN--gcQHkvHwow8Jb6hDjSlC9Lo5TpZD4e86dw";
    // Клієнт HTTP (один на всі запити)
    private final HttpClient client = HttpClient.newHttpClient();

    // Логування
    private void logInfo(String message) {
        System.out.println("[INFO][" + LocalDateTime.now() + "] " + message);
    }
    private void logError(String message, Exception ex) {
        System.err.println("[ERROR][" + LocalDateTime.now() + "] " + message);
        if (ex != null) {
            ex.printStackTrace();
        }
    }
    private void logWarning(String message) {
        System.out.println("[WARN][" + LocalDateTime.now() + "] " + message);
    }
    private void logDebug(String message) {
        System.out.println("[DEBUG][" + LocalDateTime.now() + "] " + message);
    }


    public User checkUser(String login, String password) {
        logInfo("Спроба автентифікації користувача: " + login);
        try {
            String requestUrl = BASE_URL + "users?login=eq." + login + "&password=eq." + password;

            HttpRequest request = buildRequest(requestUrl);
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            String body = response.body().trim();
            logDebug("Відповідь від Supabase для користувача " + login + ": " + body);

            if (!body.startsWith("[")) {
                logWarning("Невірний формат відповіді для користувача: " + login);
                return null;
            }

            JSONArray arr = new JSONArray(body);
            if (arr.isEmpty()) {
                logWarning("Користувач не знайдений або невірний пароль: " + login);
                return null;
            }

            JSONObject obj = arr.getJSONObject(0);
            User user = new User(obj.getInt("userid"), login, obj.optBoolean("isadmin", false));
            logInfo("Користувач успішно автентифікований: " + login + " (ID: " + user.getUserId() + ", Admin: " + user.isAdmin() + ")");
            return user;

        } catch (Exception ex) {
            logError("Помилка при автентифікації користувача: " + login, ex);
            return null;
        }
    }

    public List<Deposit> getDeposits() {
        logInfo("Запит на отримання списку депозитів");
        List<Deposit> deposits = new ArrayList<>();

        try {
            String requestUrl = BASE_URL + "deposits?select=depositid,bankid,name,interestrate,termmonths,minamount,allowtopup,earlywithdrawal,currency,description,banks(name,weburl,address,phonenumber)";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(requestUrl))
                    .header("apikey", API_KEY)
                    .header("Authorization", "Bearer " + API_KEY)
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            logDebug("getDeposits response: " + response.body());

            if (response.statusCode() != 200) {
                logError("Не вдалося завантажити депозити (код: " + response.statusCode() + ")", null);
                showAlertSafe("Помилка", "Не вдалося завантажити депозити (код: " + response.statusCode() + ")");
                return deposits;
            }

            JSONArray arr = new JSONArray(response.body().trim());
            logInfo("Отримано " + arr.length() + " депозитів з API");

            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                JSONObject bankObj = obj.optJSONObject("banks");

                Deposit deposit = new Deposit(
                        obj.optInt("depositid"),
                        obj.optInt("bankid"),
                        obj.optString("name", "Невідомий депозит"),
                        obj.optDouble("interestrate", 0.0),
                        obj.optInt("termmonths", 0),
                        obj.optDouble("minamount", 0.0),
                        obj.optBoolean("allowtopup", false),
                        obj.optBoolean("earlywithdrawal", false),
                        obj.optString("currency", "UAH"),
                        obj.optString("description", ""),
                        bankObj != null ? bankObj.optString("name", "Невідомий банк") : "Невідомий банк",
                        bankObj != null ? bankObj.optString("weburl", "") : "",
                        bankObj != null ? bankObj.optString("address", "") : "",
                        bankObj != null ? bankObj.optString("phonenumber", "—") : "—"
                );

                deposits.add(deposit);
            }

            logInfo("Успішно сформовано список з " + deposits.size() + " депозитів");

        } catch (Exception e) {
            logError("Сталася помилка при отриманні депозитів!", e);
            showAlertSafe("Помилка", "Сталася помилка при отриманні депозитів!");
        }

        return deposits;
    }
    public List<Bank> getAllBanks() {
        logInfo("Запит на отримання списку банків");
        List<Bank> banks = new ArrayList<>();
        try {
            String requestUrl = BASE_URL + "banks?select=*";

            HttpRequest request = buildRequest(requestUrl);
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            logDebug("Banks response: " + response.body());

            JSONArray arr = new JSONArray(response.body().trim());
            logInfo("Отримано " + arr.length() + " банків з API");

            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                banks.add(new Bank(
                        obj.optInt("bankid", -1),
                        obj.optString("name", "Невідомий банк"),
                        obj.optString("address", "Немає адреси"),
                        obj.optString("weburl", ""),
                        obj.optString("phonenumber", "—")
                ));
            }

            logInfo("Успішно сформовано список з " + banks.size() + " банків");

        } catch (Exception ex) {
            logError("Помилка при отриманні списку банків", ex);
        }
        return banks;
    }
    public List<Deposit> getUserDeposits(int userId) {
        logInfo("Запит на отримання депозитів користувача ID: " + userId);
        List<Deposit> userDeposits = new ArrayList<>();

        try {
            String requestUrl = BASE_URL + "opendeposits"
                    + "?select=opendepositid,depositid,moneyondeposit,startdate,enddate,"
                    + "deposits(*,banks(*)),wallets!inner(userid)"
                    + "&wallets.userid=eq." + userId;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(requestUrl))
                    .header("apikey", API_KEY)
                    .header("Authorization", "Bearer " + API_KEY)
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String body = response.body().trim();

            logDebug("getUserDeposits response: " + body);

            if (response.statusCode() != 200 || !body.startsWith("[")) {
                logWarning("Не вдалося отримати депозити користувача (код: " + response.statusCode() + ")");
                return userDeposits;
            }

            JSONArray arr = new JSONArray(body);
            logInfo("Отримано " + arr.length() + " депозитів користувача ID: " + userId);

            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                JSONObject depObj = obj.optJSONObject("deposits");
                if (depObj == null) continue;

                JSONObject bankObj = depObj.optJSONObject("banks");

                String bankName = bankObj != null ? bankObj.optString("name", "Невідомий банк") : "Невідомий банк";
                String bankUrl = bankObj != null ? bankObj.optString("weburl", "") : "";
                String bankAddr = bankObj != null ? bankObj.optString("address", "") : "";
                String bankPhone = bankObj != null ? bankObj.optString("phonenumber", "—") : "—";

                int openDepositId = obj.optInt("opendepositid", -1);
                double moneyOnDeposit = obj.optDouble("moneyondeposit", 0.0);
                String startDate = obj.isNull("startdate") ? null : obj.optString("startdate", null);
                String endDate = obj.isNull("enddate") ? null : obj.optString("enddate", null);

                Deposit deposit = new Deposit(
                        depObj.optInt("depositid", -1),
                        depObj.optInt("bankid", -1),
                        depObj.optString("name", "Невідомий депозит"),
                        depObj.optDouble("interestrate", 0.0),
                        depObj.optInt("termmonths", 0),
                        depObj.optDouble("minamount", 0.0),
                        depObj.optBoolean("allowtopup", false),
                        depObj.optBoolean("earlywithdrawal", false),
                        depObj.optString("currency", "UAH"),
                        depObj.optString("description", ""),
                        bankName,
                        bankUrl,
                        bankAddr,
                        bankPhone,
                        openDepositId,
                        moneyOnDeposit,
                        startDate,
                        endDate
                );

                userDeposits.add(deposit);
            }

            logInfo("Успішно сформовано список з " + userDeposits.size() + " депозитів користувача");

        } catch (Exception ex) {
            logError("Помилка при виконанні getUserDeposits() для користувача ID: " + userId, ex);
        }

        return userDeposits;
    }

    public List<User> findUser(String login, String password, boolean isAdmin) {
        logInfo("Пошук користувача: login=" + login + ", isAdmin=" + isAdmin);
        List<User> users = new ArrayList<>();

        try {
            StringBuilder url = new StringBuilder(BASE_URL + "users?");

            if (login != null && !login.isEmpty())
                url.append("login=eq.").append(URLEncoder.encode(login, StandardCharsets.UTF_8)).append("&");
            if (password != null && !password.isEmpty())
                url.append("password=eq.").append(URLEncoder.encode(password, StandardCharsets.UTF_8)).append("&");

            url.append("isadmin=eq.").append(String.valueOf(isAdmin).toLowerCase());

            HttpRequest request = buildRequest(url.toString());
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            String body = response.body().trim();
            logDebug("findUser response: " + body);

            if (!body.startsWith("[")) return users;

            JSONArray arr = new JSONArray(body);
            logInfo("Знайдено " + arr.length() + " користувачів за критеріями");

            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                users.add(new User(
                        obj.getInt("userid"),
                        obj.getString("login"),
                        obj.optString("password", ""),
                        obj.optBoolean("isadmin", false)
                ));
            }

        } catch (Exception e) {
            logError("Помилка при пошуку користувача", e);
        }

        return users;
    }
    public List<Bank> findBanks(String name, String address, String weburl, String phoneNumber) {
        logInfo("Пошук банків за параметрами: name=" + name + ", address=" + address);
        List<Bank> banks = new ArrayList<>();

        try {
            StringBuilder url = new StringBuilder(BASE_URL + "banks?");

            if (name != null && !name.isEmpty())
                url.append("name=eq.").append(URLEncoder.encode(name, StandardCharsets.UTF_8)).append("&");
            if (address != null && !address.isEmpty())
                url.append("address=eq.").append(URLEncoder.encode(address, StandardCharsets.UTF_8)).append("&");
            if (weburl != null && !weburl.isEmpty())
                url.append("weburl=eq.").append(URLEncoder.encode(weburl, StandardCharsets.UTF_8)).append("&");
            if (phoneNumber != null && !phoneNumber.isEmpty())
                url.append("phonenumber=eq.").append(URLEncoder.encode(phoneNumber, StandardCharsets.UTF_8)).append("&");

            if (url.charAt(url.length() - 1) == '&') url.deleteCharAt(url.length() - 1);

            HttpRequest request = buildRequest(url.toString());
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            String body = response.body().trim();
            logDebug("findBanks response: " + body);

            if (!body.startsWith("[")) return banks;

            JSONArray arr = new JSONArray(body);
            logInfo("Знайдено " + arr.length() + " банків за критеріями");

            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                banks.add(new Bank(
                        obj.getInt("bankid"),
                        obj.optString("name", ""),
                        obj.optString("address", ""),
                        obj.optString("weburl", ""),
                        obj.optString("phonenumber", "")
                ));
            }

        } catch (Exception e) {
            logError("Помилка при пошуку банків", e);
        }

        return banks;
    }
    public List<Deposit> findDeposits(String name, Integer bankId, Double rate, String currency) {
        logInfo("Пошук депозитів: name=" + name + ", bankId=" + bankId + ", rate=" + rate + ", currency=" + currency);
        List<Deposit> deposits = new ArrayList<>();

        try {
            StringBuilder url = new StringBuilder(BASE_URL + "deposits?");

            if (name != null && !name.isEmpty())
                url.append("name=eq.").append(URLEncoder.encode(name, StandardCharsets.UTF_8)).append("&");
            if (bankId != null)
                url.append("bankid=eq.").append(bankId).append("&");
            if (rate != null)
                url.append("interestrate=eq.").append(rate).append("&");
            if (currency != null && !currency.isEmpty())
                url.append("currency=eq.").append(URLEncoder.encode(currency, StandardCharsets.UTF_8)).append("&");

            if (url.charAt(url.length() - 1) == '&') url.deleteCharAt(url.length() - 1);

            HttpRequest request = buildRequest(url.toString());
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            String body = response.body().trim();
            logDebug("findDeposits response: " + body);

            if (!body.startsWith("[")) return deposits;

            JSONArray arr = new JSONArray(body);
            logInfo("Знайдено " + arr.length() + " депозитів за критеріями");

            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                deposits.add(new Deposit(
                        obj.getInt("depositid"),
                        obj.getInt("bankid"),
                        obj.optString("name", ""),
                        obj.optDouble("interestrate", 0),
                        obj.optInt("termmonths", 0),
                        obj.optDouble("minamount", 0),
                        obj.optBoolean("allowtopup", false),
                        obj.optBoolean("earlywithdrawal", false),
                        obj.optString("currency", "UAH"),
                        obj.optString("description", ""),
                        "", "", "", ""
                ));
            }

        } catch (Exception e) {
            logError("Помилка при пошуку депозитів", e);
        }

        return deposits;
    }

    public boolean addUser(String login, String password, boolean isAdmin) {
        logInfo("Спроба додати користувача: " + login + ", isAdmin=" + isAdmin);
        try {
            if (login == null || login.isEmpty() || password == null || password.isEmpty()) {
                logWarning("Спроба додати користувача з порожніми полями");
                showAlertSafe("Помилка", "Усі поля користувача повинні бути заповнені!");
                return false;
            }

            JSONObject userJson = new JSONObject();
            userJson.put("login", login);
            userJson.put("password", password);
            userJson.put("isadmin", isAdmin);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "users"))
                    .header("apikey", API_KEY)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(userJson.toString()))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            logDebug("addUser response: " + response.body());

            boolean success = response.statusCode() == 201;
            if (success) {
                logInfo("Користувач успішно доданий: " + login);
            } else {
                logError("Не вдалося додати користувача: " + login + " (код: " + response.statusCode() + ")", null);
            }
            return success;
        } catch (Exception e) {
            logError("Не вдалося додати користувача: " + login, e);
            showAlertSafe("Помилка", "Не вдалося додати користувача!");
            return false;
        }
    }
    public boolean addBank(String name, String address, String webUrl, String phone) {
        logInfo("Спроба додати банк: " + name);
        try {
            if (name == null || name.isEmpty() ||
                    address == null || address.isEmpty() ||
                    webUrl == null || webUrl.isEmpty() ||
                    phone == null || phone.isEmpty()) {
                logWarning("Спроба додати банк з порожніми полями");
                showAlertSafe("Помилка", "Усі поля банку повинні бути заповнені!");
                return false;
            }

            JSONObject bankJson = new JSONObject();
            bankJson.put("name", name);
            bankJson.put("address", address);
            bankJson.put("weburl", webUrl);
            bankJson.put("phonenumber", phone);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "banks"))
                    .header("apikey", API_KEY)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(bankJson.toString()))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            logDebug("addBank response: " + response.body());

            boolean success = response.statusCode() == 201;
            if (success) {
                logInfo("Банк успішно доданий: " + name);
            } else {
                logError("Не вдалося додати банк: " + name + " (код: " + response.statusCode() + ")", null);
            }
            return success;
        } catch (Exception e) {
            logError("Не вдалося додати банк: " + name, e);
            showAlertSafe("Помилка", "Не вдалося додати банк!");
            return false;
        }
    }
    public boolean addDeposit(String name, Integer bankId, Double rate, Integer term, Double minAmount, boolean canTopUp, boolean canWithdrawEarly, String currency) {
        logInfo("Спроба додати депозит: " + name + ", bankId=" + bankId);
        try {
            if (name == null || name.isEmpty() ||
                    bankId == null || bankId <= 0 ||
                    rate == null || term == null || minAmount == null ||
                    currency == null || currency.isEmpty()) {
                logWarning("Спроба додати депозит з некоректними полями");
                showAlertSafe("Помилка", "Не всі поля депозиту заповнені або мають некоректні значення!");
                return false;
            }

            JSONObject depJson = new JSONObject();
            depJson.put("name", name);
            depJson.put("bankid", bankId);
            depJson.put("interestrate", rate);
            depJson.put("termmonths", term);
            depJson.put("minamount", minAmount);
            depJson.put("allowtopup", canTopUp);
            depJson.put("earlywithdrawal", canWithdrawEarly);
            depJson.put("currency", currency);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "deposits"))
                    .header("apikey", API_KEY)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(depJson.toString()))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            logDebug("addDeposit response: " + response.body());

            boolean success = response.statusCode() == 201;
            if (success) {
                logInfo("Депозит успішно доданий: " + name);
            } else {
                logError("Не вдалося додати депозит: " + name + " (код: " + response.statusCode() + ")", null);
            }
            return success;
        } catch (Exception e) {
            logError("Не вдалося додати депозит: " + name, e);
            showAlertSafe("Помилка", "Не вдалося додати депозит!");
            return false;
        }
    }

    public boolean deleteUser(int userId) {
        logInfo("Спроба видалити користувача ID: " + userId);
        try {
            if (userId <= 0) {
                logWarning("Спроба видалити користувача з некоректним ID: " + userId);
                showAlertSafe("Помилка", "Некоректний ID користувача!");
                return false;
            }

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "users?userid=eq." + userId))
                    .header("apikey", API_KEY)
                    .DELETE()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            logDebug("deleteUser response: " + response.body());

            boolean success = response.statusCode() == 200 || response.statusCode() == 204;
            if (success) {
                logInfo("Користувач успішно видалений ID: " + userId);
            } else {
                logError("Не вдалося видалити користувача ID: " + userId + " (код: " + response.statusCode() + ")", null);
                showAlertSafe("Помилка", "Не вдалося видалити користувача!");
            }
            return success;
        } catch (Exception e) {
            logError("Не вдалося видалити користувача ID: " + userId, e);
            showAlertSafe("Помилка", "Не вдалося видалити користувача!");
            return false;
        }
    }
    public boolean deleteBank(int bankId) {
        logInfo("Спроба видалити банк ID: " + bankId);
        try {
            if (bankId <= 0) {
                logWarning("Спроба видалити банк з некоректним ID: " + bankId);
                showAlertSafe("Помилка", "Некоректний ID банку!");
                return false;
            }

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "banks?bankid=eq." + bankId))
                    .header("apikey", API_KEY)
                    .DELETE()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            logDebug("deleteBank response: " + response.body());

            boolean success = response.statusCode() == 200 || response.statusCode() == 204;
            if (success) {
                logInfo("Банк успішно видалений ID: " + bankId);
            } else {
                logError("Не вдалося видалити банк ID: " + bankId + " (код: " + response.statusCode() + ")", null);
                showAlertSafe("Помилка", "Не вдалося видалити банк!");
            }
            return success;
        } catch (Exception e) {
            logError("Не вдалося видалити банк ID: " + bankId, e);
            showAlertSafe("Помилка", "Не вдалося видалити банк!");
            return false;
        }
    }
    public boolean deleteDeposit(int depositId) {
        logInfo("Спроба видалити депозит ID: " + depositId);
        try {
            if (depositId <= 0) {
                logWarning("Спроба видалити депозит з некоректним ID: " + depositId);
                showAlertSafe("Помилка", "Некоректний ID депозиту!");
                return false;
            }

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "deposits?depositid=eq." + depositId))
                    .header("apikey", API_KEY)
                    .DELETE()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            logDebug("deleteDeposit response: " + response.body());

            boolean success = response.statusCode() == 200 || response.statusCode() == 204;
            if (success) {
                logInfo("Депозит успішно видалений ID: " + depositId);
            } else {
                logError("Не вдалося видалити депозит ID: " + depositId + " (код: " + response.statusCode() + ")", null);
                showAlertSafe("Помилка", "Не вдалося видалити депозит!");
            }
            return success;
        } catch (Exception e) {
            logError("Не вдалося видалити депозит ID: " + depositId, e);
            showAlertSafe("Помилка", "Не вдалося видалити депозит!");
            return false;
        }
    }


    public boolean closeUserDepositById(int openDepositId) {
        logInfo("Спроба закрити депозит користувача ID: " + openDepositId);
        if(openDepositId <= 0) {
            logWarning("Спроба закрити депозит з некоректним ID: " + openDepositId);
            return false;
        }
        try {
            String today = LocalDate.now().toString();
            JSONObject json = new JSONObject();
            json.put("enddate", today);

            String url = BASE_URL + "opendeposits?opendepositid=eq." + openDepositId;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("apikey", API_KEY)
                    .header("Authorization", "Bearer " + API_KEY)
                    .header("Content-Type", "application/json")
                    .method("PATCH", HttpRequest.BodyPublishers.ofString(json.toString()))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            logDebug("closeUserDepositById response: " + response.body());

            boolean success = response.statusCode() == 200 || response.statusCode() == 204;
            if (success) {
                logInfo("Депозит успішно закритий ID: " + openDepositId);
            } else {
                logError("Не вдалося закрити депозит ID: " + openDepositId + " (код: " + response.statusCode() + ")", null);
            }
            return success;
        } catch (Exception e) {
            logError("Не вдалося закрити депозит ID: " + openDepositId, e);
            showAlertSafe("Помилка", "Не вдалося закрити депозит!");
            return false;
        }
    }
    public boolean earlyWithdrawUserDeposit(int openDepositId) {
        logInfo("Спроба дострокового зняття з депозиту ID: " + openDepositId);
        try {
            JSONObject json = new JSONObject();
            json.put("earlywithdrawal", true);
            json.put("enddate", LocalDate.now().toString());

            String url = BASE_URL + "opendeposits?opendepositid=eq." + openDepositId;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("apikey", API_KEY)
                    .header("Authorization", "Bearer " + API_KEY)
                    .header("Content-Type", "application/json")
                    .method("PATCH", HttpRequest.BodyPublishers.ofString(json.toString()))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            logDebug("earlyWithdrawUserDeposit response: " + response.body());

            boolean success = response.statusCode() == 200 || response.statusCode() == 204;
            if (success) {
                logInfo("Дострокове зняття успішне для депозиту ID: " + openDepositId);
            } else {
                logError("Не вдалося виконати дострокове зняття для депозиту ID: " + openDepositId + " (код: " + response.statusCode() + ")", null);
            }
            return success;
        } catch (Exception e) {
            logError("Не вдалося виконати дострокове зняття для депозиту ID: " + openDepositId, e);
            showAlertSafe("Помилка", "Не вдалося виконати дострокове зняття!");
            return false;
        }
    }


    public boolean openUserDeposit(int walletId, int depositId, double startAmount) {
        logInfo("Спроба відкриття депозиту: walletId=" + walletId + ", depositId=" + depositId + ", amount=" + startAmount);
        try {
            // 🔹 1. Перевіряємо, чи депозит уже відкритий для цього користувача
            String checkUrl = BASE_URL + "opendeposits?walletid=eq." + walletId + "&depositid=eq." + depositId;
            HttpRequest checkRequest = HttpRequest.newBuilder()
                    .uri(URI.create(checkUrl))
                    .header("apikey", API_KEY)
                    .header("Authorization", "Bearer " + API_KEY)
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> checkResponse = client.send(checkRequest, HttpResponse.BodyHandlers.ofString());
            if (checkResponse.statusCode() != 200) {
                logError("Не вдалося перевірити стан депозиту для walletId=" + walletId, null);
                showAlertSafe("Помилка", "Не вдалося перевірити стан депозиту!");
                return false;
            }

            JSONArray existing = new JSONArray(checkResponse.body());
            if (!existing.isEmpty()) {
                logWarning("Депозит вже відкритий для користувача: walletId=" + walletId + ", depositId=" + depositId);
                showAlertSafe("Помилка", "Цей депозит уже відкритий для поточного користувача!");
                return false;
            }

            logDebug("Перевірка пройдена успішно - депозит ще не відкритий");

            // 🔹 2. Створюємо новий депозит
            JSONObject json = new JSONObject();
            json.put("walletid", walletId);
            json.put("depositid", depositId);
            json.put("moneyondeposit", startAmount);
            json.put("startdate", java.time.LocalDate.now().toString());

            String url = BASE_URL + "opendeposits";
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("apikey", API_KEY)
                    .header("Authorization", "Bearer " + API_KEY)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json.toString()))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            logDebug("openUserDeposit response: " + response.body());

            if (response.statusCode() == 201 || response.statusCode() == 200) {
                logInfo("Депозит успішно відкрито: walletId=" + walletId + ", depositId=" + depositId + ", сума=" + startAmount);
                showAlertSafe("Успіх", "Депозит успішно відкрито!");
                return true;
            } else {
                logError("Не вдалося відкрити депозит: walletId=" + walletId + ", depositId=" + depositId + " (код: " + response.statusCode() + ")", null);
                showAlertSafe("Помилка", "Не вдалося відкрити депозит!");
                return false;
            }

        } catch (Exception e) {
            logError("Виникла помилка при відкритті депозиту: walletId=" + walletId + ", depositId=" + depositId, e);
            showAlertSafe("Помилка", "Виникла помилка при відкритті депозиту!");
            return false;
        }
    }
    public boolean topUpUserDeposit(int openDepositId, double amount) {
        logInfo("Спроба поповнення депозиту ID: " + openDepositId + " на суму: " + amount);
        try {
            if (amount <= 0) {
                logWarning("Спроба поповнення депозиту з некоректною сумою: " + amount);
                showAlertSafe("Помилка", "Сума має бути більшою за 0!");
                return false;
            }

            // 1. Отримуємо поточний депозит
            String getUrl = BASE_URL + "opendeposits?opendepositid=eq." + openDepositId;
            HttpRequest getRequest = HttpRequest.newBuilder()
                    .uri(URI.create(getUrl))
                    .header("apikey", API_KEY)
                    .header("Authorization", "Bearer " + API_KEY)
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
            if (getResponse.statusCode() != 200) {
                logError("Не вдалося отримати дані депозиту ID: " + openDepositId, null);
                showAlertSafe("Помилка", "Не вдалося отримати дані депозиту!");
                return false;
            }

            JSONArray jsonArray = new JSONArray(getResponse.body());
            if (jsonArray.isEmpty()) {
                logWarning("Депозит не знайдено ID: " + openDepositId);
                showAlertSafe("Помилка", "Депозит не знайдено!");
                return false;
            }

            JSONObject deposit = jsonArray.getJSONObject(0);
            double currentMoney = deposit.getDouble("moneyondeposit");
            double newMoney = currentMoney + amount;

            logDebug("Поточна сума депозиту: " + currentMoney + ", нова сума: " + newMoney);

            // 2. Оновлюємо суму
            JSONObject json = new JSONObject();
            json.put("moneyondeposit", newMoney);

            String patchUrl = BASE_URL + "opendeposits?opendepositid=eq." + openDepositId;
            HttpRequest patchRequest = HttpRequest.newBuilder()
                    .uri(URI.create(patchUrl))
                    .header("apikey", API_KEY)
                    .header("Authorization", "Bearer " + API_KEY)
                    .header("Content-Type", "application/json")
                    .method("PATCH", HttpRequest.BodyPublishers.ofString(json.toString()))
                    .build();

            HttpResponse<String> patchResponse = client.send(patchRequest, HttpResponse.BodyHandlers.ofString());
            logDebug("topUpUserDeposit response: " + patchResponse.body());

            if (patchResponse.statusCode() == 200 || patchResponse.statusCode() == 204) {
                logInfo("Депозит успішно поповнено ID: " + openDepositId + " на суму: " + amount + ". Новий баланс: " + newMoney);
                showAlertSafe("Успіх", "Депозит успішно поповнено на " + amount + "!");
                return true;
            } else {
                logError("Не вдалося оновити депозит ID: " + openDepositId + " (код: " + patchResponse.statusCode() + ")", null);
                showAlertSafe("Помилка", "Не вдалося оновити депозит!");
                return false;
            }

        } catch (Exception e) {
            logError("Не вдалося поповнити депозит ID: " + openDepositId, e);
            showAlertSafe("Помилка", "Не вдалося поповнити депозит!");
            return false;
        }
    }
    public boolean isDepositAlreadyOpenedForUser(int depositId, int userId) {
        logDebug("Перевірка чи відкритий депозит depositId=" + depositId + " для userId=" + userId);
        try {
            String url = BASE_URL + "opendeposits?userid=eq." + userId + "&depositid=eq." + depositId;
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("apikey", API_KEY)
                    .header("Authorization", "Bearer " + API_KEY)
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                logWarning("Помилка при перевірці статусу депозиту depositId=" + depositId + " для userId=" + userId);
                return false;
            }

            JSONArray arr = new JSONArray(response.body());
            boolean isOpened = arr.length() > 0;

            if (isOpened) {
                logDebug("Депозит depositId=" + depositId + " вже відкритий для userId=" + userId);
            } else {
                logDebug("Депозит depositId=" + depositId + " ще не відкритий для userId=" + userId);
            }

            return isOpened;
        } catch (Exception e) {
            logError("Помилка при перевірці чи відкритий депозит depositId=" + depositId + " для userId=" + userId, e);
            return false;
        }
    }


    private void showAlertSafe(String title, String message) {
        try {
            logDebug("Показ сповіщення: [" + title + "] " + message);
            javafx.application.Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle(title);
                alert.setHeaderText(null);
                alert.setContentText(message);
                alert.showAndWait();
            });
        } catch (IllegalStateException e) {
            // Якщо JavaFX не ініціалізований, просто виводимо в консоль
            logWarning("JavaFX не ініціалізований - сповіщення в консоль: [" + title + "] " + message);
            System.out.println("ALERT [" + title + "]: " + message);
        }
    }
    public double getDepositBalance(int openDepositId) {
        logDebug("Запит балансу депозиту ID: " + openDepositId);
        try {
            String requestUrl = BASE_URL + "opendeposits?select=moneyondeposit&opendepositid=eq." + openDepositId;
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(requestUrl))
                    .header("apikey", API_KEY)
                    .header("Authorization", "Bearer " + API_KEY)
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String body = response.body().trim();

            logDebug("getDepositBalance response: " + body);

            if (response.statusCode() != 200 || !body.startsWith("[")) {
                logWarning("Не вдалося отримати баланс депозиту ID: " + openDepositId + " (код: " + response.statusCode() + ")");
                return 0.0;
            }

            JSONArray arr = new JSONArray(body);
            if (arr.length() == 0) {
                logWarning("Депозит не знайдено при отриманні балансу ID: " + openDepositId);
                return 0.0;
            }

            double balance = arr.getJSONObject(0).optDouble("moneyondeposit", 0.0);
            logDebug("Баланс депозиту ID: " + openDepositId + " = " + balance);

            return balance;

        } catch (Exception ex) {
            logError("Помилка при виконанні getDepositBalance() для ID: " + openDepositId, ex);
            return 0.0;
        }
    }

    private HttpRequest buildRequest(String url) {
        logDebug("Створення HTTP запиту до: " + url);
        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("apikey", API_KEY)
                .header("Authorization", "Bearer " + API_KEY)
                .header("Content-Type", "application/json")
                .GET()
                .build();
    }
}
