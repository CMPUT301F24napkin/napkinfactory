package com.example.napkinapp.unittest;

import com.example.napkinapp.fragments.viewevents.OrganizerViewEventFragment;
import com.example.napkinapp.models.Event;
import com.example.napkinapp.models.User;
import com.example.napkinapp.utils.AbstractFragmentTest;

import org.junit.Test;

import java.util.Date;

public class OrganizerViewEventFragmentTest extends AbstractFragmentTest<OrganizerViewEventFragment> {

    private User mockUser;
    private Event mockEvent;
    private User mockEntrant1;
    private User mockEntrant2;
    private User mockEntrant3;

    @Override
    protected void setUpMockData() {
        mockUser = new User();
        mockUser.setAndroidId("test_user_id");
        mockUser.setName("Test User");

        mockEntrant1 = new User();
        mockEntrant1.setAndroidId("entrant_1");
        mockEntrant1.setName("Entrant 1");

        mockEntrant2 = new User();
        mockEntrant2.setAndroidId("entrant_1");
        mockEntrant2.setName("Entrant 2");

        mockEntrant3 = new User();
        mockEntrant3.setAndroidId("entrant_1");
        mockEntrant3.setName("Entrant 3");

        mockEvent = new Event();
        mockEvent.init();
        mockEvent.setId("event");
        mockEvent.setName("Mock Event");
        mockEvent.setEventDate(new Date()); // Set the event date
        mockEvent.setDescription("This is a detailed description of Mock Event."); // Set description
        mockEvent.setOrganizerId("test_user_id"); // Set organizer ID

        mockEvent.addUserToWaitlist(mockEntrant1.getAndroidId()); // Add test user to waitlist
        mockEntrant1.addEventToWaitlist(mockEvent.getId());

        mockEvent.addUserToWaitlist(mockEntrant2.getAndroidId()); // Add test user to waitlist
        mockEntrant2.addEventToWaitlist(mockEvent.getId());

        mockEvent.addUserToWaitlist(mockEntrant2.getAndroidId()); // Add test user to waitlist
        mockEntrant2.addEventToWaitlist(mockEvent.getId());

    }

    @Override
    protected OrganizerViewEventFragment createFragment() {
        return new OrganizerViewEventFragment(mockEvent);
    }

}
