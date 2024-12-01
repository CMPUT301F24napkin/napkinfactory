package com.example.napkinapp;

import org.junit.Test;

import static org.junit.Assert.*;

import com.example.napkinapp.models.Notification;
import com.example.napkinapp.models.User;

import java.util.ArrayList;

public class UserUnitTest {
    private User user;
    private Notification notification1;
    private Notification notification2;


    @Test
    public void testDefaultConstructor() {
        User defaultUser = new User();
        assertEquals("", defaultUser.getName());
        assertEquals("", defaultUser.getPhoneNumber());
        assertEquals("", defaultUser.getEmail());
        assertEquals("", defaultUser.getAddress());
        assertFalse(defaultUser.getEnNotifications());
        assertFalse(defaultUser.getIsAdmin());
        assertNotNull(defaultUser.getNotifications());
        assertTrue(defaultUser.getNotifications().isEmpty());
    }

    @Test
    public void testParameterizedConstructor() {
        user = new User("android123", "John Doe", "123-456-7890", "john@example.com",
                "123 Main St", true, false, false);
        assertEquals("android123", user.getAndroidId());
        assertEquals("John Doe", user.getName());
        assertEquals("123-456-7890", user.getPhoneNumber());
        assertEquals("john@example.com", user.getEmail());
        assertEquals("123 Main St", user.getAddress());
        assertTrue(user.getEnNotifications());
        assertFalse(user.getIsAdmin());
    }

    @Test
    public void testSettersAndGetters() {
        user = new User("android123", "John Doe", "123-456-7890", "john@example.com",
                "123 Main St", true, false, false);

        user.setName("Jane Doe");
        assertEquals("Jane Doe", user.getName());

        user.setPhoneNumber("098-765-4321");
        assertEquals("098-765-4321", user.getPhoneNumber());

        user.setEmail("jane@example.com");
        assertEquals("jane@example.com", user.getEmail());

        user.setAddress("456 Elm St");
        assertEquals("456 Elm St", user.getAddress());

        user.setEnNotifications(false);
        assertFalse(user.getEnNotifications());

        user.setAdmin(true);
        assertTrue(user.getIsAdmin());
    }

    @Test
    public void testAddNotification() {
        user = new User("android123", "John Doe", "123-456-7890", "john@example.com",
                "123 Main St", true, false, false);
        notification1 = new Notification("Welcome", "Hello and welcome");
        notification2 = new Notification("Reminder", "Ooga Booga");
        user.addNotification(notification1);
        assertEquals(1, user.getNotifications().size());
        assertEquals(notification1, user.getNotifications().get(0));

        user.addNotification(notification2);
        assertEquals(2, user.getNotifications().size());
    }

    @Test
    public void testDeleteNotification() {
        user = new User("android123", "John Doe", "123-456-7890", "john@example.com",
                "123 Main St", true, false, false);
        notification1 = new Notification("Welcome", "Hello and welcome");
        notification2 = new Notification("Reminder", "Ooga Booga");
        user.addNotification(notification1);
        user.addNotification(notification2);
        user.deleteNotification(notification1);
        assertEquals(1, user.getNotifications().size());
        assertEquals(notification2, user.getNotifications().get(0));
    }

    @Test
    public void testAllNotificationsRead() {
        user = new User("android123", "John Doe", "123-456-7890", "john@example.com",
                "123 Main St", true, false, false);
        notification1 = new Notification("Welcome", "Hello and welcome");
        notification2 = new Notification("Reminder", "Ooga Booga");
        
        notification1.setRead(false);
        notification2.setRead(true);

        user.addNotification(notification1);
        user.addNotification(notification2);
        assertFalse(user.allNotificationsRead());

        notification1.setRead(true);
        assertTrue(user.allNotificationsRead());
    }

    @Test
    public void testSetNotifications() {
        user = new User("android123", "John Doe", "123-456-7890", "john@example.com",
                "123 Main St", true, false, false);
        notification1 = new Notification("Welcome", "Hello and welcome");
        notification2 = new Notification("Reminder", "Ooga Booga");
        ArrayList<Notification> notifications = new ArrayList<>();
        notifications.add(notification1);
        notifications.add(notification2);

        user.setNotifications(notifications);
        assertEquals(notifications, user.getNotifications());
    }
}
