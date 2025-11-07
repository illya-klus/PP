package data.api;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
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


    public User loginUser(String login, String password) {
        try {
            String requestUrl = BASE_URL + "users?login=eq." + login + "&password=eq." + password;

            HttpRequest request = buildRequest(requestUrl);
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            JSONArray arr = new JSONArray(response.body().trim());
            if (arr.isEmpty()) return null;

            JSONObject obj = arr.getJSONObject(0);
            int userId = obj.getInt("userid");
            boolean isAdmin = obj.optBoolean("isadmin", false);

            User user = new User(userId, login, isAdmin);
            UserSession.getInstance().login(user);

            return user;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    public User checkUser(String login, String password) {
        try {
            String requestUrl = BASE_URL + "users?login=eq." + login + "&password=eq." + password;

            HttpRequest request = buildRequest(requestUrl);
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            String body = response.body().trim();
            System.out.println("Supabase response: " + body);

            if (!body.startsWith("[")) return null;
            JSONArray arr = new JSONArray(body);
            if (arr.isEmpty()) return null;

            JSONObject obj = arr.getJSONObject(0);
            return new User(obj.getInt("userid"), login, obj.optBoolean("isadmin", false));

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }


    public List<Deposit> getDeposits() {
        List<Deposit> deposits = new ArrayList<>();

        try {
            // Отримуємо депозити разом із даними банку
            String requestUrl = BASE_URL + "deposits?select=depositid,bankid,name,interestrate,termmonths,minamount,allowtopup,earlywithdrawal,currency,description,banks(name,weburl,address,phonenumber)";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(requestUrl))
                    .header("apikey", API_KEY)
                    .header("Authorization", "Bearer " + API_KEY)
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("getDeposits response: " + response.body());

            if (response.statusCode() != 200) {
                showAlert("Помилка", "Не вдалося завантажити депозити (код: " + response.statusCode() + ")");
                return deposits;
            }

            JSONArray arr = new JSONArray(response.body().trim());
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

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Помилка", "Сталася помилка при отриманні депозитів!");
        }

        return deposits;
    }
    public List<Bank> getAllBanks() {
        List<Bank> banks = new ArrayList<>();
        try {
            String requestUrl = BASE_URL + "banks?select=*";

            HttpRequest request = buildRequest(requestUrl);
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Banks response: " + response.body());

            JSONArray arr = new JSONArray(response.body().trim());
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

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return banks;
    }
    public List<Deposit> getUserDeposits(int userId) {
        List<Deposit> userDeposits = new ArrayList<>();

        try {
            // Запит: відкриті депозити користувача з повною інформацією про депозит і банк
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

            System.out.println("getUserDeposits response: " + body);

            if (response.statusCode() != 200 || !body.startsWith("[")) {
                System.out.println("⚠️ Не вдалося отримати депозити користувача (код: " + response.statusCode() + ")");
                return userDeposits;
            }

            JSONArray arr = new JSONArray(body);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                JSONObject depObj = obj.optJSONObject("deposits");
                if (depObj == null) continue;

                JSONObject bankObj = depObj.optJSONObject("banks");

                // --- Інфо про банк ---
                String bankName = bankObj != null ? bankObj.optString("name", "Невідомий банк") : "Невідомий банк";
                String bankUrl = bankObj != null ? bankObj.optString("weburl", "") : "";
                String bankAddr = bankObj != null ? bankObj.optString("address", "") : "";
                String bankPhone = bankObj != null ? bankObj.optString("phonenumber", "—") : "—";

                // --- Інфо про депозит ---
                int openDepositId = obj.optInt("opendepositid", -1);
                double moneyOnDeposit = obj.optDouble("moneyondeposit", 0.0);

            // 🔥 Ключова зміна — правильна перевірка на null
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

        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("❌ Помилка при виконанні getUserDeposits()");
        }

        return userDeposits;
    }

    public List<User> findUser(String login, String password, boolean isAdmin) {
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
            System.out.println("findUser response: " + body);

            if (!body.startsWith("[")) return users;

            JSONArray arr = new JSONArray(body);
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
            e.printStackTrace();
        }

        return users;
    }
    public List<Bank> findBanks(String name, String address, String weburl, String phoneNumber) {
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
            System.out.println("findBanks response: " + body);

            if (!body.startsWith("[")) return banks;

            JSONArray arr = new JSONArray(body);
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
            e.printStackTrace();
        }

        return banks;
    }
    public List<Deposit> findDeposits(String name, Integer bankId, Double rate, String currency) {
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
            System.out.println("findDeposits response: " + body);

            if (!body.startsWith("[")) return deposits;

            JSONArray arr = new JSONArray(body);
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
            e.printStackTrace();
        }

        return deposits;
    }


    public boolean addUser(String login, String password, boolean isAdmin) {
        try {
            if (login == null || login.isEmpty() || password == null || password.isEmpty()) {
                showAlert("Помилка", "Усі поля користувача повинні бути заповнені!");
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
            System.out.println("addUser response: " + response.body());

            return response.statusCode() == 201; // Created
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Помилка", "Не вдалося додати користувача!");
            return false;
        }
    }
    public boolean addBank(String name, String address, String webUrl, String phone) {
        try {
            if (name == null || name.isEmpty() ||
                    address == null || address.isEmpty() ||
                    webUrl == null || webUrl.isEmpty() ||
                    phone == null || phone.isEmpty()) {
                showAlert("Помилка", "Усі поля банку повинні бути заповнені!");
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
            System.out.println("addBank response: " + response.body());

            return response.statusCode() == 201;
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Помилка", "Не вдалося додати банк!");
            return false;
        }
    }
    public boolean addDeposit(String name, Integer bankId, Double rate, Integer term, Double minAmount, boolean canTopUp, boolean canWithdrawEarly, String currency) {
        try {
            if (name == null || name.isEmpty() ||
                    bankId == null || bankId <= 0 ||
                    rate == null || term == null || minAmount == null ||
                    currency == null || currency.isEmpty()) {
                showAlert("Помилка", "Не всі поля депозиту заповнені або мають некоректні значення!");
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
            System.out.println("addDeposit response: " + response.body());

            return response.statusCode() == 201;
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Помилка", "Не вдалося додати депозит!");
            return false;
        }
    }


    public boolean deleteUser(int userId) {
        try {
            if (userId <= 0) {
                showAlert("Помилка", "Некоректний ID користувача!");
                return false;
            }

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "users?userid=eq." + userId))
                    .header("apikey", API_KEY)
                    .DELETE()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("deleteUser response: " + response.body());

            if (response.statusCode() == 200 || response.statusCode() == 204) {
                return true;
            } else {
                showAlert("Помилка", "Не вдалося видалити користувача!");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Помилка", "Не вдалося видалити користувача!");
            return false;
        }
    }
    public boolean deleteBank(int bankId) {
        try {
            if (bankId <= 0) {
                showAlert("Помилка", "Некоректний ID банку!");
                return false;
            }

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "banks?bankid=eq." + bankId))
                    .header("apikey", API_KEY)
                    .DELETE()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("deleteBank response: " + response.body());

            if (response.statusCode() == 200 || response.statusCode() == 204) {
                return true;
            } else {
                showAlert("Помилка", "Не вдалося видалити банк!");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Помилка", "Не вдалося видалити банк!");
            return false;
        }
    }
    public boolean deleteDeposit(int depositId) {
        try {
            if (depositId <= 0) {
                showAlert("Помилка", "Некоректний ID депозиту!");
                return false;
            }

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "deposits?depositid=eq." + depositId))
                    .header("apikey", API_KEY)
                    .DELETE()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("deleteDeposit response: " + response.body());

            if (response.statusCode() == 200 || response.statusCode() == 204) {
                return true;
            } else {
                showAlert("Помилка", "Не вдалося видалити депозит!");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Помилка", "Не вдалося видалити депозит!");
            return false;
        }
    }



    public boolean closeUserDepositById(int openDepositId) {
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
            System.out.println("closeUserDepositById response: " + response.body());

            return response.statusCode() == 200 || response.statusCode() == 204;
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Помилка", "Не вдалося закрити депозит!");
            return false;
        }
    }
    public boolean earlyWithdrawUserDeposit(int openDepositId) {
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
            System.out.println("earlyWithdrawUserDeposit response: " + response.body());

            return response.statusCode() == 200 || response.statusCode() == 204;
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Помилка", "Не вдалося виконати дострокове зняття!");
            return false;
        }
    }
    public boolean openUserDeposit(int walletId, int depositId, double startAmount) {
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
                showAlert("Помилка", "Не вдалося перевірити стан депозиту!");
                return false;
            }

            JSONArray existing = new JSONArray(checkResponse.body());
            if (!existing.isEmpty()) {
                showAlert("Помилка", "Цей депозит уже відкритий для поточного користувача!");
                return false;
            }

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
            System.out.println("openUserDeposit response: " + response.body());

            if (response.statusCode() == 201 || response.statusCode() == 200) {
                showAlert("Успіх", "Депозит успішно відкрито!");
                return true;
            } else {
                showAlert("Помилка", "Не вдалося відкрити депозит!");
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Помилка", "Виникла помилка при відкритті депозиту!");
            return false;
        }
    }
    public boolean topUpUserDeposit(int openDepositId, double amount) {
        try {
            if (amount <= 0) {
                showAlert("Помилка", "Сума має бути більшою за 0!");
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
                showAlert("Помилка", "Не вдалося отримати дані депозиту!");
                return false;
            }

            JSONArray jsonArray = new JSONArray(getResponse.body());
            if (jsonArray.isEmpty()) {
                showAlert("Помилка", "Депозит не знайдено!");
                return false;
            }

            JSONObject deposit = jsonArray.getJSONObject(0);
            double currentMoney = deposit.getDouble("moneyondeposit");
            double newMoney = currentMoney + amount;

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
            System.out.println("topUpUserDeposit response: " + patchResponse.body());

            if (patchResponse.statusCode() == 200 || patchResponse.statusCode() == 204) {
                showAlert("Успіх", "Депозит успішно поповнено на " + amount + "!");
                return true;
            } else {
                showAlert("Помилка", "Не вдалося оновити депозит!");
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Помилка", "Не вдалося поповнити депозит!");
            return false;
        }
    }

    public boolean isDepositAlreadyOpenedForUser(int depositId, int userId) {
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
            if (response.statusCode() != 200) return false;

            JSONArray arr = new JSONArray(response.body());
            return arr.length() > 0; // якщо є хоча б один запис — вже відкрито
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public double getWalletBalance(int userId) {
        try {
            String requestUrl = BASE_URL + "wallets?select=money&userid=eq." + userId;
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(requestUrl))
                    .header("apikey", API_KEY)
                    .header("Authorization", "Bearer " + API_KEY)
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String body = response.body().trim();

            System.out.println("getWalletBalance response: " + body);

            if (response.statusCode() != 200 || !body.startsWith("[")) return 0.0;

            JSONArray arr = new JSONArray(body);
            if (arr.length() == 0) return 0.0;

            return arr.getJSONObject(0).optDouble("money", 0.0);

        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("❌ Помилка при виконанні getWalletBalance()");
            return 0.0;
        }
    }
    public double getDepositBalance(int openDepositId) {
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

            System.out.println("getDepositBalance response: " + body);

            if (response.statusCode() != 200 || !body.startsWith("[")) return 0.0;

            JSONArray arr = new JSONArray(body);
            if (arr.length() == 0) return 0.0;

            return arr.getJSONObject(0).optDouble("moneyondeposit", 0.0);

        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("❌ Помилка при виконанні getDepositBalance()");
            return 0.0;
        }
    }



    private void showAlert(String title, String message) {
        javafx.application.Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    private HttpRequest buildRequest(String url) {
        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("apikey", API_KEY)
                .header("Authorization", "Bearer " + API_KEY)
                .header("Content-Type", "application/json")
                .GET()
                .build();
    }
}
