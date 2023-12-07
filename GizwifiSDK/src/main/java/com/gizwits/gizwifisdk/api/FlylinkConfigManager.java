package com.gizwits.gizwifisdk.api;

import android.content.Context;
import com.gizwits.gizwifisdk.api.Utils;
import com.gizwits.gizwifisdk.log.SDKLog;
import com.longthink.api.LTLink;

public class FlylinkConfigManager {

   private String TAG = "FlylinkConfigManager";
   private static FlylinkConfigManager mFlylinkConfigManager = new FlylinkConfigManager() {
   };
   private boolean isRunning = false;


   protected static FlylinkConfigManager sharedInstance() {
      return mFlylinkConfigManager;
   }

   protected void start(String ssid, String key, int timeout, Context context) {
      if(this.isRunning) {
         SDKLog.d("Flylink config is running");
      } else {
         SDKLog.d("=====> Start Flylink config(" + LTLink.getVersion() + "): ssid = " + ssid + ", key = " + Utils.dataMasking(key));
         LTLink.getInstance().startLink((String)null, key, (String)null, context);
         this.isRunning = true;
      }
   }

   protected void stop() {
      if(!this.isRunning) {
         SDKLog.d("Flylink config is not running");
      } else {
         SDKLog.d("=====> Stop Flylink config");
         this.isRunning = false;
         LTLink.getInstance().stopLink();
      }
   }

   protected boolean isRunning() {
      return this.isRunning;
   }

}
