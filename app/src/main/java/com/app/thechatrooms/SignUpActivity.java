package com.app.thechatrooms;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.app.thechatrooms.models.User;
import com.app.thechatrooms.utilities.CircleTransform;
import com.app.thechatrooms.utilities.Parameters;
import com.app.thechatrooms.utilities.TextValidator;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = "SignUpActivity";
    private static int SELECT_PICTURE = 1;

    @BindView(R.id.signUp_userProfileImageView)
    ImageView userProfileImageView;
    @BindView(R.id.signUp_firstNameEditText)
    EditText firstNameEditText;
    @BindView(R.id.signUp_lastNameEditText)
    EditText lastNameEditText;
    @BindView(R.id.signUp_emailEditText)
    EditText emailIdEditText;
    @BindView(R.id.signUp_passwordEditText)
    EditText passwordEditText;
    @BindView(R.id.signUp_confirmPasswordEditText)
    EditText confirmPasswordEditText;
    @BindView(R.id.signUp_cityEditText)
    EditText cityEditText;
    @BindView(R.id.signUp_genderRadioGroup)
    RadioGroup genderRadioGroup;

    private StorageReference mStorageRef;
    private FirebaseAuth mAuth;
    private Uri selectedImageURI;
    private User user;
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("chatRooms/userProfiles");

        firstNameEditText.setText("");
        lastNameEditText.setText("");
        emailIdEditText.setText("");
        passwordEditText.setText("");
        confirmPasswordEditText.setText("");
        cityEditText.setText("");

        firstNameEditText.addTextChangedListener(new TextValidator(firstNameEditText) {
            @Override
            public void validate(TextView textView, String text) {
                if (Parameters.EMPTY.equalsIgnoreCase(text))
                    textView.setError(Parameters.EMPTY_ERROR_MESSAGE);
            }
        });

        lastNameEditText.addTextChangedListener(new TextValidator(lastNameEditText) {
            @Override
            public void validate(TextView textView, String text) {
                if (Parameters.EMPTY.equalsIgnoreCase(text))
                    textView.setError(Parameters.EMPTY_ERROR_MESSAGE);
            }
        });

        emailIdEditText.addTextChangedListener(new TextValidator(emailIdEditText) {
            @Override
            public void validate(TextView textView, String text) {
                if (Parameters.EMPTY.equalsIgnoreCase(text))
                    textView.setError(Parameters.EMPTY_ERROR_MESSAGE);
            }
        });

        passwordEditText.addTextChangedListener(new TextValidator(passwordEditText) {
            @Override
            public void validate(TextView textView, String text) {
                if (Parameters.EMPTY.equalsIgnoreCase(text))
                    textView.setError(Parameters.EMPTY_ERROR_MESSAGE);
            }
        });

        confirmPasswordEditText.addTextChangedListener(new TextValidator(confirmPasswordEditText) {
            @Override
            public void validate(TextView textView, String text) {
                if (Parameters.EMPTY.equalsIgnoreCase(text))
                    textView.setError(Parameters.EMPTY_ERROR_MESSAGE);
                else if (!passwordEditText.getText().toString().equals(text))
                    textView.setError(Parameters.INCORRECT_CONFIRM_PASSWORD);
            }
        });

        cityEditText.addTextChangedListener(new TextValidator(cityEditText) {
            @Override
            public void validate(TextView textView, String text) {
                if (Parameters.EMPTY.equalsIgnoreCase(text))
                    textView.setError(Parameters.EMPTY_ERROR_MESSAGE);
            }
        });

        userProfileImageView.setOnClickListener(view -> {
            Intent i = new Intent(
                    Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

            startActivityForResult(i, SELECT_PICTURE);
        });

    }

    @OnClick(R.id.signUp_signUpButton)
    public void submit(View view) {
        long size = 0;
        if (selectedImageURI != null) {
            File f = new File(selectedImageURI.getPath());
            size = f.length();
        }


        if (Parameters.EMPTY.equalsIgnoreCase(firstNameEditText.getText().toString()))
            firstNameEditText.setError(Parameters.EMPTY_ERROR_MESSAGE);
        else if (Parameters.EMPTY.equalsIgnoreCase(lastNameEditText.getText().toString()))
            lastNameEditText.setError(Parameters.EMPTY_ERROR_MESSAGE);
        else if (Parameters.EMPTY.equalsIgnoreCase(emailIdEditText.getText().toString()))
            emailIdEditText.setError(Parameters.EMPTY_ERROR_MESSAGE);
        else if (Parameters.EMPTY.equalsIgnoreCase(passwordEditText.getText().toString()))
            passwordEditText.setError(Parameters.EMPTY_ERROR_MESSAGE);
        else if (Parameters.EMPTY.equalsIgnoreCase(confirmPasswordEditText.getText().toString()))
            confirmPasswordEditText.setError(Parameters.EMPTY_ERROR_MESSAGE);
        else if (!passwordEditText.getText().toString().equals(confirmPasswordEditText.getText().toString()))
            confirmPasswordEditText.setError(Parameters.INCORRECT_CONFIRM_PASSWORD);
        else if (Parameters.EMPTY.equalsIgnoreCase(cityEditText.getText().toString()))
            cityEditText.setError(Parameters.EMPTY_ERROR_MESSAGE);
        else if (selectedImageURI == null) {
            Toast.makeText(this, Parameters.UPLOAD_A_PROFILE_IMAGE, Toast.LENGTH_LONG).show();
        } else if (size >= 5242880) {
            Toast.makeText(this, Parameters.UPLOAD_IMAGE_LESS_THAN_5MB, Toast.LENGTH_LONG).show();
        } else {
            int checkedRadioButtonId = genderRadioGroup.getCheckedRadioButtonId();
            RadioButton radioButton = findViewById(checkedRadioButtonId);

            user = new User(firstNameEditText.getText().toString(), lastNameEditText.getText().toString(),
                    emailIdEditText.getText().toString(),
                    cityEditText.getText().toString(), radioButton.getText().toString(), true);
            mAuth.createUserWithEmailAndPassword(user.getEmailId(), passwordEditText.getText().toString())
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user1 = mAuth.getCurrentUser();
                            user.setId(user1.getUid());
                            uploadImage(user1.getUid());
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignUpActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                    });
        }
    }

    private void saveUserData() {
        myRef.child(user.getId()).setValue(user);
        updateUI(user.getId());
    }

    public void uploadImage(String id) {
        StorageReference storageReference = mStorageRef.child("chatRooms/userProfiles/" + id + ".jpg");

        storageReference.putFile(selectedImageURI)
                .addOnSuccessListener(taskSnapshot -> {
                    // Get a URL to the uploaded content
                    Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                    task.addOnSuccessListener(uri -> {
                        String photoLink = uri.toString();
                        //Toast.makeText(getApplicationContext(),photoLink, Toast.LENGTH_LONG).show();
                        user.setUserProfileImageUrl(photoLink);
                        saveUserData();
                    });
                })
                .addOnFailureListener(exception -> {
                    Toast.makeText(getApplicationContext(), Parameters.UNABLE_TO_UPLOAD_IMAGE, Toast.LENGTH_LONG).show();
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                selectedImageURI = data.getData();
                Picasso.get()
                        .load(selectedImageURI)
                        .transform(new CircleTransform()).centerCrop().fit()
                        .into(userProfileImageView);
            }
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null)
            updateUI(currentUser.getUid());
    }

    private void updateUI(String userId) {
        if (userId != null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(Parameters.USER_ID, userId);
            editor.apply();
            Intent intent = new Intent(SignUpActivity.this, HomeActivity.class);
            startActivity(intent);
        }
    }
}
