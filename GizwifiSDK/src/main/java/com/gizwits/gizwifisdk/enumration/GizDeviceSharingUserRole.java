package com.gizwits.gizwifisdk.enumration;


public enum GizDeviceSharingUserRole {

   GizDeviceSharingNormal("GizDeviceSharingNormal", 0),
   GizDeviceSharingSpecial("GizDeviceSharingSpecial", 1),
   GizDeviceSharingOwner("GizDeviceSharingOwner", 2),
   GizDeviceSharingGuest("GizDeviceSharingGuest", 3);
   // $FF: synthetic field
   private static final GizDeviceSharingUserRole[] $VALUES = new GizDeviceSharingUserRole[]{GizDeviceSharingNormal, GizDeviceSharingSpecial, GizDeviceSharingOwner, GizDeviceSharingGuest};


   private GizDeviceSharingUserRole(String var1, int var2) {}

   public static GizDeviceSharingUserRole valueOf(int value) {
      switch(value) {
      case 0:
         return GizDeviceSharingNormal;
      case 1:
         return GizDeviceSharingSpecial;
      case 2:
         return GizDeviceSharingOwner;
      case 3:
         return GizDeviceSharingGuest;
      default:
         return GizDeviceSharingNormal;
      }
   }

}
