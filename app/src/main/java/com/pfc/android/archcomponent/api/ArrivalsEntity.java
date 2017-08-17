package com.pfc.android.archcomponent.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by ana on 16/08/17.
 */

public class ArrivalsEntity {

    private final String TAG = ArrivalsEntity.class.getName();

    @SerializedName("$type")
    @Expose
    private String $type;
    @SerializedName("vehicleId")
    @Expose
    private String vehicleId;
    @SerializedName("lineId")
    @Expose
    private String lineId;
    @SerializedName("lineName")
    @Expose
    private String stopLetter;
    @SerializedName("stationName")
    @Expose
    private String stationName;
    @SerializedName("platformName")
    @Expose
    private String platformName;
    @SerializedName("destinationName")
    @Expose
    private String destinationName;
    @SerializedName("timeToStation")
    @Expose
    private int timeToStation;
    @SerializedName("expectedArrival")
    @Expose
    private String expectedArrival;

    public String get$type() {
        return $type;
    }

    public void set$type(String $type) {
        this.$type = $type;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getLineId() {
        return lineId;
    }

    public void setLineId(String lineId) {
        this.lineId = lineId;
    }

    public String getStopLetter() {
        return stopLetter;
    }

    public void setStopLetter(String stopLetter) {
        this.stopLetter = stopLetter;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public String getPlatformName() {
        return platformName;
    }

    public void setPlatformName(String platformName) {
        this.platformName = platformName;
    }

    public String getDestinationName() {
        return destinationName;
    }

    public void setDestinationName(String destinationName) {
        this.destinationName = destinationName;
    }

    public int getTimeToStation() {
        return timeToStation;
    }

    public void setTimeToStation(int timeToStation) {
        this.timeToStation = timeToStation;
    }

    public String getExpectedArrival() {
        return expectedArrival;
    }

    public void setExpectedArrival(String expectedArrival) {
        this.expectedArrival = expectedArrival;
    }

}