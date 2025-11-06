package data.api;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
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
            String requestUrl = BASE_URL + "deposits?select=*,banks(*)";

            HttpRequest request = buildRequest(requestUrl);
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Deposits response: " + response.body());

            JSONArray arr = new JSONArray(response.body().trim());
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                JSONObject bankObj = obj.optJSONObject("banks");

                Deposit deposit = new Deposit(
                        obj.getInt("depositid"),
                        obj.getInt("bankid"),
                        obj.getString("name"),
                        obj.getDouble("interestrate"),
                        obj.getInt("termmonths"),
                        obj.getDouble("minamount"),
                        obj.optBoolean("allowtopup", false),
                        obj.optBoolean("earlywithdrawal", false),
                        obj.getString("currency"),
                        obj.optString("description", ""),
                        bankObj != null ? bankObj.optString("name", "Невідомий банк") : "Невідомий банк",
                        bankObj != null ? bankObj.optString("weburl", "") : "",
                        bankObj != null ? bankObj.optString("address", "") : "",
                        bankObj != null ? bankObj.optString("phonenumber", "—") : "—"
                );

                deposits.add(deposit);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
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
            String requestUrl = BASE_URL + "opendeposits"
                    + "?select=*,deposits(*,banks(*)),wallets!inner(userid)"
                    + "&wallets.userid=eq." + userId;

            HttpRequest request = buildRequest(requestUrl);
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String body = response.body().trim();

            System.out.println("getUserDeposits response: " + body);
            if (!body.startsWith("[")) return userDeposits;

            JSONArray arr = new JSONArray(body);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                JSONObject depObj = obj.optJSONObject("deposits");
                if (depObj == null) continue;

                JSONObject bankObj = depObj.optJSONObject("banks");
                String bankName = bankObj != null ? bankObj.optString("name", "Невідомий банк") : "Невідомий банк";

                userDeposits.add(new Deposit(
                        depObj.getInt("depositid"),
                        depObj.getInt("bankid"),
                        depObj.getString("name"),
                        depObj.getDouble("interestrate"),
                        depObj.getInt("termmonths"),
                        depObj.getDouble("minamount"),
                        depObj.optBoolean("allowtopup", false),
                        depObj.optBoolean("earlywithdrawal", false),
                        depObj.getString("currency"),
                        depObj.optString("description", ""),
                        bankName,
                        "", "", ""
                ));
            }

        } catch (Exception ex) {
            ex.printStackTrace();
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
