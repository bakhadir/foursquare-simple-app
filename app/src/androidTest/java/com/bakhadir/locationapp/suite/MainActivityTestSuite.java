package com.bakhadir.locationapp.suite;

import com.bakhadir.locationapp.MainActivityAsyncTasksTest;
import com.bakhadir.locationapp.MainActivityIntentTest;
import com.bakhadir.locationapp.MainActivityUITest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Created by sam_ch on 1/28/2016.
 */

// Runs MainActivity unit tests
@RunWith(Suite.class)
@Suite.SuiteClasses({MainActivityUITest.class, MainActivityIntentTest.class,
        MainActivityAsyncTasksTest.class})

public class MainActivityTestSuite {
}
