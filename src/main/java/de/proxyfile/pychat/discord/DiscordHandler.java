package de.proxyfile.pychat.discord;

import org.json.simple.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class DiscordHandler {

    public void sendWebhook(JSONObject payload, String webhookUrl) throws IOException {
        URL url = new URL(webhookUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        OutputStream os = connection.getOutputStream();
        byte[] input = payload.toString().getBytes(StandardCharsets.UTF_8);
        os.write(input, 0, input.length);

        int responseCode = connection.getResponseCode();
        if (responseCode != 204) {
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
        }
    }

}
