package com.gizwits.gizwifisdk.api;

import android.content.Context;
import com.ForEE.ForEESmartconfig;
import com.gizwits.gizwifisdk.api.Utils;
import com.gizwits.gizwifisdk.log.SDKLog;

class AtmelEESmartConfigManager {

   private String TAG = "AtmelEESmartConfigManager";
   private static AtmelEESmartConfigManager mAtmelEESmartConfigManager = new AtmelEESmartConfigManager() {
   };
   private boolean isRunning = false;


   protected static AtmelEESmartConfigManager sharedInstance() {
      return mAtmelEESmartConfigManager;
   }

   protected void start(String ssid, String key, int timeout, Context context) {
      if(!this.isRunning) {
         SDKLog.d("=====> Start Atmel config: ssid = " + ssid + ", key = " + Utils.dataMasking(key));
         this.isRunning = true;
         ForEESmartconfig.getInstance().startForEEsmartconfig(context, ssid, key, timeout);
      }
   }

   protected void stop() {
      if(this.isRunning) {
         SDKLog.d("=====> Stop Atmel config");
         this.isRunning = false;
         ForEESmartconfig.getInstance().stopForEEsmartconfig();
      }
   }

   protected boolean isRunning() {
      return this.isRunning;
   }

}
