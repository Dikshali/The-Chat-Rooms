package com.app.thechatrooms.models;

import org.xmlpull.v1.sax2.Driver;

import java.util.HashMap;

public class Trips {
    private TripStatus tripStatus;
    private String riderId, driverId;
    private PlaceLatitudeLongitude startPoint, endPoint;
    private HashMap<String, Drivers> drivers = new HashMap<>();
    private Drivers driverAccepted;

    public Trips(){

    }

    public Trips(TripStatus tripStatus, String riderId, PlaceLatitudeLongitude startPoint, PlaceLatitudeLongitude endPoint) {
        this.tripStatus = tripStatus;
        this.riderId = riderId;
        this.startPoint = startPoint;
        this.endPoint = endPoint;
    }

    public TripStatus getTripStatus() {
        return tripStatus;
    }

    public void setTripStatus(TripStatus tripStatus) {
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

    public PlaceLatitudeLongitude getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(PlaceLatitudeLongitude startPoint) {
        this.startPoint = startPoint;
    }

    public PlaceLatitudeLongitude getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(PlaceLatitudeLongitude endPoint) {
        this.endPoint = endPoint;
    }

    public HashMap<String, Drivers> getDrivers() {
        return drivers;
    }

    public void setDrivers(HashMap<String, Drivers> drivers) {
        this.drivers = drivers;
    }

    public Drivers getDriverAccepted() {
        return driverAccepted;
    }

    public void setDriverAccepted(Drivers driverAccepted) {
        this.driverAccepted = driverAccepted;
    }
}
