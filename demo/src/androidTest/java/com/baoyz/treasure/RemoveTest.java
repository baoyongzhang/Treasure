package com.baoyz.treasure;

import android.app.Application;
import android.test.ApplicationTestCase;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class RemoveTest extends ApplicationTestCase<Application> {
    public RemoveTest() {
        super(Application.class);
    }

    public void testRemove() {
        final SimplePreferences simplePreferences = Treasure.get(getContext(), SimplePreferences.class);
        simplePreferences.setUsername("name");
        assertEquals(simplePreferences.getUsername(), "name");
        simplePreferences.removeUsername();
        assertEquals(simplePreferences.getUsername(), "Hello Treasure!");

        simplePreferences.setUsername("name");
        assertEquals(simplePreferences.getUsername(), "name");
        simplePreferences.delUsername();
        assertEquals(simplePreferences.getUsername(), "Hello Treasure!");

        simplePreferences.setTimeout(100);
        assertEquals(simplePreferences.getTimeout(), 100);
        simplePreferences.deleteTimeout();
        assertEquals(simplePreferences.getTimeout(), 1000 * 60 * 60);
    }
}