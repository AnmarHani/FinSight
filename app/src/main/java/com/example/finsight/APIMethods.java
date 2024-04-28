package com.example.finsight;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public class APIMethods {
    public static String HOST = "192.168.3.6";
    public static String PORT = "8125";
    public static String CONNECTION_URL = "http://" + HOST + ":" + PORT;

    public static String username = "Unknown";

    public static String AUTHORIZATION_KEY = "";
    public static JSONObject get(String link) throws JSONException {
        FutureTask<String> futureTask = new FutureTask<>(new Callable<String>() {
            @Override
            public String call() throws Exception {
                StringBuilder response = new StringBuilder();
                try {
                    URL url = new URL(link); // Your API endpoint
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    if(!APIMethods.AUTHORIZATION_KEY.equals("")) connection.setRequestProperty("Authorization", "Bearer " + AUTHORIZATION_KEY);
                    System.out.println("KEY: " +connection.getRequestProperty("Authorization"));
                    // Read the response
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    return response.toString();
                } catch (Exception e) {
                    e.printStackTrace();
                    return "{\"error\":\"No Response\"}";
                }
            }
        });

        new Thread(futureTask).start();

        try {
            return new JSONObject(futureTask.get()); // This will wait for the task to finish
        } catch (Exception e) {
            e.printStackTrace();
            return new JSONObject("{\" error\" : \"" + e.toString() + "\"}");
        }
    }

    public static JSONObject post(String link, String body) throws JSONException {
        FutureTask<String> futureTask = new FutureTask<>(new Callable<String>() {
            @Override
            public String call() throws Exception {
                StringBuilder response = new StringBuilder();
                try {
                    URL url = new URL(link); // Your API endpoint
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setDoOutput(true);
                    connection.setRequestProperty("Content-Type", "application/json; utf-8");
                    if(!APIMethods.AUTHORIZATION_KEY.equals("")) connection.setRequestProperty("Authorization", "Bearer " + AUTHORIZATION_KEY);
                    connection.getOutputStream().write(body.getBytes(StandardCharsets.UTF_8));

                    // Read the response
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    return response.toString();
                } catch (Exception e) {
                    e.printStackTrace();
                    return "{\"error\":\"No Response\"}";
                }
            }
        });

        new Thread(futureTask).start();

        try {
            return new JSONObject(futureTask.get()); // This will wait for the task to finish
        } catch (Exception e) {
            e.printStackTrace();
            return new JSONObject("{\" error\" : \"" + e.toString() + "\"}");
        }
    }

    public static JSONObject put(String link, String body) throws JSONException {
        FutureTask<String> futureTask = new FutureTask<>(new Callable<String>() {
            @Override
            public String call() throws Exception {
                StringBuilder response = new StringBuilder();
                try {
                    URL url = new URL(link); // Your API endpoint
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("PUT");
                    connection.setDoOutput(true);
                    connection.setRequestProperty("Content-Type", "application/json; utf-8");
                    if(!APIMethods.AUTHORIZATION_KEY.equals("")) connection.setRequestProperty("Authorization", "Bearer " + AUTHORIZATION_KEY);
                    connection.getOutputStream().write(body.getBytes(StandardCharsets.UTF_8));

                    // Read the response
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    return response.toString();
                } catch (Exception e) {
                    e.printStackTrace();
                    return "{\"error\":\"No Response\"}";
                }
            }
        });

        new Thread(futureTask).start();

        try {
            return new JSONObject(futureTask.get()); // This will wait for the task to finish
        } catch (Exception e) {
            e.printStackTrace();
            return new JSONObject("{\" error\" : \"" + e.toString() + "\"}");
        }
    }

    public static JSONObject delete(String link) throws JSONException {
        FutureTask<String> futureTask = new FutureTask<>(new Callable<String>() {
            @Override
            public String call() throws Exception {
                StringBuilder response = new StringBuilder();
                try {
                    URL url = new URL(link); // Your API endpoint
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("DELETE");
                    if(!APIMethods.AUTHORIZATION_KEY.equals("")) connection.setRequestProperty("Authorization", "Bearer " + AUTHORIZATION_KEY);
                    // Read the response
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    return response.toString();
                } catch (Exception e) {
                    e.printStackTrace();
                    return "{\"error\":\"No Response\"}";
                }
            }
        });

        new Thread(futureTask).start();

        try {
            return new JSONObject(futureTask.get()); // This will wait for the task to finish
        } catch (Exception e) {
            e.printStackTrace();
            return new JSONObject("{\" error\" : \"" + e.toString() + "\"}");
        }
    }

}
