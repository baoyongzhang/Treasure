package com.baoyz.treasure;

import android.app.Application;
import android.test.ApplicationTestCase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class CollectionTest extends ApplicationTestCase<Application> {
    public CollectionTest() {
        super(Application.class);
    }

    public void testCollectionPreferences() {

        Treasure.setConverterFactory(new GsonConverterFactory());

        final CollectionPreferences preferences = Treasure.get(getContext(), CollectionPreferences.class);
        assertNotNull(preferences);

        List<CollectionPreferences.Model1> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add(new CollectionPreferences.Model1("name" + i));
        }
        preferences.setList(list);
        assertEquals(preferences.getList(), list);

        Set<CollectionPreferences.Model1> set = new TreeSet<>();
        for (int i = 0; i < 10; i++) {
            set.add(new CollectionPreferences.Model1("name" + i));
        }
        preferences.setSet(set);
        assertEquals(preferences.getSet(), set);

        List<CollectionPreferences.Model2<String, CollectionPreferences.Model1>> list2 = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list2.add(new CollectionPreferences.Model2<String, CollectionPreferences.Model1>("name" + i, new CollectionPreferences.Model1("name" + i)));
        }
        preferences.setList2(list2);
        assertEquals(preferences.getList2(), list2);

        Map<String, CollectionPreferences.Model1> map = new HashMap<>();
        for (int i = 0; i < 10; i++) {
            map.put("name" + i, new CollectionPreferences.Model1("name" + i));
        }
        preferences.setMap(map);
        assertEquals(preferences.getMap(), map);
    }
}