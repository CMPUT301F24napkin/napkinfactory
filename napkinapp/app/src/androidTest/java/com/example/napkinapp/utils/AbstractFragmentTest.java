package com.example.napkinapp.utils;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;

import com.example.napkinapp.R;
import com.example.napkinapp.utils.DB_Client;
import com.example.napkinapp.utils.TestActivity;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;

public abstract class AbstractFragmentTest<T extends Fragment> {

    protected ActivityScenario<TestActivity> activityScenario;

    /**
     * Subclasses must provide the fragment instance to be tested.
     */
    protected abstract T createFragment();

    /**
     * Subclasses can override to set up mock data before launching the activity.
     */
    protected void setUpMockData() {
        // Default implementation is no-op.
    }

    @Before
    public void setUp() {
        DB_Client.reset();
        disableAnimations();

        // Allow subclasses to set up mock data
        setUpMockData();

        // Launch TestActivity
        activityScenario = ActivityScenario.launch(TestActivity.class);

        // Attach the fragment to TestActivity
        activityScenario.onActivity(activity -> {
            T fragment = createFragment();

            activity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_fragmentcontainer, fragment) // Target the content container
                    .commitNow();
        });

    }

    @After
    public void tearDown() {
        // Reset mock data after each test
        enableAnimations();
        DB_Client.reset();
    }

    /**
     * Utility to get the attached fragment for testing.
     */
    protected T getFragment() {
        final Fragment[] fragment = new Fragment[1];
        activityScenario.onActivity(activity -> {
            fragment[0] = activity.getSupportFragmentManager().findFragmentById(android.R.id.content);
        });
        return (T) fragment[0];
    }


    public void disableAnimations() {
        try {
            InstrumentationRegistry.getInstrumentation().getUiAutomation().executeShellCommand(
                    "settings put global window_animation_scale 0\n" +
                    "settings put global transition_animation_scale 0\n" +
                    "settings put global animator_duration_scale 0"
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void enableAnimations() {
        try {
            InstrumentationRegistry.getInstrumentation().getUiAutomation().executeShellCommand(
                    "settings put global window_animation_scale 1\n" +
                            "settings put global transition_animation_scale 1\n" +
                            "settings put global animator_duration_scale 1"
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

