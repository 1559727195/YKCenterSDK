package com.hiflying.smartlink.v8;

import android.content.Context;

public class AirkissProtocol {

   private Context context;
   private byte[] bMagic;
   private byte[] bPrefix;
   private byte[] bDataSend;
   private int offset = 0;


   public AirkissProtocol(Context context, String ssid, String password, byte[] dataAppend) {
      this.context = context;
      this.init(ssid, password, dataAppend);
   }

   public int getOffset() {
      return this.offset;
   }

   public void setOffset(int offset) {
      this.offset = offset;
   }

   public int[] getByte(int flyTimes) {
      int[] var9;
      if(flyTimes < 40) {
         var9 = new int[]{1, 2, 3, 4};
         return var9;
      } else if(flyTimes < 60) {
         var9 = new int[]{(this.bMagic[0] & 255) + this.offset, (this.bMagic[1] & 255) + this.offset, (this.bMagic[2] & 255) + this.offset, (this.bMagic[3] & 255) + this.offset};
         return var9;
      } else if(flyTimes < 80) {
         var9 = new int[]{(this.bPrefix[0] & 255) + this.offset, (this.bPrefix[1] & 255) + this.offset, (this.bPrefix[2] & 255) + this.offset, (this.bPrefix[3] & 255) + this.offset};
         return var9;
      } else {
         int blocks = this.bDataSend.length / 6;
         if(blocks * 6 < this.bDataSend.length) {
            ++blocks;
         }

         int loop = (flyTimes - 40 - 20 - 20) / blocks;
         if(loop >= 15) {
            return null;
         } else {
            int blockIdx = (flyTimes - 40 - 20 - 20) % blocks;
            int pos = blockIdx * 6;
            int len = 6;
            if(pos + len > this.bDataSend.length) {
               len = this.bDataSend.length - pos;
            }

            int[] ret = new int[len];
            ret[0] = (this.bDataSend[pos] & 255) + this.offset;
            ret[1] = (this.bDataSend[pos + 1] & 255) + this.offset;

            for(int i = 2; i < len; ++i) {
               ret[i] = (this.bDataSend[pos + i] & 255) + 256 + this.offset;
            }

            return ret;
         }
      }
   }

   private void init(String ssid, String pwd, byte[] dataAppend) {
      byte[] bssid = ssid.getBytes();
      byte[] bpwd = pwd.getBytes();
      int bPwdUsrLen = bpwd.length;
      if(dataAppend.length > 0) {
         bPwdUsrLen += dataAppend.length;
      }

      byte[] bPwdUsr = new byte[bPwdUsrLen];
      byte pos = 0;
      System.arraycopy(bpwd, 0, bPwdUsr, pos, bpwd.length);
      int var19 = pos + bpwd.length;
      if(dataAppend.length > 0) {
         System.arraycopy(dataAppend, 0, bPwdUsr, var19, dataAppend.length);
      }

      int dLen = bPwdUsr.length + 1 + bssid.length;
      byte[] bData = new byte[dLen];
      pos = 0;
      System.arraycopy(bPwdUsr, 0, bData, pos, bPwdUsr.length);
      var19 = pos + bPwdUsr.length;
      bData[var19++] = -86;
      System.arraycopy(bssid, 0, bData, var19, bssid.length);
      int ssidCrc = this.crc8(bssid, 0, bssid.length);
      this.bMagic = this.getMagicBytes(dLen, ssidCrc);
      int pLen = bPwdUsr.length;
      byte[] bPLen = this.itob(pLen);
      int plCrc = this.crc8(bPLen, 0, bPLen.length);
      this.bPrefix = this.getPrefixBytes(bPwdUsr.length, plCrc);
      int dblocks = bData.length / 4;
      if(dblocks * 4 < bData.length) {
         ++dblocks;
      }

      this.bDataSend = new byte[dblocks * 2 + bData.length];
      var19 = 0;

      for(int i = 0; i < dblocks; ++i) {
         byte[] block = this.getBlockBytes(bData, i * 4);
         System.arraycopy(block, 0, this.bDataSend, var19, block.length);
         var19 += block.length;
      }

   }

   private byte[] getBlockBytes(byte[] data, int pos) {
      int len = data.length - pos;
      if(len > 4) {
         len = 4;
      }

      byte[] b = new byte[len + 2];
      b[1] = (byte)(pos / 4);
      System.arraycopy(data, pos, b, 2, len);
      int crc = this.crc8(b, 1, len + 1);
      b[0] = (byte)(128 | crc & 127);
      b[1] = (byte)(128 | b[1]);
      return b;
   }

   private byte[] getMagicBytes(int dLen, int ssidCrc) {
      byte[] b = new byte[]{(byte)(0 | dLen >> 4 & 15), (byte)(16 | dLen >> 0 & 15), (byte)(32 | ssidCrc >> 4 & 15), (byte)(48 | ssidCrc >> 0 & 15)};
      return b;
   }

   private byte[] getPrefixBytes(int pLen, int plCrc) {
      byte[] b = new byte[]{(byte)(64 | pLen >> 4 & 15), (byte)(80 | pLen >> 0 & 15), (byte)(96 | plCrc >> 4 & 15), (byte)(112 | plCrc >> 0 & 15)};
      return b;
   }

   private byte[] itob(int i) {
      boolean bLen = false;
      byte var5;
      if(i > 16777215) {
         var5 = 4;
      } else if(i > '\uffff') {
         var5 = 3;
      } else if(i > 255) {
         var5 = 2;
      } else {
         var5 = 1;
      }

      byte[] b = new byte[var5];

      for(int n = 0; n < var5; ++n) {
         b[n] = (byte)(i >> 8 * n & 255);
      }

      return b;
   }

   private int crc8(byte[] bytes, int pos, int len) {
      int crc;
      for(crc = 0; len > 0; --len) {
         int data = bytes[pos] & 255;
         crc = (crc ^ data) & 255;

         for(int i = 0; i < 8; ++i) {
            if((crc & 1) != 0) {
               crc = (crc >> 1 ^ 140) & 255;
            } else {
               crc = crc >> 1 & 255;
            }
         }

         ++pos;
      }

      return crc;
   }
}
