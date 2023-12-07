package com.gizwits.gizwifisdk.api;

import android.content.Context;
import cn.com.broadlink.bleasyconfig.BLEasyConfig;
import com.gizwits.gizwifisdk.api.Utils;
import com.gizwits.gizwifisdk.log.SDKLog;

public class BLEasyConfigManager {

   private String TAG = "BLEasyConfigManager";
   private static BLEasyConfigManager mBLEasyConfigManager = new BLEasyConfigManager();
   private BLEasyConfig mBLEasyConfig = null;


   protected static BLEasyConfigManager sharedInstance() {
      return mBLEasyConfigManager;
   }

   private BLEasyConfigManager() {
      this.mBLEasyConfig = new BLEasyConfig();
   }

   protected void start(String ssid, String key, int timeout, Context context) {
      BLEasyConfig var10000 = this.mBLEasyConfig;
      if(!BLEasyConfig.isRunning()) {
         SDKLog.d("=====> start BL config: ssid = " + ssid + ", key = " + Utils.dataMasking(key));
         var10000 = this.mBLEasyConfig;
         BLEasyConfig.start(ssid, key, timeout, context);
      }
   }

   protected void stop() {
      BLEasyConfig var10000 = this.mBLEasyConfig;
      if(BLEasyConfig.isRunning()) {
         SDKLog.d("=====> Stop BL config");
         var10000 = this.mBLEasyConfig;
         BLEasyConfig.stop();
      }
   }

   protected boolean isRunning() {
      BLEasyConfig var10000 = this.mBLEasyConfig;
      return BLEasyConfig.isRunning();
   }

}
