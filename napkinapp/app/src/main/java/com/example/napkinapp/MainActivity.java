package com.example.napkinapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.napkinapp.fragments.FooterFragment;
import com.example.napkinapp.fragments.HeaderFragment;
import com.example.napkinapp.fragments.adminmenu.AdminNavagationFragment;
import com.example.napkinapp.fragments.myevents.MyEventsFragment;
import com.example.napkinapp.fragments.listevents.ListEventsFragment;
import com.example.napkinapp.fragments.notifications.ListNotificationsFragment;
import com.example.napkinapp.fragments.profile.ProfileFragment;

import com.example.napkinapp.fragments.qrscanner.QRScannerFragment;
import com.example.napkinapp.fragments.registeredevents.RegisteredEventsFragment;
import com.example.napkinapp.models.Notification;
import com.example.napkinapp.models.User;
import com.example.napkinapp.utils.DB_Client;
import com.example.napkinapp.utils.ListenForUserUpdatesWorker;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements HeaderFragment.OnHeaderButtonClick,
        FooterFragment.FooterNavigationListener, TitleUpdateListener {

    private HeaderFragment header;
    private FooterFragment footer;

    public static User user;
    public static String userID;

    public static final String CHANNEL_ID = "napkin_app_notifications";


    public void updateHeaderNotificationIcon() {
        if (header != null) {
            header.updateNotificationIcon();
        }
    }

    public void openProfileView(){
        footer.resetButtons();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_fragmentcontainer, new ProfileFragment(user))
                .addToBackStack(null)
                .commit();

    }

    /**
     * This function implementation will handle when the footer is clicked.
     * @param btnid the id of the button that is clicked.
     */
    @Override
    public void handleFooterButtonClick(int btnid) {
        if (user.getEmail().isBlank() || user.getName().isBlank()){
            openProfileView();
            return;
        }
        // add functionality to get a user from the database or instantiate if this is the first log-on
        Fragment selectedFragment = null;
        Fragment currFrag = getSupportFragmentManager().findFragmentById(R.id.content_fragmentcontainer);
        switch(btnid) {
            // Not using actual button id's as apparently they're non final
            case 0:
                selectedFragment = new ListEventsFragment(user);
                break;
            case 1:
                selectedFragment = new RegisteredEventsFragment(user);
                break;
            case 2:
                // map
                break;
            case 3:
                // QRscanner
                selectedFragment = new QRScannerFragment(user);
                break;
            case 4:
                // Myevents
                selectedFragment = new MyEventsFragment(user);
                break;
        }

        if(selectedFragment != null){
            if(currFrag != null && currFrag.getClass().equals(selectedFragment.getClass())){
                // Already on screen so do nothing
                return;
            }

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_fragmentcontainer, selectedFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    /**
     * Helper function for setting the title. Checks if not null.
     * @param title the title to set
     */
    @Override
    public void updateTitle(String title) {
        if(header != null){
            header.setHeaderTitle(title);
        }
    }

    /**
     * Handle when the notification button is clicked in the top bar. Switches the fragment to be ListNotificationsFragment.
     */
    @Override
    public void handleNotificationButtonClick() {
        if (user.getEmail().isBlank() || user.getName().isBlank()){
            openProfileView();
            return;
        }
        Fragment currFrag = getSupportFragmentManager().findFragmentById(R.id.content_fragmentcontainer);

        if(currFrag instanceof ListNotificationsFragment){
            // do nothing if notifications already opened
            return;
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_fragmentcontainer, new ListNotificationsFragment(user))
                .addToBackStack(null)
                .commit();

        footer.resetButtons();
    }

    /**
     * Handle what happens when hamburger menu is clicked. opens Admin screen.
     */
    @Override
    public void handleHamburgerButtonClick() {
        if (user.getEmail().isBlank() || user.getName().isBlank()){
            openProfileView();
            return;
        }
        Fragment currFrag = getSupportFragmentManager().findFragmentById(R.id.content_fragmentcontainer);

        if(currFrag instanceof AdminNavagationFragment){
            // do nothing if already in admin screen
            return;
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_fragmentcontainer, new AdminNavagationFragment(user))
                .addToBackStack(null)
                .commit();

        footer.resetButtons();
    }

        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        createNotificationChannel();

        initializeApp();

        // Load header fragment
        header = new HeaderFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.header_fragmentcontainer, header)
                .commit();



        // To properly update footer buttons on back button press
        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.content_fragmentcontainer);

            // Update the selected button in footer based on the current fragment
            if (currentFragment instanceof ListEventsFragment) {
                footer.setSelectedButtonById(0);
            } else if (currentFragment instanceof RegisteredEventsFragment) {
                footer.setSelectedButtonById(1);
            } /*else if (currentFragment instanceof MapFragment) {
                footer.setSelectedButtonById(2);
            }*/ else if (currentFragment instanceof QRScannerFragment) {
                footer.setSelectedButtonById(3);
            }
            else if (currentFragment instanceof MyEventsFragment) {
                footer.setSelectedButtonById(4);
            }
        });

        // Load footer fragment
        footer = new FooterFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.footer_fragmentcontainer, footer)
                .commit();


    }

    /**
     * Initializes the app
     * checks if user is already registered. if not, register. if so, log in automatically.
     */
    private void initializeApp(){
        // Ignore error
        userID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        user = new User();
        user.setAndroidId(userID);

        DB_Client db = new DB_Client();

        db.findOne("Users", Map.of("androidId", userID), new DB_Client.DatabaseCallback<User>() {
            @Override
            public void onSuccess(@Nullable User data) {
                if(data != null){
                    // User exists, retrieve and set them up
                    user = data;
                    updateHeaderNotificationIcon();
                    listenForUserUpdates();
                    Toast.makeText(getBaseContext(), "Welcome back " + user.getName(), Toast.LENGTH_SHORT).show();

                    if (user.getEmail().isBlank() || user.getName().isBlank()){
                        openProfileView();
                    } else {
                        OpenListEvents();
                    }

                    scheduleUserUpdateListener();

                    for (Notification n :
                            user.getNotifications()) {
                        if (!n.getRead()){
                            sendPushNotification(getBaseContext(), n.getTitle(), n.getMessage());
                        }
                    }
                }else {
                    // User does not exist, open profile screen
                    Toast.makeText(getBaseContext(), "Create a profile for new Login!", Toast.LENGTH_SHORT).show();
                    OpenProfile();
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("User Initialization", "Something went wrong initializing user. UserID: "+userID);
            }
        }, User.class);
    }

    /**
     * Set up a snapshot listener to listen for real-time changes in the user's document.
     */
    private void listenForUserUpdates() {
        DB_Client db = new DB_Client();

        // Set up a listener for changes to the user's document in the "Users" collection
        db.addDocumentSnapshotListener("Users", user.getAndroidId(), new DB_Client.DatabaseCallback<User>() {
            @Override
            public void onSuccess(@Nullable User updatedUser) {
                if (updatedUser != null) {
                    if (user.getNotifications().size() < updatedUser.getNotifications().size() && !user.getNotifications().isEmpty()){
                        Toast.makeText(getBaseContext(), "You received a new notification", Toast.LENGTH_SHORT).show();
                        if (user.getEnNotifications()) {
                            Notification notification = user.getNotifications().get(user.getNotifications().size() - 1);
                            sendPushNotification(getBaseContext(), notification.getTitle(), notification.getMessage());
                        }
                    }

                    user = updatedUser;
                    updateHeaderNotificationIcon();

                    Fragment currFrag = getSupportFragmentManager().findFragmentById(R.id.content_fragmentcontainer);

                    if(currFrag instanceof ListNotificationsFragment){
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.content_fragmentcontainer, new ListNotificationsFragment(user))
                                .addToBackStack(null)
                                .commit();
                    }

                    Log.i("User Update", "User data updated in real-time: " + user.getName());

                } else {
                    Log.w("User Update", "User data is null, no changes detected.");
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("User Listener", "Error listening for real-time user updates: " + e.getMessage());
            }
        }, User.class);
    }

    /**
     * helper function for opening the profile screen
     */
    private void OpenProfile(){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_fragmentcontainer, new ProfileFragment(user))
                .commit();
    }

    /**
    helper function for opening the list events screen
     */
    private void OpenListEvents(){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_fragmentcontainer, new ListEventsFragment(user))
                .commit();
    }

    public static void sendPushNotification(Context context, String title, String message) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        } else {
            pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_bell_active)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }

    private void scheduleUserUpdateListener() {
        WorkManager workManager = WorkManager.getInstance(this);
        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(ListenForUserUpdatesWorker.class)
                .build();
        workManager.enqueue(workRequest);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Napkin App Notifications";
            String description = "Notifications for event updates and profile alerts";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}