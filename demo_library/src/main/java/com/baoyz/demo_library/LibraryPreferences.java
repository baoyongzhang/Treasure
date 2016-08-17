/*
 * The MIT License (MIT)
 * Copyright (c) 2016 baoyongzhang <baoyz94@gmail.com>
 */
package com.baoyz.demo_library;

import com.baoyz.treasure.Preferences;

/**
 * Created by baoyongzhang on 16/8/17.
 */
@Preferences
public interface LibraryPreferences {

    String getName();
    void setName(String name);
}
