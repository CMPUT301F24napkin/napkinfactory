package com.example.napkinapp;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.widget.EditText;

import androidx.test.espresso.matcher.RootMatchers;

import com.example.napkinapp.fragments.createevent.CreateEventFragment;
import com.example.napkinapp.models.Tag;
import com.example.napkinapp.models.User;
import com.example.napkinapp.utils.AbstractFragmentTest;
import com.example.napkinapp.utils.DB_Client;

import org.junit.Test;

import java.util.ArrayList;

public class CreateEventFragmentTest extends AbstractFragmentTest<CreateEventFragment> {

    private User mockUser;
    private ArrayList<Tag> tags;

    @Override
    protected void setUpMockData() {
        mockUser = new User();
        mockUser.setAndroidId("test_user_id");
        mockUser.setName("Test User");
        tags = new ArrayList<>();
        tags.add(new Tag("Basketball"));
        tags.add(new Tag("Outdoors"));
        tags.add(new Tag("Soccer"));
        DB_Client.setFindAllData(new ArrayList<>(tags));
    }

    @Override
    protected CreateEventFragment createFragment() {
        return new CreateEventFragment(mockUser);
    }

    @Test
    public void testDisplayFragment() {
        // Verify the fragment is displayed
        onView(withId(R.id.event_name)).check(matches(isDisplayed()));
        onView(withId(R.id.event_date)).check(matches(isDisplayed()));
        onView(withId(R.id.lottery_date)).check(matches(isDisplayed()));
        onView(withId(R.id.event_image)).check(matches(isDisplayed()));
        onView(withId(R.id.edit_event_image_button)).check(matches(isDisplayed()));
        onView(withId(R.id.event_description)).check(matches(isDisplayed()));
        onView(withId(R.id.entrant_limit)).check(matches(isDisplayed()));
        onView(withId(R.id.participant_limit_checkbox)).check(matches(isDisplayed()));
        onView(withId(R.id.create_button)).check(matches(isDisplayed()));
    }

    @Test
    public void testEnterEventDetailsAndCreate() throws InterruptedException {
        // Enter event name
        onView(withId(R.id.event_name)).perform(replaceText("Test Event"));

        // Select event date
        onView(withId(R.id.event_date_picker)).perform(click());
        onView(withText("OK")).perform(click());

        // Select lottery date
        onView(withId(R.id.lottery_date_picker)).perform(click());
        onView(withText("OK")).perform(click());

        // Enter event description
        onView(withId(R.id.event_description)).perform(replaceText("This is a test event."));

        // Enter entrant limit
        onView(withId(R.id.entrant_limit)).perform(replaceText("100"));

        // Toggle participant limit and enter value
        onView(withId(R.id.participant_limit_checkbox)).perform(click());
        onView(withId(R.id.participant_limit)).perform(replaceText("50"));

        // Enter tagautocomplete and start typing
        onView(withId(R.id.tag_autocomplete)).perform(click(), replaceText("Ba"));

        // Select basketball
        onData(equalTo("Basketball")).inRoot(RootMatchers.isPlatformPopup()).perform(click());

        // Check if displayed in chips - Needed or else it will click through all popups
        onView(allOf(
                withParent(withId(R.id.tag_chip_group)),
                withText("Basketball"))).check(matches(isDisplayed()));
        // Toggle geolocation switch
        onView(withId(R.id.geolocation_switch)).perform(click());

        // Click Create button
        onView(withId(R.id.create_button)).perform(click());
    }

    @Test
    public void testParticipantLimitToggle() {
        // Verify participant limit is initially disabled
        onView(withId(R.id.participant_limit)).check(matches(isDisplayed()));
        onView(withId(R.id.participant_limit)).check((view, noViewFoundException) -> {
            assertFalse(((EditText) view).isEnabled());
        });

        // Enable participant limit checkbox
        onView(withId(R.id.participant_limit_checkbox)).perform(click());

        // Verify participant limit is enabled
        onView(withId(R.id.participant_limit)).check((view, noViewFoundException) -> {
            assertTrue(((EditText) view).isEnabled());
        });
    }


}
