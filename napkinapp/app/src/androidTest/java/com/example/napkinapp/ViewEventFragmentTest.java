package com.example.napkinapp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;

import com.example.napkinapp.fragments.viewevents.ViewEventFragment;
import com.example.napkinapp.models.Event;
import com.example.napkinapp.models.User;
import com.example.napkinapp.utils.AbstractFragmentTest;
import com.example.napkinapp.utils.DB_Client;

import org.junit.Test;

import java.util.Date;

public class ViewEventFragmentTest extends AbstractFragmentTest<ViewEventFragment> {
    private User mockUser;
    private Event mockEvent1;
    private User mockOrganizer;

    @Override
    protected void setUpMockData() {
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

        mockOrganizer = new User();
        mockOrganizer.setAndroidId("organizer_user_id");
        mockOrganizer.setName("Mock Organizer");
        mockOrganizer.setEmail("organizer@example.com");
        mockOrganizer.setPhoneNumber("9876543210");

        // Set DB_Client mock data
        DB_Client.setFindOneData(mockOrganizer);
    }

    @Override
    protected ViewEventFragment createFragment(){
        return new ViewEventFragment(mockEvent1, mockUser);
    }

    @Test
    public void testEventDetails() {
        ViewEventFragment fragment = getFragment();

        // Ensure the fragment is displayed
        onView(withId(R.id.event_name)).check(matches(isDisplayed()));

        // Verify event details are displayed correctly
        onView(withId(R.id.event_name)).check(matches(withText(mockEvent1.getName())));
        onView(withId(R.id.event_date)).check(matches(withText(mockEvent1.getEventDate().toString()))); // Assuming event date is set to this
        onView(withId(R.id.event_details)).check(matches(withText(mockEvent1.getDescription()))); // Replace with actual mock description if set

        // Verify that the QR code image is displayed (or the error image if QR code is null)
        onView(withId(R.id.event_qr_code)).check(matches(isDisplayed()));

        // Verify buttons are in the correct state
        onView(withId(R.id.toggle_waitlist)).check(matches(isDisplayed())); // Assuming user is on waitlist
        onView(withId(R.id.event_cancel)).check(matches(isDisplayed()));
        onView(withId(R.id.more_options)).check(matches(isDisplayed()));

        // Verify organizer details are displayed correctly
        onView(withId(R.id.organizer_name)).check(matches(withText(mockOrganizer.getName()))); // Replace with actual mock organizer name
        onView(withId(R.id.organization)).check(matches(withText(mockOrganizer.getPhoneNumber())));
    }

    @Test
    public void testRemoveFromWaitlist(){
        // Ensure the button starts in the "Remove from Waitlist" state
        onView(withId(R.id.toggle_waitlist))
                .check(matches(withText(R.string.remove_from_waitlist)));

        // Click the button to remove from waitlist
        onView(withId(R.id.toggle_waitlist)).perform(click());

        // Verify the button changes to "Add to Waitlist"
        onView(withId(R.id.toggle_waitlist))
                .check(matches(withText(R.string.add_to_waitlist)));
    }

    @Test
    public void testAddToWaitlist(){
        mockEvent1.removeUserFromWaitList(mockUser.getAndroidId());
        mockUser.removeEventFromWaitList(mockEvent1.getId());

        refreshFragment();

        // Ensure the button starts in the "Add to Waitlist" state
        onView(withId(R.id.toggle_waitlist))
                .check(matches(withText(R.string.add_to_waitlist)));

        // Click the button to add to waitlist
        onView(withId(R.id.toggle_waitlist)).perform(click());

        // Verify the button changes to "Remove from Waitlist"
        onView(withId(R.id.toggle_waitlist))
                .check(matches(withText(R.string.remove_from_waitlist)));
    }
}
