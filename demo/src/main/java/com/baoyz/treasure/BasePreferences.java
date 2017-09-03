package com.baoyz.treasure;

/**
 * Created by baoyongzhang on 2017/9/3.
 */

public interface BasePreferences {

    String getBaseUsername();
    boolean setBaseUsername(String username);

    @Clear
    void baseClear();
}
