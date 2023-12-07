package com.hiflying.smartlink;

import android.content.Context;
import com.hiflying.smartlink.OnSmartLinkListener;

public interface ISmartLinker {

   int V3 = 3;
   int V7 = 7;
   int DEFAULT_TIMEOUT_PERIOD = 60000;


   void setOnSmartLinkListener(OnSmartLinkListener var1);

   void start(Context var1, String var2, byte[] var3, String ... var4) throws Exception;

   void stop();

   boolean isSmartLinking();

   void setTimeoutPeriod(int var1);
}
