package com.espressif.iot.esptouch.gizwits.util;

import java.io.UnsupportedEncodingException;
import java.util.Random;

public class ByteUtil {

   public static final String ESPTOUCH_ENCODING_CHARSET = "UTF-8";


   public static void putString2bytes(byte[] destbytes, String srcString, int destOffset, int srcOffset, int count) {
      for(int i = 0; i < count; ++i) {
         destbytes[count + i] = srcString.getBytes()[i];
      }

   }

   public static byte convertUint8toByte(char uint8) {
      if(uint8 > 255) {
         throw new RuntimeException("Out of Boundary");
      } else {
         return (byte)uint8;
      }
   }

   public static char convertByte2Uint8(byte b) {
      return (char)(b & 255);
   }

   public static char[] convertBytes2Uint8s(byte[] bytes) {
      int len = bytes.length;
      char[] uint8s = new char[len];

      for(int i = 0; i < len; ++i) {
         uint8s[i] = convertByte2Uint8(bytes[i]);
      }

      return uint8s;
   }

   public static void putbytes2Uint8s(char[] destUint8s, byte[] srcBytes, int destOffset, int srcOffset, int count) {
      for(int i = 0; i < count; ++i) {
         destUint8s[destOffset + i] = convertByte2Uint8(srcBytes[srcOffset + i]);
      }

   }

   public static String convertByte2HexString(byte b) {
      char u8 = convertByte2Uint8(b);
      return Integer.toHexString(u8);
   }

   public static String convertU8ToHexString(char u8) {
      return Integer.toHexString(u8);
   }

   public static byte[] splitUint8To2bytes(char uint8) {
      if(uint8 >= 0 && uint8 <= 255) {
         String hexString = Integer.toHexString(uint8);
         byte low;
         byte high;
         if(hexString.length() > 1) {
            high = (byte)Integer.parseInt(hexString.substring(0, 1), 16);
            low = (byte)Integer.parseInt(hexString.substring(1, 2), 16);
         } else {
            high = 0;
            low = (byte)Integer.parseInt(hexString.substring(0, 1), 16);
         }

         byte[] result = new byte[]{high, low};
         return result;
      } else {
         throw new RuntimeException("Out of Boundary");
      }
   }

   public static byte combine2bytesToOne(byte high, byte low) {
      if(high >= 0 && high <= 15 && low >= 0 && low <= 15) {
         return (byte)(high << 4 | low);
      } else {
         throw new RuntimeException("Out of Boundary");
      }
   }

   public static char combine2bytesToU16(byte high, byte low) {
      char highU8 = convertByte2Uint8(high);
      char lowU8 = convertByte2Uint8(low);
      return (char)(highU8 << 8 | lowU8);
   }

   private static byte randomByte() {
      return (byte)(127 - (new Random()).nextInt(256));
   }

   public static byte[] randomBytes(char len) {
      byte[] data = new byte[len];

      for(int i = 0; i < len; ++i) {
         data[i] = randomByte();
      }

      return data;
   }

   public static byte[] genSpecBytes(char len) {
      byte[] data = new byte[len];

      for(int i = 0; i < len; ++i) {
         data[i] = 49;
      }

      return data;
   }

   public static byte[] randomBytes(byte len) {
      char u8 = convertByte2Uint8(len);
      return randomBytes(u8);
   }

   public static byte[] genSpecBytes(byte len) {
      char u8 = convertByte2Uint8(len);
      return genSpecBytes(u8);
   }

   public static String parseBssid(byte[] bssidBytes, int offset, int count) {
      byte[] bytes = new byte[count];
      System.arraycopy(bssidBytes, offset, bytes, 0, count);
      return parseBssid(bytes);
   }

   public static String parseBssid(byte[] bssidBytes) {
      StringBuilder sb = new StringBuilder();
      byte[] var5 = bssidBytes;
      int var6 = bssidBytes.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         byte bssidByte = var5[var7];
         int k = 255 & bssidByte;
         String hexK = Integer.toHexString(k);
         String str = k < 16?"0" + hexK:hexK;
         System.out.println(str);
         sb.append(str);
      }

      return sb.toString();
   }

   public static byte[] getBytesByString(String string) {
      try {
         return string.getBytes("UTF-8");
      } catch (UnsupportedEncodingException var2) {
         throw new IllegalArgumentException("the charset is invalid");
      }
   }

   private static void test_splitUint8To2bytes() {
      byte[] result = splitUint8To2bytes('\u0014');
      if(result[0] == 1 && result[1] == 4) {
         System.out.println("test_splitUint8To2bytes(): pass");
      } else {
         System.out.println("test_splitUint8To2bytes(): fail");
      }

   }

   private static void test_combine2bytesToOne() {
      byte high = 1;
      byte low = 4;
      if(combine2bytesToOne(high, low) == 20) {
         System.out.println("test_combine2bytesToOne(): pass");
      } else {
         System.out.println("test_combine2bytesToOne(): fail");
      }

   }

   private static void test_convertChar2Uint8() {
      byte b1 = 97;
      byte b2 = -128;
      byte b3 = -1;
      if(convertByte2Uint8(b1) == 97 && convertByte2Uint8(b2) == 128 && convertByte2Uint8(b3) == 255) {
         System.out.println("test_convertChar2Uint8(): pass");
      } else {
         System.out.println("test_convertChar2Uint8(): fail");
      }

   }

   private static void test_convertUint8toByte() {
      char c1 = 97;
      char c2 = 128;
      char c3 = 255;
      if(convertUint8toByte(c1) == 97 && convertUint8toByte(c2) == -128 && convertUint8toByte(c3) == -1) {
         System.out.println("test_convertUint8toByte(): pass");
      } else {
         System.out.println("test_convertUint8toByte(): fail");
      }

   }

   private static void test_parseBssid() {
      byte[] b = new byte[]{(byte)15, (byte)-2, (byte)52, (byte)-102, (byte)-93, (byte)-60};
      if(parseBssid(b).equals("0ffe349aa3c4")) {
         System.out.println("test_parseBssid(): pass");
      } else {
         System.out.println("test_parseBssid(): fail");
      }

   }

   public static void main(String[] args) {
      test_convertUint8toByte();
      test_convertChar2Uint8();
      test_splitUint8To2bytes();
      test_combine2bytesToOne();
      test_parseBssid();
   }
}
