package com.jmu.shikou.utils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    public static String Post(String url, String payload) throws Exception {
        URL target_url = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) target_url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        byte[] requestData = payload.getBytes("UTF-8");
        int requestLength = requestData.length;

        connection.connect();
        OutputStream output = connection.getOutputStream();
        send(output, requestData);

        int responseCode = connection.getResponseCode();
        InputStream input = connection.getInputStream();
        String response = read(input);

        input.close();
        output.close();
        connection.disconnect();
        return response.toString();
    }

    public static String Post(String url, HashMap<String, Object> map, String payload) throws Exception {
        URL target_url = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) target_url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        for(Map.Entry item: map.entrySet()){
            connection.setRequestProperty(item.getKey().toString(), item.getValue().toString());
        }
        connection.setDoOutput(true);

        byte[] requestData = payload.getBytes("UTF-8");
        int requestLength = requestData.length;

        connection.connect();
        OutputStream output = connection.getOutputStream();
        send(output, requestData);

        int responseCode = connection.getResponseCode();
        InputStream input = connection.getInputStream();
        String response = read(input);

        input.close();
        output.close();
        connection.disconnect();
        return response.toString();
    }

    static void send(OutputStream output, byte[] data) throws IOException {
        output.write(data);
        output.flush();
    }

    static String read(InputStream input) throws IOException{
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        String line;
        StringBuilder response = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        return response.toString();
    }
}
