package com.gizwits.gizwifisdk.enumration;


public enum GizScheduleRepeatRule {

   GizScheduleRepeatAll("GizScheduleRepeatAll", 0),
   GizScheduleRepeatFailed("GizScheduleRepeatFailed", 1);
   // $FF: synthetic field
   private static final GizScheduleRepeatRule[] $VALUES = new GizScheduleRepeatRule[]{GizScheduleRepeatAll, GizScheduleRepeatFailed};


   private GizScheduleRepeatRule(String var1, int var2) {}

   public static GizScheduleRepeatRule valueOf(int value) {
      switch(value) {
      case 0:
         return GizScheduleRepeatAll;
      case 1:
         return GizScheduleRepeatFailed;
      default:
         return null;
      }
   }

}
