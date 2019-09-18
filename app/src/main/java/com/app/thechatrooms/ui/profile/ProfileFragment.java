package com.app.thechatrooms.ui.profile;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import com.app.thechatrooms.R;
import com.app.thechatrooms.models.User;
import com.app.thechatrooms.ui.editProfile.EditProfileFragment;
import com.app.thechatrooms.utilities.CircleTransform;
import com.app.thechatrooms.utilities.Parameters;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";
    private FirebaseAuth mAuth;
    private User user;
    private ProfileViewModel profileViewModel;

    @Override
    public void onResume() {
        super.onResume();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setTitle(R.string.menu_profile);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        profileViewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);
        View root = inflater.inflate(R.layout.fragment_profile, container, false);
        final TextView displayName = root.findViewById(R.id.profile_displayNameTextView);
        final TextView emailId = root.findViewById(R.id.profile_emailIdTextView);
        final TextView city = root.findViewById(R.id.profile_cityTextView);
        final TextView gender = root.findViewById(R.id.profile_genderTextView);
        final ImageView profileImage = root.findViewById(R.id.profile_imageView);
        final Button editProfileButton = root.findViewById(R.id.profile_editProfileButton);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        user = (User) getArguments().getSerializable(Parameters.USER_ID);
        Log.v(TAG, user.toString());
        profileViewModel.setUserMutableLiveData(user);

        profileViewModel.getUserMutableLiveData().observe(this, user -> {
            Picasso.get()
                    .load(user.getUserProfileImageUrl())
                    .transform(new CircleTransform()).centerCrop().fit()
                    .into(profileImage);
            displayName.setText(user.getFirstName() + " " + user.getLastName());
            emailId.setText(user.getEmailId());
            city.setText(user.getCity());
            gender.setText(user.getGender());
            if (currentUser != null && currentUser.getUid().equals(user.getId()))
                editProfileButton.setVisibility(View.VISIBLE);

            editProfileButton.setOnClickListener(view -> {
                Bundle bundle = new Bundle();
                bundle.putSerializable(Parameters.USER_ID, user);
                EditProfileFragment fragment = new EditProfileFragment();
                fragment.setArguments(bundle);
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.nav_host_fragment, fragment, "EditProfile Fragment").addToBackStack(null);
                fragmentTransaction.commit();
            });
        });

        return root;
    }
}