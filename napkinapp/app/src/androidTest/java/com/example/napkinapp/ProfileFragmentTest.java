package com.example.napkinapp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isNotChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.Mockito.verify;

import com.example.napkinapp.fragments.profile.ProfileFragment;
import com.example.napkinapp.models.User;
import com.example.napkinapp.utils.AbstractFragmentTest;
import com.example.napkinapp.utils.DB_Client;

import org.junit.Test;
import org.mockito.Mockito;

public class ProfileFragmentTest extends AbstractFragmentTest<ProfileFragment> {

    private User mockUser;

    @Override
    protected void setUpMockData() {
        mockUser = new User();
        mockUser.setAndroidId("test_user_id");
        mockUser.setName("Test User");
        mockUser.setEmail("test@example.com");
        mockUser.setPhoneNumber("1234567890");
        mockUser.setAddress("123 Test Street");
        mockUser.setEnNotifications(true);
    }

    @Override
    protected ProfileFragment createFragment() {
        return new ProfileFragment(mockUser);
    }

    @Test
    public void testProfileDataDisplay() {
        onView(withId(R.id.editTextName)).check(matches(withText(mockUser.getName())));
        onView(withId(R.id.editTextEmailAddress)).check(matches(withText(mockUser.getEmail())));
        onView(withId(R.id.editTextPhone)).check(matches(withText(mockUser.getPhoneNumber())));
        onView(withId(R.id.editTextAddress)).check(matches(withText(mockUser.getAddress())));
        onView(withId(R.id.notification_switch)).check(matches(isChecked())); // Notifications enabled
    }
}