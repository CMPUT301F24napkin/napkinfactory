package com.example.napkinapp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasSibling;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;

import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertTrue;

import androidx.fragment.app.Fragment;

import com.example.napkinapp.fragments.myevents.MyEventsFragment;
import com.example.napkinapp.fragments.createevent.CreateEventFragment;
import com.example.napkinapp.fragments.viewevents.OrganizerViewEventFragment;
import com.example.napkinapp.models.Event;
import com.example.napkinapp.models.User;
import com.example.napkinapp.utils.AbstractFragmentTest;
import com.example.napkinapp.utils.DB_Client;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class MyEventsFragmentTest extends AbstractFragmentTest<MyEventsFragment> {
    private User mockUser;
    private Event mockEvent1;
    private Event mockEvent2;

    @Override
    protected void setUpMockData() {
        mockUser = new User();
        mockUser.setAndroidId("test_user_id");
        mockUser.setName("Test User");

        List<Object> mockEvents = new ArrayList<>();

        mockEvent1 = new Event();
        mockEvent1.init();
        mockEvent1.setId("event1");
        mockEvent1.setName("Mock Event 1");
        mockEvent1.setOrganizerId(mockUser.getAndroidId());

        mockEvent2 = new Event();
        mockEvent2.setId("event2");
        mockEvent2.setName("Mock Event 2");
        mockEvent2.setOrganizerId(mockUser.getAndroidId());

        mockEvents.add(mockEvent1);
        mockEvents.add(mockEvent2);

        DB_Client.addFindAllData(mockEvents);
    }

    @Override
    protected MyEventsFragment createFragment() {
        return new MyEventsFragment(mockUser);
    }

    @Test
    public void testDisplayEvents() {
        onView(withId(R.id.events_list_view))
                .check(matches(isDisplayed()));

        // Verify that the mock events are displayed
        onView(withText(mockEvent1.getName())).check(matches(isDisplayed()));
        onView(withText(mockEvent2.getName())).check(matches(isDisplayed()));
    }

    @Test
    public void testViewEventButton() {
        // Click the "View" button for the first event
        onView(allOf(
                withText("View"),
                hasSibling(withText(mockEvent1.getName()))))
                .check(matches(isDisplayed()));

        // Perform click on the "View" button associated with the first event
        onView(allOf(
                withText("View"),
                hasSibling(withText(mockEvent1.getName()))))
                .perform(click());

        // Verify navigation to OrganizerViewEventFragment
        activityScenario.onActivity(activity -> {
            Fragment currentFragment = activity.getSupportFragmentManager()
                    .findFragmentById(R.id.content_fragmentcontainer);
            assertTrue(currentFragment instanceof OrganizerViewEventFragment);
        });
    }

    @Test
    public void testCreateEventButton() {
        // Scroll to and click the "Create Event" button
        onView(withId(R.id.trailing_button)).perform(click());

        // Verify navigation to CreateEventFragment
        activityScenario.onActivity(activity -> {
            Fragment currentFragment = activity.getSupportFragmentManager()
                    .findFragmentById(R.id.content_fragmentcontainer);
            assertTrue(currentFragment instanceof CreateEventFragment);
        });
    }
}
