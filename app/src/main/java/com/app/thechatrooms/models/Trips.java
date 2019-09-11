package com.app.thechatrooms.models;

import java.util.ArrayList;
import java.util.HashMap;

public class Trips {
    private String  tripStatus, riderId, driverId;
    private PlaceLatitueLongitude startPoint, endPoint;
    HashMap<String, Drivers> drivers = new HashMap<>();

    public Trips(){

    }
    public Trips( String tripStatus, String riderId, String driverId, PlaceLatitueLongitude startPoint, PlaceLatitueLongitude endPoint, HashMap<String, Drivers> drivers) {

        this.tripStatus = tripStatus;
        this.riderId = riderId;
        this.driverId = driverId;
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.drivers = drivers;
    }

//    public String getTripId() {
//        return tripId;
//    }

//    public void setTripId(String tripId) {
//        this.tripId = tripId;
//    }

    public String getTripStatus() {
        return tripStatus;
    }

    public void setTripStatus(String tripStatus) {
        this.tripStatus = tripStatus;
    }

    public String getRiderId() {
        return riderId;
    }

    public void setRiderId(String riderId) {
        this.riderId = riderId;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public PlaceLatitueLongitude getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(PlaceLatitueLongitude startPoint) {
        this.startPoint = startPoint;
    }

    public PlaceLatitueLongitude getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(PlaceLatitueLongitude endPoint) {
        this.endPoint = endPoint;
    }

    public HashMap<String, Drivers> getDrivers() {
        return drivers;
    }

    public void setDrivers(HashMap<String, Drivers> drivers) {
        this.drivers = drivers;
    }
}
