package com.example.napkinapp.fragments.adminfacilitysearch;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.napkinapp.R;
import com.example.napkinapp.models.Facility;

import java.util.ArrayList;

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
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.napkinapp.R;
import com.example.napkinapp.models.Event;

import java.util.ArrayList;

/**
 * Adapter class which allows Events to be displayed in lists.
 */
public class AdminFacilityArrayAdapter extends ArrayAdapter<Facility> {

    public interface FacilityClickListener {
        void onDeleteButtonClick(Facility facility);
    }

    private final ArrayList<Facility> facilities;
    private final Context context;
    private final FacilityClickListener facilityClickListener;


    public AdminFacilityArrayAdapter(@NonNull Context context, ArrayList<Facility> facilities, FacilityClickListener facilityClickListener) {
        super(context, 0, facilities);
        this.context = context;
        this.facilities = facilities;
        this.facilityClickListener = facilityClickListener;

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
            view = LayoutInflater.from(context).inflate(R.layout.facility_card, parent,false);
        }else {
            view = convertView;
        }

        Facility facility = facilities.get(position);

        TextView eventName = view.findViewById(R.id.eventName);
        TextView eventDate = view.findViewById(R.id.eventDate);
        ImageView facilityImage = view.findViewById(R.id.image);

        Glide.with(context)
                .load(facility.getImageUri() != null ? Uri.parse(facility.getImageUri()) : null)
                .placeholder(R.drawable.default_image)  //laceholder while loading
                .error(R.drawable.default_image) // Fallback in case of error
                .into(facilityImage);

        Button button = view.findViewById(R.id.button);
        button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.colorRemoveDark)));


        eventName.setText(facility.getName());
        eventDate.setText(facility.getDescription().toString());


        button.setTag(facility); // store the event on this button so the event listener can grab it!

        button.setOnClickListener(v -> {
            // Pass the facility to the fragment to handle the deletion
            if (facilityClickListener != null) {
                facilityClickListener.onDeleteButtonClick(facility);
            }
        });

        return view;
    }
}