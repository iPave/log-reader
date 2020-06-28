package com.plarium.logreader.processing;

import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

public class SendingProcess implements Process<ArrayNode, Boolean> {

    private static final Logger logger = Logger.getLogger(SendingProcess.class.getName());

    private String apiUrl;

    public SendingProcess(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    @Override
    public Boolean process(ArrayNode input) {
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);
            try (OutputStream outputStream = connection.getOutputStream()) {
                byte[] byteInput = input.toString().getBytes(StandardCharsets.UTF_8);
                outputStream.write(byteInput, 0, byteInput.length);
            }
            StringBuilder response = new StringBuilder();
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                String responseLine;
                while ((responseLine = bufferedReader.readLine()) != null) {
                    response.append(responseLine.trim());
                }
            }
            int code = connection.getResponseCode();
            if (code != 200) {
                logger.severe(String.format("Sending of batch failed with respose code: %s and message: %s", code, response));
                return false;
            }
        } catch (IOException e) {
            logger.severe(String.format("Sending of batch failed, error occured: %s ", e.getMessage()));
            return false;
        }
        return true;
    }
}
