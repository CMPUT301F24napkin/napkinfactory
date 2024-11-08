/**
 * Array adapter for viewing users. This is used in the OrganizerViewEventFragment
 * it inflates a bunch of brief_user_list_item views.
 */

package com.example.napkinapp.fragments.viewevents;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.napkinapp.R;
import com.example.napkinapp.models.User;

import java.util.ArrayList;
import java.util.List;

public class UserArrayAdapter extends ArrayAdapter<User> {

    private final ArrayList<User> users;
    private final Context mContext;

    public UserArrayAdapter(@NonNull Context context, @NonNull ArrayList<User> users) {
        super(context, 0, users);

        this.mContext = context;
        this.users = users;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;

        if(convertView == null){
            view = LayoutInflater.from(mContext).inflate(R.layout.brief_user_list_item, parent,false);
        }else {
            view = convertView;
        }

        User user = users.get(position);

        TextView userName = view.findViewById(R.id.user_name);
        userName.setText(user.getName());

        return view;
    }

}
