/**
 * Array Adapter for the Admin search events page.
 */

package com.example.napkinapp.fragments.adminusersearch;

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
import com.example.napkinapp.models.User;

import java.util.ArrayList;

/**
 * Adapter class which allows Events to be displayed in lists.
 */
public class AdminUserArrayAdapter extends ArrayAdapter<User> {

    public interface UserListCustomizer {
        void CustomizeUserCardButton(Button button);
    }

    private final ArrayList<User> users;
    private final Context context;
    private final UserListCustomizer userListCustomizer;

    /**
     * @param context The context
     * @param users The list of users
     * @param userListCustomizer A customizer for the button behavior
     */
    public AdminUserArrayAdapter(@NonNull Context context, ArrayList<User> users, UserListCustomizer userListCustomizer) {
        super(context, 0, users);
        this.context = context;
        this.users = users;
        this.userListCustomizer = userListCustomizer;
    }

    /**
     * Gets the view for a particular item in the list.
     * @param position The position of the item within the adapter's data set
     * @param convertView The old view to reuse, if possible
     * @param parent The parent that this view will eventually be attached to
     * @return the view for the item
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;

        // If there's no reusable view, create a new one
        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.user_card, parent, false); // Change to a user-specific layout
        } else {
            view = convertView;
        }

        User user = users.get(position);

        // Assuming you're displaying user name and other info here
        TextView userName = view.findViewById(R.id.userName);  // Change to your specific user field
        TextView userEmail = view.findViewById(R.id.userEmail);  // Add other relevant fields if needed
        ImageView userImage = view.findViewById(R.id.userImage);


        Glide.with(context)
                .load(user.getProfileImageUri() != null ? Uri.parse(user.getProfileImageUri()) : null)
                .placeholder(R.drawable.default_image)  //laceholder while loading
                .error(R.drawable.default_image) // Fallback in case of error
                .into(userImage);


        Button button = view.findViewById(R.id.button);  // Assuming you're using a button to trigger actions

        userName.setText(user.getName());  // Display user name
        userEmail.setText(user.getEmail());  // Display user email or any other property

        // Customize the button behavior
        userListCustomizer.CustomizeUserCardButton(button);

        Glide.with(context)
                .load(user.getProfileImageUri() != null ? Uri.parse(user.getProfileImageUri()) : null)
                .placeholder(R.drawable.default_image)  //laceholder while loading
                .error(R.drawable.default_image) // Fallback in case of error
                .into(userImage);

        // Set the user as the tag for the button to retrieve when clicked
        button.setTag(user);

        return view;
    }
}
