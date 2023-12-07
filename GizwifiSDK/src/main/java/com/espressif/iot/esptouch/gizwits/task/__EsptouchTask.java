package com.espressif.iot.esptouch.gizwits.task;

import android.content.Context;
import android.os.Looper;
import android.util.Log;
import com.espressif.iot.esptouch.gizwits.EsptouchResult;
import com.espressif.iot.esptouch.gizwits.IEsptouchListener;
import com.espressif.iot.esptouch.gizwits.IEsptouchResult;
import com.espressif.iot.esptouch.gizwits.protocol.EsptouchGenerator;
import com.espressif.iot.esptouch.gizwits.protocol.TouchData;
import com.espressif.iot.esptouch.gizwits.security.ITouchEncryptor;
import com.espressif.iot.esptouch.gizwits.task.IEsptouchGenerator;
import com.espressif.iot.esptouch.gizwits.task.IEsptouchTaskParameter;
import com.espressif.iot.esptouch.gizwits.task.__IEsptouchTask;
import com.espressif.iot.esptouch.gizwits.udp.UDPSocketClient;
import com.espressif.iot.esptouch.gizwits.udp.UDPSocketServer;
import com.espressif.iot.esptouch.gizwits.util.ByteUtil;
import com.espressif.iot.esptouch.gizwits.util.TouchNetUtil;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class __EsptouchTask implements __IEsptouchTask {

   private static final int ONE_DATA_LEN = 3;
   private static final String TAG = "__EsptouchTask";
   private final UDPSocketClient mSocketClient;
   private final UDPSocketServer mSocketServer;
   private final byte[] mApSsid;
   private final byte[] mApPassword;
   private final byte[] mApBssid;
   private final ITouchEncryptor mEncryptor;
   private final Context mContext;
   private final List mEsptouchResultList;
   private volatile boolean mIsSuc = false;
   private volatile boolean mIsInterrupt = false;
   private volatile boolean mIsExecuted = false;
   private AtomicBoolean mIsCancelled;
   private IEsptouchTaskParameter mParameter;
   private volatile Map mBssidTaskSucCountMap;
   private IEsptouchListener mEsptouchListener;
   private Thread mTask;


   public __EsptouchTask(Context context, TouchData apSsid, TouchData apBssid, TouchData apPassword, ITouchEncryptor encryptor, IEsptouchTaskParameter parameter) {
      Log.i("__EsptouchTask", "Welcome Esptouch 1.0.0");
      this.mContext = context;
      this.mEncryptor = encryptor;
      this.mApSsid = apSsid.getData();
      this.mApPassword = apPassword.getData();
      this.mApBssid = apBssid.getData();
      this.mIsCancelled = new AtomicBoolean(false);
      this.mSocketClient = new UDPSocketClient();
      this.mParameter = parameter;
      this.mSocketServer = new UDPSocketServer(this.mParameter.getPortListening(), this.mParameter.getWaitUdpTotalMillisecond(), context);
      this.mEsptouchResultList = new ArrayList();
      this.mBssidTaskSucCountMap = new HashMap();
   }

   private void __putEsptouchResult(boolean isSuc, String bssid, InetAddress inetAddress) {
      List var4 = this.mEsptouchResultList;
      synchronized(this.mEsptouchResultList) {
         boolean isTaskSucCountEnough = false;
         Integer count = (Integer)this.mBssidTaskSucCountMap.get(bssid);
         if(count == null) {
            count = Integer.valueOf(0);
         }

         count = Integer.valueOf(count.intValue() + 1);
         Log.d("__EsptouchTask", "__putEsptouchResult(): count = " + count);
         this.mBssidTaskSucCountMap.put(bssid, count);
         isTaskSucCountEnough = count.intValue() >= this.mParameter.getThresholdSucBroadcastCount();
         if(!isTaskSucCountEnough) {
            Log.d("__EsptouchTask", "__putEsptouchResult(): count = " + count + ", isn\'t enough");
         } else {
            boolean isExist = false;
            Iterator esptouchResult = this.mEsptouchResultList.iterator();

            while(esptouchResult.hasNext()) {
               IEsptouchResult esptouchResultInList = (IEsptouchResult)esptouchResult.next();
               if(esptouchResultInList.getBssid().equals(bssid)) {
                  isExist = true;
                  break;
               }
            }

            if(!isExist) {
               Log.d("__EsptouchTask", "__putEsptouchResult(): put one more result bssid = " + bssid + " , address = " + inetAddress);
               EsptouchResult esptouchResult1 = new EsptouchResult(isSuc, bssid, inetAddress);
               this.mEsptouchResultList.add(esptouchResult1);
               if(this.mEsptouchListener != null) {
                  this.mEsptouchListener.onEsptouchResultAdded(esptouchResult1);
               }
            }

         }
      }
   }

   private List __getEsptouchResultList() {
      List var1 = this.mEsptouchResultList;
      synchronized(this.mEsptouchResultList) {
         if(this.mEsptouchResultList.isEmpty()) {
            EsptouchResult esptouchResultFail = new EsptouchResult(false, (String)null, (InetAddress)null);
            esptouchResultFail.setIsCancelled(this.mIsCancelled.get());
            this.mEsptouchResultList.add(esptouchResultFail);
         }

         return this.mEsptouchResultList;
      }
   }

   private synchronized void __interrupt() {
      if(!this.mIsInterrupt) {
         this.mIsInterrupt = true;
         this.mSocketClient.interrupt();
         this.mSocketServer.interrupt();
         if(this.mTask != null) {
            this.mTask.interrupt();
            this.mTask = null;
         }
      }

   }

   public void interrupt() {
      Log.d("__EsptouchTask", "interrupt()");
      this.mIsCancelled.set(true);
      this.__interrupt();
   }

   private void __listenAsyn(final int expectDataLen) {
      this.mTask = new Thread() {
         public void run() {
            Log.d("__EsptouchTask", "__listenAsyn() start");
            long startTimestamp = System.currentTimeMillis();
            byte expectOneByte = (byte)(__EsptouchTask.this.mApSsid.length + __EsptouchTask.this.mApPassword.length + 9);
            Log.i("__EsptouchTask", "expectOneByte: " + expectOneByte);
            boolean receiveOneByte = true;
            Object receiveBytes = null;

            while(__EsptouchTask.this.mEsptouchResultList.size() < __EsptouchTask.this.mParameter.getExpectTaskResultCount() && !__EsptouchTask.this.mIsInterrupt) {
               byte[] receiveBytes1 = __EsptouchTask.this.mSocketServer.receiveSpecLenBytes(expectDataLen);
               byte receiveOneByte1;
               if(receiveBytes1 != null) {
                  receiveOneByte1 = receiveBytes1[0];
               } else {
                  receiveOneByte1 = -1;
               }

               if(receiveOneByte1 == expectOneByte) {
                  Log.i("__EsptouchTask", "receive correct broadcast");
                  long consume = System.currentTimeMillis() - startTimestamp;
                  int timeout = (int)((long)__EsptouchTask.this.mParameter.getWaitUdpTotalMillisecond() - consume);
                  if(timeout < 0) {
                     Log.i("__EsptouchTask", "esptouch timeout");
                     break;
                  }

                  Log.i("__EsptouchTask", "mSocketServer\'s new timeout is " + timeout + " milliseconds");
                  __EsptouchTask.this.mSocketServer.setSoTimeout(timeout);
                  Log.i("__EsptouchTask", "receive correct broadcast");
                  if(receiveBytes1 != null) {
                     String bssid = ByteUtil.parseBssid(receiveBytes1, __EsptouchTask.this.mParameter.getEsptouchResultOneLen(), __EsptouchTask.this.mParameter.getEsptouchResultMacLen());
                     InetAddress inetAddress = TouchNetUtil.parseInetAddr(receiveBytes1, __EsptouchTask.this.mParameter.getEsptouchResultOneLen() + __EsptouchTask.this.mParameter.getEsptouchResultMacLen(), __EsptouchTask.this.mParameter.getEsptouchResultIpLen());
                     __EsptouchTask.this.__putEsptouchResult(true, bssid, inetAddress);
                  }
               } else {
                  Log.i("__EsptouchTask", "receive rubbish message, just ignore");
               }
            }

            __EsptouchTask.this.mIsSuc = __EsptouchTask.this.mEsptouchResultList.size() >= __EsptouchTask.this.mParameter.getExpectTaskResultCount();
            __EsptouchTask.this.__interrupt();
            Log.d("__EsptouchTask", "__listenAsyn() finish");
         }
      };
      this.mTask.start();
   }

   private boolean __execute(IEsptouchGenerator generator) {
      long startTime = System.currentTimeMillis();
      long currentTime = startTime;
      long lastTime = startTime - this.mParameter.getTimeoutTotalCodeMillisecond();
      byte[][] gcBytes2 = generator.getGCBytes2();
      byte[][] dcBytes2 = generator.getDCBytes2();
      int index = 0;

      while(!this.mIsInterrupt) {
         if(currentTime - lastTime >= this.mParameter.getTimeoutTotalCodeMillisecond()) {
            Log.d("__EsptouchTask", "send gc code ");

            while(!this.mIsInterrupt && System.currentTimeMillis() - currentTime < this.mParameter.getTimeoutGuideCodeMillisecond()) {
               this.mSocketClient.sendData(gcBytes2, this.mParameter.getTargetHostname(), this.mParameter.getTargetPort(), this.mParameter.getIntervalGuideCodeMillisecond());
               if(System.currentTimeMillis() - startTime > (long)this.mParameter.getWaitUdpSendingMillisecond()) {
                  break;
               }
            }

            lastTime = currentTime;
         } else {
            this.mSocketClient.sendData(dcBytes2, index, 3, this.mParameter.getTargetHostname(), this.mParameter.getTargetPort(), this.mParameter.getIntervalDataCodeMillisecond());
            index = (index + 3) % dcBytes2.length;
         }

         currentTime = System.currentTimeMillis();
         if(currentTime - startTime > (long)this.mParameter.getWaitUdpSendingMillisecond()) {
            break;
         }
      }

      return this.mIsSuc;
   }

   private void __checkTaskValid() {
      if(this.mIsExecuted) {
         throw new IllegalStateException("the Esptouch task could be executed only once");
      } else {
         this.mIsExecuted = true;
      }
   }

   public IEsptouchResult executeForResult() throws RuntimeException {
      return (IEsptouchResult)this.executeForResults(1).get(0);
   }

   public boolean isCancelled() {
      return this.mIsCancelled.get();
   }

   public List executeForResults(int expectTaskResultCount) throws RuntimeException {
      this.__checkTaskValid();
      this.mParameter.setExpectTaskResultCount(expectTaskResultCount);
      Log.d("__EsptouchTask", "execute()");
      if(Looper.myLooper() == Looper.getMainLooper()) {
         throw new RuntimeException("Don\'t call the esptouch Task at Main(UI) thread directly.");
      } else {
         InetAddress localInetAddress = TouchNetUtil.getLocalInetAddress(this.mContext);
         Log.i("__EsptouchTask", "localInetAddress: " + localInetAddress);
         EsptouchGenerator generator = new EsptouchGenerator(this.mApSsid, this.mApBssid, this.mApPassword, localInetAddress, this.mEncryptor);
         this.__listenAsyn(this.mParameter.getEsptouchResultTotalLen());
         boolean isSuc = false;

         for(int e = 0; e < this.mParameter.getTotalRepeatTime(); ++e) {
            isSuc = this.__execute(generator);
            if(isSuc) {
               return this.__getEsptouchResultList();
            }
         }

         if(!this.mIsInterrupt) {
            try {
               Thread.sleep((long)this.mParameter.getWaitUdpReceivingMillisecond());
            } catch (InterruptedException var6) {
               if(this.mIsSuc) {
                  return this.__getEsptouchResultList();
               }

               this.__interrupt();
               return this.__getEsptouchResultList();
            }

            this.__interrupt();
         }

         return this.__getEsptouchResultList();
      }
   }

   public void setEsptouchListener(IEsptouchListener esptouchListener) {
      this.mEsptouchListener = esptouchListener;
   }
}
