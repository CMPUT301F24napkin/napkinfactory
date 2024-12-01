package com.example.napkinapp.unittest;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import android.app.Activity;
import android.provider.Settings;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.core.app.ApplicationProvider;

import com.example.napkinapp.R;
import com.example.napkinapp.fragments.createevent.CreateEventFragment;
import com.example.napkinapp.models.Event;
import com.example.napkinapp.models.User;
import com.example.napkinapp.utils.DB_Client;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CreateEventFragmentTest {

    private DB_Client mockDbClient;
    private User testUser;

    @Before
    public void setUp() {
        // Initialize the mock DB client
        mockDbClient = new DB_Client();
        DB_Client.reset();

        // Create a test user
        testUser = new User();
        testUser.setAndroidId("test_user_id");
        testUser.setName("Test User");
    }

    @After
    public void tearDown() {
        DB_Client.reset();
    }

    @Test
    public void testCreateEvent_SuccessfulCreation() throws Exception {
        // Mock input data
        String eventName = "Test Event";
        String eventDescription = "This is a test event.";
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        Date eventDate = dateFormat.parse("12/31/2024");
        Date lotteryDate = dateFormat.parse("12/25/2024");
        int entrantLimit = 100;
        int participantLimit = 50;
        boolean geolocationEnabled = true;

        // Set up scenario
        FragmentScenario<CreateEventFragment> scenario = FragmentScenario.launchInContainer(
                CreateEventFragment.class,
                null
        );

        scenario.onFragment(fragment -> {
            fragment.createEvent(
                    eventName,
                    eventDate,
                    lotteryDate,
                    eventDescription,
                    entrantLimit,
                    participantLimit,
                    geolocationEnabled
            );

            // Verify that the event was inserted into the mock DB
            assertEquals(1, DB_Client.getInsertedData().size());
            Event createdEvent = (Event) DB_Client.getInsertedData().get(0);

            // Validate the event properties
            assertEquals(testUser.getAndroidId(), createdEvent.getOrganizerId());
            assertEquals(eventName, createdEvent.getName());
            assertEquals(eventDescription, createdEvent.getDescription());
            assertEquals(eventDate, createdEvent.getEventDate());
            assertEquals(lotteryDate, createdEvent.getLotteryDate());
            assertEquals(entrantLimit, createdEvent.getEntrantLimit());
            assertEquals(participantLimit, createdEvent.getParticipantLimit());
            assertEquals(geolocationEnabled, createdEvent.isRequireGeolocation());
        });
    }
}
