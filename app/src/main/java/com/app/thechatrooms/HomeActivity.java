package com.app.thechatrooms;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.app.thechatrooms.models.GroupChatRoom;
import com.app.thechatrooms.models.GroupOnlineUsers;
import com.app.thechatrooms.models.User;
import com.app.thechatrooms.ui.chats.ChatsFragment;
import com.app.thechatrooms.ui.contacts.ContactsFragment;
import com.app.thechatrooms.ui.groups.GroupsFragment;
import com.app.thechatrooms.ui.myTrips.MyTripsFragment;
import com.app.thechatrooms.ui.profile.ProfileFragment;
import com.app.thechatrooms.utilities.CircleTransform;
import com.app.thechatrooms.utilities.Parameters;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, ContactsFragment.OnContactsSelectedListener {

    private static final String TAG = "HomeActivity";
    StorageReference storageReference;
    ImageView userProfileImageView;
    TextView displayNameTextView;
    TextView displayEmailIdTextView;
    //private AppBarConfiguration mAppBarConfiguration;
    private Toolbar toolbar;
    private ActionBarDrawerToggle toggle;
    private StorageReference mStorageRef;
    private FirebaseAuth mAuth;
    //private User user;
    private User user;
    private DrawerLayout drawer;
    // Write a message to the database
    private FirebaseDatabase database;
    private DatabaseReference myRef, groupChatDbRef;
    private String userId;
    private String groupName;
    private NavigationView navigationView;
    FragmentManager fragmentManager = getSupportFragmentManager();
    FragmentTransaction fragmentTransaction ;

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        userId = preferences.getString(Parameters.USER_ID, "");
        myRef = database.getReference("chatRooms/userProfiles/");
        myRef.child(userId).child("online").setValue(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        // Initialize Firebase Auth
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        database = FirebaseDatabase.getInstance();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        userId = preferences.getString(Parameters.USER_ID, "");
        groupChatDbRef = database.getReference("chatRooms/groupChatRoom/");
        if (!userId.equalsIgnoreCase("")) {
            myRef = database.getReference("chatRooms/userProfiles/" + userId);
            storageReference = mStorageRef.child("chatRooms/userProfiles/" + userId + ".jpg");
            // Read from the database
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    user = dataSnapshot.getValue(User.class);
                    displayNameTextView.setText(user.getFirstName() + " " + user.getLastName());
                    displayEmailIdTextView.setText(user.getEmailId());
                    Log.d(TAG, "Value is: " + user.toString());
                    Picasso.get()
                            .load(user.getUserProfileImageUrl())
                            .transform(new CircleTransform()).centerCrop().fit()
                            .into(userProfileImageView);
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w(TAG, "Failed to read value.", error.toException());
                }
            });
        }
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView logOut = findViewById(R.id.logout);
        logOut.setOnClickListener(view -> {
            mAuth.signOut();
            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
        drawer = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        View hView = navigationView.getHeaderView(0);
        displayNameTextView = hView.findViewById(R.id.displayNameTextView);
        displayEmailIdTextView = hView.findViewById(R.id.displayEmailIdTextView);
        userProfileImageView = hView.findViewById(R.id.userProfileImageView);

        if (savedInstanceState == null) {
            Bundle bundle1 = new Bundle();
            bundle1.putSerializable(Parameters.USER_ID, user);
            ChatsFragment fragment1 = new ChatsFragment();
            fragment1.setArguments(bundle1);
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.nav_host_fragment, fragment1, "Chat Fragment").addToBackStack(null).commit();
            toolbar.setTitle(R.string.menu_chats);
            navigationView.setCheckedItem(R.id.nav_chats);
        }

    }

    @Override
    public void onBackPressed() {
        int backCount = fragmentManager.getBackStackEntryCount();
        if(backCount > 1){
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            }
            fragmentManager.popBackStack();
        }else{
            super.onBackPressed();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        myRef = database.getReference("chatRooms/userProfiles/");
        myRef.child(userId).child("online").setValue(false);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_chats:
                Bundle bundle1 = new Bundle();
                bundle1.putSerializable(Parameters.USER_ID, user);
                ChatsFragment fragment1 = new ChatsFragment();
                fragment1.setArguments(bundle1);
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.nav_host_fragment, fragment1,"Chat Fragment").addToBackStack(null);
                fragmentTransaction.commit();
                toolbar.setTitle(R.string.menu_chats);
                break;
            case R.id.nav_contacts:
                getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, new ContactsFragment(),"Contacts Fragment").addToBackStack(null).commit();
                toolbar.setTitle(R.string.menu_contacts);
                break;
            case R.id.nav_profile:
                Bundle bundle = new Bundle();
                bundle.putSerializable(Parameters.USER_ID, user);
                ProfileFragment fragment = new ProfileFragment();
                fragment.setArguments(bundle);
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.nav_host_fragment, fragment,"Profile Fragment").addToBackStack(null);
                fragmentTransaction.commit();
                toolbar.setTitle(R.string.menu_profile);
                break;
            case R.id.nav_myTrips:
                Bundle bundleMyTrips = new Bundle();
                bundleMyTrips.putSerializable(Parameters.USER_ID, user);
                MyTripsFragment myTripsFragment = new MyTripsFragment();
                myTripsFragment.setArguments(bundleMyTrips);
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.nav_host_fragment, myTripsFragment, "My Trips Fragment").addToBackStack(null);
                fragmentTransaction.commit();
                toolbar.setTitle(R.string.menu_myTrips);
                break;
            case R.id.nav_groups:
                Bundle bundleGroup = new Bundle();
                bundleGroup.putSerializable(Parameters.USER_ID, user);
                GroupsFragment groupsFragment = new GroupsFragment();
                groupsFragment.setArguments(bundleGroup);
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.nav_host_fragment, groupsFragment, "Group Fragment").addToBackStack(null);
                fragmentTransaction.commit();
                toolbar.setTitle(R.string.menu_groups);
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_createGroup:
                LayoutInflater layoutInflater = getLayoutInflater();
                final View v = layoutInflater.inflate(R.layout.alert_dialog, null);
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this, R.style.CreateGroupDialogTheme);
                alertDialog.setTitle("Enter Group Name :");
                EditText input = v.findViewById(R.id.etComments);
                input.setHint(R.string.group_name);
                input.setTextColor(Color.WHITE);

                alertDialog.setPositiveButton("Create",
                        (dialog, which) -> {
                            groupName = input.getText().toString();
                            Log.e(TAG, "groupName = " + groupName);
                            GroupChatRoom groupChatRoom = new GroupChatRoom();

                            String grpId = groupChatDbRef.push().getKey();
                            String createdOn = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss").format(new Date());
                            groupChatRoom.setGroupId(grpId);
                            groupChatRoom.setGroupName(groupName);
                            groupChatRoom.setCreatedByName(user.getFirstName() + " " + user.getLastName());
                            groupChatRoom.setCreatedById(user.getId());
                            groupChatRoom.setCreatedOn(createdOn);
                            groupChatDbRef.child(grpId).setValue(groupChatRoom);
                            GroupOnlineUsers groupOnlineUsers = new GroupOnlineUsers(user.getId(),
                                    user.getFirstName()+ " "+ user.getLastName(),user.getUserProfileImageUrl(),false);
                            groupChatDbRef.child(grpId).child("membersListWithOnlineStatus").child(user.getId()).setValue(groupOnlineUsers);
                            Toast.makeText(HomeActivity.this, "Group Created", Toast.LENGTH_LONG).show();

                            Bundle bundle = new Bundle();
                            bundle.putSerializable(Parameters.USER_ID, user);
                            ChatsFragment fragment = new ChatsFragment();
                            fragment.setArguments(bundle);
                            fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.nav_host_fragment, fragment,"Chat Fragment").addToBackStack(null).commit();
                            toolbar.setTitle(R.string.menu_chats);
                            navigationView.setCheckedItem(R.id.nav_chats);
                        });

                alertDialog.setNegativeButton("Cancel",
                        (dialog, which) -> dialog.cancel());
                alertDialog.setView(v);
                alertDialog.show();
                return true;
        }
        return false;

    }

    @Override
    public void updateProfileToolbar(String title) {
        toolbar.setTitle(title);
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        if (fragment instanceof ContactsFragment) {
            ContactsFragment contactsFragment = (ContactsFragment) fragment;
            contactsFragment.setOnContactsSelectedListener(this);
        }
    }

}
