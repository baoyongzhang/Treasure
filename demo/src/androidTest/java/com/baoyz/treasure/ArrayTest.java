package com.baoyz.treasure;

import android.app.Application;
import android.test.ApplicationTestCase;

import java.util.ArrayList;
import java.util.List;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ArrayTest extends ApplicationTestCase<Application> {
    public ArrayTest() {
        super(Application.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        Treasure.setConverterFactory(new GsonConverterFactory());
    }

    public void testIntArray() {
        final SimplePreferences simplePreferences = Treasure.get(getContext(), SimplePreferences.class);
        int[] arr = {1, 2, 3};
        simplePreferences.setIntArray(arr);
        assertSame(simplePreferences.getIntArray().length, arr.length);
        assertSame(simplePreferences.getIntArray()[0], 1);
        assertSame(simplePreferences.getIntArray()[1], 2);
        assertSame(simplePreferences.getIntArray()[2], 3);
    }

    public void testUserArray() {
        final SimplePreferences simplePreferences = Treasure.get(getContext(), SimplePreferences.class);
        User user1 = new User("name1", 12);
        User user2 = new User("name2", 13);
        User[] arr = {user1, user2};
        simplePreferences.setUserArray(arr);
        assertSame(simplePreferences.getUserArray().length, arr.length);
        assertEquals(simplePreferences.getUserArray()[0], user1);
        assertEquals(simplePreferences.getUserArray()[1], user2);
    }

}