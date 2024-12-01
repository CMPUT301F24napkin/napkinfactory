package com.example.napkinapp.fragments.adminimagesearch;

import static androidx.test.InstrumentationRegistry.getContext;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.fragment.app.Fragment;

import com.example.napkinapp.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class adminImageFragment extends Fragment {

    private RecyclerView recyclerView;
    private ImageAdapter imageAdapter;
    private List<String> imageUrls;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.admin_list_images, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewImages);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        imageUrls = new ArrayList<>();
        imageAdapter = new ImageAdapter(imageUrls);
        recyclerView.setAdapter(imageAdapter);
        fetchImagesFromStorage();
        return view;
    }

    private void fetchImagesFromStorage() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference().child("images"); // Replace "images" with your folder name.

        storageReference.listAll()
                .addOnSuccessListener(listResult -> {
                    for (StorageReference fileRef : listResult.getItems()) {
                        fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            imageUrls.add(uri.toString());
                            imageAdapter.notifyDataSetChanged();
                        });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to fetch images", Toast.LENGTH_SHORT).show();
                });
    }
}


