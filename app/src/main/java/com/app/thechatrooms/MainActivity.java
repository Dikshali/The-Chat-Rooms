package com.app.thechatrooms;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.app.thechatrooms.utilities.Parameters;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    @BindView(R.id.main_emailEditText)
    EditText emailEditText;
    @BindView(R.id.main_passwordEditText)
    EditText passwordTextBox;
    @BindView(R.id.main_forgotPasswordTextView)
    TextView forgotPasswordTextView;
    private DatabaseReference myRef, groupChatDbRef;
    private FirebaseAuth mAuth;
    private String email;
    private String password;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();
        checkInternetPermissionGranted();
        checkReadStoragePermissionGranted();
        checkWriteStoragePermissionGranted();
        /*checkCoarseLocationPermissionGranted();
        checkFineLocationPermissionGranted();*/

        findViewById(R.id.main_signUpTextView).setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
            startActivity(intent);
            emailEditText.setText("");
            passwordTextBox.setText("");
        });

        forgotPasswordTextView.setOnClickListener(view -> {
            LayoutInflater layoutInflater = getLayoutInflater();
            final View v = layoutInflater.inflate(R.layout.alert_dialog, null);
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this, R.style.MyDialogTheme);
            alertDialog.setTitle("Enter Email Id :");
            final EditText input = v.findViewById(R.id.etComments);
            alertDialog.setPositiveButton("YES",
                    (dialog, which) -> {
                        email = input.getText().toString();
                        Log.e(TAG, "Email = " + email);
                        mAuth.sendPasswordResetEmail(email)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "Email sent.");
                                        Toast.makeText(view.getContext(), "Email Sent Successfully to " + email, Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(view.getContext(), Parameters.UNABLE_SEND, Toast.LENGTH_LONG).show();
                                    }
                                });
                    });

            alertDialog.setNegativeButton("NO",
                    (dialog, which) -> dialog.cancel());
            alertDialog.setView(v);
            alertDialog.show();

        });

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    @OnClick(R.id.main_loginButton)
    public void submit(View view) {
        email = emailEditText.getText().toString();
        password = passwordTextBox.getText().toString();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        Log.d(TAG, "Display Name = " + user.getDisplayName() + "\n " +
                                "Email Id = " + user.getEmail());
                        updateUI(user);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        updateUI(null);
                    }
                });
    }

    public void updateUI(FirebaseUser user) {
        if (user != null) {

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(Parameters.USER_ID, user.getUid());
            editor.apply();
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intent);
//            FirebaseDatabase database = FirebaseDatabase.getInstance();
//            myRef = database.getReference("chatRooms/userProfiles/");
//            myRef.child(Parameters.USER_ID).child("isOnline").setValue(false);
            finish();


        }
    }

    public void checkWriteStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted");
            } else {
                Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted");
        }
    }

    public void checkReadStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted");
            } else {
                Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted");
        }
    }

    public void checkInternetPermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted");
            } else {
                Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, 1);
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted");
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
