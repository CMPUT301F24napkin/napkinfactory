package com.example.napkinapp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasSibling;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withTagValue;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

import com.example.napkinapp.fragments.notifications.ListNotificationsFragment;
import com.example.napkinapp.models.Notification;
import com.example.napkinapp.models.User;
import com.example.napkinapp.utils.AbstractFragmentTest;
import com.example.napkinapp.utils.TestActivity;

import org.junit.Test;

import java.util.ArrayList;

public class ListNotificationsFragmentTest extends AbstractFragmentTest<ListNotificationsFragment> {

    private User mockUser;
    private ArrayList<Notification> notifications;

    @Override
    protected void setUpMockData() {
        // Create a mock user with notifications
        mockUser = new User();
        mockUser.setAndroidId("test_user_id");
        mockUser.setName("Test User");

        // Add mock notifications
        notifications = new ArrayList<>();
        notifications.add(new Notification("Notification 1", "This is the first notification.", false));
        notifications.add(new Notification("Notification 2", "This is the second notification.", true));
        mockUser.setNotifications(notifications);

        TestActivity.user = mockUser;
    }

    @Override
    protected ListNotificationsFragment createFragment() {
        return new ListNotificationsFragment(mockUser);
    }

    @Test
    public void testDisplayNotifications() {
        // Verify the fragment is displayed
        onView(withId(R.id.notifications_list_view)).check(matches(isDisplayed()));

        // Check that the first notification title and message are displayed
        onView(withText("Notification 1")).check(matches(isDisplayed()));
        onView(withText("This is the first notification.")).check(matches(isDisplayed()));

        // Check that the second notification title and message are displayed
        onView(withText("Notification 2")).check(matches(isDisplayed()));
        onView(withText("This is the second notification.")).check(matches(isDisplayed()));

        // Check that the first notification has the unread icon tag
        onView(allOf(withId(R.id.readButton), hasSibling(withText("Notification 1"))))
                .check(matches(withTagValue(is(R.drawable.notification_bell_active))));

        // Check that the second notification has the read icon tag
        onView(allOf(withId(R.id.readButton), hasSibling(withText("Notification 2"))))
                .check(matches(withTagValue(is(R.drawable.notification_bell_empty))));
    }

    @Test
    public void testToggleNotificationReadStatus() {
        // Initially check the first notification is unread
        onView(allOf(withId(R.id.readButton), hasSibling(withText("Notification 1"))))
                .check(matches(withTagValue(is(R.drawable.notification_bell_active))));

        // Click the unread icon
        onView(allOf(withId(R.id.readButton), hasSibling(withText("Notification 1")))).perform(click());

        // Check that the icon toggles to read
        onView(allOf(withId(R.id.readButton), hasSibling(withText("Notification 1"))))
                .check(matches(withTagValue(is(R.drawable.notification_bell_empty))));

        // Click the icon again
        onView(allOf(withId(R.id.readButton), hasSibling(withText("Notification 1")))).perform(click());

        // Check that the icon toggles back to unread
        onView(allOf(withId(R.id.readButton), hasSibling(withText("Notification 1"))))
                .check(matches(withTagValue(is(R.drawable.notification_bell_active))));
    }


    @Test
    public void testDeleteNotification() {
        // Verify the first notification is displayed
        onView(withText("Notification 1")).check(matches(isDisplayed()));

        // Click the delete button for the first notification
        onView(allOf(withId(R.id.deleteButton), hasSibling(withText("Notification 1")))).perform(click());

        // Verify the first notification is no longer displayed
        onView(withText("Notification 1")).check(doesNotExist());

        // Verify the second notification is still displayed
        onView(withText("Notification 2")).check(matches(isDisplayed()));
    }

}
