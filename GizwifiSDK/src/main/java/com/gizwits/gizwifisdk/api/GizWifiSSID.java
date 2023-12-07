//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.gizwits.gizwifisdk.api;

public class GizWifiSSID {
    private String ssid;
    private int rssi;

    protected GizWifiSSID(String ssid, int rssi) {
        this.ssid = ssid;
        this.rssi = rssi;
    }

    public String getSsid() {
        return this.ssid;
    }

    public int getRssi() {
        return this.rssi;
    }
}
