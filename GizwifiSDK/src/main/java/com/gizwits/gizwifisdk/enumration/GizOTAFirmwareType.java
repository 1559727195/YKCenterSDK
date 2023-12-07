package com.gizwits.gizwifisdk.enumration;


public enum GizOTAFirmwareType {

   GizOTAFirmareModule("GizOTAFirmareModule", 0),
   GizOTAFirmareMcu("GizOTAFirmareMcu", 1);
   // $FF: synthetic field
   private static final GizOTAFirmwareType[] $VALUES = new GizOTAFirmwareType[]{GizOTAFirmareModule, GizOTAFirmareMcu};


   private GizOTAFirmwareType(String var1, int var2) {}

   public static GizOTAFirmwareType valueOf(int value) {
      switch(value) {
      case 0:
         return GizOTAFirmareModule;
      case 1:
         return GizOTAFirmareMcu;
      default:
         return null;
      }
   }

}
