package com.scottmcclellan.lockereatsapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PrevOrders extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
    ArrayAdapter<Order> orderArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prev_orders);
        sharedPreferences = getSharedPreferences("LockerEats", Context.MODE_PRIVATE);
        Set<String> ordersJson = sharedPreferences.getStringSet("previousOrders", new HashSet<String>());

        ListView listOrders = (ListView) findViewById(R.id.listViewPrevOrders);
        List<Order> orders = new ArrayList<>();
        for(String s : ordersJson) {
            try {
                JSONObject j = new JSONObject(s);
                Order order = new Order();
                order.setOrderId(j.getInt("id"));
                order.setSubmittedTime(sdf.parse(j.getString("date")));
                orders.add(order);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
           orderArrayAdapter = new ArrayAdapter<Order>(this, android.R.layout.simple_list_item_1, orders.toArray(new Order[0]));
            listOrders.setAdapter(orderArrayAdapter);
        }

        listOrders.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MainActivity.order.setOrderId(orderArrayAdapter.getItem(position).orderId);
                startActivity(new Intent(getApplicationContext(), OrderView.class));

            }
        });





    }
}
