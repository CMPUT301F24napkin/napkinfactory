/**
 * Array Adapter for the Admin search events page.
 */

package com.example.napkinapp.fragments.admineventsearch;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.napkinapp.R;
import com.example.napkinapp.models.Event;

import java.util.ArrayList;

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
            view = LayoutInflater.from(context).inflate(R.layout.event_card, parent,false);
        }else {
            view = convertView;
        }

        Event event = events.get(position);

        TextView eventName = view.findViewById(R.id.event_name);
        TextView eventDate = view.findViewById(R.id.event_date);

        Button button = view.findViewById(R.id.button);

        eventName.setText(event.getName());
        eventDate.setText(event.getEventDate().toString());

        eventListCustomizer.CustomizeEventCardButton(button);

        button.setTag(event); // store the event on this button so the event listener can grab it!

        return view;
    }
}