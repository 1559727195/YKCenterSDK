package com.gizwits.gizwifisdk.enumration;


public enum GizThirdAccountType {

   GizThirdBAIDU("GizThirdBAIDU", 0),
   GizThirdSINA("GizThirdSINA", 1),
   GizThirdQQ("GizThirdQQ", 2),
   GizThirdWeChat("GizThirdWeChat", 3),
   GizThirdFacebook("GizThirdFacebook", 4),
   GizThirdTwitter("GizThirdTwitter", 5);
   // $FF: synthetic field
   private static final GizThirdAccountType[] $VALUES = new GizThirdAccountType[]{GizThirdBAIDU, GizThirdSINA, GizThirdQQ, GizThirdWeChat, GizThirdFacebook, GizThirdTwitter};


   private GizThirdAccountType(String var1, int var2) {}

   public static GizThirdAccountType valueOf(int value) {
      switch(value) {
      case 0:
         return GizThirdBAIDU;
      case 1:
         return GizThirdSINA;
      case 2:
         return GizThirdQQ;
      case 3:
         return GizThirdWeChat;
      case 4:
         return GizThirdFacebook;
      case 5:
         return GizThirdTwitter;
      default:
         return null;
      }
   }

}
