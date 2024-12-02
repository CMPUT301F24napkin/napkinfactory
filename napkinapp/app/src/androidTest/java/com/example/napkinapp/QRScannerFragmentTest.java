package com.example.napkinapp;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.napkinapp.fragments.qrscanner.QRScannerFragment;
import com.example.napkinapp.utils.AbstractFragmentTest;

import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.withText;


@RunWith(AndroidJUnit4.class)
public class QRScannerFragmentTest extends AbstractFragmentTest<QRScannerFragment> {

    @Override
    protected QRScannerFragment createFragment() {
        return new QRScannerFragment();
    }

    @Test
    public void testQRScannerFragment_isDisplayed() {
        // Check if the QR scanner view is displayed
        onView(withId(R.layout.qr_scanner)).check(matches(isDisplayed()));
        onView(withId(R.id.header_title)).check(matches(withText("QR Scanner")));
    }


}
