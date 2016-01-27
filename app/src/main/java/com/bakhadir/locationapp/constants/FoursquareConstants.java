package com.bakhadir.locationapp.constants;

/**
 * Created by sam_ch on 1/20/2016.
 */

public class FoursquareConstants {
    public static final String CLIENT_ID = "UAJFV2RPKAK0BSU4MMZAFSLMTF5VBEO0LQ4U1XKVL1I5J2N2";
    public static final String CLIENT_SECRET = "OWA1CT2DORCYEBNPIIO2YDGGN25WRB2FDGO21AH0N42QOSOK";

    public static final String CALLBACK_URL = "http://localhost:8888";
    public static final String API_DATE_VERSION = "20151201";

    public static final String ACCESS_TOKEN_STRING = "https://foursquare.com/oauth2/access_token";
    public static final String GRANT_TYPE_STRING = "&grant_type=authorization_code";

    public static final String USERS_URL = "https://api.foursquare.com/v2/users/self?v=";
    public static final String OAUTH_TOKEN_STRING = "&oauth_token=";
    public static final String CODE_STRING = "&code=";

    public static final String AUTHENTICATE_STRING = "https://foursquare.com/oauth2/authenticate";
    public static final String CLIENT_SECRET_STRING = "&client_secret=";
    public static final String CLIENT_ID_STRING = "?client_id=";
    public static final String RESPONSE_TYPE_STRING = "&response_type=code";
    public static final String REDIRECT_URI_STRING = "&redirect_uri=";

    public static final String EXPLORE_VENUES_URL_FORMAT = "https://api.foursquare.com/v2/venues/explore?v=%1$s&ll=%2$s,%3$s&limit%4$s&radius%5$s&sortByDistance%6$s";
    public static final String COMPETE_VENUE_URL_FORMAT = "https://api.foursquare.com/v2/venues/%1$s?v=%2$s";
    public static final String VENUE_PHOTOS_URL_FORMAT = "https://api.foursquare.com/v2/venues/%1$s/photos?v=%2$s";
}
