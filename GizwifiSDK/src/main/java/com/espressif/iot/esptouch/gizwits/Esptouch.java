package com.espressif.iot.esptouch.gizwits;

import android.content.Context;
import com.espressif.iot.esptouch.gizwits.Esptouch1;
import com.gizwits.gizwifisdk.log.SDKLog;

public class Esptouch {

   private String TAG;
   private volatile Esptouch1 mEsptouch1;
   private volatile boolean mIsEsptouch1StartSuc;
   private volatile long mStartTimestamp;
   private volatile long mTimeout;


   private Esptouch() {
      this.TAG = "Esptouch";
      this.mStartTimestamp = -1L;
      this.mTimeout = -1L;
      SDKLog.d("Esptouch() ok");
   }

   public static Esptouch getInstance() {
      SDKLog.d("Esptouch get instance");
      return Esptouch.InstanceHolder.instance;
   }

   private boolean isTimeout() {
      SDKLog.d("Esptouch isTimeout" + this.mStartTimestamp + ',' + this.mTimeout);
      if(this.mStartTimestamp >= 0L && this.mTimeout >= 0L) {
         boolean isTimeout = System.currentTimeMillis() > this.mStartTimestamp + this.mTimeout;
         return isTimeout;
      } else {
         return true;
      }
   }

   public synchronized void start(String ssid, String psw, Context context, int timeout, boolean broadcast) {
      SDKLog.d(this.TAG + ", Esptouch start() ssid:" + ssid + ",psw:****,timeout:" + timeout);
      if(this.isRunning()) {
         SDKLog.e(this.TAG + ", Esptouch start(): one task is running, so stop it before start a new one");
         this.stop();
      }

      this.mEsptouch1 = new Esptouch1();
      this.mIsEsptouch1StartSuc = this.mEsptouch1.start(ssid, psw, context, timeout, broadcast);
      this.mTimeout = (long)(timeout * 1000);
      this.mStartTimestamp = System.currentTimeMillis() + 100L;
   }

   public synchronized void stop() {
      SDKLog.d(this.TAG + ", Esptouch stop()");
      if(this.mEsptouch1 != null) {
         this.mEsptouch1.stop();
         this.mEsptouch1 = null;
         this.mIsEsptouch1StartSuc = false;
         this.mStartTimestamp = -1L;
         this.mTimeout = -1L;
      }

   }

   public synchronized boolean isRunning() {
      return !this.isTimeout();
   }

   // $FF: synthetic method
   Esptouch(Object x0) {
      this();
   }

   private static class InstanceHolder {

      static Esptouch instance = new Esptouch(null);


   }
}
