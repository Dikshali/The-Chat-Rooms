package com.app.thechatrooms.utilities;

import android.app.Activity;
import android.view.inputmethod.InputMethodManager;

import com.app.thechatrooms.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Utility {
    private User user;
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    public static void hideSoftKeyboard(Activity activity) {
        if (activity.getCurrentFocus() != null) {
            InputMethodManager inputMethodManager =
                    (InputMethodManager) activity.getSystemService(
                            Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(
                    activity.getCurrentFocus().getWindowToken(), 0);
        }
    }

    public User getUserDetails(String userId) {
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("chatRooms/userProfiles/" + userId);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                user = null;
            }
        });
        return user;
    }
}
