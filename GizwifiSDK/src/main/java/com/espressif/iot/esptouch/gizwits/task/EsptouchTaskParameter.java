package com.espressif.iot.esptouch.gizwits.task;

import com.espressif.iot.esptouch.gizwits.task.IEsptouchTaskParameter;

public class EsptouchTaskParameter implements IEsptouchTaskParameter {

   private static int _datagramCount = 0;
   private long mIntervalGuideCodeMillisecond = 8L;
   private long mIntervalDataCodeMillisecond = 8L;
   private long mTimeoutGuideCodeMillisecond = 2000L;
   private long mTimeoutDataCodeMillisecond = 4000L;
   private int mTotalRepeatTime = 1;
   private int mEsptouchResultOneLen = 1;
   private int mEsptouchResultMacLen = 6;
   private int mEsptouchResultIpLen = 4;
   private int mEsptouchResultTotalLen = 11;
   private int mPortListening = 18266;
   private int mTargetPort = 7001;
   private int mWaitUdpReceivingMilliseond = 15000;
   private int mWaitUdpSendingMillisecond = '\uafc8';
   private int mThresholdSucBroadcastCount = 1;
   private int mExpectTaskResultCount = 1;
   private boolean mBroadcast = true;


   private static int __getNextDatagramCount() {
      return 1 + _datagramCount++ % 100;
   }

   public long getIntervalGuideCodeMillisecond() {
      return this.mIntervalGuideCodeMillisecond;
   }

   public long getIntervalDataCodeMillisecond() {
      return this.mIntervalDataCodeMillisecond;
   }

   public long getTimeoutGuideCodeMillisecond() {
      return this.mTimeoutGuideCodeMillisecond;
   }

   public long getTimeoutDataCodeMillisecond() {
      return this.mTimeoutDataCodeMillisecond;
   }

   public long getTimeoutTotalCodeMillisecond() {
      return this.mTimeoutGuideCodeMillisecond + this.mTimeoutDataCodeMillisecond;
   }

   public int getTotalRepeatTime() {
      return this.mTotalRepeatTime;
   }

   public int getEsptouchResultOneLen() {
      return this.mEsptouchResultOneLen;
   }

   public int getEsptouchResultMacLen() {
      return this.mEsptouchResultMacLen;
   }

   public int getEsptouchResultIpLen() {
      return this.mEsptouchResultIpLen;
   }

   public int getEsptouchResultTotalLen() {
      return this.mEsptouchResultTotalLen;
   }

   public int getPortListening() {
      return this.mPortListening;
   }

   public String getTargetHostname() {
      if(this.mBroadcast) {
         return "255.255.255.255";
      } else {
         int count = __getNextDatagramCount();
         return "234." + count + "." + count + "." + count;
      }
   }

   public int getTargetPort() {
      return this.mTargetPort;
   }

   public int getWaitUdpReceivingMillisecond() {
      return this.mWaitUdpReceivingMilliseond;
   }

   public int getWaitUdpSendingMillisecond() {
      return this.mWaitUdpSendingMillisecond;
   }

   public int getWaitUdpTotalMillisecond() {
      return this.mWaitUdpReceivingMilliseond + this.mWaitUdpSendingMillisecond;
   }

   public void setWaitUdpTotalMillisecond(int waitUdpTotalMillisecond) {
      if((long)waitUdpTotalMillisecond < (long)this.mWaitUdpReceivingMilliseond + this.getTimeoutTotalCodeMillisecond()) {
         throw new IllegalArgumentException("waitUdpTotalMillisecod is invalid, it is less than mWaitUdpReceivingMilliseond + getTimeoutTotalCodeMillisecond()");
      } else {
         this.mWaitUdpSendingMillisecond = waitUdpTotalMillisecond - this.mWaitUdpReceivingMilliseond;
      }
   }

   public int getThresholdSucBroadcastCount() {
      return this.mThresholdSucBroadcastCount;
   }

   public int getExpectTaskResultCount() {
      return this.mExpectTaskResultCount;
   }

   public void setExpectTaskResultCount(int expectTaskResultCount) {
      this.mExpectTaskResultCount = expectTaskResultCount;
   }

   public void setBroadcast(boolean broadcast) {
      this.mBroadcast = broadcast;
   }

}
