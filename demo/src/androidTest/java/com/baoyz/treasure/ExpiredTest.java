package com.baoyz.treasure;

import android.app.Application;
import android.os.SystemClock;
import android.test.ApplicationTestCase;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ExpiredTest extends ApplicationTestCase<Application> {
    public ExpiredTest() {
        super(Application.class);
    }

    public void testExpired() {
        final SimplePreferences simplePreferences = Treasure.get(getContext(), SimplePreferences.class);
        simplePreferences.setTestExpired("value");
        assertEquals(simplePreferences.getTestExpired(), "value");
        SystemClock.sleep(1000);
        assertEquals(simplePreferences.getTestExpired(), "value");
        SystemClock.sleep(1500);
        assertNull(simplePreferences.getTestExpired());
    }
}