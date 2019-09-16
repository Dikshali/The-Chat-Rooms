package com.app.thechatrooms.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.thechatrooms.R;
import com.app.thechatrooms.models.Drivers;
import com.app.thechatrooms.models.User;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

public class DriversAdapter extends RecyclerView.Adapter<DriversAdapter.ViewHolder> {
    Context context;
    FirebaseStorage storage;
    FirebaseDatabase dbRef;
    ArrayList<Drivers> driversArrayList;

    @NonNull
    @Override
    public DriversAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_showdrivers_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        storage = FirebaseStorage.getInstance();
        dbRef = FirebaseDatabase.getInstance();
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull DriversAdapter.ViewHolder holder, int position) {

        final Drivers drivers = driversArrayList.get(position);
        holder.driverName.setText(drivers.getDriverName());

    }

    @Override
    public int getItemCount() {
        return driversArrayList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView driverName;
        ImageView driverImage;
        ViewHolder(View itemView){
            super(itemView);
            driverImage = itemView.findViewById(R.id.fragment_showdrivers_item_userImage);
            driverName = itemView.findViewById(R.id.fragment_showdrivers_item_userName);
        }
    }
}
