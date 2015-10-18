package com.scottmcclellan.lockereatsapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private EditText email;
    private EditText pass;
    private Button login;
    public static Order order;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialize Variables
        email = (EditText) findViewById(R.id.loginEmailId);
        pass = (EditText) findViewById(R.id.loginPasswordId);
        login = (Button) findViewById(R.id.loginButtonId);
        order = new Order();



        login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent Intent = new Intent(MainActivity.this, RestaurantView.class);
                order.setEmail(email.getText().toString());
                order.setPassword(pass.getText().toString());
                startActivity(Intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
