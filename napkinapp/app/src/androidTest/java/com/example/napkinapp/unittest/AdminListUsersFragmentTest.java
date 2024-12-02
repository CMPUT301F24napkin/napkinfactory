package com.example.napkinapp.unittest;

import static org.junit.Assert.assertEquals;

import com.example.napkinapp.fragments.adminusersearch.AdminListUsersFragment;
import com.example.napkinapp.models.Event;
import com.example.napkinapp.models.User;
import com.example.napkinapp.utils.AbstractFragmentTest;
import com.example.napkinapp.utils.DB_Client;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminListUsersFragmentTest extends AbstractFragmentTest<AdminListUsersFragment> {

    private User mockUser1;
    private User mockUser2;
    private Event mockEvent1;
    private Event mockEvent2;
    private Event mockEvent3;
    private Event mockEvent4;

    @Override
    protected void setUpMockData() {

        // Mock User 1
        mockUser1 = new User();
        mockUser1.setAndroidId("user1_id");
        mockUser1.setName("Mock User 1");


        // Mock User 2
        mockUser2 = new User();
        mockUser2.setAndroidId("user2_id");
        mockUser2.setName("Mock User 2");

        // Mock Event 1
        mockEvent1 = new Event();
        mockEvent1.init();
        mockEvent1.setId("event1");
        mockEvent1.setOrganizerId(mockUser1.getAndroidId());
        mockEvent1.addUserToWaitlist(mockUser2.getAndroidId());
        mockUser2.addEventToWaitlist(mockEvent1.getId());

        // Mock Event 2
        mockEvent2 = new Event();
        mockEvent2.init();
        mockEvent2.setId("event2");
        mockEvent2.setOrganizerId(mockUser2.getAndroidId());
        mockEvent2.addUserToCancelled(mockUser1.getAndroidId());

        mockEvent3 = new Event();
        mockEvent3.init();
        mockEvent3.setId("event3");
        mockEvent3.setOrganizerId(mockUser2.getAndroidId());
        mockEvent3.addUserToRegistered(mockUser1.getAndroidId());
        mockUser1.addEventToRegistered(mockEvent3.getId());

        mockEvent4 = new Event();
        mockEvent4.init();
        mockEvent4.setId("event4");
        mockEvent4.setOrganizerId(mockUser2.getAndroidId());
        mockEvent4.addUserToWaitlist(mockUser1.getAndroidId());
        mockUser1.addEventToWaitlist(mockEvent4.getId());

        // Set up DB_Client mock data
        List<Object> mockUserList = new ArrayList<>();
        mockUserList.add(mockUser1);
        mockUserList.add(mockUser2);

        DB_Client.setExecuteQueryListData(mockUserList);

    }

    @Override
    protected AdminListUsersFragment createFragment() {
        return new AdminListUsersFragment();
    }

    @Test
    public void testDeleteUser() {
        AdminListUsersFragment fragment = getFragment();
        List<Object> mockEventList = new ArrayList<>();
        mockEventList.add(mockEvent1);
        mockEventList.add(mockEvent2);
        mockEventList.add(mockEvent3);
        mockEventList.add(mockEvent4);

        DB_Client.addFindAllData(mockEventList);

        DB_Client.addFindOneData(mockUser2);

        DB_Client.addFindOneData(mockEvent3);
        DB_Client.addFindOneData(mockEvent4);


        fragment.deleteUser(mockUser1);

        // Delete MockUser1's Event
        Map<String, Object> deleteUserFromEventFilters = new HashMap<>();
        deleteUserFromEventFilters.put("androidId", mockUser2.getAndroidId());
        mockUser2.removeEventFromWaitList(mockEvent1.getId());
        Map<String, Object> deleteUserFromEventUpdates = new HashMap<>();
        deleteUserFromEventUpdates.put("waitlist", mockUser2.getWaitlist());


        assertEquals(DB_Client.getUpdatedFilters().get(0), deleteUserFromEventFilters);
        assertEquals(DB_Client.getUpdatedData().get(0), deleteUserFromEventUpdates);

        Map<String, Object> deleteEventFilters = new HashMap<>();
        deleteEventFilters.put("id", mockEvent1.getId());

        assertEquals(DB_Client.getDeletedFilters().get(0), deleteEventFilters);

        // Delete MockUser1 from MockEvent2's cancelled list
        mockEvent2.removeUserFromCancelled(mockUser1.getAndroidId());

        Map<String, Object> cancelledUpdates = new HashMap<>();
        cancelledUpdates.put("cancelled", mockEvent2.getCancelled());

        Map<String, Object> cancelledFilters = new HashMap<>();
        cancelledFilters.put("id", mockEvent2.getId());

        assertEquals(DB_Client.getUpdatedFilters().get(1), cancelledFilters);
        assertEquals(DB_Client.getUpdatedData().get(1), cancelledUpdates);

        // Delete MockUser1 from MockEvent3 and MockEvent4



        mockEvent4.removeUserFromWaitList(mockUser1.getAndroidId());
        mockEvent4.removeUserFromRegistered(mockUser1.getAndroidId());
        mockEvent4.removeUserFromChosen(mockUser1.getAndroidId());

        Map<String, Object> event4Filters = new HashMap<>();
        event4Filters.put("id", mockEvent4.getId());

        Map<String, Object> event4Updates = new HashMap<>();
        event4Updates.put("waitlist", mockEvent4.getWaitlist());
        event4Updates.put("registered", mockEvent4.getRegistered());
        event4Updates.put("chosen", mockEvent4.getChosen());

        assertEquals(DB_Client.getUpdatedFilters().get(2), event4Filters);
        assertEquals(DB_Client.getUpdatedData().get(2), event4Updates);

        mockEvent3.removeUserFromWaitList(mockUser1.getAndroidId());
        mockEvent3.removeUserFromRegistered(mockUser1.getAndroidId());
        mockEvent3.removeUserFromChosen(mockUser1.getAndroidId());

        Map<String, Object> event3Filters = new HashMap<>();
        event3Filters.put("id", mockEvent3.getId());

        Map<String, Object> event3Updates = new HashMap<>();
        event3Updates.put("waitlist", mockEvent3.getWaitlist());
        event3Updates.put("registered", mockEvent3.getRegistered());
        event3Updates.put("chosen", mockEvent3.getChosen());

        assertEquals(DB_Client.getUpdatedFilters().get(3), event3Filters);
        assertEquals(DB_Client.getUpdatedData().get(3), event3Updates);

        Map<String, Object> deleteFilters = new HashMap<>();
        deleteFilters.put("organizerId", mockUser1.getAndroidId());

        assertEquals(DB_Client.getDeletedFilters().get(1), deleteFilters);

        // Verify database deletion for User 1
        Map<String, Object> userFilters = new HashMap<>();
        userFilters.put("androidId", mockUser1.getAndroidId());

        assertEquals(DB_Client.getDeletedFilters().get(2), userFilters);
    }
}
