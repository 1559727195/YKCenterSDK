package com.gizwits.gizwifisdk.enumration;


public enum GizDeviceSceneStatus {

   GizDeviceSceneCancel("GizDeviceSceneCancel", 0),
   GizDeviceSceneStart("GizDeviceSceneStart", 1),
   GizDeviceSceneDone("GizDeviceSceneDone", 2);
   // $FF: synthetic field
   private static final GizDeviceSceneStatus[] $VALUES = new GizDeviceSceneStatus[]{GizDeviceSceneCancel, GizDeviceSceneStart, GizDeviceSceneDone};


   private GizDeviceSceneStatus(String var1, int var2) {}

   public static GizDeviceSceneStatus valueOf(int value) {
      switch(value) {
      case 0:
         return GizDeviceSceneCancel;
      case 1:
         return GizDeviceSceneStart;
      case 2:
         return GizDeviceSceneDone;
      default:
         return null;
      }
   }

}
