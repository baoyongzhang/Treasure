package com.baoyz.treasure;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final SimplePreferences preferences = Treasure.get(this, SimplePreferences.class);
        preferences.setUsername("Hello Treasure!");
        String username = preferences.getUsername(); // "Hello Treasure!"

        preferences.setLogin(true);
        boolean isLogin = preferences.isLogin();  // true

        preferences.setTimeout(1000 * 60);
        long timeout = preferences.getTimeout();   // 60000

        Set<String> stringSet = new HashSet<>();
        stringSet.add("Hello");
        stringSet.add("World");
        preferences.setStringSet(stringSet);

        Set<String> set = preferences.getStringSet(); // {"Hello", "World"}

        preferences.clear();    // clear preferences
    }
}
