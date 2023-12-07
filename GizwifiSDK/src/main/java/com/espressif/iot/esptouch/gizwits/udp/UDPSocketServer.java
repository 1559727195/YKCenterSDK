package com.espressif.iot.esptouch.gizwits.udp;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;
import android.util.Log;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.Arrays;

public class UDPSocketServer {

   private static final String TAG = "UDPSocketServer";
   private DatagramSocket mServerSocket;
   private Context mContext;
   private MulticastLock mLock;
   private volatile boolean mIsClosed;


   public UDPSocketServer(int port, int socketTimeout, Context context) {
      this.mContext = context;

      try {
         this.mServerSocket = new DatagramSocket((SocketAddress)null);
         this.mServerSocket.setReuseAddress(true);
         this.mServerSocket.bind(new InetSocketAddress(port));
         this.mServerSocket.setSoTimeout(socketTimeout);
      } catch (IOException var5) {
         Log.w("UDPSocketServer", "IOException");
         var5.printStackTrace();
      }

      this.mIsClosed = false;
      WifiManager manager = (WifiManager)this.mContext.getApplicationContext().getSystemService("wifi");
      this.mLock = manager.createMulticastLock("test wifi");
      Log.d("UDPSocketServer", "mServerSocket is created, socket read timeout: " + socketTimeout + ", port: " + port);
   }

   private synchronized void acquireLock() {
      if(this.mLock != null && !this.mLock.isHeld()) {
         this.mLock.acquire();
      }

   }

   private synchronized void releaseLock() {
      if(this.mLock != null && this.mLock.isHeld()) {
         try {
            this.mLock.release();
         } catch (Throwable var2) {
            ;
         }
      }

   }

   public boolean setSoTimeout(int timeout) {
      try {
         this.mServerSocket.setSoTimeout(timeout);
         return true;
      } catch (SocketException var3) {
         var3.printStackTrace();
         return false;
      }
   }

   public byte receiveOneByte() {
      Log.d("UDPSocketServer", "receiveOneByte() entrance");

      try {
         this.acquireLock();
         DatagramPacket e = new DatagramPacket(new byte[1], 1);
         this.mServerSocket.receive(e);
         Log.d("UDPSocketServer", "receive: " + e.getData()[0]);
         return e.getData()[0];
      } catch (Exception var2) {
         var2.printStackTrace();
         return (byte)-1;
      }
   }

   public byte[] receiveSpecLenBytes(int len) {
      Log.d("UDPSocketServer", "receiveSpecLenBytes() entrance: len = " + len);

      try {
         this.acquireLock();
         DatagramPacket e = new DatagramPacket(new byte[64], 64);
         this.mServerSocket.receive(e);
         byte[] recDatas = Arrays.copyOf(e.getData(), e.getLength());
         Log.d("UDPSocketServer", "received len : " + recDatas.length);

         for(int i = 0; i < recDatas.length; ++i) {
            Log.w("UDPSocketServer", "recDatas[" + i + "]:" + recDatas[i]);
         }

         Log.w("UDPSocketServer", "receiveSpecLenBytes: " + new String(recDatas));
         if(recDatas.length != len) {
            Log.w("UDPSocketServer", "received len is different from specific len, return null");
            return null;
         } else {
            return recDatas;
         }
      } catch (Exception var5) {
         var5.printStackTrace();
         return null;
      }
   }

   public void interrupt() {
      Log.i("UDPSocketServer", "USPSocketServer is interrupt");
      this.close();
   }

   public synchronized void close() {
      if(!this.mIsClosed) {
         Log.w("UDPSocketServer", "mServerSocket is closed");
         this.mServerSocket.close();
         this.releaseLock();
         this.mIsClosed = true;
      }

   }

   protected void finalize() throws Throwable {
      this.close();
      super.finalize();
   }
}
