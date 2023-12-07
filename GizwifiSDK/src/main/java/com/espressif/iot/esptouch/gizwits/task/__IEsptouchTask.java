package com.espressif.iot.esptouch.gizwits.task;

import com.espressif.iot.esptouch.gizwits.IEsptouchListener;
import com.espressif.iot.esptouch.gizwits.IEsptouchResult;
import java.util.List;

public interface __IEsptouchTask {

   boolean DEBUG = true;


   void setEsptouchListener(IEsptouchListener var1);

   void interrupt();

   IEsptouchResult executeForResult() throws RuntimeException;

   List executeForResults(int var1) throws RuntimeException;

   boolean isCancelled();
}
