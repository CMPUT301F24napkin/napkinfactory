package com.example.napkinapp;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.hasSibling;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;


import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;

import com.example.napkinapp.fragments.admineventsearch.AdminListEventsFragment;
import com.example.napkinapp.fragments.listevents.ListEventsFragment;
import com.example.napkinapp.fragments.viewevents.ViewEventFragment;
import com.example.napkinapp.models.Event;
import com.example.napkinapp.models.Facility;
import com.example.napkinapp.models.Tag;
import com.example.napkinapp.models.User;
import com.example.napkinapp.utils.AbstractFragmentTest;
import com.example.napkinapp.utils.DB_Client;

import org.hamcrest.Matcher;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AdminEventsFragmentTest extends AbstractFragmentTest<AdminListEventsFragment> {

    private User mockUser;
    private Event mockEvent1;
    private Event mockEvent2;
    private String mockTag1;

    @Override
    protected void setUpMockData() {
        // Prepare mock user
        mockUser = new User();
        mockUser.setAndroidId("test_user_id");
        mockUser.setName("Test User");

        mockTag1 = "Basketball";

        mockEvent1 = new Event();
        mockEvent1.init();
        mockEvent1.setId("event1");
        mockEvent1.setName("Mock Event 1");
        mockEvent1.setEventDate(new Date()); // Set the event date
        mockEvent1.setDescription("This is a detailed description of Mock Event 1."); // Set description
        mockEvent1.setOrganizerId("organizer_user_id"); // Set organizer ID
        mockEvent1.addUserToWaitlist(mockUser.getAndroidId()); // Add test user to waitlist

        mockEvent2 = new Event();
        mockEvent2.init();
        mockEvent2.setId("event2");
        mockEvent2.setName("Mock Event 2");
        mockEvent2.setEventDate(new Date());
        mockEvent2.setDescription("This is a detailed description of Mock Event 2.");
        mockEvent2.setOrganizerId("organizer_user_id_2");
        mockEvent2.setTags(new ArrayList<>(List.of(mockTag1)));

        List<Object> mockEventList = new ArrayList<>();
        mockEventList.add(mockEvent1);
        mockEventList.add(mockEvent2);

        DB_Client.setExecuteQueryListData(mockEventList);
        DB_Client.addFindAllData(new ArrayList<>(List.of(new Tag(mockTag1))));
    }

    @Override
    protected AdminListEventsFragment createFragment() {
        return new AdminListEventsFragment();
    }

    @Test
    public void testFragmentDisplayed() {
        // Ensure all elements displayed
        onView(withId(R.id.events_list_view)).check(matches(isDisplayed()));
        onView(withId(R.id.search_button)).check(matches(isDisplayed()));
        onView(withId(R.id.search_event_name)).check(matches(isDisplayed()));

    }

    @Test
    public void testAllEventsDisplayed(){
        // Ensure the ListView is displayed
        onView(withId(R.id.events_list_view))
                .check(matches(isDisplayed()));

        // Check that the first event is displayed with the correct name
        onView(allOf(
                withId(R.id.eventName),
                withText("Mock Event 1")))
                .check(matches(isDisplayed()));

        // Check that the second event name is displayed
        onView(allOf(
                withId(R.id.eventName),
                withText("Mock Event 2")))
                .check(matches(isDisplayed()));


    }

    @Test
    public void testSearchedEventDisplayed(){
        // Type "Mock Event 1" into the search box
        List<Object> mockEventList = new ArrayList<>();
        mockEventList.add(mockEvent1);

        DB_Client.setExecuteQueryListData(mockEventList);

        onView(withId(R.id.search_event_name)).perform(typeText("Mock Event 1"));
        onView(withId(R.id.search_button)).perform(click());

        onView(allOf(
                withId(R.id.eventName),
                withText("Mock Event 1")))
                .check(matches(isDisplayed()));

        onView(allOf(
                withId(R.id.eventName),
                withText("Mock Event 2")))
                .check(doesNotExist());
    }

}
