package com.gizwits.gizwifisdk.enumration;


public enum GizConditionOperator {

   GizConditionEqual("GizConditionEqual", 0),
   GizConditionLessThan("GizConditionLessThan", 1),
   GizConditionMoreThan("GizConditionMoreThan", 2),
   GizConditionLessThanOrEqual("GizConditionLessThanOrEqual", 3),
   GizConditionMoreThanOrEqual("GizConditionMoreThanOrEqual", 4),
   GizConditionNotEqual("GizConditionNotEqual", 5);
   // $FF: synthetic field
   private static final GizConditionOperator[] $VALUES = new GizConditionOperator[]{GizConditionEqual, GizConditionLessThan, GizConditionMoreThan, GizConditionLessThanOrEqual, GizConditionMoreThanOrEqual, GizConditionNotEqual};


   private GizConditionOperator(String var1, int var2) {}

   public static GizConditionOperator valueOf(int value) {
      switch(value) {
      case 0:
         return GizConditionEqual;
      case 1:
         return GizConditionLessThan;
      case 2:
         return GizConditionMoreThan;
      case 3:
         return GizConditionLessThanOrEqual;
      case 4:
         return GizConditionMoreThanOrEqual;
      case 5:
         return GizConditionNotEqual;
      default:
         return null;
      }
   }

}
