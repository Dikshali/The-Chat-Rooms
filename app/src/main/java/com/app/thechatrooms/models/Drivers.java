package com.app.thechatrooms.models;

public class Drivers {
    private String driverId, driverName;
    private PlaceLatitueLongitude driverLocation;

    public Drivers(){}
    public Drivers(String driverId, String driverName, PlaceLatitueLongitude driverLocation) {
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

    public PlaceLatitueLongitude getDriverLocation() {
        return driverLocation;
    }

    public void setDriverLocation(PlaceLatitueLongitude driverLocation) {
        this.driverLocation = driverLocation;
    }
}
