//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.gizwits.gizwifisdk.listener;

import com.gizwits.gizwifisdk.api.GizWifiGroup;
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public abstract class GizWifiGroupListener {
    public GizWifiGroupListener() {
    }

    public void didGetDevices(GizWifiErrorCode result, GizWifiGroup group, List<ConcurrentHashMap<String, String>> deviceList) {
    }

    /** @deprecated */
    public void didGetDevices(int error, List<ConcurrentHashMap<String, String>> devicesList) {
    }
}
