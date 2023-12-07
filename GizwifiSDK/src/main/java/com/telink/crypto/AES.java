//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.telink.crypto;

import com.gizwits.gizwifisdk.api.Utils;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public abstract class AES {
    public static boolean Security = true;

    private AES() {
    }

    public static byte[] encrypt(byte[] key, byte[] content) throws NoSuchAlgorithmException, NoSuchPaddingException, UnsupportedEncodingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchProviderException {
        if (!Security) {
            return content;
        } else {
            key = Utils.reverse(key);
            content = Utils.reverse(content);
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
            cipher.init(1, secretKeySpec);
            return cipher.doFinal(content);
        }
    }

    public static byte[] decrypt(byte[] key, byte[] content) throws IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, NoSuchProviderException {
        if (!Security) {
            return content;
        } else {
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
            cipher.init(2, secretKeySpec);
            return cipher.doFinal(content);
        }
    }

    public static byte[] encrypt(byte[] key, byte[] nonce, byte[] plaintext) {
        return !Security ? plaintext : encryptCmd(plaintext, nonce, key);
    }

    public static byte[] decrypt(byte[] key, byte[] nonce, byte[] plaintext) {
        return !Security ? plaintext : decryptCmd(plaintext, nonce, key);
    }

    private static native byte[] encryptCmd(byte[] var0, byte[] var1, byte[] var2);

    private static native byte[] decryptCmd(byte[] var0, byte[] var1, byte[] var2);

    static {
        System.loadLibrary("TelinkCrypto");
    }
}
