package com.baoyz.treasure;

import android.app.Application;
import android.content.SharedPreferences;
import android.test.ApplicationTestCase;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class PrototypeTest extends ApplicationTestCase<Application> {

    private boolean changed;

    public PrototypeTest() {
        super(Application.class);
    }

    public void testPrototypeTest() {
        final SimplePreferences simplePreferences = Treasure.get(getContext(), SimplePreferences.class);
        final SharedPreferences sharedPreferences = simplePreferences.getSharedPreferences();
        sharedPreferences.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                changed = true;
                assertEquals(key, "username");
            }
        });
        simplePreferences.setUsername("test");
        assertEquals(sharedPreferences.getString("username", null), "test");
        assertTrue(changed);
    }
}