package com.app.thechatrooms;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;

import com.app.thechatrooms.models.Drivers;
import com.app.thechatrooms.models.PlaceLatitueLongitude;
import com.app.thechatrooms.ui.trips.GetDirectionData;
import com.app.thechatrooms.utilities.Parameters;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private PlaceLatitueLongitude startPoint, endPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_request_trip_map);
        Intent intent = getIntent();
        //drivers = (Drivers) intent.getSerializableExtra(Parameters.DRIVER_ACCEPTED);
        //startPoint = (PlaceLatitueLongitude) intent.getSerializableExtra(Parameters.START_POINT);
        startPoint = (PlaceLatitueLongitude) intent.getSerializableExtra(Parameters.START_POINT);
        endPoint = (PlaceLatitueLongitude) intent.getSerializableExtra(Parameters.END_POINT);
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
            //double destinationLat = startPoint.getLatitude(), destinationLong = startPoint.getLongitude();
            //double startLat = endPoint.getLatitude(), startLong = endPoint.getLongitude();
            StringBuilder sb = new StringBuilder();
            sb.append("https://maps.googleapis.com/maps/api/directions/json?");
            sb.append("origin="+startPoint.getLatitude()+","+ startPoint.getLongitude());
            sb.append("&destination="+endPoint.getLatitude()+","+ endPoint.getLongitude());
            sb.append("&key="+"AIzaSyCjQlEN9SKDCtC30zy7grp-lyhPjEv792Q");

            GetDirectionData getDirectionData = new GetDirectionData(getApplicationContext());
            Object[] data = new Object[4];
            data[0] = mMap;
            data[1] = sb.toString();
            data[2] = new LatLng(startPoint.getLatitude(), startPoint.getLongitude());//start
            data[3] = new LatLng(endPoint.getLatitude(), endPoint.getLongitude());//end

            getDirectionData.execute(data);
        }
        // Add a marker in Sydney and move the camera
        /*LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/
    }
}
