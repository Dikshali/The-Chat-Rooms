package com.app.thechatrooms;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;

import com.app.thechatrooms.models.PlaceLatitudeLongitude;
import com.app.thechatrooms.ui.trips.GetDirectionData;
import com.app.thechatrooms.utilities.Parameters;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private PlaceLatitudeLongitude startPoint, endPoint, driversCurrentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_request_trip_map);
        Intent intent = getIntent();
        startPoint = (PlaceLatitudeLongitude) intent.getSerializableExtra(Parameters.START_POINT);
        endPoint = (PlaceLatitudeLongitude) intent.getSerializableExtra(Parameters.END_POINT);
        driversCurrentLocation = (PlaceLatitudeLongitude) intent.getSerializableExtra(Parameters.DRIVERS);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if(endPoint!=null && startPoint!=null){
            StringBuilder sourceToDestination = new StringBuilder();
            sourceToDestination.append("https://maps.googleapis.com/maps/api/directions/json?");
            sourceToDestination.append("origin="+driversCurrentLocation.getLatitude()+","+ driversCurrentLocation.getLongitude());
            sourceToDestination.append("&destination="+endPoint.getLatitude()+","+ endPoint.getLongitude());
            sourceToDestination.append("&waypoints=via:"+startPoint.getLatitude()+","+startPoint.getLongitude());
            /*sourceToDestination.append("origin="+startPoint.getLatitude()+","+ startPoint.getLongitude());
            sourceToDestination.append("&destination="+endPoint.getLatitude()+","+ endPoint.getLongitude());
            sourceToDestination.append("&waypoints=via:"+driversCurrentLocation.getLatitude()+","+driversCurrentLocation.getLongitude());*/
            sourceToDestination.append("&key="+getResources().getString(R.string.google_api_key));
            GetDirectionData getDirectionData = new GetDirectionData(getApplicationContext());
            Object[] data = new Object[5];
            data[0] = mMap;
            data[1] = sourceToDestination.toString();
            data[2] = new LatLng(startPoint.getLatitude(), startPoint.getLongitude());//start
            data[3] = new LatLng(endPoint.getLatitude(), endPoint.getLongitude());//end
            data[4] = new LatLng(driversCurrentLocation.getLatitude(), driversCurrentLocation.getLongitude());
            //data[5] = driverToSource.toString();

            getDirectionData.execute(data);
        }
    }
}
