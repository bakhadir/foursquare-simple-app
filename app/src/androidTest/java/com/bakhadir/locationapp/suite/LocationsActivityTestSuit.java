package com.bakhadir.locationapp.suite;

import com.bakhadir.locationapp.LocationsActivityGetVenuesNearbyTest;
import com.bakhadir.locationapp.LocationsActivityUITest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Created by sam_ch on 2/4/2016.
 */

// Runs MainActivity unit tests
@RunWith(Suite.class)
@Suite.SuiteClasses({LocationsActivityUITest.class, LocationsActivityGetVenuesNearbyTest.class})

public class LocationsActivityTestSuit {
}
