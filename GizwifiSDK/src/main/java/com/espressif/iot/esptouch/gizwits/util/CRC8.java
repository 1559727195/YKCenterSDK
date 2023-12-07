package com.espressif.iot.esptouch.gizwits.util;

import java.util.zip.Checksum;

public class CRC8 implements Checksum {

   private static final short[] crcTable = new short[256];
   private static final short CRC_POLYNOM = 140;
   private static final short CRC_INITIAL = 0;
   private final short init;
   private short value;


   public CRC8() {
      this.value = this.init = 0;
   }

   public void update(byte[] buffer, int offset, int len) {
      for(int i = 0; i < len; ++i) {
         int data = buffer[offset + i] ^ this.value;
         this.value = (short)(crcTable[data & 255] ^ this.value << 8);
      }

   }

   public void update(byte[] buffer) {
      this.update(buffer, 0, buffer.length);
   }

   public void update(int b) {
      this.update(new byte[]{(byte)b}, 0, 1);
   }

   public long getValue() {
      return (long)(this.value & 255);
   }

   public void reset() {
      this.value = this.init;
   }

   static {
      for(int dividend = 0; dividend < 256; ++dividend) {
         int remainder = dividend;

         for(int bit = 0; bit < 8; ++bit) {
            if((remainder & 1) != 0) {
               remainder = remainder >>> 1 ^ 140;
            } else {
               remainder >>>= 1;
            }
         }

         crcTable[dividend] = (short)remainder;
      }

   }
}
