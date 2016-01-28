# foursquare-simple-app
A simple application that consumes Foursquare rest service to query a list of businesses near you. 

github LocationApp README.md

foursquare-simple-app

LocationApp is a simple application that consumes Foursquare rest service to query a list of
businesses near you. It displays and sort those businesses based on distance from the device’s
current location.

The application uses a caching mechanism (Volley) to store queried data so it can
still be displayed if the device cannot access the internet. It also allows to display queried
locations on a mapview (Google Maps) with some details about the businesses.

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
- Complete with Material Design specifications (+ animations).
- Settings (default search Criteria, sorting).
- UI adjustments (portrait/landscape, tablet layout).







