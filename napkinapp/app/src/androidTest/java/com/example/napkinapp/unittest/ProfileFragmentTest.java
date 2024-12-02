package com.example.napkinapp.unittest;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isNotChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

import static java.util.Collections.copy;

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
    }

    @Override
    protected ProfileFragment createFragment() {
        return new ProfileFragment(mockUser);
    }

    @Test
    public void testCreateEvent(){
        ProfileFragment fragment = getFragment();

        User newUser = new User();
        newUser.setName("New User");
        newUser.setEmail("example@newuser.com");
        newUser.setPhoneNumber("0987654321");
        newUser.setAddress("321 New User Street");

        fragment.updateUserInfo(newUser.getName(), newUser.getEmail(), newUser.getPhoneNumber(), newUser.getAddress());

        User writtenUser = (User) DB_Client.getWrittenData().get(0);

        assertEquals(newUser.getName(), writtenUser.getName());
        assertEquals(newUser.getEmail(), writtenUser.getEmail());
        assertEquals(newUser.getPhoneNumber(), writtenUser.getPhoneNumber());
        assertEquals(newUser.getAddress(), writtenUser.getAddress());

    }
}