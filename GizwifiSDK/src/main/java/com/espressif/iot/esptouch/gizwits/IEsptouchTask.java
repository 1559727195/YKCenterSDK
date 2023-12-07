package com.espressif.iot.esptouch.gizwits;

import com.espressif.iot.esptouch.gizwits.IEsptouchListener;
import com.espressif.iot.esptouch.gizwits.IEsptouchResult;
import java.util.List;

public interface IEsptouchTask {

   String ESPTOUCH_VERSION = "1.0.0";


   void setEsptouchListener(IEsptouchListener var1);

   void interrupt();

   IEsptouchResult executeForResult() throws RuntimeException;

   List executeForResults(int var1) throws RuntimeException;

   boolean isCancelled();

   void setPackageBroadcast(boolean var1);
}
