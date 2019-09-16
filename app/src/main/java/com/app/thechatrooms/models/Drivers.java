package com.app.thechatrooms.models;

import java.io.Serializable;

public class Drivers implements Serializable {
    private String driverId, driverName;
    private PlaceLatitudeLongitude driverLocation;

    public Drivers(){}
    public Drivers(String driverId, String driverName, PlaceLatitudeLongitude driverLocation) {
        this.driverId = driverId;
        this.driverName = driverName;
        this.driverLocation = driverLocation;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public PlaceLatitudeLongitude getDriverLocation() {
        return driverLocation;
    }

    public void setDriverLocation(PlaceLatitudeLongitude driverLocation) {
        this.driverLocation = driverLocation;
    }
}
