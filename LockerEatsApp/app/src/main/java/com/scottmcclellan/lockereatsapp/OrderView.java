package com.scottmcclellan.lockereatsapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class OrderView extends AppCompatActivity {

    TextView txtResult;
    ImageView imgViewQrCode;
    protected JSONObject data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        (new OrderDownloader()).execute();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        txtResult = (TextView) findViewById(R.id.txtOrder);
        imgViewQrCode = (ImageView) findViewById(R.id.imgViewQrCode);
    }


    private class OrderDownloader extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL("http://agglo.mooo.com:3000/api/v1/orders/" + MainActivity.order.getOrderId());
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

                data = new JSONObject(json.toString());


                // This value will be 404 if the request was not
                // successful

            } catch (Exception e) {
                System.err.println(e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                if (data.getString("extQr") == "null") {
                    txtResult.setText("Your order has been placed, we will notify you when it's ready.");
                } else {
                    txtResult.setText("Please scan the following code to get your food");
                    String[] rawBase64 = data.getString("extQrImage").split(",");
                    byte[] decodedString = Base64.decode(rawBase64[1], Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    imgViewQrCode.setImageBitmap(decodedByte);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //ListView myList = (ListView) findViewById(android.R.id.list);
            //myList.setAdapter(myAdapter);
        }
    }


}
