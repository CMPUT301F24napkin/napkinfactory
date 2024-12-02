package com.example.napkinapp.fragments.adminimagesearch;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.napkinapp.R;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private List<String> imageUrls;
    private OnButtonClickListener buttonClickListener;

    public ImageAdapter(List<String> imageUrls, OnButtonClickListener buttonClickListener) {
        this.imageUrls = imageUrls;
        this.buttonClickListener = buttonClickListener;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        String imageUrl = imageUrls.get(position);

        // Load the image using Glide
        Glide.with(holder.imageView.getContext())
                .load(imageUrl)
                .into(holder.imageView);

        // Set the button click listener, passing the index (position) of the clicked item
        holder.actionButton.setOnClickListener(v -> {
            if (buttonClickListener != null) {
                buttonClickListener.onButtonClick(position); // Pass the position here
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }

    public interface OnButtonClickListener {
        void onButtonClick(int position); // Will receive the index of the clicked item
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        Button actionButton;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            actionButton = itemView.findViewById(R.id.actionButton); // Button ID
        }
    }
}

