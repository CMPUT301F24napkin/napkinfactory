package com.example.napkinapp;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.hasSibling;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import androidx.fragment.app.Fragment;

import com.example.napkinapp.fragments.listevents.ListEventsFragment;
import com.example.napkinapp.fragments.viewevents.ViewEventFragment;
import com.example.napkinapp.models.Event;
import com.example.napkinapp.models.User;
import com.example.napkinapp.utils.AbstractFragmentTest;
import com.example.napkinapp.utils.DB_Client;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ListEventsFragmentTest extends AbstractFragmentTest<ListEventsFragment> {

    private User mockUser;
    private Event mockEvent1;
    private Event mockEvent2;

    @Override
    protected void setUpMockData() {
        // Prepare mock user
        mockUser = new User();
        mockUser.setAndroidId("test_user_id");
        mockUser.setName("Test User");

        mockEvent1 = new Event();
        mockEvent1.init();
        mockEvent1.setId("event1");
        mockEvent1.setName("Mock Event 1");
        mockEvent1.setEventDate(new Date()); // Set the event date
        mockEvent1.setDescription("This is a detailed description of Mock Event 1."); // Set description
        mockEvent1.setOrganizerId("organizer_user_id"); // Set organizer ID
        mockEvent1.addUserToWaitlist(mockUser.getAndroidId()); // Add test user to waitlist

        mockUser.addEventToWaitlist(mockEvent1.getId());

        mockEvent2 = new Event();
        mockEvent2.init();
        mockEvent2.setId("event2");
        mockEvent2.setName("Mock Event 2");
        mockEvent2.setEventDate(new Date());
        mockEvent2.setDescription("This is a detailed description of Mock Event 2.");
        mockEvent2.setOrganizerId("organizer_user_id_2");

        List<Object> mockEventList = new ArrayList<>();
        mockEventList.add(mockEvent1);
        mockEventList.add(mockEvent2);

        DB_Client.setExecuteQueryListData(mockEventList);
    }

    @Override
    protected ListEventsFragment createFragment() {
        return new ListEventsFragment(mockUser);
    }

    @Test
    public void testDisplayAllEvents() {
        ListEventsFragment fragment = getFragment();
        // Ensure the ListView is displayed
        onView(withId(R.id.events_list_view))
                .check(matches(isDisplayed()));

        // Check that the first event is displayed with the correct name
        onView(allOf(
                withId(R.id.eventName),
                withText("Mock Event 1")))
                .check(matches(isDisplayed()));

        // Check that the button for the first event has "Remove from Waitlist"
        onView(allOf(
                withId(R.id.button),
                withText(R.string.remove_from_waitlist),
                hasSibling(withText("Mock Event 1"))))
                .check(matches(isDisplayed()));

        // Check that the second event name is displayed
        onView(allOf(
                withId(R.id.eventName),
                withText("Mock Event 2")))
                .check(matches(isDisplayed()));

        // Check that the button for the second event has "Add to Waitlist"
        onView(allOf(
                withId(R.id.button),
                withText(R.string.add_to_waitlist),
                hasSibling(withText("Mock Event 2"))))
                .check(matches(isDisplayed()));

    }

    @Test
    public void testAddToWaitlist() {
        ListEventsFragment fragment = getFragment();

        // Click the "Add to Waitlist" button for the second event (Mock Event 2)
        onView(allOf(
                withId(R.id.button),
                withText(R.string.add_to_waitlist),
                hasSibling(withText("Mock Event 2"))))
                .perform(click());

        // Check that the button for the second event has "Remove from Waitlist"
        onView(allOf(
                withId(R.id.button),
                withText(R.string.remove_from_waitlist),
                hasSibling(withText("Mock Event 2"))))
                .check(matches(isDisplayed()));
    }


    @Test
    public void testRemoveFromWaitlist(){
        ListEventsFragment fragment = getFragment();

        onView(allOf(
                withId(R.id.button),
                withText(R.string.remove_from_waitlist),
                hasSibling(withText("Mock Event 1"))))
                .perform(click());

        onView(allOf(
                withId(R.id.button),
                withText(R.string.add_to_waitlist),
                hasSibling(withText("Mock Event 1"))))
                .check(matches(isDisplayed()));

    }

    @Test
    public void testEventItemClickOpensViewEventFragment() {
        // Interact with the content_fragmentcontainer
        onData(anything()).inAdapterView(withId(R.id.events_list_view)).atPosition(0).perform(click());

        activityScenario.onActivity(activity -> {
            Fragment currentFragment = activity.getSupportFragmentManager().findFragmentById(R.id.content_fragmentcontainer);
            assertNotNull(currentFragment);
            assertTrue(currentFragment instanceof ViewEventFragment);
        });
    }

    @Test
    public void testWaitlistChipFilter() {
        List<Object> mockEventList = new ArrayList<>();
        mockEventList.add(mockEvent1);

        DB_Client.addFindAllInData(mockEventList);
        // Ensure the fragment is loaded
        ListEventsFragment fragment = getFragment();

        // Click on the "Waitlist" chip in the chip group
        onView(allOf(
                withId(R.id.chipGroup), // Ensure it's in the chip group
                hasDescendant(withText("Waitlist")))) // Match the specific chip by text
                .perform(click());

        // Verify that "Mock Event 1" is displayed
        onView(allOf(withId(R.id.eventName), withText("Mock Event 1")))
                .check(matches(isDisplayed()));

        // Verify that "Mock Event 2" is not displayed
        onView(allOf(withId(R.id.eventName), withText("Mock Event 2")))
                .check(doesNotExist());
    }




}
