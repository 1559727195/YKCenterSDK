package com.espressif.iot.esptouch.gizwits.protocol;

import com.espressif.iot.esptouch.gizwits.util.ByteUtil;

public class TouchData {

   private final byte[] mData;


   public TouchData(String string) {
      this.mData = ByteUtil.getBytesByString(string);
   }

   public TouchData(byte[] data) {
      if(data == null) {
         throw new NullPointerException("data can\'t be null");
      } else {
         this.mData = data;
      }
   }

   public byte[] getData() {
      return this.mData;
   }
}
