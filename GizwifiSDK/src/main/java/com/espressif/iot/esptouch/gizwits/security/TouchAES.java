package com.espressif.iot.esptouch.gizwits.security;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class TouchAES implements ITouchEncryptor {

   private static final String TRANSFORMATION_DEFAULT = "AES/ECB/PKCS5Padding";
   private final byte[] mKey;
   private final byte[] mIV;
   private final String mTransformation;
   private Cipher mEncryptCipher;
   private Cipher mDecryptCipher;


   public TouchAES(byte[] key) {
      this(key, (byte[])null, "AES/ECB/PKCS5Padding");
   }

   public TouchAES(byte[] key, String transformation) {
      this(key, (byte[])null, transformation);
   }

   public TouchAES(byte[] key, byte[] iv) {
      this(key, iv, "AES/ECB/PKCS5Padding");
   }

   public TouchAES(byte[] key, byte[] iv, String transformation) {
      this.mKey = key;
      this.mIV = iv;
      this.mTransformation = transformation;
      this.mEncryptCipher = this.createEncryptCipher();
      this.mDecryptCipher = this.createDecryptCipher();
   }

   private Cipher createEncryptCipher() {
      Cipher e = null;
      try {
         e = Cipher.getInstance(this.mTransformation);
      } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
         noSuchAlgorithmException.printStackTrace();
      } catch (NoSuchPaddingException noSuchPaddingException) {
         noSuchPaddingException.printStackTrace();
      }
      SecretKeySpec secretKeySpec = new SecretKeySpec(this.mKey, "AES");
         if(this.mIV == null) {
            try {
               e.init(1, secretKeySpec);
            } catch (InvalidKeyException invalidKeyException) {
               invalidKeyException.printStackTrace();
            }
         } else {
            IvParameterSpec parameterSpec = new IvParameterSpec(this.mIV);
            try {
               e.init(1, secretKeySpec, parameterSpec);
            } catch (InvalidAlgorithmParameterException invalidAlgorithmParameterException) {
               invalidAlgorithmParameterException.printStackTrace();
            } catch (InvalidKeyException invalidKeyException) {
               invalidKeyException.printStackTrace();
            }
         }

         return e;
   }

   private Cipher createDecryptCipher() {
      Cipher e = null;
      try {
         e = Cipher.getInstance(this.mTransformation);
      } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
         noSuchAlgorithmException.printStackTrace();
      } catch (NoSuchPaddingException noSuchPaddingException) {
         noSuchPaddingException.printStackTrace();
      }
      SecretKeySpec secretKeySpec = new SecretKeySpec(this.mKey, "AES");
         if(this.mIV == null) {
            try {
               e.init(2, secretKeySpec);
            } catch (InvalidKeyException invalidKeyException) {
               invalidKeyException.printStackTrace();
            }
         } else {
            IvParameterSpec parameterSpec = new IvParameterSpec(this.mIV);
            try {
               e.init(2, secretKeySpec, parameterSpec);
            } catch (InvalidAlgorithmParameterException invalidAlgorithmParameterException) {
               invalidAlgorithmParameterException.printStackTrace();
            } catch (InvalidKeyException invalidKeyException) {
               invalidKeyException.printStackTrace();
            }
         }

         return e;
   }

   public byte[] encrypt(byte[] content) {
      try {
         return this.mEncryptCipher.doFinal(content);
      } catch (BadPaddingException e) {
         e.printStackTrace();
      } catch (IllegalBlockSizeException e) {
         e.printStackTrace();
      }
      return null;
   }

   public void decrypt(byte[] content) {
      try {
          this.mDecryptCipher.doFinal(content);
      } catch (BadPaddingException e) {
         e.printStackTrace();
      } catch (IllegalBlockSizeException e) {
         e.printStackTrace();
      }
   }
}
