package com.hiflying.commons.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

public class SmartLinkUtils {

   private static final String META_CLIENT = "com.hiflying.smartlink.client";
   public static final String CLIENT_GIZWITS = "gizwits";


   public static String getBroadcastAddress(Context ctx) {
      WifiManager wifiManager = (WifiManager)ctx.getSystemService("wifi");
      int ipInt = 0;
      int netmaskInt = 0;
      DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();
      if(dhcpInfo != null) {
         ipInt = dhcpInfo.ipAddress;
         netmaskInt = dhcpInfo.netmask;
         if(ipInt != 0 && netmaskInt != 0) {
            try {
               return calculateBroadcastIpAddress(ipInt, netmaskInt);
            } catch (UnknownHostException var14) {
               var14.printStackTrace();
            }
         }
      } else {
         WifiInfo e = wifiManager.getConnectionInfo();
         if(e != null) {
            ipInt = e.getIpAddress();
         }
      }

      try {
         Enumeration e1 = NetworkInterface.getNetworkInterfaces();
         if(e1 != null) {
            while(e1.hasMoreElements()) {
               NetworkInterface networkInterface = (NetworkInterface)e1.nextElement();
               if(networkInterface.getName().trim().toLowerCase().startsWith("wlan")) {
                  List interfaceAddresses = networkInterface.getInterfaceAddresses();
                  if(interfaceAddresses != null && !interfaceAddresses.isEmpty()) {
                     Iterator var8 = interfaceAddresses.iterator();

                     while(var8.hasNext()) {
                        InterfaceAddress interfaceAddress = (InterfaceAddress)var8.next();
                        InetAddress broadcastInetAddress = interfaceAddress.getBroadcast();
                        if(broadcastInetAddress != null && broadcastInetAddress instanceof Inet4Address) {
                           String ip = broadcastInetAddress.getHostAddress();
                           if(!TextUtils.isEmpty(ip)) {
                              return ip;
                           }
                        }
                     }
                  }
               }
            }
         }
      } catch (Exception var13) {
         var13.printStackTrace();
      }

      if(ipInt == 0) {
         return "255.255.255.255";
      } else {
         if(netmaskInt == 0) {
            netmaskInt = 16777215;
         }

         try {
            return calculateBroadcastIpAddress(ipInt, netmaskInt);
         } catch (UnknownHostException var12) {
            var12.printStackTrace();
            return "255.255.255.255";
         }
      }
   }

   public static String calculateBroadcastIpAddress(int ip, int netmask) throws UnknownHostException {
      int broadcastInt = ip & netmask | ~netmask;
      byte[] broadcastIpAddress = new byte[4];

      for(int i = 0; i < 4; ++i) {
         broadcastIpAddress[i] = (byte)(broadcastInt >> i * 8 & 255);
      }

      return InetAddress.getByAddress(broadcastIpAddress).getHostAddress();
   }

   public static String getMetadataClient(Context context) {
      try {
         ApplicationInfo e = context.getPackageManager().getApplicationInfo(context.getPackageName(), 128);
         if(e.metaData.containsKey("com.hiflying.smartlink.client")) {
            return e.metaData.getString("com.hiflying.smartlink.client");
         }
      } catch (Exception var2) {
         var2.printStackTrace();
      }

      return null;
   }

   public static boolean isClientGizwits(Context context) {
      return "gizwits".equalsIgnoreCase(getMetadataClient(context));
   }
}
