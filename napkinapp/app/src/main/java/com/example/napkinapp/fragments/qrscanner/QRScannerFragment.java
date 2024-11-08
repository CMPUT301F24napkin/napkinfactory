package com.example.napkinapp.fragments.qrscanner;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.napkinapp.R;
import com.example.napkinapp.TitleUpdateListener;

import com.example.napkinapp.fragments.viewevents.ViewEventFragment;
import com.example.napkinapp.models.Event;
import com.example.napkinapp.models.User;
import com.example.napkinapp.utils.DB_Client;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.BarcodeView;
import com.journeyapps.barcodescanner.DefaultDecoderFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class QRScannerFragment extends Fragment {
    private BarcodeView qrScannerView;
    private TitleUpdateListener titleUpdateListener;

    public QRScannerFragment(){

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if(context instanceof TitleUpdateListener){
            titleUpdateListener = (TitleUpdateListener) context;
        }else{
            throw new RuntimeException(context + " needs to implement TitleUpdateListener");
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.qr_scanner, container, false);
        qrScannerView = view.findViewById(R.id.barcode_view);

        titleUpdateListener.updateTitle("QR Scanner");

        // Check for camera permissions, if not set, then will ask
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CAMERA}, 101);
        } else {
            startScanner();
        }

        return view;
    }
    private void startScanner() {
        DB_Client db = new DB_Client();
        qrScannerView.setDecoderFactory(new DefaultDecoderFactory(Collections.singletonList(BarcodeFormat.QR_CODE)));
        qrScannerView.decodeContinuous(new BarcodeCallback() {

            // query event and open view event fragment
            @Override
            public void barcodeResult(BarcodeResult result) {
                HashMap<String,Object> filter = new HashMap<>();
                filter.put("qrHashCode", result.toString());
                db.findOne("Events", filter, new DB_Client.DatabaseCallback<Event>() {

                    @Override
                    public void onSuccess(@Nullable Event data) {
                        if (data == null){
                            Log.e("Database Issue", "Event not found in Database for the specified qrHashCode: " + result.toString());
                            return;
                        }
                        getParentFragmentManager().beginTransaction()
                                .replace(R.id.content_fragmentcontainer, new ViewEventFragment(data)) // Use your actual container ID
                                .addToBackStack(null) // Allows user to go back to ListEventsFragment
                                .commit();
                    }
                    },
                        Event.class);
            }

            @Override
            public void possibleResultPoints(List<ResultPoint> resultPoints) {
                // Optional: handle possible result points
            }
        });
        qrScannerView.resume();
    }
    public void onResume() {
        super.onResume();
        qrScannerView.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        qrScannerView.pause();
    }
}
