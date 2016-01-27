package com.bakhadir.locationapp.listeners;

import com.bakhadir.locationapp.models.User;

/**
 * Created by sam_ch on 1/20/2016.
 */

public interface UserInfoRequestListener extends ErrorListener {

    void onUserInfoFetched(User user);
}