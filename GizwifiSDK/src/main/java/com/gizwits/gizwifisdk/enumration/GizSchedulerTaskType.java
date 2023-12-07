package com.gizwits.gizwifisdk.enumration;


public enum GizSchedulerTaskType {

   GizSchedulerTaskDevice("GizSchedulerTaskDevice", 0),
   GizSchedulerTaskGroup("GizSchedulerTaskGroup", 1),
   GizSchedulerTaskScene("GizSchedulerTaskScene", 2);
   // $FF: synthetic field
   private static final GizSchedulerTaskType[] $VALUES = new GizSchedulerTaskType[]{GizSchedulerTaskDevice, GizSchedulerTaskGroup, GizSchedulerTaskScene};


   private GizSchedulerTaskType(String var1, int var2) {}

   public static GizSchedulerTaskType valueOf(int value) {
      switch(value) {
      case 0:
         return GizSchedulerTaskDevice;
      case 1:
         return GizSchedulerTaskGroup;
      case 2:
         return GizSchedulerTaskScene;
      default:
         return null;
      }
   }

}
