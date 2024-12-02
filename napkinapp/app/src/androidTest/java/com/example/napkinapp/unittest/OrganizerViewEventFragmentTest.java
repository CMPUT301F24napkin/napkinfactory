package com.example.napkinapp.unittest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.example.napkinapp.R;
import com.example.napkinapp.fragments.viewevents.OrganizerViewEventFragment;
import com.example.napkinapp.models.Event;
import com.example.napkinapp.models.Notification;
import com.example.napkinapp.models.User;
import com.example.napkinapp.utils.AbstractFragmentTest;
import com.example.napkinapp.utils.DB_Client;

import org.junit.Test;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
        mockEntrant2.setAndroidId("entrant_2");
        mockEntrant2.setName("Entrant 2");

        mockEntrant3 = new User();
        mockEntrant3.setAndroidId("entrant_3");
        mockEntrant3.setName("Entrant 3");

        mockEvent = new Event();
        mockEvent.init();
        mockEvent.setId("event");
        mockEvent.setName("Mock Event");
        mockEvent.setEventDate(new Date()); // Set the event date
        mockEvent.setDescription("This is a detailed description of Mock Event."); // Set description
        mockEvent.setOrganizerId("test_user_id"); // Set organizer ID
        mockEvent.setEventImageUri("test/eventuri");
        mockEvent.setParticipantLimit(2); // Set participant limit

        mockEvent.addUserToWaitlist(mockEntrant1.getAndroidId()); // Add test user to waitlist
        mockEntrant1.addEventToWaitlist(mockEvent.getId());

        mockEvent.addUserToWaitlist(mockEntrant2.getAndroidId()); // Add test user to waitlist
        mockEntrant2.addEventToWaitlist(mockEvent.getId());

        mockEvent.addUserToWaitlist(mockEntrant2.getAndroidId()); // Add test user to waitlist
        mockEntrant2.addEventToWaitlist(mockEvent.getId());

        DB_Client.addFindOneData(mockUser);
    }

    @Override
    protected OrganizerViewEventFragment createFragment() {
        return new OrganizerViewEventFragment(mockEvent);
    }

    @Test
    public void testUpdateEventName() {
        OrganizerViewEventFragment fragment = getFragment();
        String newName = "Updated Event Name";

        assertNotEquals(newName, mockEvent.getName());

        fragment.updateEventName(newName);

        assertEquals(newName, mockEvent.getName());
        assertEquals(mockEvent, DB_Client.getWrittenData().get(0));
    }

    @Test
    public void testUpdateEventDescription() {
        OrganizerViewEventFragment fragment = getFragment();
        String newDescription = "Updated Event Description";

        assertNotEquals(newDescription, mockEvent.getDescription());

        fragment.updateEventDescription(newDescription);

        assertEquals(newDescription, mockEvent.getDescription());
        assertEquals(mockEvent, DB_Client.getWrittenData().get(0));
    }

    @Test
    public void testUpdateEventDate() {
        OrganizerViewEventFragment fragment = getFragment();
        Date newDate = new Date(System.currentTimeMillis() + 86400000); // Tomorrow's date
        assertNotEquals(newDate, mockEvent.getEventDate());

        fragment.updateEventDate(newDate);

        assertEquals(newDate, mockEvent.getEventDate());
        assertEquals(mockEvent, DB_Client.getWrittenData().get(0));
    }

    @Test
    public void testUpdateLotteryDate() {
        OrganizerViewEventFragment fragment = getFragment();
        Date newLotteryDate = new Date(System.currentTimeMillis() + 172800000); // Two days from now
        assertNotEquals(newLotteryDate, mockEvent.getLotteryDate());

        fragment.updateLotteryDate(newLotteryDate);

        assertEquals(newLotteryDate, mockEvent.getLotteryDate());
        assertEquals(mockEvent, DB_Client.getWrittenData().get(0));
    }

    @Test
    public void testUpdateRequireGeolocation() {
        OrganizerViewEventFragment fragment = getFragment();
        boolean newGeolocationRequirement = !mockEvent.isRequireGeolocation();

        fragment.updateRequireGeolocation(newGeolocationRequirement);

        assertEquals(newGeolocationRequirement, mockEvent.isRequireGeolocation());
        assertEquals(mockEvent, DB_Client.getWrittenData().get(0));
    }

    @Test
    public void testSendNotification() {
        OrganizerViewEventFragment fragment = getFragment();

        Notification testNotification = new Notification(
                "Test Notification",
                "This is a test message",
                false,
                mockEvent.getId(),
                false
        );

        DB_Client.addFindOneData(mockEntrant1);
        DB_Client.addFindOneData(mockEntrant2);

        List<String> targetAndroidIds = new ArrayList<>();
        targetAndroidIds.add(mockEntrant1.getAndroidId());
        targetAndroidIds.add(mockEntrant2.getAndroidId());

        fragment.sendNotification(targetAndroidIds, testNotification);

        // Verify that notifications were added to users in the database
        for (String androidId : targetAndroidIds) {
            User updatedUser = (User) DB_Client.getWrittenData().stream()
                    .filter(data -> ((User) data).getAndroidId().equals(androidId))
                    .findFirst()
                    .orElse(null);
            assertNotNull(updatedUser);
            assertTrue(updatedUser.getNotifications().contains(testNotification));
        }
    }


    @Test
    public void testDoLottery() {
        OrganizerViewEventFragment fragment = getFragment();;

        // Set up initial mock data
        ArrayList<String> expectedWaitlist = new ArrayList<>();
        expectedWaitlist.add(mockEntrant1.getAndroidId());

        ArrayList<String> expectedChosen = new ArrayList<>();
        expectedChosen.add(mockEntrant2.getAndroidId());
        expectedChosen.add(mockEntrant3.getAndroidId());

        // Updating the event with chosen entrants
        DB_Client.addFindOneData(mockEntrant2);
        DB_Client.addFindOneData(mockEntrant3);

        //Notifying failed entrants
        DB_Client.addFindOneData(mockEntrant1);

        //Notifying chosen entrant
        DB_Client.addFindOneData(mockEntrant2);
        DB_Client.addFindOneData(mockEntrant3);

        // Perform the lottery
        fragment.doLottery();

        // Validate chosen list
        assertEquals(mockEvent.getChosen(), expectedChosen);
        // Validate waitlist
        assertEquals(mockEvent.getWaitlist(), expectedWaitlist);

        // Validate event updates in the database
        Map<String, Object> expectedEventUpdates = Map.of(
                "waitlist", mockEvent.getWaitlist(),
                "chosen", mockEvent.getChosen()
        );
        assertEquals(DB_Client.getUpdatedFilters().get(0), Map.of("id", mockEvent.getId()));
        assertEquals(DB_Client.getUpdatedData().get(0), expectedEventUpdates);

        Map<String, Object> eventUpdates = Map.of(
                "waitlist", expectedWaitlist,
                "chosen", expectedChosen
        );

        // Validate user list updates in the database
        assertEquals(DB_Client.getUpdatedFilters().get(0), Map.of("id", mockEvent.getId()));
        assertEquals(DB_Client.getUpdatedData().get(0), eventUpdates);

        int writtenDataCounter = 0;

        for (String androidId : expectedWaitlist){
            // Validate notifications for the waitlist
            User notChosenUser = (User) DB_Client.getWrittenData().get(writtenDataCounter++);
            Notification notChosenNotification = new Notification(
                    fragment.getText(R.string.notification_not_chosen_name).toString() + mockEvent.getName(),
                    fragment.getText(R.string.notification_not_chosen_description).toString(), false, mockEvent.getId(), false);

            assertEquals(androidId, notChosenUser.getAndroidId());
            assertEquals(notChosenUser.getNotifications().get(0).getMessage(), notChosenNotification.getMessage());
        }


        // Validate notifications for the chosen list
        for (String androidId : expectedChosen){
            // Validate notifications for the waitlist
            User chosenUser = (User) DB_Client.getWrittenData().get(writtenDataCounter++);
            Notification chosenNotification = new Notification(
                    fragment.getText(R.string.notification_chosen_name).toString() + mockEvent.getName(),
                    fragment.getText(R.string.notification_chosen_description).toString() + mockEvent.getName(), false, mockEvent.getId(), false);

            assertEquals(androidId, chosenUser.getAndroidId());
            assertEquals(chosenUser.getNotifications().get(0).getMessage(), chosenNotification.getMessage());
        }

    }



}
