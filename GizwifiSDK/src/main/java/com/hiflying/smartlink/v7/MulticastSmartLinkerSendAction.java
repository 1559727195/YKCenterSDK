package com.hiflying.smartlink.v7;

import android.content.Context;
import android.text.TextUtils;
import com.hiflying.smartlink.ISmartLinker;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.ByteBuffer;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class MulticastSmartLinkerSendAction implements Runnable {

   private static final int UDP_PORT = 6000;
   private static final int DEFAULT_DATA_LENGTH = 30;
   private ISmartLinker mSmartLinker;
   private String mPassword;
   private String mSsid;
   private byte[] mDataAppend;


   public MulticastSmartLinkerSendAction(Context context, ISmartLinker smartLinker, String ssid, String password, byte[] dataAppend) throws Exception {
      this.mSmartLinker = smartLinker;
      this.mSsid = ssid;
      this.mPassword = password;
      this.mDataAppend = dataAppend;
      if(context == null) {
         throw new NullPointerException("params context is null");
      } else if(smartLinker == null) {
         throw new NullPointerException("params smartLinker is null");
      } else if(ssid == null) {
         throw new NullPointerException("params ssid is null");
      } else if(password == null) {
         throw new NullPointerException("params password is null");
      }
   }

   private void sleep(long millseconds) {
      try {
         Thread.sleep(millseconds);
      } catch (InterruptedException var4) {
         ;
      }

   }

   public void run() {
      Object datas = null;

      byte[] var13;
      try {
         var13 = this.generateData2Send(this.mSsid, this.mPassword, this.mDataAppend);
      } catch (Exception var10) {
         var10.printStackTrace();
         throw new RuntimeException("generateData2Send error: " + var10.getMessage());
      }

      MulticastSocket multicastSocket = null;

      try {
         multicastSocket = new MulticastSocket();

         while(this.mSmartLinker.isSmartLinking()) {
            DatagramPacket e = new DatagramPacket(new byte[30], 30, InetAddress.getByName("239.48.0.0"), 6000);

            int length;
            for(length = 0; length < 5; ++length) {
               multicastSocket.send(e);
               this.sleep(10L);
            }

            length = var13.length / 2;

            for(int i = 0; i < length; ++i) {
               e = new DatagramPacket(new byte[30 + i], 30 + i, InetAddress.getByName(String.format("239.46.%s.%s", new Object[]{Integer.valueOf(var13[2 * i] & 255), Integer.valueOf(var13[2 * i + 1] & 255)})), 6000);
               multicastSocket.send(e);
               this.sleep(10L);
            }

            this.sleep(100L);
         }
      } catch (IOException var11) {
         var11.printStackTrace();
      } finally {
         if(multicastSocket != null) {
            multicastSocket.close();
            multicastSocket.disconnect();
         }

      }

   }

   private byte[] encodedPMK(String ssid, String password) throws Exception {
      if(TextUtils.isEmpty(password)) {
         return new byte[0];
      } else {
         PBEKeySpec keySpec = new PBEKeySpec(TextUtils.isEmpty(password)?new char[0]:password.toCharArray(), ssid.getBytes(), 4096, 256);
         SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
         return secretKeyFactory.generateSecret(keySpec).getEncoded();
      }
   }

   private byte[] generateData2Send(String ssid, String password, byte[] data) throws Exception {
      byte[] ssidDataArray = ssid.getBytes();
      byte[] passwordArray = TextUtils.isEmpty(password)?new byte[0]:password.getBytes();
      byte[] pmkDataArray = this.encodedPMK(ssid, password);
      int bufferSize = 4 + ssidDataArray.length + passwordArray.length + pmkDataArray.length + data.length;
      boolean odd = bufferSize % 2 != 0;
      if(odd) {
         ++bufferSize;
      }

      ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
      buffer.put((byte)ssidDataArray.length);
      buffer.put((byte)passwordArray.length);
      buffer.put((byte)pmkDataArray.length);
      buffer.put((byte)data.length);
      buffer.put(ssidDataArray);
      buffer.put(passwordArray);
      buffer.put(pmkDataArray);
      buffer.put(data);
      if(odd) {
         buffer.put((byte)0);
      }

      return buffer.array();
   }
}
