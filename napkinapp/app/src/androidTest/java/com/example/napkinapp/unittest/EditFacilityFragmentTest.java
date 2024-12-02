package com.example.napkinapp.unittest;

import static org.junit.Assert.assertEquals;

import com.example.napkinapp.fragments.facility.EditFacilityFragment;
import com.example.napkinapp.fragments.facility.ViewFacilityFragment;
import com.example.napkinapp.models.Facility;
import com.example.napkinapp.models.User;
import com.example.napkinapp.utils.AbstractFragmentTest;
import com.example.napkinapp.utils.DB_Client;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class EditFacilityFragmentTest extends AbstractFragmentTest<EditFacilityFragment> {
    private User mockUser;
    private Facility mockFacility;

    @Override
    protected void setUpMockData() {
        mockUser = new User();
        mockUser.setAndroidId("user1_id");
        mockUser.setName("Mock User 1");

        mockFacility = new Facility();
        mockFacility.init();
        mockFacility.setId("test_facility");
        mockFacility.setName("Mock Facility");
        mockFacility.setDescription("Mock Facility Description");
        mockFacility.setLocation(new ArrayList<>(List.of(3.5d, 4.5d)));
        mockUser.setFacility(mockFacility.getId());
    }

    @Override
    protected EditFacilityFragment createFragment() {
        return new EditFacilityFragment(mockFacility, mockUser);
    }

    @Test
    public void testSaveFacility(){
        EditFacilityFragment fragment = getFragment();

        Facility newFacility = new Facility();
        newFacility.init();
        newFacility.setId(mockFacility.getId());
        newFacility.setName("New Facility");
        newFacility.setDescription("New Facility Description");
        newFacility.setLocation(new ArrayList<>(List.of(6.5d, 0.5d)));

        fragment.saveFacility(newFacility.getName(), newFacility.getDescription(), newFacility.getLocation());

        Facility writtenFacility = (Facility) DB_Client.getWrittenData().get(0);

        assertEquals(newFacility.getName(), writtenFacility.getName());
        assertEquals(newFacility.getDescription(), writtenFacility.getDescription());
        assertEquals(newFacility.getLocation(), writtenFacility.getLocation());


        User writtenUser = (User) DB_Client.getWrittenData().get(1);

        assertEquals(newFacility.getId(), writtenUser.getFacility());
    }
}
