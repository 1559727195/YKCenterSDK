package com.gizwits.gizwifisdk.enumration;


public enum GizLogPrintLevel {

   GizLogPrintNone("GizLogPrintNone", 0),
   GizLogPrintI("GizLogPrintI", 1),
   GizLogPrintII("GizLogPrintII", 2),
   GizLogPrintAll("GizLogPrintAll", 3);
   // $FF: synthetic field
   private static final GizLogPrintLevel[] $VALUES = new GizLogPrintLevel[]{GizLogPrintNone, GizLogPrintI, GizLogPrintII, GizLogPrintAll};


   private GizLogPrintLevel(String var1, int var2) {}

   public static GizLogPrintLevel valueOf(int value) {
      switch(value) {
      case 0:
         return GizLogPrintNone;
      case 1:
         return GizLogPrintI;
      case 2:
         return GizLogPrintII;
      case 3:
         return GizLogPrintAll;
      default:
         return null;
      }
   }

}
