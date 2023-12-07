//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.gizwits.gizwifisdk.listener;

import com.gizwits.gizwifisdk.api.GizDeviceScheduler;
import com.gizwits.gizwifisdk.api.GizDeviceSchedulerSuper;
import com.gizwits.gizwifisdk.api.GizWifiDevice;
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode;
import java.util.List;

public class GizDeviceSchedulerCenterListener {
    public GizDeviceSchedulerCenterListener() {
    }

    /** @deprecated */
    public void didUpdateSchedulers(GizWifiErrorCode result, GizWifiDevice schedulerOwner, List<GizDeviceScheduler> schedulerList) {
    }

    public void didUpdateSchedulers(GizWifiDevice schedulerOwner, GizWifiErrorCode result, List<GizDeviceSchedulerSuper> schedulerList) {
    }
}
