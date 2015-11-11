package com.baoyz.treasure;

/**
 * Created by baoyz on 15/11/10.
 */
@Preferences(edit = Preferences.Edit.COMMIT)
public interface UserPreferences {

    String getUsername();
    void setUsername(String username);

    int getUserId();
    void setUserId(int id);

    @Clear
    void clear();

}
