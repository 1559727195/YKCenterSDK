//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.gizwits.gizwifisdk.listener;

import com.gizwits.gizwifisdk.api.GizDeviceSchedulerGateway;
import com.gizwits.gizwifisdk.api.GizDeviceSchedulerTask;
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode;
import java.util.List;

public class GizDeviceSchedulerGatewayListener {
    public GizDeviceSchedulerGatewayListener() {
    }

    public void didUpdateSchedulerInfo(GizDeviceSchedulerGateway scheduler, GizWifiErrorCode result) {
    }

    public void didUpdateSchedulerTasks(GizDeviceSchedulerGateway scheduler, GizWifiErrorCode result, List<GizDeviceSchedulerTask> taskList) {
    }

    public void didEnableScheduler(GizDeviceSchedulerGateway scheduler, GizWifiErrorCode result, int sn) {
    }

    public void didUpdateSceneStatus(GizDeviceSchedulerGateway scheduler, GizWifiErrorCode result, boolean enableStatus) {
    }
}
