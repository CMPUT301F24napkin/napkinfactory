package com.example.napkinapp.unittest;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.example.napkinapp.fragments.registeredevents.RegisteredEventsFragment;
import com.example.napkinapp.models.Event;
import com.example.napkinapp.models.User;
import com.example.napkinapp.utils.AbstractFragmentTest;
import com.example.napkinapp.utils.DB_Client;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RegisteredEventsFragmentTest extends AbstractFragmentTest<RegisteredEventsFragment> {

    private User mockUser;
    private Event mockEvent1;
    private Event mockEvent2;

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
        mockEvent1.addUserToChosen(mockUser.getAndroidId()); // Add test user to waitlist

        mockUser.addEventToChosen(mockEvent1.getId());

        mockEvent2 = new Event();
        mockEvent2.init();
        mockEvent2.setId("event2");
        mockEvent2.setName("Mock Event 2");
        mockEvent2.setEventDate(new Date());
        mockEvent2.setDescription("This is a detailed description of Mock Event 2.");
        mockEvent2.setOrganizerId("organizer_user_id_2");
        mockEvent2.addUserToRegistered(mockUser.getAndroidId());

        mockUser.addEventToRegistered(mockEvent2.getId());

        List<Object> mockEventList = new ArrayList<>();
        mockEventList.add(mockEvent1);
        mockEventList.add(mockEvent2);

        DB_Client.setExecuteQueryListData(mockEventList);
    }

    @Override
    protected RegisteredEventsFragment createFragment() {
        return new RegisteredEventsFragment(mockUser);
    }

    @Test
    public void testRegister(){
        RegisteredEventsFragment fragment = getFragment();

        assertTrue(mockEvent1.getChosen().contains(mockUser.getAndroidId()));
        assertFalse(mockEvent1.getCancelled().contains(mockUser.getAndroidId()));
        assertFalse(mockEvent1.getRegistered().contains(mockUser.getAndroidId()));

        fragment.registerUser(mockEvent1);

        assertFalse(mockEvent1.getChosen().contains(mockUser.getAndroidId()));
        assertTrue(mockEvent1.getRegistered().contains(mockUser.getAndroidId()));
        assertFalse(mockEvent1.getCancelled().contains(mockUser.getAndroidId()));
    }

    @Test
    public void testDecline(){
        RegisteredEventsFragment fragment = getFragment();

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

