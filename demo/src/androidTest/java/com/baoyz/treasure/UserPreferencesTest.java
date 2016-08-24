package com.baoyz.treasure;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class UserPreferencesTest extends ApplicationTestCase<Application> {
    public UserPreferencesTest() {
        super(Application.class);
    }

    public void testUserPreferences() {

        Treasure.setConverterFactory(new GsonConverterFactory());

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
        User user = new User();
        user.name = "Mr.Bao";
        user.age = 21;
        userPreferences.setUser(user);
        assertEquals(user, userPreferences.getUser());

        List<User> users = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            users.add(new User("name" + i, 18));
        }
        userPreferences.setUserList(users);
        users = userPreferences.getUserList();
        for (int i = 0; i < users.size(); i++) {
            assertEquals(users.get(i).name, "name" + i);
        }
    }

    public void testCustomGsonConverter() {
        Treasure.setConverterFactory(new GsonConverterFactory());
        testUserPreferences();
    }

}