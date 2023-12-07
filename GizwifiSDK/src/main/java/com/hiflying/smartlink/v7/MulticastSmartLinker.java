package com.hiflying.smartlink.v7;

import com.gizwits.gizwifisdk.log.SDKLog;
import com.hiflying.commons.log.HFLog;
import com.hiflying.smartlink.AbstractSmartLinker;
import com.hiflying.smartlink.v3.SnifferSmartLinkerSendAction;
import com.hiflying.smartlink.v7.MulticastSmartLinkerSendAction;
import com.hiflying.smartlink.v8.AirkissSendAction;
import java.util.ArrayList;

public class MulticastSmartLinker extends AbstractSmartLinker {

   private boolean mMixSmartLink3 = true;
   private boolean mMixSmartLink8 = false;


   public static MulticastSmartLinker getInstance() {
      SDKLog.d("ready to get MulticastSmartLinker() instance.");
      return MulticastSmartLinker.MulticastSmartLinkerInner.MULTICAST_SMART_LINKER;
   }

   @Deprecated
   public void setMaxRetryTimes(int maxRetryTimes) {}

   protected Runnable[] setupSendAction(String password, byte[] dataAppend, String ... ssid) throws Exception {
      if(ssid.length < 0) {
         throw new RuntimeException("It must set a ssid in setupSendAction");
      } else {
         ArrayList runnables = new ArrayList();
         MulticastSmartLinkerSendAction multicastRunnable = new MulticastSmartLinkerSendAction(this.mContext, this, ssid[0], password, dataAppend);
         runnables.add(multicastRunnable);
         if(this.mMixSmartLink8) {
            HFLog.d((Object)this, "Mixed with smartlink8!");
            runnables.add(new AirkissSendAction(this.mContext, this, ssid[0], password, dataAppend));
         } else if(this.mMixSmartLink3) {
            HFLog.d((Object)this, "Mixed with smartlink3!");
            runnables.add(new SnifferSmartLinkerSendAction(this.mContext, this.mSmartConfigSocket, this, ssid[0], password, dataAppend));
         }

         return (Runnable[])runnables.toArray(new Runnable[runnables.size()]);
      }
   }

   public void setMixSmartLink3(boolean enabled) {
      this.mMixSmartLink3 = enabled;
   }

   public boolean isMixSmartLink3() {
      return this.mMixSmartLink3;
   }

   public boolean ismMixSmartLink8() {
      return this.mMixSmartLink8;
   }

   public void setmMixSmartLink8(boolean mMixSmartLink8) {
      this.mMixSmartLink8 = mMixSmartLink8;
   }

   private static final class MulticastSmartLinkerInner {

      private static final MulticastSmartLinker MULTICAST_SMART_LINKER = new MulticastSmartLinker();


   }
}
