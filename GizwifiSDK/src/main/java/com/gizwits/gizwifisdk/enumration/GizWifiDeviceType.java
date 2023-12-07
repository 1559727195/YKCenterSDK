package com.gizwits.gizwifisdk.enumration;

import java.io.Serializable;

public enum GizWifiDeviceType implements Serializable {

   GizDeviceNormal("GizDeviceNormal", 0),
   GizDeviceCenterControl("GizDeviceCenterControl", 1),
   GizDeviceSub("GizDeviceSub", 2);
   // $FF: synthetic field
   private static final GizWifiDeviceType[] $VALUES = new GizWifiDeviceType[]{GizDeviceNormal, GizDeviceCenterControl, GizDeviceSub};


   private GizWifiDeviceType(String var1, int var2) {}

   public static GizWifiDeviceType valueOf(int value) {
      switch(value) {
      case 0:
         return GizDeviceNormal;
      case 1:
         return GizDeviceCenterControl;
      case 2:
         return GizDeviceSub;
      default:
         return GizDeviceNormal;
      }
   }

}
