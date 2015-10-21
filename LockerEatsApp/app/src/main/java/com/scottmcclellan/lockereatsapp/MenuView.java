package com.scottmcclellan.lockereatsapp;

import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.iid.InstanceID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;


public class MenuView extends ListActivity {

    private Button sendOrder;
    //private Product[] menuList;
    private static final String LockerEatsProductAPI = "http://agglo.mooo.com:3000/api/v1/products";
    private static final String LockerEatsOrderAPI = "http://agglo.mooo.com:3000/api/v1/orders";
    //private static final String LockerEatsProductAPI = "http://192.168.1.13:3000/api/v1/products";
    //private static final String LockerEatsOrderAPI = "http://192.168.1.13:3000/api/v1/orders";
    protected ArrayAdapter<Product> myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_view);
        myAdapter = new ArrayAdapter<Product>(this, android.R.layout.simple_list_item_1);
        (new ProductDownloader()).execute();


        sendOrder = (Button) findViewById(R.id.sendOrder);

        sendOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MenuView.this, "Your order has been sent!", Toast.LENGTH_SHORT).show();
                (new OrderSubmitter()).execute(MainActivity.order);

                Intent orderView = new Intent(MenuView.this, OrderView.class);
                startActivity(orderView);
                //startActivity(new Intent(MenuView.this, MainActivity.class));
            }
        });
    }


    protected void onListItemClick(ListView parent, View v, int position, long id) {
        super.onListItemClick(parent, v, position, id);
        int set = 0;
        if (MainActivity.order.items.contains(myAdapter.getItem(position))) {
            ((TextView) v).setText(myAdapter.getItem(position).toString());
            MainActivity.order.removeItem(myAdapter.getItem(position));
            Toast.makeText(MenuView.this, myAdapter.getItem(position) + " has been removed from your order.", Toast.LENGTH_SHORT).show();
        } else {
            ((TextView) v).setText(((TextView) v).getText().toString() + "(selected)");
            Toast.makeText(MenuView.this, myAdapter.getItem(position) + " has been added to your order.", Toast.LENGTH_SHORT).show();
            MainActivity.order.addItem(myAdapter.getItem(position));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_menu_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class OrderSubmitter extends AsyncTask<Order, Void, Void> {

        @Override
        protected Void doInBackground(Order... params) {

            Order order = params[0];
            JSONObject jsonOrder = new JSONObject();
            try {


                jsonOrder.put("restaurant", order.getRestaurant());
                jsonOrder.put("customer", order.getEmail());
                jsonOrder.put("gcmToken", MainActivity.order.getGcmRegistrationId());

                JSONArray productArray = new JSONArray();
                for (Product p : order.items) {
                    JSONObject products = new JSONObject();
                    products.put("id", p.getId());
                    productArray.put(products);
                }
                jsonOrder.put("products", productArray);

                URL url = new URL(LockerEatsOrderAPI);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(true);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");

                OutputStream os = connection.getOutputStream();

                os.write(jsonOrder.toString().getBytes());
                os.flush();

                if (connection.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
                    throw new RuntimeException("Failed : HTTP error code : "
                            + connection.getResponseCode());
                }

                BufferedReader br = new BufferedReader(new InputStreamReader(
                        (connection.getInputStream())));

                String output, res;
                res = new String();
                System.out.println("Output from Server .... \n");
                while ((output = br.readLine()) != null) {
                    System.out.println(output);
                    res += output;
                }

                JSONObject result = new JSONObject(res);
                MainActivity.order.setOrderId(result.getInt("id"));

                connection.disconnect();


            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println(jsonOrder.toString());






            return null;
        }
    }



    private class ProductDownloader extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL(LockerEatsProductAPI);
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
                   myAdapter.add(new Product(e.getInt("id"), e.getString("name"), e.getDouble("price")));
                }



                // This value will be 404 if the request was not
                // successful

            } catch (Exception e) {
                System.err.println(e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            ListView myList = (ListView) findViewById(android.R.id.list);
            myList.setAdapter(myAdapter);
        }
    }

}
