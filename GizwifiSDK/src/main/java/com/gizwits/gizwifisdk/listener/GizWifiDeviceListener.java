//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.gizwits.gizwifisdk.listener;

import com.gizwits.gizwifisdk.api.GizWifiDevice;
import com.gizwits.gizwifisdk.enumration.GizWifiDeviceNetStatus;
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode;
import java.util.concurrent.ConcurrentHashMap;

public class GizWifiDeviceListener {
    public GizWifiDeviceListener() {
    }

    public void didSetSubscribe(GizWifiErrorCode result, GizWifiDevice device, boolean isSubscribed) {
    }

    public void didGetHardwareInfo(GizWifiErrorCode result, GizWifiDevice device, ConcurrentHashMap<String, String> hardwareInfo) {
    }

    /** @deprecated */
    public void didQueryHardwareInfo(GizWifiDevice device, int result, ConcurrentHashMap<String, String> hardwareInfo) {
    }

    /** @deprecated */
    public void didDisconnected(GizWifiDevice device, int result) {
    }

    /** @deprecated */
    public void didLogin(GizWifiDevice device, int result) {
    }

    /** @deprecated */
    public void didDeviceOnline(GizWifiDevice device, boolean isOnline) {
    }

    public void didExitProductionTesting(GizWifiErrorCode result, GizWifiDevice device) {
    }

    public void didReceiveAttrStatus(GizWifiErrorCode result, GizWifiDevice device, ConcurrentHashMap<String, Object> attrStatus, ConcurrentHashMap<String, Object> adapterAttrStatus, int sn) {
    }

    public void didReceiveAppToDevAttrStatus(GizWifiErrorCode result, GizWifiDevice device, ConcurrentHashMap<String, Object> attrStatus, ConcurrentHashMap<String, Object> adapterAttrStatus, int sn) {
    }

    /** @deprecated */
    public void didReceiveData(GizWifiErrorCode result, GizWifiDevice device, ConcurrentHashMap<String, Object> dataMap, int sn) {
    }

    /** @deprecated */
    public void didReceiveData(GizWifiDevice device, ConcurrentHashMap<String, Object> dataMap, int result) {
    }

    public void didUpdateNetStatus(GizWifiDevice device, GizWifiDeviceNetStatus netStatus) {
    }

    public void didSetCustomInfo(GizWifiErrorCode result, GizWifiDevice device) {
    }

    public void didUpdateProduct(GizWifiErrorCode result, GizWifiDevice device, String productUI) {
    }
}
