package com.example.napkinapp.unittest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.util.Log;

import com.example.napkinapp.fragments.admineventsearch.AdminListEventsFragment;
import com.example.napkinapp.models.Event;
import com.example.napkinapp.models.User;
import com.example.napkinapp.utils.AbstractFragmentTest;
import com.example.napkinapp.utils.DB_Client;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminListEventsFragmentTest extends AbstractFragmentTest<AdminListEventsFragment> {

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
    protected AdminListEventsFragment createFragment() {
        return new AdminListEventsFragment();
    }

    @Test
    public void testDeleteEvent() {
        AdminListEventsFragment fragment = getFragment();
        DB_Client.addFindOneData(mockUser);


        fragment.deleteEvent(mockEvent1);

        Map<String, Object> filters = new HashMap<>();
        filters.put("androidId", mockUser.getAndroidId());

        List<String> waitlist = (List<String>) mockUser.getWaitlist();
        Log.d("RegisteredEventsFragment", "waitlist" + waitlist);
        List<String> registered = (List<String>) mockUser.getRegistered();
        Log.d("RegisteredEventsFragment", "regisered" + registered);
        List<String> chosen = (List<String>) mockUser.getChosen();
        // adjust the waitlist
        waitlist.remove(mockEvent1.getId());
        Log.d("RegisteredEventsFragment", "removed waitlist" + waitlist);
        registered.remove(mockEvent1.getId());
        Log.d("RegisteredEventsFragment", "removed regisered" + registered);
        chosen.remove(mockEvent1.getId());

        Map<String, Object> updates = new HashMap<>();
        updates.put("waitlist", waitlist);
        updates.put("registered", registered);
        updates.put("chosen", chosen);

        assertEquals(DB_Client.getUpdatedFilters().get(0), filters);
        assertEquals(DB_Client.getUpdatedData().get(0), updates);

        Map<String, Object> deleteFilters = new HashMap<>();
        deleteFilters.put("id", mockEvent1.getId());

        assertEquals(DB_Client.getDeletedFilters().get(0), deleteFilters);
    }


}
