package com.baoyz.treasure;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.test.ApplicationTestCase;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class PrototypeTest extends ApplicationTestCase<Application> {

    private boolean changed;
    private SharedPreferences.OnSharedPreferenceChangeListener mListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            changed = true;
            assertEquals(key, "username");
        }
    };

    public PrototypeTest() {
        super(Application.class);
    }

    public void testPrototypeTest() {
        final SimplePreferences simplePreferences = Treasure.get(getContext(), SimplePreferences.class);
        final SharedPreferences sharedPreferences = simplePreferences.getSharedPreferences();
        sharedPreferences.registerOnSharedPreferenceChangeListener(mListener);
        simplePreferences.setUsername("test");
        assertEquals(sharedPreferences.getString("username", null), "test");
        simplePreferences.clear();
        assertNull(sharedPreferences.getString("username", null));
        SystemClock.sleep(1000);
        assertTrue(changed);
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(mListener);
    }
}