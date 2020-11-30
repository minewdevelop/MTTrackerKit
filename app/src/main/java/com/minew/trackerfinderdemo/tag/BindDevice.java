package com.minew.trackerfinderdemo.tag;

import com.google.gson.annotations.Expose;

/**
 * @author boyce
 * @date 2018/5/14 16:49
 */
public class BindDevice {
    @Expose
    private String macAddress;
    @Expose
    private int    trackerModel;

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public int getTrackerModel() {
        return trackerModel;
    }

    public void setTrackerModel(int trackerModel) {
        this.trackerModel = trackerModel;
    }
}
