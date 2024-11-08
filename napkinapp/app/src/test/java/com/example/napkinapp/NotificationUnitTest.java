package com.example.napkinapp;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import static org.junit.Assert.*;

import com.example.napkinapp.models.Notification;

public class NotificationUnitTest {
    private Notification notification;
    private String title = "Sample Notification";
    private String message = "This is a sample notification message";
    private Boolean read = true;
    private String eventId = "event123";
    private boolean isOrganizerNotification = true;


    @Test
    public void testConstructorWithBasicFields() {
        notification = new Notification(title, message);
        Assertions.assertNotNull(notification.getId());
        Assertions.assertEquals(title, notification.getTitle());
        Assertions.assertEquals(message, notification.getMessage());
        assertFalse(notification.getRead());
        assertEquals("", notification.getEventId());
        assertFalse(notification.isOrganizerNotification());
    }

    @Test
    public void testConstructorWithReadField() {
        Notification readNotification = new Notification(title, message, read);
        assertNotNull(readNotification.getId());
        assertEquals(title, readNotification.getTitle());
        assertEquals(message, readNotification.getMessage());
        assertTrue(readNotification.getRead());
        assertEquals("", readNotification.getEventId());
        assertFalse(readNotification.isOrganizerNotification());
    }

    @Test
    public void testConstructorWithAllFields() {
        notification = new Notification(title, message);
        Notification fullNotification = new Notification(title, message, read, eventId, isOrganizerNotification);
        assertNotNull(fullNotification.getId());
        assertEquals(title, fullNotification.getTitle());
        assertEquals(message, fullNotification.getMessage());
        assertEquals(read, fullNotification.getRead());
        assertEquals(eventId, fullNotification.getEventId());
        assertTrue(fullNotification.isOrganizerNotification());
    }

    @Test
    public void testSettersAndGetters() {
        notification = new Notification(title, message);
        notification.setTitle("Updated Title");
        assertEquals("Updated Title", notification.getTitle());

        notification.setMessage("Updated Message");
        assertEquals("Updated Message", notification.getMessage());

        notification.setRead(true);
        assertTrue(notification.getRead());

        notification.setId("newId");
        assertEquals("newId", notification.getId());
    }
}
