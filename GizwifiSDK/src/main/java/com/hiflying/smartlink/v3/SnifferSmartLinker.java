package com.hiflying.smartlink.v3;

import com.hiflying.commons.log.HFLog;
import com.hiflying.smartlink.AbstractSmartLinker;
import com.hiflying.smartlink.v3.SnifferSmartLinkerSendAction;
import java.util.Arrays;

public class SnifferSmartLinker extends AbstractSmartLinker {

   private SnifferSmartLinker() {}

   public static SnifferSmartLinker getInstence() {
      return getInstance();
   }

   public static SnifferSmartLinker getInstance() {
      return SnifferSmartLinker.SnifferSmartLinkerInner.SNIFFER_SMART_LINKER;
   }

   protected Runnable[] setupSendAction(String password, byte[] dataAppend, String ... ssid) throws Exception {
      HFLog.d((Object)this, String.format("setupSendAction: password-%s ssid-%s", new Object[]{password, Arrays.toString(ssid)}));
      return new Runnable[]{new SnifferSmartLinkerSendAction(this.mContext, this.mSmartConfigSocket, this, ssid.length > 0?ssid[0]:null, password, dataAppend)};
   }

   // $FF: synthetic method
   SnifferSmartLinker(Object x0) {
      this();
   }

   private static class SnifferSmartLinkerInner {

      private static final SnifferSmartLinker SNIFFER_SMART_LINKER = new SnifferSmartLinker(null);


   }
}
