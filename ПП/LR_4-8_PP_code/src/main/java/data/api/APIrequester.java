package data.api;

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
