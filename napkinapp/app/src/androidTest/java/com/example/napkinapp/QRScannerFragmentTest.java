package com.example.napkinapp;

import androidx.fragment.app.Fragment;
import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.example.napkinapp.fragments.qrscanner.QRScannerFragment;
import com.example.napkinapp.fragments.viewevents.ViewEventFragment;
import com.example.napkinapp.utils.AbstractFragmentTest;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.BarcodeView;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@RunWith(AndroidJUnit4.class)
public class QRScannerFragmentTest extends AbstractFragmentTest<QRScannerFragment> {

    @Override
    protected QRScannerFragment createFragment() {
        return new QRScannerFragment();
    }

    @Test
    public void testQRScannerIsDisplayed(){
        // Check if the QR scanner view is displayed
        onView(withId(R.id.barcode_view)).check(matches(isDisplayed()));
        onView(withId(R.id.qr_overlay)).check(matches(isDisplayed()));
        onView(withId(R.id.scan_instructions)).check(matches(withText("Align QR code within the frame")));
    }

    @Test
    public void testQRScannerOpensViewEvent(){
        onView(withId(R.id.barcode_view)).perform();
    }




}
