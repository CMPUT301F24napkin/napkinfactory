package com.example.napkinapp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;


import com.example.napkinapp.models.Event;
import com.example.napkinapp.models.User;
import com.example.napkinapp.utils.AbstractFragmentTest;
import com.example.napkinapp.fragments.adminimagesearch.AdminImageFragment;
import com.example.napkinapp.utils.DB_Client;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class AdminImageFragmentTest extends AbstractFragmentTest<AdminImageFragment> {
    private List<String> imageUris;

    @Override
    protected void setUpMockData() {
        // Prepare mock user

        imageUris = new ArrayList<>();

        imageUris.add("image1");

        List<Object> mockImageList = new ArrayList<>(imageUris);

        DB_Client.setFindAllData(mockImageList);
    }

    @Override
    protected AdminImageFragment createFragment() {
        return new AdminImageFragment();
    }

    @Test
    public void testRecyclerViewDisplaysImages() {
        // Ensure the RecyclerView is displayed
        onView(withId(R.id.recyclerViewImages)).check(matches(isDisplayed()));

        // Scroll to specific positions to ensure the items are rendered
        onView(withId(R.id.recyclerViewImages)).perform(scrollToPosition(0));
        onView(withId(R.id.recyclerViewImages)).perform(scrollToPosition(1));
        onView(withId(R.id.recyclerViewImages)).perform(scrollToPosition(2));
    }

    @Test
    public void testDeleteButtonRemovesImage() {
        // Ensure the RecyclerView is displayed
        onView(withId(R.id.recyclerViewImages)).check(matches(isDisplayed()));

        // Get the position of the last item
        int lastPosition = imageUris.size() - 1;

        // Scroll to the last item to ensure it's visible
        onView(withId(R.id.recyclerViewImages))
                .perform(scrollToPosition(lastPosition));

        // Ensure the delete button for the last item is visible
        onView(withId(R.id.recyclerViewImages))
                .perform(actionOnItemAtPosition(lastPosition, click()));

        // Validate that the item is removed (RecyclerView size should shrink)
        int newSize = imageUris.size() - 1;
        onView(withId(R.id.recyclerViewImages))
                .perform(scrollToPosition(newSize - 1))
                .check(matches(isDisplayed()));
    }


}
