import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class GameSnakeClient {
    private final String baseUrl;
    private final Gson gson;

    public GameSnakeClient(String baseUrl) {
        this.baseUrl = baseUrl;
        this.gson = new Gson();
    }

    public GameSnakeClient() {
        this("http://localhost:8080");
    }

    public int getSize() {
        try {
            String response = sendGetRequest("/getSize");
            return Integer.parseInt(response);
        } catch (Exception e) {
            System.err.println("Ошибка при получении размера: " + e.getMessage());
            return 20; // значение по умолчанию
        }
    }

    public void moveSnake() {
        try {
            sendPostRequest("/moveSnake", null);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при движении змейки: " + e.getMessage());
        }
    }

    public List<Coordinates> getSnakeCoordinates() {
        try {
            String response = sendGetRequest("/getSnakeCoordinates");
            return gson.fromJson(response, new TypeToken<List<Coordinates>>(){}.getType());
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при получении координат змейки: " + e.getMessage());
        }
    }

    public List<Coordinates> getAppleCoordinates() {
        try {
            String response = sendGetRequest("/getAppleCoordinates");
            return gson.fromJson(response, new TypeToken<List<Coordinates>>(){}.getType());
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при получении координат яблока: " + e.getMessage());
        }
    }

    public void turnTo(Direction direction) {
        try {
            String json = gson.toJson(direction);
            sendPostRequest("/turnTo", json);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при повороте: " + e.getMessage());
        }
    }

    public boolean isServerAvailable() {
        try {
            sendGetRequest("/getSize");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private String sendGetRequest(String endpoint) throws IOException {
        URL url = new URL(baseUrl + endpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            return readResponse(connection);
        } else {
            throw new IOException("HTTP error code: " + responseCode);
        }
    }

    private void sendPostRequest(String endpoint, String jsonBody) throws IOException {
        URL url = new URL(baseUrl + endpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);

        if (jsonBody != null) {
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            try (OutputStream os = connection.getOutputStream();
                 OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8")) {
                osw.write(jsonBody);
                osw.flush();
            }
        }

        int responseCode = connection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new IOException("HTTP error code: " + responseCode);
        }
    }

    private String readResponse(HttpURLConnection connection) throws IOException {
        try (InputStream inputStream = connection.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        }
    }

    public String getBaseUrl() {
        return baseUrl;
    }
}