package com.example.napkinapp.fragments.listevents;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.napkinapp.R;
import com.example.napkinapp.models.Event;

import java.util.ArrayList;

/**
 * Adapter class which allows Events to be displayed in lists.
 */
public class EventArrayAdapter extends ArrayAdapter<Event> {

    private ArrayList<Event> events;
    private Context context;

    private String buttonText;
    private int drawableIcon;
    private View.OnClickListener onClickListener;
    /**
     *
     * @param context
     * @param events
     * @param buttonText The text that we want to display on the buttons
     * @param drawableIcon The drawable id of the icon we want to display on the left. Do 0 for no icon.
     */
    public EventArrayAdapter(@NonNull Context context, ArrayList<Event> events, String buttonText, int drawableIcon, View.OnClickListener onClickListener) {
        super(context, 0, events);
        this.context = context;
        this.events = events;
        this.buttonText = buttonText;
        this.drawableIcon = drawableIcon;
        this.onClickListener = onClickListener;
    }


    /**
     * @brief copied from Listy City lab 5 (Firestore integration)
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

        TextView text1 = view.findViewById(R.id.text1);
        TextView text2 = view.findViewById(R.id.text2);

        text1.setText(event.getName());
        text2.setText(event.getDate().toString());

        Button button = view.findViewById(R.id.button);
        button.setText(buttonText);
        if(drawableIcon != 0) {
            button.setCompoundDrawablesWithIntrinsicBounds(drawableIcon, 0, 0, 0);
        }
        button.setOnClickListener(onClickListener);

        button.setTag(event); // store the event on this button so the event listener can grab it!

        return view;
    }
}