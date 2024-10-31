package com.example.napkinapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.napkinapp.R;
import com.example.napkinapp.TitleUpdateListener;

public class HeaderFragment extends Fragment {
    public interface OnHeaderButtonClick {
        void handleProfileButtonClick();
        void handleHamburgerButtonClick();
    }

    private OnHeaderButtonClick listener;
    private TextView headerTitle;

    public HeaderFragment(){
        // Required empty public constructor
    }

    public void setHeaderTitle(String title){
        headerTitle.setText(title);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if(context instanceof OnHeaderButtonClick) {
            listener = (OnHeaderButtonClick) context;
        }else{
            throw new RuntimeException(context + " must implement HeaderHandler");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_header, container, false);

        ImageButton profileBtn = view.findViewById(R.id.btn_profile);
        ImageButton hamburgerBtn = view.findViewById(R.id.btn_hamburger);
        headerTitle = view.findViewById(R.id.header_title);

        profileBtn.setOnClickListener((v) -> {
            listener.handleProfileButtonClick();
        });

        hamburgerBtn.setOnClickListener((v) -> {
            listener.handleHamburgerButtonClick();
        });

        return view;

    }
}
