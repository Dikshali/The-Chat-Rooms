package com.app.thechatrooms.models;

public class PlaceLatitueLongitude {
    private Double latitude;

    public PlaceLatitueLongitude(){

    }
    public PlaceLatitueLongitude(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public boolean isEmpty(){
        if (this.longitude.isNaN() || this.latitude.isNaN())
            return true;
        return false;
    }
    private Double longitude;
}
