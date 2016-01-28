# LocationApp

LocationApp is a simple application that consumes Foursquare rest service to query a list of
businesses near you. It displays and sort those businesses based on distance from the device’s
current location.

The application uses a caching mechanism (Volley) to store queried data so it can
still be displayed if the device cannot access the internet. It also displays choosed
location on a mapview (Google Maps) with some details about the business.

![RecyclerView-List-alt-tag](https://cloud.githubusercontent.com/assets/16329953/12633418/3e26fd00-c537-11e5-8c59-0a82770b61ba.gif)

![GMaps-BottomList-alt-tag](https://cloud.githubusercontent.com/assets/16329953/12633430/57500a2e-c537-11e5-9d16-7cb9d855773a.gif)

![Grid-Fullscreen-alt-tag](https://cloud.githubusercontent.com/assets/16329953/12633435/6de4da08-c537-11e5-8697-ea901b7540b9.gif)

Dependencies:

    compile project(':volley')
    compile files('libs/gson-2.2.1.jar')
    compile 'com.android.support:appcompat-v7:23.1.1'
    compile 'com.android.support:recyclerview-v7:23.1.1'
    compile 'com.android.support:cardview-v7:23.1.1'
    compile 'com.google.android.gms:play-services:8.4.0'
    compile 'com.squareup.okhttp:okhttp:2.7.0'
    compile 'commons-io:commons-io:2.4'
    compile 'com.flipboard:bottomsheet-core:1.5.0'

Part of the code was inspired by this Foursquare Java libraries:

https://github.com/wallabyfinancial/foursquare-api-java

https://github.com/condesales/easyFoursquare4Android


TODO:
- More tests.
- Userless requests.
- Material Design (animations).
- One map with all locations.
- Settings (default search Criteria, sorting).
- UI adjustments (portrait/landscape, tablet layout).







