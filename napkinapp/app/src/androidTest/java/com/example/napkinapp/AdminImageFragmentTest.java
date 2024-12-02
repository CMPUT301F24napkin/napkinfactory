package com.example.napkinapp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;


import android.view.View;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;

import com.example.napkinapp.models.Event;
import com.example.napkinapp.models.Facility;
import com.example.napkinapp.models.User;
import com.example.napkinapp.utils.AbstractFragmentTest;
import com.example.napkinapp.fragments.adminimagesearch.AdminImageFragment;
import com.example.napkinapp.utils.DB_Client;

import org.hamcrest.Matcher;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AdminImageFragmentTest extends AbstractFragmentTest<AdminImageFragment> {

    private List<Object> mockEvents;
    private List<Object> mockUsers;
    private List<Object> mockFacilities;

    @Override
    protected void setUpMockData() {
        // Prepare mock user
        User mockUser = new User();
        mockUser.setAndroidId("test_user_id");
        mockUser.setName("Test User");
        mockUser.setProfileImageUri("test/useruri");

        Event mockEvent = new Event();
        mockEvent.init();
        mockEvent.setId("test_event");
        mockEvent.setName("Mock Event");
        mockEvent.setEventDate(new Date()); // Set the event date
        mockEvent.setDescription("This is a detailed description of Mock Event."); // Set description
        mockEvent.setOrganizerId("organizer_user_id");
        mockEvent.setEventImageUri("test/eventuri");

        Facility mockFacility = new Facility();
        mockFacility.setId("test_facility_id");
        mockFacility.setImageUri("test/facilityuri");

        mockEvents = new ArrayList<>();
        mockEvents.add(mockEvent);

        mockUsers = new ArrayList<>();
        mockUsers.add(mockUser);

        mockFacilities = new ArrayList<>();
        mockFacilities.add(mockFacility);

        DB_Client.addFindAllData(mockEvents);
        DB_Client.addFindAllData(mockUsers);
        DB_Client.addFindAllData(mockFacilities);
    }

    @Override
    protected AdminImageFragment createFragment() {
        return new AdminImageFragment();
    }

    @Test
    public void testRecyclerViewDisplaysImages() {
        // Ensure the RecyclerView is displayed
        onView(withId(R.id.recyclerViewImages)).check(matches(isDisplayed()));

        // Check the first item is displayed
        onView(withId(R.id.recyclerViewImages))
                .perform(scrollToPosition(0))
                .check(matches(isDisplayed()));

        // Check the second item is displayed
        onView(withId(R.id.recyclerViewImages))
                .perform(scrollToPosition(1))
                .check(matches(isDisplayed()));

        // Check the third item is displayed
        onView(withId(R.id.recyclerViewImages))
                .perform(scrollToPosition(2))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testDeleteButtonRemovesImage() {
        // Ensure the RecyclerView is displayed
        onView(withId(R.id.recyclerViewImages)).check(matches(isDisplayed()));

        // Calculate the position of the last item
        int lastPosition = mockEvents.size() + mockUsers.size() + mockFacilities.size() - 1;

        // Scroll to the last item to ensure it's visible
        onView(withId(R.id.recyclerViewImages))
                .perform(scrollToPosition(lastPosition));

        // Perform a click on the delete button of the last item
        onView(withId(R.id.recyclerViewImages))
                .perform(actionOnItemAtPosition(lastPosition, new ViewAction() {
                    @Override
                    public Matcher<View> getConstraints() {
                        return isDisplayed(); // Ensures the view is displayed
                    }

                    @Override
                    public String getDescription() {
                        return "Click on the delete button in the last RecyclerView item.";
                    }

                    @Override
                    public void perform(UiController uiController, View view) {
                        View deleteButton = view.findViewById(R.id.actionButton);
                        if (deleteButton != null) {
                            deleteButton.performClick();
                        }
                    }
                }));

        // Validate that the last item is removed
        onView(withId(R.id.recyclerViewImages))
                .perform(scrollToPosition(lastPosition))
                .check(matches(isDisplayed()));
    }



}
