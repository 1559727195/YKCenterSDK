//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.gizwits.gizwifisdk.listener;

import com.gizwits.gizwifisdk.api.GizWifiCentralControlDevice;
import com.gizwits.gizwifisdk.api.GizWifiDevice;
import com.gizwits.gizwifisdk.api.GizWifiSubDevice;
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode;
import java.util.List;

public abstract class GizWifiCentralControlDeviceListener extends GizWifiDeviceListener {
    public GizWifiCentralControlDeviceListener() {
    }

    /** @deprecated */
    public void didDiscovered(GizWifiErrorCode result, GizWifiCentralControlDevice device, List<GizWifiSubDevice> subDeviceList) {
    }

    /** @deprecated */
    public void didDiscovered(int error, List<GizWifiSubDevice> subDeviceList) {
    }

    public void didUpdateSubDevices(GizWifiCentralControlDevice device, GizWifiErrorCode result, List<GizWifiDevice> subDeviceList) {
    }
}
