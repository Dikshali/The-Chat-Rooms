package com.app.thechatrooms.ui.editProfile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import com.app.thechatrooms.R;
import com.app.thechatrooms.models.User;
import com.app.thechatrooms.ui.profile.ProfileFragment;
import com.app.thechatrooms.ui.profile.ProfileViewModel;
import com.app.thechatrooms.utilities.CircleTransform;
import com.app.thechatrooms.utilities.Parameters;
import com.app.thechatrooms.utilities.TextValidator;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;

import static android.app.Activity.RESULT_OK;

public class EditProfileFragment extends Fragment {

    private static final String TAG = "EditProfileFragment";
    private static int SELECT_PICTURE = 1;
    private ProfileViewModel mViewModel;
    private User user;
    private ImageView profileImage;
    private Uri selectedImageURI;
    private StorageReference mStorageRef;
    private DatabaseReference myRef;
    private FirebaseDatabase database;

    @Override
    public void onResume() {
        super.onResume();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setTitle(R.string.editProfile);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.edit_profile_fragment, container, false);
        mViewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);
        final EditText firstNameEditText = root.findViewById(R.id.edit_firstNameEditText);
        final EditText lastNameEditText = root.findViewById(R.id.edit_lastNameEditText);
        final EditText emailIdEditText = root.findViewById(R.id.edit_emailEditText);
        final EditText cityEditText = root.findViewById(R.id.edit_cityEditText);
        final RadioGroup gender = root.findViewById(R.id.edit_genderRadioGroup);
        profileImage = root.findViewById(R.id.edit_userProfileImageView);
        final Button updateProfileButton = root.findViewById(R.id.edit_updateButton);
        final RadioButton maleButton = root.findViewById(R.id.edit_maleRadioButton);
        final RadioButton femaleButton = root.findViewById(R.id.edit_femaleRadioButton);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("chatRooms/userProfiles");

        user = (User) getArguments().getSerializable(Parameters.USER_ID);
        Log.v(TAG, user.toString());
        mViewModel.setUserMutableLiveData(user);

        mViewModel.getUserMutableLiveData().observe(this, user -> {
            Picasso.get()
                    .load(user.getUserProfileImageUrl())
                    .transform(new CircleTransform()).centerCrop().fit()
                    .into(profileImage);
            firstNameEditText.setText(user.getFirstName());
            lastNameEditText.setText(user.getLastName());
            emailIdEditText.setText(user.getEmailId());
            cityEditText.setText(user.getCity());
            if (user.getGender().equalsIgnoreCase("MALE"))
                maleButton.setChecked(true);
            else
                femaleButton.setChecked(true);
        });

        updateProfileButton.setOnClickListener(view -> {
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
            else if (Parameters.EMPTY.equalsIgnoreCase(cityEditText.getText().toString()))
                cityEditText.setError(Parameters.EMPTY_ERROR_MESSAGE);
            else if (selectedImageURI == null) {
                Toast.makeText(getContext(), Parameters.UPLOAD_A_PROFILE_IMAGE, Toast.LENGTH_LONG).show();
            } else if (size >= 5242880) {
                Toast.makeText(getContext(), Parameters.UPLOAD_IMAGE_LESS_THAN_5MB, Toast.LENGTH_LONG).show();
            } else {
                int checkedRadioButtonId = gender.getCheckedRadioButtonId();
                RadioButton radioButton = root.findViewById(checkedRadioButtonId);
                user.setFirstName(firstNameEditText.getText().toString());
                user.setLastName(lastNameEditText.getText().toString());
                user.setCity(cityEditText.getText().toString());
                user.setGender(radioButton.getText().toString());
                uploadImage(user.getId());
            }
        });

        profileImage.setOnClickListener(view -> {
            Intent i = new Intent(
                    Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

            startActivityForResult(i, SELECT_PICTURE);
        });

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

        cityEditText.addTextChangedListener(new TextValidator(cityEditText) {
            @Override
            public void validate(TextView textView, String text) {
                if (Parameters.EMPTY.equalsIgnoreCase(text))
                    textView.setError(Parameters.EMPTY_ERROR_MESSAGE);
            }
        });

        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                selectedImageURI = data.getData();
                Picasso.get()
                        .load(selectedImageURI)
                        .transform(new CircleTransform()).centerCrop().fit()
                        .into(profileImage);
            }
        }
    }

    public void uploadImage(String id) {
        StorageReference storageReference = mStorageRef.child("chatRooms/userProfiles/" + id + ".jpg");

        storageReference.putFile(selectedImageURI)
                .addOnSuccessListener(taskSnapshot -> {
                    Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                    task.addOnSuccessListener(uri -> {
                        String photoLink = uri.toString();
                        user.setUserProfileImageUrl(photoLink);
                        saveUserData();
                    });
                })
                .addOnFailureListener(exception -> {
                    Toast.makeText(getContext(), Parameters.UNABLE_TO_UPLOAD_IMAGE, Toast.LENGTH_LONG).show();
                });
    }

    private void saveUserData() {
        myRef.child(user.getId()).setValue(user);
        Bundle bundle = new Bundle();
        bundle.putSerializable(Parameters.USER_ID, user);
        ProfileFragment fragment = new ProfileFragment();
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.nav_host_fragment, fragment, "Profile Fragment").addToBackStack(null);
        fragmentTransaction.commit();
    }

}
