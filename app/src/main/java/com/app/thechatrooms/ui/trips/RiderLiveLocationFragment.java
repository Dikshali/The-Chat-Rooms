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

public class RiderLiveLocationFragment extends FragmentActivity implements OnMapReadyCallback{

    private GoogleMap mMap;
    private Drivers drivers;
    private String messageId, loggedInId;
    private PlaceLatitudeLongitude startPoint;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private LatLng driverLocation, pickUpLocation;
    private SupportMapFragment mapFragment;


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }



    @Override
    protected void onResume() {
        super.onResume();
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
        setContentView(R.layout.fragment_rider_trip_live_location);

        Intent intent = getIntent();
        messageId = intent.getStringExtra(Parameters.MESSAGE_ID);
        //groupId = intent.getStringExtra(Parameters.GROUP_ID);
        locationRequest = LocationRequest.create();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_rider_live_location_map);
        DatabaseReference tripRef = firebaseDatabase.getReference("chatRooms/trips/" + messageId );
        tripRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                drivers = dataSnapshot.child(Parameters.DRIVER_ACCEPTED).getValue(Drivers.class);
                startPoint = dataSnapshot.child(Parameters.START_POINT).getValue(PlaceLatitudeLongitude.class);
                driverLocation = new LatLng(drivers.getDriverLocation().getLatitude(), drivers.getDriverLocation().getLongitude());
                pickUpLocation = new LatLng(startPoint.getLatitude(), startPoint.getLongitude());
                mapFragment.getMapAsync(RiderLiveLocationFragment.this::onMapReady);
                Log.d("tripstatus", (String) dataSnapshot.child(Parameters.TRIP_STATUS).getValue());
                if(dataSnapshot.child(Parameters.TRIP_STATUS).getValue().equals(Parameters.TRIP_STATUS_COMPLETED)){
                    Toast.makeText(getApplicationContext(), "Driver has Arrived", Toast.LENGTH_LONG).show();
                    finish();
                }
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
        markerOptions.title("Driver");
        markerOptions.icon(bitmapDescriptorFromVector(this, R.drawable.ic_car)).rotation(0);
        mMap.addMarker(markerOptions);
        getDirectionData.execute(data);

    }
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
