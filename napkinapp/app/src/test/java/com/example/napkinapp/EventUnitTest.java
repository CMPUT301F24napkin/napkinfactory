package com.example.napkinapp;

import org.junit.Test;

import static org.junit.Assert.*;

import com.example.napkinapp.models.Event;

import java.util.ArrayList;
import java.util.Date;

public class EventUnitTest {
    private Event event;
    private String organizerId = "organizer1";
    private String name = "Sample Event";
    private Date eventDate = new Date();
    private Date lotteryDate = new Date();
    private String description = "This is a sample event";
    private int entrantLimit = 100;
    private int participantLimit = 50;
    private boolean requireGeolocation = true;
    private String qrHashCode = "sampleQRHash";
    private ArrayList<String> waitlist = new ArrayList<>();
    private ArrayList<String> chosen = new ArrayList<>();
    private ArrayList<String> cancelled = new ArrayList<>();
    private ArrayList<String> registered = new ArrayList<>();

    @Test
    public void testConstructorWithRequiredFields() {
        event = new Event(organizerId, name, eventDate, lotteryDate, description, entrantLimit, participantLimit, requireGeolocation);
        assertEquals(organizerId, event.getOrganizerId());
        assertEquals(name, event.getName());
        assertEquals(eventDate, event.getEventDate());
        assertEquals(lotteryDate, event.getLotteryDate());
        assertEquals(description, event.getDescription());
        assertEquals(entrantLimit, event.getEntrantLimit());
        assertEquals(participantLimit, event.getParticipantLimit());
        assertTrue(event.isRequireGeolocation());

        // Update with values once defaults are chosen
    }

    @Test
    public void testConstructorWithAllFields() {
        Event dbEvent = new Event("event1", organizerId, name, eventDate, lotteryDate, description, entrantLimit,
                participantLimit, requireGeolocation, qrHashCode, waitlist, chosen, cancelled, registered);
        assertEquals("event1", dbEvent.getId());
        assertEquals(organizerId, dbEvent.getOrganizerId());
        assertEquals(name, dbEvent.getName());
        assertEquals(eventDate, dbEvent.getEventDate());
        assertEquals(lotteryDate, dbEvent.getLotteryDate());
        assertEquals(description, dbEvent.getDescription());
        assertEquals(entrantLimit, dbEvent.getEntrantLimit());
        assertEquals(participantLimit, dbEvent.getParticipantLimit());
        assertTrue(dbEvent.isRequireGeolocation());
        assertEquals(qrHashCode, dbEvent.getQrHashCode());
        assertEquals(waitlist, dbEvent.getWaitlist());
        assertEquals(chosen, dbEvent.getChosen());
        assertEquals(cancelled, dbEvent.getCancelled());
        assertEquals(registered, dbEvent.getRegistered());
    }

    @Test
    public void testSettersAndGetters() {
        event = new Event(organizerId, name, eventDate, lotteryDate, description, entrantLimit, participantLimit, requireGeolocation);

        event.setId("newId");
        assertEquals("newId", event.getId());

        event.setName("Updated Event Name");
        assertEquals("Updated Event Name", event.getName());

        Date newEventDate = new Date();
        event.setEventDate(newEventDate);
        assertEquals(newEventDate, event.getEventDate());

        Date newLotteryDate = new Date();
        event.setLotteryDate(newLotteryDate);
        assertEquals(newLotteryDate, event.getLotteryDate());

        event.setDescription("Updated Description");
        assertEquals("Updated Description", event.getDescription());

        event.setEntrantLimit(200);
        assertEquals(200, event.getEntrantLimit());

        event.setParticipantLimit(75);
        assertEquals(75, event.getParticipantLimit());

        event.setRequireGeolocation(false);
        assertFalse(event.isRequireGeolocation());
    }

    @Test
    public void testAddUserToCancelled_AddsUserWhenNotPresent() {
        event = new Event(organizerId, name, eventDate, lotteryDate, description, entrantLimit, participantLimit, requireGeolocation);

        String userId = "user1";
        event.addUserToCancelled(userId);

        assertTrue(event.getCancelled().contains(userId));
    }

    @Test
    public void testAddUserToCancelled_DoesNotAddUserIfAlreadyPresent() {
        String userId = "user1";
        cancelled.add(userId);

        event = new Event(organizerId, name, eventDate, lotteryDate, description, entrantLimit, participantLimit, requireGeolocation);

        event.addUserToCancelled(userId);
        assertEquals(1, event.getCancelled().size());
    }

    @Test
    public void testRemoveUserFromCancelled_RemovesUserIfPresent() {
        String userId = "user1";
        cancelled.add(userId);

        event = new Event(organizerId, name, eventDate, lotteryDate, description, entrantLimit, participantLimit, requireGeolocation);

        event.removeUserFromCancelled(userId);
        assertFalse(event.getCancelled().contains(userId));
    }

    @Test
    public void testRemoveUserFromCancelled_DoesNothingIfUserNotPresent() {
        event = new Event(organizerId, name, eventDate, lotteryDate, description, entrantLimit, participantLimit, requireGeolocation);

        String userId = "user1";
        event.removeUserFromCancelled(userId);

        assertTrue(cancelled.isEmpty());
    }

    @Test
    public void testAddUserToRegistered_AddsUserWhenNotPresent() {
        event = new Event(organizerId, name, eventDate, lotteryDate, description, entrantLimit, participantLimit, requireGeolocation);

        String userId = "user1";
        event.addUserToRegistered(userId);
        assertTrue(event.getRegistered().contains(userId));
    }

    @Test
    public void testAddUserToRegistered_DoesNotAddUserIfAlreadyPresent() {

        String userId = "user1";
        registered.add(userId);
        event = new Event(organizerId, name, eventDate, lotteryDate, description, entrantLimit, participantLimit, requireGeolocation);

        event.addUserToRegistered(userId);
        assertEquals(1, event.getRegistered().size());
    }

    @Test
    public void testRemoveUserFromRegistered_RemovesUserIfPresent() {
        String userId = "user1";
        registered.add(userId);
        event = new Event(organizerId, name, eventDate, lotteryDate, description, entrantLimit, participantLimit, requireGeolocation);

        event.removeUserFromRegistered(userId);
        assertFalse(event.getRegistered().contains(userId));
    }

    @Test
    public void testRemoveUserFromRegistered_DoesNothingIfUserNotPresent() {
        event = new Event(organizerId, name, eventDate, lotteryDate, description, entrantLimit, participantLimit, requireGeolocation);

        String userId = "user1";
        event.removeUserFromRegistered(userId);
        assertTrue(registered.isEmpty());
    }
}
