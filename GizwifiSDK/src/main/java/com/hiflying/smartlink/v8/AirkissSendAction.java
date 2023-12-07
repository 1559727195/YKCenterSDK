package com.hiflying.smartlink.v8;

import android.content.Context;
import android.util.Log;
import com.hiflying.smartlink.ISmartLinker;
import com.hiflying.smartlink.v8.AirkissProtocol;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

public class AirkissSendAction implements Runnable {

   private DatagramSocket udpSocket;
   private int port = '\ubaa1';
   private Context mContext;
   private ISmartLinker mSmartLinker;
   private String mPassword;
   private String mSsid;
   private byte[] mOthers;
   private AirkissProtocol airkissProtocol;


   public AirkissSendAction(Context mContext, ISmartLinker mSmartLinker, String mSsid, String mPassword, byte[] mOthers) {
      this.mContext = mContext;
      this.mSmartLinker = mSmartLinker;
      this.mSsid = mSsid;
      this.mPassword = mPassword;
      this.mOthers = mOthers;
   }

   private void send(int len) {
      try {
         if(len != 0) {
            Log.v("AirkissSendAction", "Send:" + len + String.format(", 0x%02X", new Object[]{Integer.valueOf(len)}));
         } else {
            len = 8;
            Log.v("AirkissSendAction", "Send (0->8):" + len + String.format(", 0x%02X", new Object[]{Integer.valueOf(len)}));
         }

         byte[] e = new byte[len];
         Arrays.fill(e, (byte)-86);
         InetAddress addr = InetAddress.getByName("255.255.255.255");
         this.udpSocket.send(new DatagramPacket(e, len, addr, this.port));
      } catch (UnknownHostException var4) {
         var4.printStackTrace();
      } catch (IOException var5) {
         var5.printStackTrace();
      }

   }

   public void run() {
      this.airkissProtocol = new AirkissProtocol(this.mContext, this.mSsid, this.mPassword, this.mOthers);

      try {
         this.udpSocket = new DatagramSocket();

         for(int e = 0; this.mSmartLinker.isSmartLinking(); ++e) {
            int[] bSend = this.airkissProtocol.getByte(e);
            if(bSend == null) {
               e = -1;
            } else {
               for(int j = 0; j < bSend.length; ++j) {
                  this.send(bSend[j]);
                  this.msleep(5);
               }
            }

            this.msleep(50);
         }
      } catch (IOException var7) {
         var7.printStackTrace();
      } finally {
         if(this.udpSocket != null) {
            this.udpSocket.close();
            this.udpSocket.disconnect();
         }

      }

   }

   private void msleep(int ms) {
      try {
         Thread.sleep((long)ms);
      } catch (InterruptedException var3) {
         var3.printStackTrace();
      }

   }
}
