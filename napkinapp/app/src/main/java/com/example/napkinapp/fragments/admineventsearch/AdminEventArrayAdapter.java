/**
 * Array Adapter for the Admin search events page.
 */

package com.example.napkinapp.fragments.admineventsearch;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.napkinapp.R;
import com.example.napkinapp.models.Event;
import com.example.napkinapp.utils.DB_Client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Adapter class which allows Events to be displayed in lists.
 */
public class AdminEventArrayAdapter extends ArrayAdapter<Event> {

    public interface EventListCustomizer {
        void CustomizeEventCardButton(Button button);
    }

    private final ArrayList<Event> events;
    private final Context context;

    private final EventListCustomizer eventListCustomizer;
    /**
     *
     * @param eventListCustomizer The drawable id of the icon we want to display on the left. Do 0 for no icon.
     */
    public AdminEventArrayAdapter(@NonNull Context context, ArrayList<Event> events, EventListCustomizer eventListCustomizer) {
        super(context, 0, events);
        this.context = context;
        this.events = events;
        this.eventListCustomizer = eventListCustomizer;
    }


    /**
     * copied from Listy City lab 5 (Firestore integration)
     * @param position The position of the item within the adapter's data set of the item whose view
     *        we want.
     * @param convertView The old view to reuse, if possible. Note: You should check that this view
     *        is non-null and of an appropriate type before using. If it is not possible to convert
     *        this view to display the correct data, this method can create a new view.
     *        Heterogeneous lists can specify their number of view types, so that this View is
     *        always of the right type (see {@link #getViewTypeCount()} and
     *        {@link #getItemViewType(int)}).
     * @param parent The parent that this view will eventually be attached to
     * @return the view
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;

        if(convertView == null){
            view = LayoutInflater.from(context).inflate(R.layout.admin_event_card, parent,false);
        }else {
            view = convertView;
        }

        DB_Client db = new DB_Client();

        Event event = events.get(position);

        TextView eventName = view.findViewById(R.id.eventName);
        TextView eventDate = view.findViewById(R.id.eventDate);
        ImageView eventImage = view.findViewById(R.id.image);

        Glide.with(context)
                .load(event.getEventImageUri() != null ? Uri.parse(event.getEventImageUri()) : null)
                .placeholder(R.drawable.default_image)  //laceholder while loading
                .error(R.drawable.default_image) // Fallback in case of error
                .into(eventImage);

        Button button = view.findViewById(R.id.button);

        eventName.setText(event.getName());
        eventDate.setText(event.getEventDate().toString());

        eventListCustomizer.CustomizeEventCardButton(button);

        Button clearQRButton = view.findViewById(R.id.clearQr);
        if (event.getQrHashCode() == null) {
            clearQRButton.setVisibility(View.GONE); // Hide the button
        }
        else {
            clearQRButton.setVisibility(View.VISIBLE); // Show the button

            clearQRButton.setOnClickListener(v -> {
                Map<String, Object> filters = new HashMap<>();
                filters.put("id", event.getId());

                Map<String, Object> updates = new HashMap<>();
                updates.put("qrHashCode", null);
                db.updateAll("Events", filters, updates, new DB_Client.DatabaseCallback<Void>() {
                });
                Toast.makeText(this.getContext(), "QR code data cleared for event: " + event.getName(), Toast.LENGTH_SHORT).show();
                clearQRButton.setVisibility(View.GONE); // Hide the button
            });
        }
        button.setTag(event); // store the event on this button so the event listener can grab it!

        return view;
    }
}