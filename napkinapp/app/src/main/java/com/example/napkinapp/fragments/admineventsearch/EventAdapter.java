package com.example.napkinapp.fragments.admineventsearch;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.napkinapp.R;
import com.example.napkinapp.models.Event;

import java.util.ArrayList;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {
    private List<Event> events = new ArrayList<>();  // Initialize as an empty list

    // Constructor is optional now, since we're using the setter method
    public EventAdapter() {}

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_event_item, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = events.get(position);
        holder.eventName.setText(event.getName());
        //holder.eventDate.setText(event.getDate().toString());  // Format date as needed
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    /**
     * Sets a new list of events and notifies the adapter to refresh the view.
     *
     * @param newEvents The list of new events to display.
     */
    public void setEvents(List<Event> newEvents) {
        this.events = newEvents;
        notifyDataSetChanged();  // Refreshes the RecyclerView with the new data
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView eventName, eventDate;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            eventName = itemView.findViewById(R.id.event_name);
            eventDate = itemView.findViewById(R.id.event_date);
        }
    }
}