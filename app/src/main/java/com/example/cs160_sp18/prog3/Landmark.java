package com.example.cs160_sp18.prog3;

import android.location.Location;

public class Landmark {

    private String landmark_name;
    private Location landmark_location;
    private String filename;

    public Landmark(String landmark_name, String coordinates, String filename) {
        this.landmark_name = landmark_name;
        String[] splitCoords = coordinates.split(",");
        this.landmark_location = new Location("landmark_location");
        this.landmark_location.setLatitude(Double.parseDouble(splitCoords[0]));
        this.landmark_location.setLongitude(Double.parseDouble(splitCoords[1]));
        this.filename = filename;
    }

    public String getLandmarkName() {
        return landmark_name;
    }

    public String getFilename() {
        return filename;
    }

    public double getDistance(Location currLocation) {
        return Math.round(currLocation.distanceTo(this.landmark_location));
    }
}
