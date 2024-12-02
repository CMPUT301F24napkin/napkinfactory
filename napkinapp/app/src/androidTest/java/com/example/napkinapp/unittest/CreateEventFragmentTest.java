package com.example.napkinapp.unittest;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import android.net.Uri;

import com.example.napkinapp.fragments.createevent.CreateEventFragment;
import com.example.napkinapp.models.Event;
import com.example.napkinapp.models.User;
import com.example.napkinapp.utils.AbstractFragmentTest;
import com.example.napkinapp.utils.DB_Client;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CreateEventFragmentTest extends AbstractFragmentTest<CreateEventFragment> {

    private DB_Client mockDbClient;
    private User testUser;

    @Override
    protected CreateEventFragment createFragment() {
        return new CreateEventFragment();
    }

    @Override
    public void setUpMockData() {
        // Initialize the mock DB client
        mockDbClient = new DB_Client();
        DB_Client.reset();

        // Create a test user
        testUser = new User();
        testUser.setAndroidId("test_user_id");
        testUser.setName("Test User");
    }

    @Test
    public void testCreateEvent() throws Exception {
        // Mock input data
        String eventName = "Test Event";
        String eventDescription = "This is a test event.";
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        Date eventDate = dateFormat.parse("12/31/2024");
        Date lotteryDate = dateFormat.parse("12/25/2024");
        int entrantLimit = 100;
        int participantLimit = 50;
        boolean geolocationEnabled = true;
        Uri imageUri = Uri.parse("content://mock/path/to/image");

        CreateEventFragment fragment = getFragment();

        fragment.createEvent(
                testUser.getAndroidId(),
                eventName,
                eventDate,
                lotteryDate,
                eventDescription,
                entrantLimit,
                participantLimit,
                geolocationEnabled,
                imageUri);

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
    }
}
