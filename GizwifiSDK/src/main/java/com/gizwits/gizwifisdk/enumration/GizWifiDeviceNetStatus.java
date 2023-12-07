package com.gizwits.gizwifisdk.enumration;

import java.io.Serializable;

public enum GizWifiDeviceNetStatus implements Serializable {

   GizDeviceOffline("GizDeviceOffline", 0),
   GizDeviceOnline("GizDeviceOnline", 1),
   GizDeviceControlled("GizDeviceControlled", 2),
   GizDeviceUnavailable("GizDeviceUnavailable", 3);
   // $FF: synthetic field
   private static final GizWifiDeviceNetStatus[] $VALUES = new GizWifiDeviceNetStatus[]{GizDeviceOffline, GizDeviceOnline, GizDeviceControlled, GizDeviceUnavailable};


   private GizWifiDeviceNetStatus(String var1, int var2) {}

   public static GizWifiDeviceNetStatus valueOf(int value) {
      switch(value) {
      case 0:
         return GizDeviceOffline;
      case 1:
         return GizDeviceOnline;
      case 2:
         return GizDeviceControlled;
      case 3:
         return GizDeviceUnavailable;
      default:
         return GizDeviceOffline;
      }
   }

}
