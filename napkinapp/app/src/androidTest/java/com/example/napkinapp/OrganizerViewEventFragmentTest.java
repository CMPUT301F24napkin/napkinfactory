package com.example.napkinapp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.example.napkinapp.fragments.viewevents.OrganizerViewEventFragment;
import com.example.napkinapp.models.Event;
import com.example.napkinapp.models.User;
import com.example.napkinapp.utils.AbstractFragmentTest;
import com.example.napkinapp.utils.DB_Client;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;

public class OrganizerViewEventFragmentTest extends AbstractFragmentTest<OrganizerViewEventFragment> {
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
    protected OrganizerViewEventFragment createFragment(){
        return new OrganizerViewEventFragment(mockEvent1);
    }

    @Test
    public void testEventDetails() {
        OrganizerViewEventFragment fragment = getFragment();

        // Ensure the fragment is displayed
        onView(withId(R.id.event_name)).check(matches(isDisplayed()));

        // Verify event details are displayed correctly
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

        onView(withId(R.id.event_name)).check(matches(withText(mockEvent1.getName())));
        onView(withId(R.id.event_date)).check(matches(withText(formatter.format(mockEvent1.getEventDate())))); // Assuming event date is set to this
        onView(withId(R.id.lottery_date)).check(matches(withText(formatter.format(mockEvent1.getLotteryDate())))); // Assuming event date is set to this
        onView(withId(R.id.event_details)).check(matches(withText(mockEvent1.getDescription()))); // Replace with actual mock description if set

        String expectedEntrantString = (mockEvent1.getEntrantLimit() == Integer.MAX_VALUE) ? "" : String.valueOf(mockEvent1.getEntrantLimit());
        String expectedParticipantString = (mockEvent1.getParticipantLimit() == Integer.MAX_VALUE) ? "" : String.valueOf(mockEvent1.getParticipantLimit());

        onView(withId(R.id.entrant_limit)).check(matches(withText(expectedEntrantString))); // Replace with actual mock description if set
        onView(withId(R.id.participant_limit)).check(matches(withText(expectedParticipantString))); // Replace with actual mock description if set

        // Verify that the QR code image is displayed (or the error image if QR code is null)
        onView(withId(R.id.qr_code)).check(matches(isDisplayed()));

        // Verify organizer details are displayed correctly
        onView(withId(R.id.organizer_name)).check(matches(withText(mockOrganizer.getName()))); // Replace with actual mock organizer name
        onView(withId(R.id.organization)).check(matches(withText(mockOrganizer.getPhoneNumber())));
    }

    @Test
    public void testEditEventName() {
        // Ensure the initial text is correct
        onView(withId(R.id.event_name)).check(matches(withText(mockEvent1.getName())));

        // Click the button to open the edit dialog
        onView(withId(R.id.edit_event_name)).perform(click());

        // Verify the dialog opens
        onView(withId(R.id.edit_text)).check(matches(isDisplayed()));

        // Verify the default text in the EditText
        onView(withId(R.id.edit_text)).check(matches(withText(mockEvent1.getName())));

        // New name to type into the EditText
        String newName = "new name!";

        // Perform typeText to change the name (use replaceText to overwrite)
        onView(withId(R.id.edit_text)).perform(replaceText(newName));

        // Click the OK button
        onView(withText("OK")).perform(click());

        // Ensure the dialog is dismissed and the correct fragment is displayed
        onView(withId(R.id.edit_text)).check(doesNotExist());

        // Ensure the text field is updated
        onView(withId(R.id.event_name)).check(matches(withText(newName)));

        // Additional check: Verify the model is updated
        activityScenario.onActivity(activity -> {
            assertEquals(newName, mockEvent1.getName());
        });
    }

    @Test
    public void testEditEventDesription() {
        // Ensure the initial text is correct
        onView(withId(R.id.event_details)).check(matches(withText(mockEvent1.getDescription())));

        // Click the button to open the edit dialog
        onView(withId(R.id.edit_event_details)).perform(click());

        // Verify the dialog opens
        onView(withId(R.id.edit_text)).check(matches(isDisplayed()));

        // Verify the default text in the EditText
        onView(withId(R.id.edit_text)).check(matches(withText(mockEvent1.getDescription())));

        // New name to type into the EditText
        String newDescription = "new description!";

        // Perform typeText to change the name (use replaceText to overwrite)
        onView(withId(R.id.edit_text)).perform(replaceText(newDescription));

        // Click the OK button
        onView(withText("OK")).perform(click());

        // Ensure the dialog is dismissed and the correct fragment is displayed
        onView(withId(R.id.edit_text)).check(doesNotExist());

        // Ensure the text field is updated
        onView(withId(R.id.event_details)).check(matches(withText(newDescription)));

        // Additional check: Verify the model is updated
        activityScenario.onActivity(activity -> {
            assertEquals(newDescription, mockEvent1.getDescription());
        });
    }

    @Test
    public void testEdit() {
        // Ensure the initial text is correct
        onView(withId(R.id.event_details)).check(matches(withText(mockEvent1.getDescription())));

        // Click the button to open the edit dialog
        onView(withId(R.id.edit_event_details)).perform(click());

        // Verify the dialog opens
        onView(withId(R.id.edit_text)).check(matches(isDisplayed()));

        // Verify the default text in the EditText
        onView(withId(R.id.edit_text)).check(matches(withText(mockEvent1.getDescription())));

        // New name to type into the EditText
        String newDescription = "new description!";

        // Perform typeText to change the name (use replaceText to overwrite)
        onView(withId(R.id.edit_text)).perform(replaceText(newDescription));

        // Click the OK button
        onView(withText("OK")).perform(click());

        // Ensure the dialog is dismissed and the correct fragment is displayed
        onView(withId(R.id.edit_text)).check(doesNotExist());

        // Ensure the text field is updated
        onView(withId(R.id.event_details)).check(matches(withText(newDescription)));

        // Additional check: Verify the model is updated
        activityScenario.onActivity(activity -> {
            assertEquals(newDescription, mockEvent1.getDescription());
        });
    }

}
