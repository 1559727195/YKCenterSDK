package com.gizwits.gizwifisdk.enumration;


public enum GizSceneItemType {

   GizSceneItemDevice("GizSceneItemDevice", 0),
   GizSceneItemGroup("GizSceneItemGroup", 1),
   GizSceneItemDelay("GizSceneItemDelay", 2);
   // $FF: synthetic field
   private static final GizSceneItemType[] $VALUES = new GizSceneItemType[]{GizSceneItemDevice, GizSceneItemGroup, GizSceneItemDelay};


   private GizSceneItemType(String var1, int var2) {}

   public static GizSceneItemType valueOf(int value) {
      switch(value) {
      case 0:
         return GizSceneItemDevice;
      case 1:
         return GizSceneItemGroup;
      case 2:
         return GizSceneItemDelay;
      default:
         return null;
      }
   }

}
