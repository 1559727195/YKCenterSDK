package com.gizwits.gizwifisdk.listener;

import com.gizwits.gizwifisdk.api.GizWifiDevice;
import com.gizwits.gizwifisdk.enumration.GizOTAFirmwareType;
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode;
import java.util.concurrent.ConcurrentHashMap;

public class GizDeviceOTAListener {

   public void didCheckDeviceUpdate(GizWifiDevice device, GizWifiErrorCode result, ConcurrentHashMap wifiVersion, ConcurrentHashMap mcuVersion) {}

   public void didUpgradeDevice(GizWifiDevice device, GizWifiErrorCode result, GizOTAFirmwareType firmwareType) {}

   public void didNotifyDeviceUpdate(GizWifiDevice device, ConcurrentHashMap wifiVersion, ConcurrentHashMap mcuVersion) {}

   public void didNotifyDeviceUpgradeStatus(GizWifiDevice device, GizOTAFirmwareType firmwareType, GizWifiErrorCode upgradeStatus) {}
}
