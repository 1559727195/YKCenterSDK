package com.gizwits.gizwifisdk.enumration;

import java.io.Serializable;

public enum GizDeviceNetType implements Serializable {

   GizDeviceNetWifi("GizDeviceNetWifi", 0),
   GizDeviceNetNB("GizDeviceNetNB", 1),
   GizDeviceNetBT("GizDeviceNetBT", 2),
   GizDeviceNetBLE("GizDeviceNetBLE", 3);
   // $FF: synthetic field
   private static final GizDeviceNetType[] $VALUES = new GizDeviceNetType[]{GizDeviceNetWifi, GizDeviceNetNB, GizDeviceNetBT, GizDeviceNetBLE};


   private GizDeviceNetType(String var1, int var2) {}

   public static GizDeviceNetType valueOf(int value) {
      switch(value) {
      case 0:
         return GizDeviceNetWifi;
      case 1:
         return GizDeviceNetNB;
      case 2:
         return GizDeviceNetBT;
      case 3:
         return GizDeviceNetBLE;
      default:
         return GizDeviceNetWifi;
      }
   }

}
