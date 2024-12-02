package com.example.napkinapp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.swipeUp;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.PickerActions.setDate;
import static androidx.test.espresso.matcher.ViewMatchers.hasChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;

import android.view.View;

import com.example.napkinapp.fragments.viewevents.OrganizerViewEventFragment;
import com.example.napkinapp.models.Event;
import com.example.napkinapp.models.User;
import com.example.napkinapp.utils.AbstractFragmentTest;
import com.example.napkinapp.utils.DB_Client;
import com.google.android.material.textfield.TextInputLayout;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class OrganizerViewEventFragmentTest extends AbstractFragmentTest<OrganizerViewEventFragment> {
    private User mockUser;
    private Event mockEvent1;
    private User mockOrganizer;

    public static Matcher<View> hasHintText(final String hint) {
        return new TypeSafeMatcher<View>() {
            @Override
            protected boolean matchesSafely(View view) {
                if (view instanceof TextInputLayout) {
                    return ((TextInputLayout) view).getHint().toString().equals(hint);
                }
                return false;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with hint text: ").appendText(hint);
            }
        };
    }


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
        mockEvent1.setWaitlist(new ArrayList<>(List.of("1", "2", "3")));
        mockEvent1.setChosen(new ArrayList<>());
        mockEvent1.setCancelled(new ArrayList<>(List.of("1")));
        mockEvent1.setRegistered(new ArrayList<>(List.of("1", "2")));

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
    protected OrganizerViewEventFragment createFragment() {
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
    public void testEditEntrantLimit() {
        // Ensure the initial text is correct
        onView(withId(R.id.entrant_limit))
                .check(matches(withText(String.valueOf(mockEvent1.getEntrantLimit()))));

        // Click the button to open the edit dialog
        onView(withId(R.id.edit_entrant_limit)).perform(click());

        // Verify the dialog opens
        onView(withId(R.id.edit_text)).check(matches(isDisplayed()));

        // Verify the default text in the EditText
        onView(withId(R.id.edit_text))
                .check(matches(withText(String.valueOf(mockEvent1.getEntrantLimit()))));

        // Set an invalid entrant limit
        String invalidLimit = "2";
        onView(withId(R.id.edit_text)).perform(replaceText(invalidLimit));
        onView(withText("OK")).perform(click());

        // Ensure the dialog remains open on invalid input
        onView(withId(R.id.edit_text)).check(matches(isDisplayed()));

        // Set a valid entrant limit
        String validLimit = "50";
        onView(withId(R.id.edit_text)).perform(replaceText(validLimit));
        onView(withText("OK")).perform(click());

        // Ensure the dialog is dismissed on valid input
        onView(withId(R.id.edit_text)).check(doesNotExist());

        // Ensure the TextView is updated
        onView(withId(R.id.entrant_limit)).check(matches(withText(validLimit)));
    }

    @Test
    public void testEditParticipantLimit() {

        String participantLimitText = (mockEvent1.getParticipantLimit() == Integer.MAX_VALUE) ? "" : String.valueOf(mockEvent1.getParticipantLimit());

        // Ensure the initial text is correct
        onView(withId(R.id.participant_limit))
                .check(matches(withText(participantLimitText)));

        // Click the button to open the edit dialog
        onView(withId(R.id.edit_participant_limit)).perform(click());

        // Verify the dialog opens
        onView(withId(R.id.edit_text)).check(matches(isDisplayed()));

        // Verify the default text in the EditText
        onView(withId(R.id.edit_text))
                .check(matches(withText(participantLimitText)));

        // Set an invalid participant limit
        String invalidLimit = "1";
        onView(withId(R.id.edit_text)).perform(replaceText(invalidLimit));
        onView(withText("OK")).perform(click());

        // Ensure the dialog remains open on invalid input
        onView(withId(R.id.edit_text)).check(matches(isDisplayed()));

        // Set a valid participant limit
        String validLimit = "50";
        onView(withId(R.id.edit_text)).perform(replaceText(validLimit));
        onView(withText("OK")).perform(click());

        // Ensure the dialog is dismissed on valid input
        onView(withId(R.id.edit_text)).check(doesNotExist());

        // Ensure the TextView is updated
        onView(withId(R.id.participant_limit)).check(matches(withText(validLimit)));

    }

    @Test
    public void testEditEventDate() {
        // Ensure the initial date is correct
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

        String initialDate = formatter.format(new Date());  // Assuming this is the current date on the event
        onView(withId(R.id.event_date)).check(matches(withText(initialDate)));

        // Click the button to open the date picker dialog
        onView(withId(R.id.edit_event_date)).perform(click());

        // Verify the dialog opens
        onView(withText("OK")).check(matches(isDisplayed()));

        // Set to yesterday's date (calendar -1 day)
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -1);  // Set to yesterday
        String yesterdayDate = formatter.format(calendar.getTime());

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 2; // No idea why i need to add 2
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Set the date to yesterday in the DatePickerDialog
        onView(withClassName(is("android.widget.DatePicker"))).perform(setDate(year, month, day));

        onView(withText("OK")).perform(click()); // You would have to simulate this via direct touch on the DatePicker dialog

        // TODO make sure that the picker doesn't close, and instead prompts user to pick a date in thte future!

        // Ensure the dialog is dismissed and the date is updated
        onView(withText("OK")).check(doesNotExist()); // Check if dialog is dismissed
        onView(withId(R.id.event_date)).check(matches(withText(yesterdayDate)));
    }

    @Test
    public void testEditLotteryDate() {
        // Ensure the initial date is correct
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

        String initialLotteryDate = formatter.format(new Date());  // Assuming this is the current lottery date
        onView(withId(R.id.lottery_date)).check(matches(withText(initialLotteryDate)));

        // Click the button to open the date picker dialog
        onView(withId(R.id.edit_lottery_date)).perform(click());

        // Verify the dialog opens
        onView(withText("OK")).check(matches(isDisplayed()));

        // Set to yesterday's date (calendar -1 day)
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -1);  // Set to yesterday
        String yesterdayDate = formatter.format(calendar.getTime());

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 2; // Adjust for 0-based month
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // TODO make sure that the picker doesn't close, and instead prompts user to pick a date in thte future!

        // Set the date to yesterday in the DatePickerDialog
        onView(withClassName(is("android.widget.DatePicker"))).perform(setDate(year, month, day));

        // Simulate clicking "OK"
        onView(withText("OK")).perform(click());

        // Ensure the dialog is dismissed and the date is updated
        onView(withText("OK")).check(doesNotExist()); // Check if dialog is dismissed
        onView(withId(R.id.lottery_date)).check(matches(withText(yesterdayDate)));
    }

    @Test
    public void testDoLotteryButtonExistsAndClickable() {
        // Check if the button exists
        onView(withId(R.id.do_lottery)).check(matches(isDisplayed()));

        // Check if the button is clickable
        onView(withId(R.id.do_lottery)).check(matches(isClickable()));

        // Optionally, you can also perform a click action to confirm it's working
        onView(withId(R.id.do_lottery)).perform(click());
    }

    @Test
    public void testShareQRCodeButton() {
        // Ensure the button is visible and clickable
        onView(withId(R.id.share_qr_code)).check(matches(isDisplayed()));
        onView(withId(R.id.share_qr_code)).perform(click());
    }

    @Test
    public void testChipSelectionUpdatesList() {

        onView(withId(R.id.nested_scroll_view)).perform(swipeUp());

        onView(withId(R.id.entrants_list_view)).check(matches(hasChildCount(4+2+1+0)));

        // Ensure the initial list has the correct number of items (for example, 3 items on 'Waitlist' chip)
        onView(withId(R.id.chip_waitlist)).perform(click());
        onView(withId(R.id.entrants_list_view)).check(matches(hasChildCount(4)));

        // Select the 'Chosen' chip and check if the list updates to 2 items
        onView(withId(R.id.chip_chosen)).perform(click());
        onView(withId(R.id.entrants_list_view)).check(matches(hasChildCount(0)));

        // Select the 'Cancelled' chip and check if the list updates to 1 item
        onView(withId(R.id.chip_cancelled)).perform(click());
        onView(withId(R.id.entrants_list_view)).check(matches(hasChildCount(1)));

        // Select the 'Registered' chip and check if the list updates to 4 items
        onView(withId(R.id.chip_registered)).perform(scrollTo(), click());
        onView(withId(R.id.entrants_list_view)).check(matches(hasChildCount(2)));
    }

    @Test
    public void testChipSelectionUpdatesMessageHint() {

        onView(withId(R.id.nested_scroll_view)).perform(swipeUp());

        onView(withId(R.id.message_text_field)).check(matches(hasHintText("Message to all")));

        // Ensure the initial list has the correct number of items (for example, 3 items on 'Waitlist' chip)
        onView(withId(R.id.chip_waitlist)).perform(click());
        onView(withId(R.id.message_text_field)).check(matches(hasHintText("Message to waitlisters")));

        // Select the 'Chosen' chip and check if the list updates to 2 items
        onView(withId(R.id.chip_chosen)).perform(click());
        onView(withId(R.id.message_text_field)).check(matches(hasHintText("Message to chosen")));

        // Select the 'Cancelled' chip and check if the list updates to 1 item
        onView(withId(R.id.chip_cancelled)).perform(click());
        onView(withId(R.id.message_text_field)).check(matches(hasHintText("Message to cancelled")));

        // Select the 'Registered' chip and check if the list updates to 4 items
        onView(withId(R.id.chip_registered)).perform(scrollTo(), click());
        onView(withId(R.id.message_text_field)).check(matches(hasHintText("Message to registered")));
    }
}
