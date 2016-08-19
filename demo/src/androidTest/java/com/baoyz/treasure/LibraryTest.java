package com.baoyz.treasure;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.baoyz.demo_library.LibraryPreferences;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class LibraryTest extends ApplicationTestCase<Application> {
    public LibraryTest() {
        super(Application.class);
    }

    public void testLibraryPreferences() {
        final LibraryPreferences libraryPreferences = Treasure.get(getContext(), LibraryPreferences.class);
        assertNotNull(libraryPreferences);
        libraryPreferences.setName("library");
        assertEquals("library", libraryPreferences.getName());
    }
}