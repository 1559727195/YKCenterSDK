package com.gizwits.gizwifisdk.enumration;


public enum XPGThirdAccountType {

   XPGThirdAccountTypeBAIDU("XPGThirdAccountTypeBAIDU", 0),
   XPGThirdAccountTypeSINA("XPGThirdAccountTypeSINA", 1),
   XPGThirdAccountTypeQQ("XPGThirdAccountTypeQQ", 2);
   // $FF: synthetic field
   private static final XPGThirdAccountType[] $VALUES = new XPGThirdAccountType[]{XPGThirdAccountTypeBAIDU, XPGThirdAccountTypeSINA, XPGThirdAccountTypeQQ};


   private XPGThirdAccountType(String var1, int var2) {}

   public static XPGThirdAccountType valueOf(int value) {
      switch(value) {
      case 0:
         return XPGThirdAccountTypeBAIDU;
      case 1:
         return XPGThirdAccountTypeSINA;
      case 2:
         return XPGThirdAccountTypeQQ;
      default:
         return null;
      }
   }

}
