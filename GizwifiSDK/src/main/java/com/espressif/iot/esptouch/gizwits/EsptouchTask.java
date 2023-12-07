package com.espressif.iot.esptouch.gizwits;

import android.content.Context;
import android.text.TextUtils;
import com.espressif.iot.esptouch.gizwits.IEsptouchListener;
import com.espressif.iot.esptouch.gizwits.IEsptouchResult;
import com.espressif.iot.esptouch.gizwits.IEsptouchTask;
import com.espressif.iot.esptouch.gizwits.protocol.TouchData;
import com.espressif.iot.esptouch.gizwits.security.ITouchEncryptor;
import com.espressif.iot.esptouch.gizwits.task.EsptouchTaskParameter;
import com.espressif.iot.esptouch.gizwits.task.__EsptouchTask;
import com.espressif.iot.esptouch.gizwits.util.TouchNetUtil;
import java.util.List;

public class EsptouchTask implements IEsptouchTask {

   private __EsptouchTask _mEsptouchTask;
   private EsptouchTaskParameter _mParameter;


   public EsptouchTask(String apSsid, String apBssid, String apPassword, Context context) {
      this(apSsid, apBssid, apPassword, (ITouchEncryptor)null, context);
   }

   public EsptouchTask(byte[] apSsid, byte[] apBssid, byte[] apPassword, Context context) {
      this(apSsid, apBssid, apPassword, (ITouchEncryptor)null, context);
   }

   private EsptouchTask(String apSsid, String apBssid, String apPassword, ITouchEncryptor encryptor, Context context) {
      if(TextUtils.isEmpty(apSsid)) {
         throw new NullPointerException("SSID can\'t be empty");
      } else if(TextUtils.isEmpty(apBssid)) {
         throw new NullPointerException("BSSID can\'t be empty");
      } else {
         if(apPassword == null) {
            apPassword = "";
         }

         TouchData ssid = new TouchData(apSsid);
         TouchData bssid = new TouchData(TouchNetUtil.parseBssid2bytes(apBssid));
         if(bssid.getData().length != 6) {
            throw new IllegalArgumentException("Bssid format must be aa:bb:cc:dd:ee:ff");
         } else {
            TouchData password = new TouchData(apPassword);
            this.init(context, ssid, bssid, password, encryptor);
         }
      }
   }

   private EsptouchTask(byte[] apSsid, byte[] apBssid, byte[] apPassword, ITouchEncryptor encryptor, Context context) {
      if(apSsid != null && apSsid.length != 0) {
         if(apBssid != null && apBssid.length == 6) {
            if(apPassword == null) {
               apPassword = new byte[0];
            }

            TouchData ssid = new TouchData(apSsid);
            TouchData bssid = new TouchData(apBssid);
            TouchData password = new TouchData(apPassword);
            this.init(context, ssid, bssid, password, encryptor);
         } else {
            throw new NullPointerException("BSSID is empty or length is not 6");
         }
      } else {
         throw new NullPointerException("SSID can\'t be empty");
      }
   }

   private void init(Context context, TouchData ssid, TouchData bssid, TouchData password, ITouchEncryptor encryptor) {
      this._mParameter = new EsptouchTaskParameter();
      this._mEsptouchTask = new __EsptouchTask(context, ssid, bssid, password, encryptor, this._mParameter);
   }

   public void interrupt() {
      this._mEsptouchTask.interrupt();
   }

   public IEsptouchResult executeForResult() throws RuntimeException {
      return this._mEsptouchTask.executeForResult();
   }

   public boolean isCancelled() {
      return this._mEsptouchTask.isCancelled();
   }

   public List executeForResults(int expectTaskResultCount) throws RuntimeException {
      if(expectTaskResultCount <= 0) {
         expectTaskResultCount = Integer.MAX_VALUE;
      }

      return this._mEsptouchTask.executeForResults(expectTaskResultCount);
   }

   public void setEsptouchListener(IEsptouchListener esptouchListener) {
      this._mEsptouchTask.setEsptouchListener(esptouchListener);
   }

   public void setPackageBroadcast(boolean broadcast) {
      this._mParameter.setBroadcast(broadcast);
   }
}
