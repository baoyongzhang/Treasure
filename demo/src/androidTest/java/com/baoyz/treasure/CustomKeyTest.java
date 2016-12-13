package com.baoyz.treasure;

import android.app.Application;
import android.os.SystemClock;
import android.test.ApplicationTestCase;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class CustomKeyTest extends ApplicationTestCase<Application> {
    public CustomKeyTest() {
        super(Application.class);
    }

    public void testCustomKey() {
        final SimplePreferences simplePreferences = Treasure.get(getContext(), SimplePreferences.class);
        simplePreferences.setTestKey("testKey");
        assertEquals(simplePreferences.getSharedPreferences().getString("test_key", null), simplePreferences.getTestKey());
        assertNotSame(simplePreferences.getSharedPreferences().getString("testKey", null), simplePreferences.getTestKey());
    }
}