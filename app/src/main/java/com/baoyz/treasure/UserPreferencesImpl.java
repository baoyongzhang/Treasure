package com.baoyz.treasure;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by baoyz on 15/11/10.
 */
public class UserPreferencesImpl implements UserPreferences {

    private SharedPreferences mPreferences;

    public UserPreferencesImpl(Context context) {
        mPreferences = context.getSharedPreferences("user_preferences", Context.MODE_PRIVATE);
    }

    @Override
    public String getUsername() {
        return mPreferences.getString("username", null);
    }

    @Override
    public void setUsername(String username) {
        mPreferences.edit().putString("username", username).apply();

    }

    @Override
    public int getUserId() {
        return 0;
    }

    @Override
    public void setUserId(int id) {

    }

    @Override
    public void clear() {
        mPreferences.edit().clear().apply();
    }
}
