package com.gizwits.gizwifisdk.enumration;


public enum GizUserAccountType {

   GizUserNormal("GizUserNormal", 0),
   GizUserPhone("GizUserPhone", 1),
   GizUserEmail("GizUserEmail", 2),
   GizUserOther("GizUserOther", 3);
   // $FF: synthetic field
   private static final GizUserAccountType[] $VALUES = new GizUserAccountType[]{GizUserNormal, GizUserPhone, GizUserEmail, GizUserOther};


   private GizUserAccountType(String var1, int var2) {}

   public static GizUserAccountType valueOf(int value) {
      switch(value) {
      case 0:
         return GizUserNormal;
      case 1:
         return GizUserPhone;
      case 2:
         return GizUserEmail;
      case 3:
         return GizUserOther;
      default:
         return null;
      }
   }

}
