package com.example.napkinapp.fragments.registeredevents;

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
            view = LayoutInflater.from(context).inflate(R.layout.event_card_2_buttons, parent,false);
        }else {
            view = convertView;
        }

        TextView text1 = view.findViewById(R.id.text1);
        TextView text2 = view.findViewById(R.id.text2);
        TextView text3 = view.findViewById(R.id.text3);

        Button button1 = view.findViewById(R.id.button1);
        Button button2 = view.findViewById(R.id.button2);

        text1.setText(event.getName());
        text2.setText(event.getEventDate().toString());

        registeredEventListCustomizer.CustomizeEventCardButton(button1, button2, text3, event);

        return view;
    }
}