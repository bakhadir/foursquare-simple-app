package com.bakhadir.locationapp;

import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.SmallTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.intent.Intents.times;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import static android.support.test.espresso.intent.Intents.intended;

/**
 * Created by sam_ch on 2/2/2016.
 */

@RunWith(AndroidJUnit4.class)
@SmallTest
public class MainActivityIntentTest {

    @Rule
    public IntentsTestRule<MainActivity> intentsTestRule =
            new IntentsTestRule<>(MainActivity.class);

    @Test
    public void testOnButtonClick_validateIntentSentToPackage() {
        // User action that results in a LocationsActivity activity being launched.
        onView(withId(R.id.locationButton)).perform(click());

        // Using a canned RecordedIntentMatcher to validate that an intent resolving
        // to the LocationsActivity activity has been sent, exactly 1 time
        intended(hasComponent(LocationsActivity.class.getName()), times(1));
    }

}
