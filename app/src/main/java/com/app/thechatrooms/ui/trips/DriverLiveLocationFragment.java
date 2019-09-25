package com.app.thechatrooms.ui.trips;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.app.thechatrooms.R;
import com.app.thechatrooms.models.Drivers;
import com.app.thechatrooms.models.PlaceLatitudeLongitude;
import com.app.thechatrooms.models.TripStatus;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DriverLiveLocationFragment extends FragmentActivity implements OnMapReadyCallback{

    private GoogleMap mMap;
    private Drivers drivers;
    private String messageId, loggedInId, groupId;
    private PlaceLatitudeLongitude startPoint;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private LatLng driverLocation, pickUpLocation;
    private SupportMapFragment mapFragment;
    private Marker mCurrLocationMarker;

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
        loggedInId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Intent intent = getIntent();
        messageId = intent.getStringExtra(Parameters.MESSAGE_ID);
        groupId = intent.getStringExtra(Parameters.GROUP_ID);
        locationRequest = LocationRequest.create();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_trip_live_location_map);
        DatabaseReference tripRef = firebaseDatabase.getReference("chatRooms/trips/" + messageId );
        tripRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                drivers = dataSnapshot.child(Parameters.DRIVER_ACCEPTED).getValue(Drivers.class);
                /*if(!drivers.getDriverId().equals(loggedInId)){
                    Intent intent;
                    intent = new Intent(getApplicationContext(), RiderLiveLocationFragment.class);
                    intent.putExtra(Parameters.MESSAGE_ID, messageId);
                    //intent.putExtra(Parameters.GROUP_ID, groupId);
                    startActivity(intent);
                }*/
                startPoint = dataSnapshot.child(Parameters.START_POINT).getValue(PlaceLatitudeLongitude.class);
                driverLocation = new LatLng(drivers.getDriverLocation().getLatitude(), drivers.getDriverLocation().getLongitude());
                pickUpLocation = new LatLng(startPoint.getLatitude(), startPoint.getLongitude());
                mapFragment.getMapAsync(DriverLiveLocationFragment.this::onMapReady);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if(mMap!=null) {
            mMap.clear();
        }
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        locationRequest = LocationRequest.create();
//        locationRequest.setInterval();
        locationRequest.setFastestInterval(50);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);


        StringBuilder sb = new StringBuilder();
        sb.append("https://maps.googleapis.com/maps/api/directions/json?");
        sb.append("origin="+driverLocation.latitude+","+ driverLocation.longitude);
        sb.append("&destination="+pickUpLocation.latitude+","+ pickUpLocation.longitude);
        sb.append("&key="+getResources().getString(R.string.google_api_key));
        GetDriverDirectionData getDirectionData = new GetDriverDirectionData(getApplicationContext());
        Object[] data = new Object[4];
        data[0] = mMap;
        data[1] = sb.toString();
        data[2] = driverLocation;//start
        data[3] = pickUpLocation;//end
        //data[4] = messageId;
        mMap.addMarker(new MarkerOptions().position(pickUpLocation)).setTitle("Rider");
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(driverLocation);
        markerOptions.title("You are here");
        markerOptions.icon(bitmapDescriptorFromVector(this, R.drawable.ic_car)).rotation(0);
        mCurrLocationMarker = mMap.addMarker(markerOptions);
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

    private LocationCallback mLocationCallBack = new LocationCallback(){
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult == null) {
                return;
            }
            for (Location location : locationResult.getLocations()) {
                if (location != null) {
                    //start = new LatLng(location.getLatitude(), location.getLongitude());
                    firebaseDatabase.getReference("chatRooms/trips/" + messageId).child(Parameters.DRIVER_ACCEPTED).child("driverLocation").child(Parameters.LATITUDE).setValue(location.getLatitude());
                    firebaseDatabase.getReference("chatRooms/trips/" + messageId).child(Parameters.DRIVER_ACCEPTED).child("driverLocation").child(Parameters.LONGITUDE).setValue(location.getLongitude());
                    //mMap.clear();
                    /*if (mCurrLocationMarker != null) {
                        mCurrLocationMarker.remove();
                    }*/
                    if(startPoint!=null)
                        if(getDistance(new LatLng(startPoint.getLatitude(), startPoint.getLongitude()), new LatLng(location.getLatitude(), location.getLongitude())) < 0.05){
                            Toast.makeText(getApplicationContext(), "You have arrived", Toast.LENGTH_LONG).show();
                            firebaseDatabase.getReference("chatRooms/trips/" + messageId).child(Parameters.TRIP_STATUS).setValue(TripStatus.COMPLETED);
                            firebaseDatabase.getReference("chatRooms/messages/").child(groupId).child(messageId).child(Parameters.MESSAGE_TYPE).setValue(Parameters.TRIP_STATUS_END);
                            firebaseDatabase.getReference("chatRooms/messages").child(groupId).child(messageId).child(Parameters.MESSAGE).setValue(Parameters.TRIP_ENDED);
                            firebaseDatabase.getReference("chatRooms/messages").child(groupId).child(messageId).child("notification").setValue(true);
                            finish();

                        }


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
                                ActivityCompat.requestPermissions(DriverLiveLocationFragment.this,
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

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, @DrawableRes int vectorDrawableResourceId) {
        Drawable background = ContextCompat.getDrawable(context, R.drawable.ic_car);
        background.setBounds(0, 0, background.getIntrinsicWidth(), background.getIntrinsicHeight());
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(background.getIntrinsicWidth(), background.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        background.draw(canvas);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

}
