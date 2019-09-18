package com.app.thechatrooms.models;

import android.location.Location;

public class OfferDrivers {
    private Drivers drivers;
    private Location startPoint;

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public OfferDrivers(Drivers drivers, String profileImage, Location startPoint) {
        this.drivers = drivers;
        this.profileImage = profileImage;
        this.startPoint = startPoint;
    }

    public Drivers getDrivers() {
        return drivers;
    }

    public void setDrivers(Drivers drivers) {
        this.drivers = drivers;
    }

    private String profileImage;

    public Location getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(Location startPoint) {
        this.startPoint = startPoint;
    }
}
