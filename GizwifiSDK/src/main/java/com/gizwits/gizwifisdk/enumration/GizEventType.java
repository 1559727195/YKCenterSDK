package com.gizwits.gizwifisdk.enumration;


public enum GizEventType {

   GizEventSDK("GizEventSDK", 0),
   GizEventDevice("GizEventDevice", 1),
   GizEventM2MService("GizEventM2MService", 2),
   GizEventOpenAPIService("GizEventOpenAPIService", 3),
   GizEventProductService("GizEventProductService", 4),
   GizEventToken("GizEventToken", 5);
   // $FF: synthetic field
   private static final GizEventType[] $VALUES = new GizEventType[]{GizEventSDK, GizEventDevice, GizEventM2MService, GizEventOpenAPIService, GizEventProductService, GizEventToken};


   private GizEventType(String var1, int var2) {}

   public static GizEventType valueOf(int value) {
      switch(value) {
      case 0:
         return GizEventSDK;
      case 1:
         return GizEventDevice;
      case 2:
         return GizEventM2MService;
      case 3:
         return GizEventOpenAPIService;
      case 4:
         return GizEventProductService;
      case 5:
         return GizEventToken;
      default:
         return null;
      }
   }

}
