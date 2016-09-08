package com.baoyz.treasure;

import android.app.Application;
import android.test.ApplicationTestCase;

import java.util.ArrayList;
import java.util.List;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class DefaultValueTest extends ApplicationTestCase<Application> {
    public DefaultValueTest() {
        super(Application.class);
    }

    public void testDefaultValue() {

        Treasure.setConverterFactory(new GsonConverterFactory());

        final UserPreferences userPreferences = Treasure.get(getContext(), UserPreferences.class);

        userPreferences.clear();

        assertEquals(userPreferences.isLogin(), false);
        assertNull(userPreferences.getUsername());
        assertNull(userPreferences.getUser());
        assertNull(userPreferences.getUserList());

        final SimplePreferences preferences = Treasure.get(getContext(), SimplePreferences.class, "test");
        User person = new User();
        person.name = "Mr.Bao";
        person.age = 21;
        preferences.setPerson(person);

        preferences.removePerson();

        User person2 = preferences.getPerson();
        assertNull(person2);
    }
}