/**
 * Array adapter which allows events to be displayed with 2 buttons, and ability to easily customize those buttons with a interface.
 */

package com.example.napkinapp.fragments.registeredevents;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.napkinapp.R;
import com.example.napkinapp.models.Event;

import java.util.ArrayList;

/**
 * Adapter class which allows Events to be displayed in lists.
 */
public class RegisteredEventArrayAdapter extends ArrayAdapter<Event> {

    public interface RegisteredEventListCustomizer {
        void CustomizeEventCardButton(Button button1, Button button2, TextView text3, Event event);
    }

    private final ArrayList<Event> events;
    private final Context context;

    private final RegisteredEventListCustomizer registeredEventListCustomizer;
    /**
     *
     * @param eventListCustomizer The drawable id of the icon we want to display on the left. Do 0 for no icon.
     */
    public RegisteredEventArrayAdapter(@NonNull Context context, ArrayList<Event> events, RegisteredEventListCustomizer eventListCustomizer) {
        super(context, 0, events);
        this.context = context;
        this.events = events;
        this.registeredEventListCustomizer = eventListCustomizer;
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

        Event event = events.get(position);

        if(convertView == null){
            view = LayoutInflater.from(context).inflate(R.layout.register_event_card, parent,false);
        }else {
            view = convertView;
        }

        TextView eventName = view.findViewById(R.id.eventName);
        TextView eventDate = view.findViewById(R.id.eventDate);
        TextView waitForLotteryText = view.findViewById(R.id.waitForLotteryText);
        ImageView eventImage = view.findViewById(R.id.image);
        Glide.with(context)
                .load(event.getEventImageUri() != null ? Uri.parse(event.getEventImageUri()) : null)
                .placeholder(R.drawable.default_image)  //laceholder while loading
                .error(R.drawable.default_image) // Fallback in case of error
                .into(eventImage);

        Button btnAccept = view.findViewById(R.id.btnAccept);
        Button btnDecline = view.findViewById(R.id.btnDecline);

        eventName.setText(event.getName());
        eventDate.setText(event.getEventDate().toString());

        registeredEventListCustomizer.CustomizeEventCardButton(btnAccept, btnDecline, waitForLotteryText, event);

        return view;
    }
}