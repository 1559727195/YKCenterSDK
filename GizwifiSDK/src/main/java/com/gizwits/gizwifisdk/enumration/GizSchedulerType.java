package com.gizwits.gizwifisdk.enumration;


public enum GizSchedulerType {

   GizSchedulerDelay("GizSchedulerDelay", 0),
   GizSchedulerOneTime("GizSchedulerOneTime", 1),
   GizSchedulerWeekRepeat("GizSchedulerWeekRepeat", 2),
   GizSchedulerDayRepeat("GizSchedulerDayRepeat", 3);
   // $FF: synthetic field
   private static final GizSchedulerType[] $VALUES = new GizSchedulerType[]{GizSchedulerDelay, GizSchedulerOneTime, GizSchedulerWeekRepeat, GizSchedulerDayRepeat};


   private GizSchedulerType(String var1, int var2) {}

   public static GizSchedulerType valueOf(int value) {
      switch(value) {
      case 0:
         return GizSchedulerDelay;
      case 1:
         return GizSchedulerOneTime;
      case 2:
         return GizSchedulerWeekRepeat;
      case 3:
         return GizSchedulerDayRepeat;
      default:
         return null;
      }
   }

}
