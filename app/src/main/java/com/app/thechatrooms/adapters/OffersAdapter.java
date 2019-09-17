package com.app.thechatrooms.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.thechatrooms.R;
import com.app.thechatrooms.models.Drivers;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;


import java.util.ArrayList;

public class OffersAdapter extends RecyclerView.Adapter<OffersAdapter.ViewHolder> {
    Context context;
    FirebaseStorage storage;
    FirebaseDatabase dbRef;
    ArrayList<Drivers> driversHashMap;
    OffersInterface offersInterface;

    @NonNull
    @Override
    public OffersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_view_ride_offers_items, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        storage = FirebaseStorage.getInstance();
        dbRef = FirebaseDatabase.getInstance().getInstance();
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull OffersAdapter.ViewHolder holder, int position) {
        final Drivers drivers = driversHashMap.get(position);
        holder.driverName.setText(drivers.getDriverName());
        holder.accept.setOnClickListener(view -> {
            offersInterface.driverSelected(drivers);
        });
    }

    public OffersAdapter(Context context, ArrayList driversHashMap, OffersInterface offersInterface) {
        this.context = context;
        this.driversHashMap = driversHashMap;
        this.offersInterface = offersInterface;
    }

    @Override
    public int getItemCount() {
        return driversHashMap.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView driverName;
        ImageButton accept;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            driverName = itemView.findViewById(R.id.fragment_view_offers_item_userName);
            accept = itemView.findViewById(R.id.fragment_view_offers_accept);
        }
    }
    public interface OffersInterface{
        void driverSelected(Drivers drivers);
    }
}
