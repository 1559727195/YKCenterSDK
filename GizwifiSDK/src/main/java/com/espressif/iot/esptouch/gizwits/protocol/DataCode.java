package com.espressif.iot.esptouch.gizwits.protocol;

import com.espressif.iot.esptouch.gizwits.task.ICodeData;
import com.espressif.iot.esptouch.gizwits.util.ByteUtil;
import com.espressif.iot.esptouch.gizwits.util.CRC8;

public class DataCode implements ICodeData {

   public static final int DATA_CODE_LEN = 6;
   private static final int INDEX_MAX = 127;
   private final byte mSeqHeader;
   private final byte mDataHigh;
   private final byte mDataLow;
   private final byte mCrcHigh;
   private final byte mCrcLow;


   public DataCode(char u8, int index) {
      if(index > 127) {
         throw new RuntimeException("index > INDEX_MAX");
      } else {
         byte[] dataBytes = ByteUtil.splitUint8To2bytes(u8);
         this.mDataHigh = dataBytes[0];
         this.mDataLow = dataBytes[1];
         CRC8 crc8 = new CRC8();
         crc8.update(ByteUtil.convertUint8toByte(u8));
         crc8.update(index);
         byte[] crcBytes = ByteUtil.splitUint8To2bytes((char)((int)crc8.getValue()));
         this.mCrcHigh = crcBytes[0];
         this.mCrcLow = crcBytes[1];
         this.mSeqHeader = (byte)index;
      }
   }

   public byte[] getBytes() {
      byte[] dataBytes = new byte[]{(byte)0, ByteUtil.combine2bytesToOne(this.mCrcHigh, this.mDataHigh), (byte)1, this.mSeqHeader, (byte)0, ByteUtil.combine2bytesToOne(this.mCrcLow, this.mDataLow)};
      return dataBytes;
   }

   public String toString() {
      StringBuilder sb = new StringBuilder();
      byte[] dataBytes = this.getBytes();

      for(int i = 0; i < 6; ++i) {
         String hexString = ByteUtil.convertByte2HexString(dataBytes[i]);
         sb.append("0x");
         if(hexString.length() == 1) {
            sb.append("0");
         }

         sb.append(hexString).append(" ");
      }

      return sb.toString();
   }

   public char[] getU8s() {
      throw new RuntimeException("DataCode don\'t support getU8s()");
   }
}
