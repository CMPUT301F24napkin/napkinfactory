package com.example.napkinapp.fragments.map;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.napkinapp.R;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.infowindow.InfoWindow;

public class CustomInfoWindow extends InfoWindow {

    View.OnClickListener buttonListener;

    public CustomInfoWindow(MapView mapView, View.OnClickListener buttonListener) {
        super(R.layout.custom_info_window, mapView);
        this.buttonListener = buttonListener;
    }

    @Override
    public void onOpen(Object item) {
        Marker marker = (Marker) item;

        View view = mView;
        TextView title = view.findViewById(R.id.title);
        TextView description = view.findViewById(R.id.description);
        Button button = view.findViewById(R.id.button);

        title.setText(marker.getTitle());
        description.setText(marker.getSnippet());

        button.setTag(marker.getRelatedObject()); // propogate the custom info window's user data to the button

        button.setOnClickListener(buttonListener);
    }

    @Override
    public void onClose() {
    }
}
