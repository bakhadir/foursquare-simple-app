package com.bakhadir.locationapp.listeners;

/**
 * Created by sam_ch on 1/20/2016.
 */

public interface AccessTokenRequestListener extends ErrorListener{

        void onAccessGrant(String accessToken);

}
