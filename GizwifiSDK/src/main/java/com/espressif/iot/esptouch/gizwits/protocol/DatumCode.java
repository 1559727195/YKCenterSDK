package com.espressif.iot.esptouch.gizwits.protocol;

import com.espressif.iot.esptouch.gizwits.protocol.DataCode;
import com.espressif.iot.esptouch.gizwits.security.ITouchEncryptor;
import com.espressif.iot.esptouch.gizwits.task.ICodeData;
import com.espressif.iot.esptouch.gizwits.util.ByteUtil;
import com.espressif.iot.esptouch.gizwits.util.CRC8;
import java.net.InetAddress;
import java.util.Iterator;
import java.util.LinkedList;

public class DatumCode implements ICodeData {

   private static final int EXTRA_LEN = 40;
   private static final int EXTRA_HEAD_LEN = 5;
   private final LinkedList mDataCodes;


   public DatumCode(byte[] apSsid, byte[] apBssid, byte[] apPassword, InetAddress ipAddress, ITouchEncryptor encryptor) {
      byte totalXor = 0;
      char apPwdLen = (char)apPassword.length;
      CRC8 crc = new CRC8();
      crc.update(apSsid);
      char apSsidCrc = (char)((int)crc.getValue());
      crc.reset();
      crc.update(apBssid);
      char apBssidCrc = (char)((int)crc.getValue());
      char apSsidLen = (char)apSsid.length;
      byte[] ipBytes = ipAddress.getAddress();
      int ipLen = ipBytes.length;
      char totalLen = (char)(5 + ipLen + apPwdLen + apSsidLen);
      this.mDataCodes = new LinkedList();
      this.mDataCodes.add(new DataCode(totalLen, 0));
      char var20 = (char)(totalXor ^ totalLen);
      this.mDataCodes.add(new DataCode(apPwdLen, 1));
      var20 ^= apPwdLen;
      this.mDataCodes.add(new DataCode(apSsidCrc, 2));
      var20 ^= apSsidCrc;
      this.mDataCodes.add(new DataCode(apBssidCrc, 3));
      var20 ^= apBssidCrc;

      int bssidInsertIndex;
      char i;
      for(bssidInsertIndex = 0; bssidInsertIndex < ipLen; ++bssidInsertIndex) {
         i = ByteUtil.convertByte2Uint8(ipBytes[bssidInsertIndex]);
         var20 ^= i;
         this.mDataCodes.add(new DataCode(i, bssidInsertIndex + 5));
      }

      for(bssidInsertIndex = 0; bssidInsertIndex < apPassword.length; ++bssidInsertIndex) {
         i = ByteUtil.convertByte2Uint8(apPassword[bssidInsertIndex]);
         var20 ^= i;
         this.mDataCodes.add(new DataCode(i, bssidInsertIndex + 5 + ipLen));
      }

      for(bssidInsertIndex = 0; bssidInsertIndex < apSsid.length; ++bssidInsertIndex) {
         i = ByteUtil.convertByte2Uint8(apSsid[bssidInsertIndex]);
         var20 ^= i;
         this.mDataCodes.add(new DataCode(i, bssidInsertIndex + 5 + ipLen + apPwdLen));
      }

      this.mDataCodes.add(4, new DataCode(var20, 4));
      bssidInsertIndex = 5;

      for(int var21 = 0; var21 < apBssid.length; ++var21) {
         int index = totalLen + var21;
         char c = ByteUtil.convertByte2Uint8(apBssid[var21]);
         DataCode dc = new DataCode(c, index);
         if(bssidInsertIndex >= this.mDataCodes.size()) {
            this.mDataCodes.add(dc);
         } else {
            this.mDataCodes.add(bssidInsertIndex, dc);
         }

         bssidInsertIndex += 4;
      }

   }

   public byte[] getBytes() {
      byte[] datumCode = new byte[this.mDataCodes.size() * 6];
      int index = 0;
      Iterator var3 = this.mDataCodes.iterator();

      while(var3.hasNext()) {
         DataCode dc = (DataCode)var3.next();
         byte[] var5 = dc.getBytes();
         int var6 = var5.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            byte b = var5[var7];
            datumCode[index++] = b;
         }
      }

      return datumCode;
   }

   public String toString() {
      StringBuilder sb = new StringBuilder();
      byte[] dataBytes = this.getBytes();
      byte[] var3 = dataBytes;
      int var4 = dataBytes.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         byte dataByte = var3[var5];
         String hexString = ByteUtil.convertByte2HexString(dataByte);
         sb.append("0x");
         if(hexString.length() == 1) {
            sb.append("0");
         }

         sb.append(hexString).append(" ");
      }

      return sb.toString();
   }

   public char[] getU8s() {
      byte[] dataBytes = this.getBytes();
      int len = dataBytes.length / 2;
      char[] dataU8s = new char[len];

      for(int i = 0; i < len; ++i) {
         byte high = dataBytes[i * 2];
         byte low = dataBytes[i * 2 + 1];
         dataU8s[i] = (char)(ByteUtil.combine2bytesToU16(high, low) + 40);
      }

      return dataU8s;
   }
}
