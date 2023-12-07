package com.espressif.iot.esptouch.gizwits.udp;

import android.util.Log;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class UDPSocketClient {

   private static final String TAG = "UDPSocketClient";
   private DatagramSocket mSocket;
   private volatile boolean mIsStop;
   private volatile boolean mIsClosed;


   public UDPSocketClient() {
      try {
         this.mSocket = new DatagramSocket();
         this.mIsStop = false;
         this.mIsClosed = false;
      } catch (SocketException var2) {
         Log.w("UDPSocketClient", "SocketException");
         var2.printStackTrace();
      }

   }

   protected void finalize() throws Throwable {
      this.close();
      super.finalize();
   }

   public void interrupt() {
      Log.i("UDPSocketClient", "USPSocketClient is interrupt");
      this.mIsStop = true;
   }

   public synchronized void close() {
      if(!this.mIsClosed) {
         this.mSocket.close();
         this.mIsClosed = true;
      }

   }

   public void sendData(byte[][] data, String targetHostName, int targetPort, long interval) {
      this.sendData(data, 0, data.length, targetHostName, targetPort, interval);
   }

   public void sendData(byte[][] data, int offset, int count, String targetHostName, int targetPort, long interval) {
      if(data != null && data.length > 0) {
         for(int i = offset; !this.mIsStop && i < offset + count; ++i) {
            if(data[i].length != 0) {
               try {
                  InetAddress e = InetAddress.getByName(targetHostName);
                  DatagramPacket localDatagramPacket = new DatagramPacket(data[i], data[i].length, e, targetPort);
                  this.mSocket.send(localDatagramPacket);
               } catch (UnknownHostException var12) {
                  Log.w("UDPSocketClient", "sendData(): UnknownHostException");
                  var12.printStackTrace();
                  this.mIsStop = true;
                  break;
               } catch (IOException var13) {
                  Log.w("UDPSocketClient", "sendData(): IOException, but just ignore it");
               }

               try {
                  Thread.sleep(interval);
               } catch (InterruptedException var11) {
                  var11.printStackTrace();
                  Log.w("UDPSocketClient", "sendData is Interrupted");
                  this.mIsStop = true;
                  break;
               }
            }
         }

         if(this.mIsStop) {
            this.close();
         }

      } else {
         Log.w("UDPSocketClient", "sendData(): data == null or length <= 0");
      }
   }
}
