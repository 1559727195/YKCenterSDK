package com.hiflying.smartlink.v3;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import com.hiflying.smartlink.ISmartLinker;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class SnifferSmartLinkerSendAction implements Runnable {

   private int HEADER_COUNT = 200;
   private int HEADER_PACKAGE_DELAY_TIME = 10;
   private int HEADER_CAPACITY = 76;
   private int CONTENT_COUNT = 5;
   private int CONTENT_PACKAGE_DELAY_TIME = 50;
   private int CONTENT_CHECKSUM_BEFORE_DELAY_TIME = 100;
   private int CONTENT_GROUP_DELAY_TIME = 500;
   private int port = '\uc34f';
   private Context mContext;
   private ISmartLinker mSmartLinker;
   private String mPassword;
   private String mSsid;
   private byte[] mDataAppend;
   private InetAddress mBroadcastInetAddress;
   private DatagramSocket mSocket;


   public SnifferSmartLinkerSendAction(Context context, DatagramSocket socket, ISmartLinker smartLinker, String ssid, String password, byte[] dataAppend) throws Exception {
      this.mContext = context;
      this.mSocket = socket;
      this.mSmartLinker = smartLinker;
      this.mSsid = ssid;
      this.mPassword = password;
      this.mDataAppend = dataAppend;
      this.mBroadcastInetAddress = InetAddress.getByName(this.getBroadcastAddress(this.mContext));
      if(context == null) {
         throw new NullPointerException("params context is null");
      } else if(socket == null) {
         throw new NullPointerException("params socket is null");
      } else if(smartLinker == null) {
         throw new NullPointerException("params smartLinker is null");
      } else if(password == null) {
         throw new NullPointerException("params password is null");
      }
   }

   public void run() {
      while(this.mSmartLinker.isSmartLinking()) {
         this.sendContents();
      }

   }

   private void sleep(int millseconds) {
      try {
         Thread.sleep((long)millseconds);
      } catch (InterruptedException var3) {
         var3.printStackTrace();
      }

   }

   private String getBroadcastAddress(Context ctx) {
      WifiManager cm = (WifiManager)ctx.getSystemService("wifi");
      DhcpInfo myDhcpInfo = cm.getDhcpInfo();
      if(myDhcpInfo == null) {
         return "255.255.255.255";
      } else {
         int broadcast = myDhcpInfo.ipAddress & myDhcpInfo.netmask | ~myDhcpInfo.netmask;
         byte[] quads = new byte[4];

         for(int e = 0; e < 4; ++e) {
            quads[e] = (byte)(broadcast >> e * 8 & 255);
         }

         try {
            return InetAddress.getByAddress(quads).getHostAddress();
         } catch (Exception var7) {
            return "255.255.255.255";
         }
      }
   }

   private void sendContents() {
      byte[] header = this.genPackageContents(this.HEADER_CAPACITY);

      for(int passwordSBuffer = 0; this.mSmartLinker.isSmartLinking() && passwordSBuffer < this.HEADER_COUNT; ++passwordSBuffer) {
         this.send(header);
         this.sleep(this.HEADER_PACKAGE_DELAY_TIME);
      }

      StringBuffer var9 = new StringBuffer(this.mPassword);

      for(int password = 0; password < this.mDataAppend.length; ++password) {
         var9.append((char)this.mDataAppend[password]);
      }

      String var10 = var9.toString();
      int[] content = new int[var10.length() + 2];
      content[0] = 89;

      int count;
      for(count = 0; count < var10.length(); ++count) {
         content[count + 1] = var10.charAt(count) + 76;
      }

      content[content.length - 1] = 86;

      for(count = 1; this.mSmartLinker.isSmartLinking() && count <= this.CONTENT_COUNT; ++count) {
         int checkLength;
         int i;
         for(checkLength = 0; checkLength < content.length; ++checkLength) {
            i = checkLength != 0 && checkLength != content.length - 1?1:3;

            for(int j = 0; this.mSmartLinker.isSmartLinking() && j < i; ++j) {
               this.send(this.genPackageContents(content[checkLength]));
               if(checkLength != content.length - 1) {
                  this.sleep(this.CONTENT_PACKAGE_DELAY_TIME);
               }
            }

            if(checkLength != content.length - 1) {
               this.sleep(this.CONTENT_PACKAGE_DELAY_TIME);
            }
         }

         this.sleep(this.CONTENT_CHECKSUM_BEFORE_DELAY_TIME);
         checkLength = var10.length() + 256 + 76;

         for(i = 0; this.mSmartLinker.isSmartLinking() && i < 3; ++i) {
            this.send(this.genPackageContents(checkLength));
            if(i < 2) {
               this.sleep(this.CONTENT_PACKAGE_DELAY_TIME);
            }
         }

         this.sleep(this.CONTENT_GROUP_DELAY_TIME);
      }

   }

   private byte[] genPackageContents(int capacity) {
      byte[] data = new byte[capacity];

      for(int i = 0; i < capacity; ++i) {
         data[i] = 5;
      }

      return data;
   }

   private void send(byte[] data) {
      try {
         this.mSocket.send(new DatagramPacket(data, data.length, this.mBroadcastInetAddress, this.port));
      } catch (IOException var3) {
         var3.printStackTrace();
      }

   }
}
