package data;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import domain.banks.Bank;
import domain.deposits.Deposit;
import domain.users.User;
import domain.users.UserSession;
import org.json.JSONArray;
import org.json.JSONObject;

public class APIrequester {

    private final String url;
    private final String apiKey;
    private final String depositsUrl;
    private final String banksUrl;

    public APIrequester() {
        // Вкажи свій проект Supabase та таблиці
        this.url = "https://amslaoqjbyvpnvsohmij.supabase.co/rest/v1/users";
        this.depositsUrl = "https://amslaoqjbyvpnvsohmij.supabase.co/rest/v1/deposits?select=*,banks(*)";
        this.banksUrl = "https://amslaoqjbyvpnvsohmij.supabase.co/rest/v1/banks";
        this.apiKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImFtc2xhb3FqYnl2cG52c29obWlqIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjE2ODk2ODMsImV4cCI6MjA3NzI2NTY4M30.Am9TFNEt9dttolGpum7Q99oTbTLsqxq7FcsdZSJnpTA";
    }

    public User loginUser(String login, String password) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            String requestUrl = url + "?login=eq." + login + "&password=eq." + password;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(requestUrl))
                    .header("apikey", apiKey)
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JSONArray arr = new JSONArray(response.body().trim());

            if (arr.isEmpty()) return null;

            JSONObject obj = arr.getJSONObject(0);
            int userId = obj.getInt("userid");
            boolean isAdmin = obj.optBoolean("isadmin", false);

            User user = new User(userId, login, isAdmin);

            // зберігаємо в сесію
            UserSession.getInstance().login(user);

            return user;

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    public User checkUser(String login, String password) {
        try {
            HttpClient client = HttpClient.newHttpClient();

            String requestUrl = url + "?login=eq." + login + "&password=eq." + password;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(requestUrl))
                    .header("apikey", apiKey)
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            String body = response.body().trim();
            System.out.println("Supabase response: " + body);

            if (!body.startsWith("[")) {
                System.out.println("Unexpected response from Supabase API");
                return null;
            }

            JSONArray arr = new JSONArray(body);

            if (arr.isEmpty()) {
                return null;
            }

            JSONObject obj = arr.getJSONObject(0);
            boolean isAdmin = obj.optBoolean("isadmin", false);
            int userId = obj.optInt("userid");

            return new User(userId, login, isAdmin);

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }


    public List<Deposit> getDeposits() {
        List<Deposit> depositList = new ArrayList<>();
        try {
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(depositsUrl))
                    .header("apikey", apiKey)
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Response: " + response.body());

            JSONArray arr = new JSONArray(response.body().trim());

            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);

                JSONObject bankObj = obj.optJSONObject("banks");
                String bankName = (bankObj != null) ? bankObj.optString("name", "Невідомий банк") : "Невідомий банк";
                String bankWeb = (bankObj != null) ? bankObj.optString("weburl", "") : "";
                String bankAddr = (bankObj != null) ? bankObj.optString("address", "") : "";
                String bankPhone = (bankObj != null) ? bankObj.optString("phonenumber", "") : "";

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
                        bankName,
                        bankWeb,
                        bankAddr,
                        bankPhone
                );

                depositList.add(deposit);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return depositList;
    }
    public List<Bank> getAllBanks() {
        List<Bank> banks = new ArrayList<>();
        try {
            HttpClient client = HttpClient.newHttpClient();
            String requestUrl = "https://amslaoqjbyvpnvsohmij.supabase.co/rest/v1/banks?select=*";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(requestUrl))
                    .header("apikey", apiKey)
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Banks response: " + response.body());

            JSONArray arr = new JSONArray(response.body().trim());
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);

                Bank bank = new Bank(
                        obj.optInt("bankid", -1),
                        obj.optString("name", "Невідомий банк"),
                        obj.optString("address", "Немає адреси"),
                        obj.optString("weburl", ""),
                        obj.optString("phonenumber", "—")
                );
                banks.add(bank);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return banks;
    }
    public List<Deposit> getUserDeposits(int userId) {
        List<Deposit> userDeposits = new ArrayList<>();
        try {
            HttpClient client = HttpClient.newHttpClient();

            // ✅ Тепер без фільтру в URL, просто забираємо все
            String requestUrl = "https://amslaoqjbyvpnvsohmij.supabase.co/rest/v1/opendeposits"
                    + "?select=deposits(*,banks(*)),wallets(userid)";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(requestUrl))
                    .header("apikey", apiKey)
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String body = response.body().trim();
            System.out.println("getUserDeposits response: " + body);

            if (!body.startsWith("[")) {
                System.out.println("⚠️ Неправильна відповідь від Supabase: " + body);
                return userDeposits;
            }

            JSONArray arr = new JSONArray(body);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                JSONObject walletObj = obj.optJSONObject("wallets");
                if (walletObj == null || walletObj.optInt("userid", -1) != userId)
                    continue; // фільтруємо вручну

                JSONObject depObj = obj.optJSONObject("deposits");
                if (depObj == null) continue;

                JSONObject bankObj = depObj.optJSONObject("banks");
                String bankName = (bankObj != null) ? bankObj.optString("name", "Невідомий банк") : "Невідомий банк";

                Deposit deposit = new Deposit(
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
                );

                userDeposits.add(deposit);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return userDeposits;
    }


    public boolean isOpened(int userId, int depositId) {
        try {
            HttpClient client = HttpClient.newHttpClient();

            String requestUrl = "https://amslaoqjbyvpnvsohmij.supabase.co/rest/v1/opened_deposits" +
                    "?userid=eq." + userId + "&depositid=eq." + depositId;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(requestUrl))
                    .header("apikey", apiKey)
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JSONArray arr = new JSONArray(response.body().trim());

            return !arr.isEmpty();

        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
}
