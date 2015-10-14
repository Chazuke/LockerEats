package com.scottmcclellan.lockereatsapp;

import java.util.ArrayList;

/**
 * Created by Scott on 10/8/2015.
 */
public class Order {
    public Order() {};

    String email;
    String password;
    String restaurant;
    ArrayList<String> items = new ArrayList<String>();
    int index = 0;

    public void setEmail(String email) {
        this.email = email;
        return;
    }

    public String getEmail() {
        return this.email;
    }

    public void setPassword(String password) {
        this.password = password;
        return;
    }

    public String getPassword() {
        return this.password;
    }

    public void setRestaurant(String restaurant) {
        this.restaurant = restaurant;
    }

    public String getRestaurant(String restaurant) {
        return this.restaurant;
    }

    public void addItem(String item) {
        items.add(item);
        return;
    }

    public void removeItem(String item) {
        items.remove(item);
        return;
    }


}
