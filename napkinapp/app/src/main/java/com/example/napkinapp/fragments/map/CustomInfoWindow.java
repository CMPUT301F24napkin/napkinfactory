package com.example.napkinapp.fragments.map;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.napkinapp.R;
import com.example.napkinapp.fragments.viewevents.OrganizerViewEventFragment;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.infowindow.InfoWindow;

public class CustomInfoWindow extends InfoWindow {

    View.OnClickListener listener;

    public CustomInfoWindow(MapView mapView, View.OnClickListener listener) {
        super(R.layout.custom_info_window, mapView);
        this.listener = listener;
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

        button.setOnClickListener(listener);
    }

    @Override
    public void onClose() {
    }
}
