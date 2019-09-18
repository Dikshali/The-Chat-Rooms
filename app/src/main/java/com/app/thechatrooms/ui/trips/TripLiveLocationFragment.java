package com.app.thechatrooms.ui.trips;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

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
    private LocationCallback mLocationCallBack;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private LatLng start;
    SupportMapFragment mapFragment;
    private MarkerOptions markerRider = new MarkerOptions(), markerDriver = new MarkerOptions();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_trip_live_location);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_trip_live_location_map);

        Intent intent = getIntent();
        drivers = (Drivers) intent.getSerializableExtra(Parameters.DRIVER_ACCEPTED);
        startPoint = (PlaceLatitudeLongitude) intent.getSerializableExtra(Parameters.START_POINT);
        messageId = intent.getStringExtra(Parameters.MESSAGE_ID);
        driverId = drivers.getDriverId();
        groupId = intent.getStringExtra(Parameters.GROUP_ID);
        id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
        if(savedInstanceState == null) {
            mapFragment.getMapAsync(TripLiveLocationFragment.this::onMapReady);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng driveLoc = new LatLng(drivers.getDriverLocation().getLatitude(), drivers.getDriverLocation().getLongitude());
        mMap.setMyLocationEnabled(true);

        mMap.addMarker(markerDriver.position(driveLoc).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))).setTitle("You are here");

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5 * 1000);

        mLocationCallBack = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        start = new LatLng(location.getLatitude(), location.getLongitude());
                        if(driverId.equals(id)) {
                            Log.d("****************TAG", "update location"+ start.latitude+ " , "+start.longitude);
                            firebaseDatabase.getReference("chatRooms/trips/" + messageId + "/" + Parameters.DRIVER_ACCEPTED + "/driverLocation").child(Parameters.LATITUDE).setValue(start.latitude);
                            firebaseDatabase.getReference("chatRooms/trips/" + messageId + "/" + Parameters.DRIVER_ACCEPTED + "/driverLocation").child(Parameters.LONGITUDE).setValue(start.longitude);
                            Log.d("******" +
                                    "**********TAG1236", "update location"+ start.latitude+ " , "+start.longitude);
                            markerDriver.position(start);
                            if (getDistance(start, new LatLng(startPoint.getLatitude(), startPoint.getLongitude())) < 30.0){
                                firebaseDatabase.getReference("chatRooms/trips/" + messageId).child(Parameters.TRIP_STATUS).setValue(TripStatus.COMPLETED);
                                firebaseDatabase.getReference("chatRooms/messages/" + groupId+"/"+messageId).child(Parameters.MESSAGE_TYPE).setValue(Parameters.TRIP_STATUS_END);
                                firebaseDatabase.getReference("chatRooms/messages"+groupId+"/"+messageId).child(Parameters.MESSAGE).setValue(Parameters.MESSAGE_TYPE_RIDE_END);
                                finish();
                            }

                        }
                    }
                }
            }
        };
        mFusedLocationClient.requestLocationUpdates(locationRequest, mLocationCallBack, null);

        if(drivers!=null && startPoint!=null){
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
            mMap.addMarker(markerDriver.position(driveLoc).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))).setTitle("You are here");
            getDirectionData.execute(data);

        }
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

}
