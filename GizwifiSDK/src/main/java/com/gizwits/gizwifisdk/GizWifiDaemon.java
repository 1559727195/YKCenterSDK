package com.gizwits.gizwifisdk;


public class GizWifiDaemon {

   private static GizWifiDaemon mInstance = new GizWifiDaemon();


   public static void initSDK(String phoneID, String phoneModel, String phoneOS, String phoneOSVer, String logDir) {
      cInitDaemon(phoneID, phoneModel, phoneOS, phoneOSVer, logDir);
   }

   public static void updateBackgroundToDaemon(boolean inBackground) {
      cUpdateBackgroundToDaemon(inBackground);
   }

   private static native void cInitDaemon(String var0, String var1, String var2, String var3, String var4);

   private static native void cUpdateBackgroundToDaemon(boolean var0);

   static {
      System.loadLibrary("GizWifiDaemon");
   }
}
