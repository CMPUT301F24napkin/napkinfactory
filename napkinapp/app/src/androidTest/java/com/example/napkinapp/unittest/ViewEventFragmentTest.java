package com.example.napkinapp.unittest;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
        DB_Client.addFindOneData(mockOrganizer);
    }

    @Override
    protected ViewEventFragment createFragment(){
        return new ViewEventFragment(mockEvent1, mockUser);
    }

    @Test
    public void testRemoveFromWaitlist(){
        ViewEventFragment fragment = getFragment();

        assertTrue(mockEvent1.getWaitlist().contains(mockUser.getAndroidId()));
        assertTrue(mockUser.getWaitlist().contains(mockEvent1.getId()));

        fragment.removeEventFromWaitlist();

        assertFalse(mockEvent1.getWaitlist().contains(mockUser.getAndroidId()));
        assertFalse(mockUser.getWaitlist().contains(mockEvent1.getId()));

    }

    @Test
    public void testAddToWaitlist(){
        mockEvent1.removeUserFromWaitList(mockUser.getAndroidId());
        mockUser.removeEventFromWaitList(mockEvent1.getId());

        refreshFragment();

        ViewEventFragment fragment = getFragment();

        assertFalse(mockEvent1.getWaitlist().contains(mockUser.getAndroidId()));
        assertFalse(mockUser.getWaitlist().contains(mockEvent1.getId()));

        fragment.addEventToWaitlist();

        assertTrue(mockEvent1.getWaitlist().contains(mockUser.getAndroidId()));
        assertTrue(mockUser.getWaitlist().contains(mockEvent1.getId()));
    }


    @Test
    public void testRegisterUser() {
        ViewEventFragment fragment = getFragment();

        // Initially, user is in the chosen list but not in the registered list
        mockUser.addEventToChosen(mockEvent1.getId());
        mockEvent1.addUserToChosen(mockUser.getAndroidId());
        assertTrue(mockEvent1.getChosen().contains(mockUser.getAndroidId()));
        assertFalse(mockEvent1.getRegistered().contains(mockUser.getAndroidId()));
        assertFalse(mockEvent1.getCancelled().contains(mockUser.getAndroidId()));

        // Call registerUser
        fragment.registerUser(mockEvent1);

        // Assert user has been moved from chosen to registered
        assertFalse(mockEvent1.getChosen().contains(mockUser.getAndroidId()));
        assertTrue(mockEvent1.getRegistered().contains(mockUser.getAndroidId()));
        assertFalse(mockEvent1.getCancelled().contains(mockUser.getAndroidId()));

    }

    @Test
    public void testDeclineEvent() {
        ViewEventFragment fragment = getFragment();

        // Initially, user is in the chosen list but not in the cancelled list
        mockUser.addEventToChosen(mockEvent1.getId());
        mockEvent1.addUserToChosen(mockUser.getAndroidId());

        assertTrue(mockEvent1.getChosen().contains(mockUser.getAndroidId()));
        assertFalse(mockEvent1.getCancelled().contains(mockUser.getAndroidId()));
        assertFalse(mockEvent1.getRegistered().contains(mockUser.getAndroidId()));

        // Call declineEvent
        fragment.declineEvent(mockEvent1);

        // Assert user has been moved from chosen to cancelled
        assertFalse(mockEvent1.getChosen().contains(mockUser.getAndroidId()));
        assertTrue(mockEvent1.getCancelled().contains(mockUser.getAndroidId()));
        assertFalse(mockEvent1.getRegistered().contains(mockUser.getAndroidId()));
    }

}
