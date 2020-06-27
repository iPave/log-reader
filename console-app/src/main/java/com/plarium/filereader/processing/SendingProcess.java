package com.plarium.filereader.processing;

import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.logging.Logger;

public class SendingProcess implements Process<Map<String, ArrayNode>, Boolean> {

    private static final Logger logger = Logger.getLogger(SendingProcess.class.getName());

    private String apiUrl;

    public SendingProcess(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    @Override
    public Boolean process(Map<String, ArrayNode> input) {

        for (Map.Entry<String, ArrayNode> groupedLines : input.entrySet()) {
            try {
                URL url = new URL(apiUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json; utf-8");
                connection.setRequestProperty("Accept", "application/json");
                connection.setDoOutput(true);
                try (OutputStream outputStream = connection.getOutputStream()) {
                    byte[] byteInput = groupedLines.getValue().toString().getBytes("utf-8");
                    outputStream.write(byteInput, 0, byteInput.length);
                }
                try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = bufferedReader.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    System.out.println(response.toString());
                }
            } catch (IOException e) {
                logger.severe(String.format("Sending of batch failed, error occured: %s ", e.getMessage()));
                return false;
            }
        }

        return true;
    }
}
