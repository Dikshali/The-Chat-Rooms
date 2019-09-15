package com.app.thechatrooms.ui.trips;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.app.thechatrooms.R;
import com.app.thechatrooms.models.Drivers;
import com.app.thechatrooms.models.PlaceLatitudeLongitude;
import com.app.thechatrooms.utilities.Parameters;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

public class TripLiveLocationFragment extends FragmentActivity implements OnMapReadyCallback{

    private GoogleMap mMap;
    Drivers drivers;
    PlaceLatitudeLongitude startPoint;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_trip_live_location);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_trip_live_location_map);
        Intent intent = getIntent();
        drivers = (Drivers) intent.getSerializableExtra(Parameters.DRIVER_ACCEPTED);
        startPoint = (PlaceLatitudeLongitude) intent.getSerializableExtra(Parameters.START_POINT);

        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if(drivers!=null && startPoint!=null){
            double destinationLat = startPoint.getLatitude(), destinationLong = startPoint.getLongitude();
            double startLat=drivers.getDriverLocation().getLatitude(), startLong=drivers.getDriverLocation().getLongitude();
            StringBuilder sb = new StringBuilder();
            sb.append("https://maps.googleapis.com/maps/api/directions/json?");
            sb.append("origin="+startLat+","+ startLong);
            sb.append("&destination="+destinationLat+","+ destinationLong);
            sb.append("&key="+getResources().getString(R.string.google_api_key));

            GetDirectionData getDirectionData = new GetDirectionData(getApplicationContext());
            Object[] data = new Object[4];
            data[0] = mMap;
            data[1] = sb.toString();
            data[2] = new LatLng(startLat, startLong);//start
            data[3] = new LatLng(destinationLat, destinationLong);//end

            getDirectionData.execute(data);
        }

    }
}
