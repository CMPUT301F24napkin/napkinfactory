package com.example.napkinapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import org.apache.commons.text.RandomStringGenerator;

public class MainActivity extends AppCompatActivity {

    private ListView eventsListView;
    private ArrayList<Event> events;
    private EventArrayAdapter eventArrayAdapter;

    private FirebaseFirestore db;
    private CollectionReference eventsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("events");


        events = new ArrayList<>(); // the data to be displayed
        eventArrayAdapter = new EventArrayAdapter(this, events); // the thing that tells the UI to update
        eventsListView = findViewById(R.id.events_list_view); // the UI component
        eventsListView.setAdapter(eventArrayAdapter);


        // This is where you listen to the database and update the ArrayList<Event> accordingly.
        // Then call eventArrayAdapter.notifyDataSetChanged() to actually update the UI
        eventsRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null) {
                    Log.e("Firestore", error.toString());
                    return;
                }
                if (value != null) {
                    events.clear();
                    for (QueryDocumentSnapshot doc : value) {
                        events.add((new EventSerializer()).deserialize(doc));
                    }
                    eventArrayAdapter.notifyDataSetChanged();
                }
            }
        });

        //
        // ---------- testing the firestore
        //
        Button test_CreateEvent = findViewById(R.id.test_create_button);
        Button test_DestroyEvent = findViewById(R.id.test_destroy_button);

        test_CreateEvent.setOnClickListener(v -> {
            Random rng = new Random();
            RandomStringGenerator rsg = RandomStringGenerator.builder().withinRange('0', 'z').filteredBy(Character::isLetterOrDigit).get();
            String eventId = rsg.generate(12);
            String eventName = rsg.generate(5);
//            String eventDescription = rsg.generate(25);
            Date eventDate = new Date(rng.nextLong() % 1729903898);

            Event event = new Event(eventId, eventName, eventDate);
            events.add(event); // add it to local list
            eventArrayAdapter.notifyDataSetChanged(); // update view

            eventsRef.document(eventId).set((new EventSerializer()).serialize(event))
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("Firestore", "DocumentSnapshot successfully written!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("Firestore", "DocumentSnapshot failed to write!");
                            Log.d("Firestire", e.getMessage());

                        }
                    });
            ;
        });

        test_DestroyEvent.setOnClickListener(v -> {
            eventsRef.document(eventArrayAdapter.getItem(0).getId()).delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("Firestore", "DocumentSnapshot successfully deleted!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("Firestore", "DocumentSnapshot failed to delete!");
                            Log.d("Firestire", e.getMessage());
                        }
            });
        });

    }
}