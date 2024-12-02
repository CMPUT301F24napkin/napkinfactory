package com.example.napkinapp.unittest;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
    public void testRemoveFromWaitlist(){
        ListEventsFragment fragment = getFragment();

        assertTrue(mockEvent1.getWaitlist().contains(mockUser.getAndroidId()));
        assertTrue(mockUser.getWaitlist().contains(mockEvent1.getId()));

        fragment.removeEventFromWaitlist(mockEvent1);

        assertFalse(mockEvent1.getWaitlist().contains(mockUser.getAndroidId()));
        assertFalse(mockUser.getWaitlist().contains(mockEvent1.getId()));

    }

    @Test
    public void testAddToWaitlist(){
        mockEvent1.removeUserFromWaitList(mockUser.getAndroidId());
        mockUser.removeEventFromWaitList(mockEvent1.getId());

        refreshFragment();

        ListEventsFragment fragment = getFragment();

        assertFalse(mockEvent1.getWaitlist().contains(mockUser.getAndroidId()));
        assertFalse(mockUser.getWaitlist().contains(mockEvent1.getId()));

        fragment.addEventToWaitlist(mockEvent1);

        assertTrue(mockEvent1.getWaitlist().contains(mockUser.getAndroidId()));
        assertTrue(mockUser.getWaitlist().contains(mockEvent1.getId()));
    }
}
