package com.gizwits.gizwifisdk.api;

import android.content.Context;
import com.gizwits.gizwifisdk.api.Utils;
import com.gizwits.gizwifisdk.log.SDKLog;
import com.winnermicro.smartconfig.ConfigType;
import com.winnermicro.smartconfig.IOneShotConfig;
import com.winnermicro.smartconfig.SmartConfigFactory;

public class WMOneShotConfigManager {

   private String TAG = "WMOneShotConfigManager";
   private static WMOneShotConfigManager mWMOneShotConfigManager = new WMOneShotConfigManager();
   private SmartConfigFactory mFactoryWM = null;
   private IOneShotConfig mSmartConfigWM = null;
   private boolean isRunning = false;


   protected static WMOneShotConfigManager sharedInstance() {
      return mWMOneShotConfigManager;
   }

   private WMOneShotConfigManager() {
      this.mFactoryWM = new SmartConfigFactory();
      this.mSmartConfigWM = this.mFactoryWM.createOneShotConfig(ConfigType.UDP);
   }

   protected void start(String ssid, String key, int timeout, Context context) {
      if(!this.isRunning) {
         SDKLog.d("=====> Start WM config: ssid = " + ssid + ", key = " + Utils.dataMasking(key));
         this.isRunning = true;
         this.mSmartConfigWM.start(ssid, key, timeout, context);
      }
   }

   protected void stop() {
      if(this.isRunning) {
         SDKLog.d("=====> Stop WM config");
         this.mSmartConfigWM.stop();
         this.isRunning = false;
      }
   }

   protected boolean isRunning() {
      return this.isRunning;
   }

}
