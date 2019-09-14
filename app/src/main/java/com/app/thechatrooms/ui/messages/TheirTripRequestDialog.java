package com.app.thechatrooms.ui.messages;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.app.thechatrooms.R;
import com.app.thechatrooms.utilities.CircleTransform;
import com.squareup.picasso.Picasso;

public class TheirTripRequestDialog extends DialogFragment {
    static TheirTripRequestDialog newInstance(String name,String start,String end,String profileImage) {
        TheirTripRequestDialog theirTripRequestDialog = new TheirTripRequestDialog();
        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        bundle.putString("start", start);
        bundle.putString("end", end);
        bundle.putString("profileImage", profileImage);
        theirTripRequestDialog.setArguments(bundle);
        return theirTripRequestDialog;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_their_trip_info, container, false);
        TextView name = view.findViewById(R.id.dialog_their_trip_name);
        TextView startPoint = view.findViewById(R.id.dialog_their_Trip_startpoint);
        TextView endPoint = view.findViewById(R.id.dialog_their_trip_endPoint);
        ImageView profileImage = view.findViewById(R.id.dialog_their_trip_info_profileImage);
        String riderName = getArguments().getString("name");
        String riderStartPoint = getArguments().getString("start");
        String riderEndPoint = getArguments().getString("end");
        String riderProfileImage = getArguments().getString("profileImage");
        name.setText(riderName);
        startPoint.setText(riderStartPoint);
        endPoint.setText(riderEndPoint);
        Picasso.get()
                .load(riderProfileImage)
                .transform(new CircleTransform()).centerCrop().fit()
                .into(profileImage);

        return view;


    }
}
