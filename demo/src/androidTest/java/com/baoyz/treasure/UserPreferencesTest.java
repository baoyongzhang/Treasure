package com.baoyz.treasure;

import android.app.Application;
import android.test.ApplicationTestCase;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class UserPreferencesTest extends ApplicationTestCase<Application> {
    public UserPreferencesTest() {
        super(Application.class);
    }

    public void testUserPreferences() {
        final UserPreferences userPreferences = Treasure.get(getContext(), UserPreferences.class);
        assertNotNull(userPreferences);
        userPreferences.clear();
        assertNull(userPreferences.getUsername());
        assertTrue(userPreferences.setUsername("Treasure"));
        assertEquals(userPreferences.getUsername(), "Treasure");
        assertFalse(userPreferences.isLogin());
        userPreferences.setLogin(true);
        assertTrue(userPreferences.isLogin());
        userPreferences.clear();
        assertFalse(userPreferences.isLogin());
        assertNull(userPreferences.getUsername());
    }
}