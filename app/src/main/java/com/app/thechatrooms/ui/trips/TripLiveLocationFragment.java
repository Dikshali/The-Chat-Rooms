package com.app.thechatrooms.ui.trips;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.app.thechatrooms.MapsActivity;
import com.app.thechatrooms.R;
import com.app.thechatrooms.models.Drivers;
import com.app.thechatrooms.models.PlaceLatitudeLongitude;
import com.app.thechatrooms.models.TripStatus;
import com.app.thechatrooms.utilities.Parameters;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TripLiveLocationFragment extends FragmentActivity implements OnMapReadyCallback{

    private GoogleMap mMap;
    Drivers drivers;
    private String messageId, driverId, id, groupId;
    PlaceLatitudeLongitude startPoint;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private LatLng start;
    SupportMapFragment mapFragment;
    private MarkerOptions markerRider = new MarkerOptions(), markerDriver = new MarkerOptions();


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    private void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallBack);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();
    }

    private void startLocationUpdates() {
        mFusedLocationClient.requestLocationUpdates(locationRequest,
                mLocationCallBack,
                Looper.getMainLooper());
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_trip_live_location);
        Intent intent = getIntent();
        drivers = (Drivers) intent.getSerializableExtra(Parameters.DRIVER_ACCEPTED);
        startPoint = (PlaceLatitudeLongitude) intent.getSerializableExtra(Parameters.START_POINT);
        messageId = intent.getStringExtra(Parameters.MESSAGE_ID);
        driverId = drivers.getDriverId();
        groupId = intent.getStringExtra(Parameters.GROUP_ID);
        id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        locationRequest = LocationRequest.create();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_trip_live_location_map);
        mapFragment.getMapAsync(TripLiveLocationFragment.this::onMapReady);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(5 * 1000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        double destinationLat = startPoint.getLatitude(), destinationLong = startPoint.getLongitude();
        double startLat=drivers.getDriverLocation().getLatitude(), startLong=drivers.getDriverLocation().getLongitude();
        StringBuilder sb = new StringBuilder();
        sb.append("https://maps.googleapis.com/maps/api/directions/json?");
        sb.append("origin="+startLat+","+ startLong);
        sb.append("&destination="+destinationLat+","+ destinationLong);
        sb.append("&key="+getResources().getString(R.string.google_api_key));
        GetDriverDirectionData getDirectionData = new GetDriverDirectionData(getApplicationContext());
        Object[] data = new Object[4];
        data[0] = mMap;
        data[1] = sb.toString();
        data[2] = new LatLng(startLat, startLong);//start
        data[3] = new LatLng(destinationLat, destinationLong);//end
        //data[4] = messageId;
        mMap.addMarker(new MarkerOptions().position((LatLng) data[3])).setTitle("Rider");
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position((LatLng) data[2]);
        markerOptions.title("You are here");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        mCurrLocationMarker = mMap.addMarker(markerOptions);
        //mMap.addMarker(markerDriver.position((LatLng) data[2]).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))).setTitle("You are here");
        getDirectionData.execute(data);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                mFusedLocationClient.requestLocationUpdates(locationRequest, mLocationCallBack, Looper.myLooper());
                mMap.setMyLocationEnabled(true);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        }
        else {
            mFusedLocationClient.requestLocationUpdates(locationRequest, mLocationCallBack, Looper.myLooper());
            mMap.setMyLocationEnabled(true);
        }

    }
    Marker mCurrLocationMarker;
    private LocationCallback mLocationCallBack = new LocationCallback(){
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult == null) {
                return;
            }
            for (Location location : locationResult.getLocations()) {
                if (location != null) {
                    start = new LatLng(location.getLatitude(), location.getLongitude());
                    if(driverId.equals(id)) {
                        mMap.clear();
                        if (mCurrLocationMarker != null) {
                            mCurrLocationMarker.remove();
                        }

                        if (getDistance(start, new LatLng(startPoint.getLatitude(), startPoint.getLongitude())) < 10){
                            firebaseDatabase.getReference("chatRooms/trips/" + messageId).child(Parameters.TRIP_STATUS).setValue(TripStatus.COMPLETED);
                            firebaseDatabase.getReference("chatRooms/messages/").child(groupId).child(messageId).child(Parameters.MESSAGE_TYPE).setValue(Parameters.TRIP_STATUS_END);
                            firebaseDatabase.getReference("chatRooms/messages").child(groupId).child(messageId).child(Parameters.MESSAGE).setValue(Parameters.MESSAGE_TYPE_RIDE_END);
                            firebaseDatabase.getReference("chatRooms/messages").child(groupId).child(messageId).child("notification").setValue(true);
                            finish();
                        }

                    }
                    double destinationLat = startPoint.getLatitude(), destinationLong = startPoint.getLongitude();
                    double startLat = start.latitude, startLong = start.longitude;
                    StringBuilder sb = new StringBuilder();
                    sb.append("https://maps.googleapis.com/maps/api/directions/json?");
                    sb.append("origin="+startLat+","+ startLong);
                    sb.append("&destination="+destinationLat+","+ destinationLong);
                    sb.append("&key="+getResources().getString(R.string.google_api_key));
                    GetDriverDirectionData getDirectionData = new GetDriverDirectionData(getApplicationContext());
                    Object[] data = new Object[4];
                    data[0] = mMap;
                    data[1] = sb.toString();
                    data[2] = new LatLng(startLat, startLong);//start
                    data[3] = new LatLng(destinationLat, destinationLong);//end
                    //data[4] = messageId;
                    if (mCurrLocationMarker != null) {
                        mCurrLocationMarker.remove();
                    }
                    mMap.addMarker(new MarkerOptions().position((LatLng) data[3])).setTitle("Rider");
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position((LatLng) data[2]);
                    markerOptions.title("You are here");
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                    mCurrLocationMarker = mMap.addMarker(markerOptions);
                    //mMap.addMarker(markerDriver.position((LatLng) data[2]).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))).setTitle("You are here");
                    getDirectionData.execute(data);
                }
            }
        }
    };

    public static float getDistance(LatLng latlngA, LatLng latlngB) {
        Location locationA = new Location("point A");

        locationA.setLatitude(latlngA.latitude);
        locationA.setLongitude(latlngA.longitude);

        Location locationB = new Location("point B");

        locationB.setLatitude(latlngB.latitude);
        locationB.setLongitude(latlngB.longitude);

        float distance = locationA.distanceTo(locationB)/1000;//To convert Meter in Kilometer
        return distance;
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(TripLiveLocationFragment.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION );
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION );
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        mFusedLocationClient.requestLocationUpdates(locationRequest, mLocationCallBack, Looper.myLooper());
                        mMap.setMyLocationEnabled(true);
                    }

                } else {
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

}
