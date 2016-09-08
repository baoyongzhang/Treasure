/*
 * The MIT License (MIT)
 * 
 * Copyright (c) 2015 baoyongzhang <baoyz94@gmail.com>
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.baoyz.treasure;

import android.content.SharedPreferences;

import java.util.Set;

/**
 * Created by baoyz on 15/11/14.
 */
@Preferences
public interface SimplePreferences {

    @Default("Hello Treasure!")
    String getUsername();

    @Commit
    void setUsername(String username);

    @Default("false")
    boolean isLogin();
    void setLogin(boolean login);

    // default is 1 hour
    @Default("1000 * 60 * 60")
    long getTimeout();

    void setTimeout(long timeout);

    @Default({"hello", "world", "!"})
    Set<String> getStringSet();

    void setPerson(User obj);
    User getPerson();
    @Remove
    void removePerson();

    // if return boolean, that call edit().commit() and return this commit result.
    boolean setStringSet(Set<String> stringSet);

    @Commit
    @Remove
    void removeUsername();

    @Remove
    boolean deleteTimeout();

    @Clear
    void clear();

    @Expired(value = 2, unit = Expired.UNIT_SECONDS)
    String getTestExpired();

    void setTestExpired(String value);

    String getTestExpired2();

    @Expired(value = 2, unit = Expired.UNIT_SECONDS)
    void setTestExpired2(String value);

    String getTestExpired3();

    void setTestExpired3(String value, @Expired(unit = Expired.UNIT_SECONDS) int second);

    @Prototype
    SharedPreferences getSharedPreferences();
}
