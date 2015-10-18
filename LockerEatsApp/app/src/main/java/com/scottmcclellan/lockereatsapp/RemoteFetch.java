package com.scottmcclellan.lockereatsapp;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by abuchmann on 14.10.2015.
 */
public class RemoteFetch {
    private static final String LockerEatsWebAPI =
            "http://agglo.mooo.com:8080/api/v1/products";
    protected static List<Product> results = new ArrayList<>();

    public static void submitOrder(Order order) {
        JSONObject jsonOrder = new JSONObject();
        try {


            jsonOrder.put("restaurant", order.getRestaurant());
            jsonOrder.put("customer", order.getEmail());

            JSONArray productArray = new JSONArray();
            for (Product p : order.items) {
                JSONObject products = new JSONObject();
                products.put("id", p.getId());
                productArray.put(products);
            }
            jsonOrder.put("products", productArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println(jsonOrder.toString());

    }


    public static List<Product> getProducts() {
        results.clear();


        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    URL url = new URL(LockerEatsWebAPI);
                    HttpURLConnection connection =
                            (HttpURLConnection) url.openConnection();

                    //connection.addRequestProperty("x-api-key", context.getString(R.string.open_weather_maps_app_id));

                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(connection.getInputStream()));

                    StringBuffer json = new StringBuffer(1024);
                    String tmp = "";
                    while ((tmp = reader.readLine()) != null)
                        json.append(tmp).append("\n");
                    reader.close();

                    //JSONObject data = new JSONObject(json.toString());
                    JSONArray products = new JSONArray(json.toString());

                    for (int i = 0; i < products.length(); i++) {
                        JSONObject e = products.getJSONObject(i);
                        results.add(new Product(e.getInt("id"), e.getString("name"), e.getDouble("price")));
                    }

                    // This value will be 404 if the request was not
                    // successful

                } catch (Exception e) {
                    System.err.println(e);
                }

            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return results;

    }
}
