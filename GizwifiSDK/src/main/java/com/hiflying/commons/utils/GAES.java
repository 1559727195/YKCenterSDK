package com.hiflying.commons.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class GAES {

   public static final String DEFAULT_KEY_128 = "0123456789abcdef";


   private static byte[] addPadBytes(byte[] plain) {
      int size = plain.length;
      int remainder = size % 16;
      if(remainder == 0) {
         return plain;
      } else {
         int padding = 16 - remainder;
         byte[] result = new byte[size + padding];
         System.arraycopy(plain, 0, result, 0, plain.length);
         return result;
      }
   }

   public static byte[] encrypt(byte[] plain, byte[] key) throws Exception {
      if(key.length != 16) {
         throw new Exception("The key length is not 16 bytes.");
      } else {
         Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
         SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
         byte[] iv = "0123456789abcdef".getBytes("UTF-8");
         IvParameterSpec ivspec = new IvParameterSpec(iv);
         cipher.init(1, skeySpec, ivspec);
         byte[] plainPadded = addPadBytes(plain);
         byte[] encrypted = cipher.doFinal(plainPadded);
         return encrypted;
      }
   }

   public static byte[] decrypt(byte[] coded, byte[] key) throws Exception {
      if(key == null) {
         throw new Exception("Key is null!");
      } else if(key.length != 16) {
         throw new Exception("Key length is not 16 bytes.");
      } else {
         Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
         SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
         byte[] iv = "0123456789abcdef".getBytes("UTF-8");
         IvParameterSpec ivspec = new IvParameterSpec(iv);
         cipher.init(2, skeySpec, ivspec);
         byte[] plain = cipher.doFinal(coded);
         return plain;
      }
   }
}
