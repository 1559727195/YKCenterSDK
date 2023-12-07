package com.gizwits.gizwifisdk.api;

import android.content.Context;
import com.espressif.iot.esptouch.gizwits.Esptouch;
import com.gizwits.gizwifisdk.api.Utils;
import com.gizwits.gizwifisdk.log.SDKLog;

public class ESPTouchConfigManager {

   private String TAG = "ESPTouchConfigManager";
   private static ESPTouchConfigManager mESPTouchConfigManager = new ESPTouchConfigManager() {
   };


   protected static ESPTouchConfigManager sharedInstance() {
      return mESPTouchConfigManager;
   }

   protected void start(String ssid, String key, int timeout, Context context, boolean broadcast) {
      if(!Esptouch.getInstance().isRunning()) {
         SDKLog.d("=====> Start ESP config: ssid = " + ssid + ", key = " + Utils.dataMasking(key));
         Esptouch.getInstance().start(ssid, key, context, timeout, broadcast);
      }
   }

   protected void stop() {
      if(Esptouch.getInstance().isRunning()) {
         SDKLog.d("=====> Stop ESP config");
         Esptouch.getInstance().stop();
      }
   }

   protected boolean isRunning() {
      return Esptouch.getInstance().isRunning();
   }

}
