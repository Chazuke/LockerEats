package com.scottmcclellan.lockereatsapp;

import android.app.ListActivity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MenuView extends ListActivity {

    private Button sendOrder;
    String[] menuList = {"Item A", "Item B", "Item C", "Item D", "Item E", "Item F", "Item G", "Item H"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_view);

        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, menuList);
        ListView myList = (ListView) findViewById(android.R.id.list);
        myList.setAdapter(myAdapter);

        sendOrder = (Button) findViewById(R.id.sendOrder);

        sendOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MenuView.this, "Your order has been sent!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MenuView.this, MainActivity.class));
            }
        });
    }

    protected void onListItemClick(ListView parent, View v, int position, long id) {
        super.onListItemClick(parent, v, position, id);
        int set = 0;
        if (((TextView)v).getText().toString() != "selected") {
            ((TextView)v).setText("selected");
            set = 1;
        }
        if (((TextView)v).getText().toString() == "selected" && set == 0) {
            ((TextView)v).setText(menuList[position]);
            MainActivity.order.removeItem(menuList[position]);
            Toast.makeText(MenuView.this, menuList[position] + " has been removed from your order.", Toast.LENGTH_SHORT).show();
        }
        else if (position == 0) {
            MainActivity.order.setRestaurant(menuList[position]);
            Toast.makeText(MenuView.this, menuList[position] + " has been added to your order.", Toast.LENGTH_SHORT).show();
        }
        else if (position == 1) {
            MainActivity.order.setRestaurant(menuList[position]);
            Toast.makeText(MenuView.this, menuList[position] + " has been added to your order.", Toast.LENGTH_SHORT).show();
        }
        else if (position == 2) {
            MainActivity.order.setRestaurant(menuList[position]);
            Toast.makeText(MenuView.this, menuList[position] + " has been added to your order.", Toast.LENGTH_SHORT).show();
        }
        else if (position == 3) {
            MainActivity.order.setRestaurant(menuList[position]);
            Toast.makeText(MenuView.this, menuList[position] + " has been added to your order.", Toast.LENGTH_SHORT).show();
        }
        else if (position == 4) {
            MainActivity.order.setRestaurant(menuList[position]);
            Toast.makeText(MenuView.this, menuList[position] + " has been added to your order.", Toast.LENGTH_SHORT).show();
        }
        else if (position == 5) {
            MainActivity.order.setRestaurant(menuList[position]);
            Toast.makeText(MenuView.this, menuList[position] + " has been added to your order.", Toast.LENGTH_SHORT).show();
        }
        else if (position == 6) {
            MainActivity.order.setRestaurant(menuList[position]);
            Toast.makeText(MenuView.this, menuList[position] + " has been added to your order.", Toast.LENGTH_SHORT).show();
        }
        else if (position == 7) {
            MainActivity.order.setRestaurant(menuList[position]);
            Toast.makeText(MenuView.this, menuList[position] + " has been added to your order.", Toast.LENGTH_SHORT).show();
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
}
