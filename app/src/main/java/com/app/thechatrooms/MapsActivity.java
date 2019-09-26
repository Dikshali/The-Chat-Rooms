package com.app.thechatrooms;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.app.thechatrooms.models.PlaceLatitudeLongitude;
import com.app.thechatrooms.models.TripStatus;
import com.app.thechatrooms.ui.trips.DriverLiveLocationFragment;
import com.app.thechatrooms.ui.trips.GetDirectionData;
import com.app.thechatrooms.utilities.Parameters;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private PlaceLatitudeLongitude startPoint, endPoint;
    private LatLng driverLocation;
    private SupportMapFragment mapFragment;
    private LocationRequest locationRequest;
    private FusedLocationProviderClient mFusedLocationClient;
    private Marker mCurrLocationMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Intent intent = getIntent();
        startPoint = (PlaceLatitudeLongitude) intent.getSerializableExtra(Parameters.START_POINT);
        endPoint = (PlaceLatitudeLongitude) intent.getSerializableExtra(Parameters.END_POINT);
        locationRequest = LocationRequest.create();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
        mFusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if(location!=null){
                driverLocation = new LatLng(location.getLatitude(), location.getLongitude());
            }
        });
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_request_trip_map);
        mapFragment.getMapAsync(this);
    }

    private LocationCallback mLocationCallBack = new LocationCallback(){
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult == null) {
                return;
            }
            for (Location location : locationResult.getLocations()) {
                if (location != null) {
                 driverLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    StringBuilder sourceToDestination = new StringBuilder();
                    sourceToDestination.append("https://maps.googleapis.com/maps/api/directions/json?");
                    sourceToDestination.append("origin=" + driverLocation.latitude + "," + driverLocation.longitude);
                    sourceToDestination.append("&destination=" + endPoint.getLatitude() + "," + endPoint.getLongitude());
                    sourceToDestination.append("&waypoints=via:" + startPoint.getLatitude() + "," + startPoint.getLongitude());
                    sourceToDestination.append("&key=" + getResources().getString(R.string.google_api_key));
                    GetDirectionData getDirectionData = new GetDirectionData(getApplicationContext());
                    Object[] data = new Object[5];
                    data[0] = mMap;
                    data[1] = sourceToDestination.toString();
                    data[2] = new LatLng(startPoint.getLatitude(), startPoint.getLongitude());//start
                    data[3] = new LatLng(endPoint.getLatitude(), endPoint.getLongitude());//end
                    data[4] = new LatLng(driverLocation.latitude, driverLocation.longitude);
                    //data[5] = driverToSource.toString();
                    mMap.addMarker(new MarkerOptions().position(new LatLng(startPoint.getLatitude(), startPoint.getLongitude()))).setTitle("Source");
                    mMap.addMarker(new MarkerOptions().position(new LatLng(endPoint.getLatitude(), endPoint.getLongitude()))).setTitle("Destination");
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(driverLocation);
                    markerOptions.title("You are here");
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                    if(mCurrLocationMarker!=null){
                        mCurrLocationMarker.remove();
                    }
                    mCurrLocationMarker = mMap.addMarker(markerOptions);
                    //mMap.addMarker(new MarkerOptions().position().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))).setTitle("You are here");
                    getDirectionData.execute(data);
                }
            }
        }
    };

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
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        locationRequest = LocationRequest.create();
        locationRequest.setFastestInterval(12000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if(driverLocation!=null) {
            StringBuilder sourceToDestination = new StringBuilder();
            sourceToDestination.append("https://maps.googleapis.com/maps/api/directions/json?");
            sourceToDestination.append("origin=" + driverLocation.latitude + "," + driverLocation.longitude);
            sourceToDestination.append("&destination=" + endPoint.getLatitude() + "," + endPoint.getLongitude());
            sourceToDestination.append("&waypoints=via:" + startPoint.getLatitude() + "," + startPoint.getLongitude());
            sourceToDestination.append("&key=" + getResources().getString(R.string.google_api_key));
            GetDirectionData getDirectionData = new GetDirectionData(getApplicationContext());
            Object[] data = new Object[5];
            data[0] = mMap;
            data[1] = sourceToDestination.toString();
            data[2] = new LatLng(startPoint.getLatitude(), startPoint.getLongitude());//start
            data[3] = new LatLng(endPoint.getLatitude(), endPoint.getLongitude());//end
            data[4] = new LatLng(driverLocation.latitude, driverLocation.longitude);
            //data[5] = driverToSource.toString();
            mMap.addMarker(new MarkerOptions().position(new LatLng(startPoint.getLatitude(), startPoint.getLongitude()))).setTitle("Source");
            mMap.addMarker(new MarkerOptions().position(new LatLng(endPoint.getLatitude(), endPoint.getLongitude()))).setTitle("Destination");
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(driverLocation);
            markerOptions.title("You are here");
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            mCurrLocationMarker = mMap.addMarker(markerOptions);
            //mMap.addMarker(new MarkerOptions().position().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))).setTitle("You are here");
            getDirectionData.execute(data);
        }
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
                                ActivityCompat.requestPermissions(MapsActivity.this,
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
