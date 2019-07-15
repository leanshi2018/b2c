package com.framework.loippi.utils.wechat.mobile.util;


import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.*;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.MessageDigest;
import java.security.SecureRandom;

/**
 * Created by neil on 2017/6/23.
 */
public class XDigestUtils extends DigestUtils {
    public static final String KEY_SHA = "SHA";
    public static final String KEY_MD5 = "MD5";
    public static final String ALGORITHM = "DES";
    public static final String KEY_MAC = "HmacMD5";
    private static final char[] HEX_DIGITS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public XDigestUtils() {
    }

    private static String getFormattedText(byte[] bytes) {
        int len = bytes.length;
        StringBuilder buf = new StringBuilder(len * 2);

        for (int j = 0; j < len; ++j) {
            buf.append(HEX_DIGITS[bytes[j] >> 4 & 15]);
            buf.append(HEX_DIGITS[bytes[j] & 15]);
        }

        return buf.toString();
    }

    public static final String encryptSHA1(String data) {
        if (StringUtils.isEmpty(data)) {
            return null;
        } else {
            try {
                MessageDigest e = MessageDigest.getInstance("SHA1");
                e.update(data.getBytes());
                return getFormattedText(e.digest());
            } catch (Exception var2) {
                throw new RuntimeException(var2);
            }
        }
    }

    public static byte[] decryptBASE64(String key) throws Exception {
        return (new BASE64Decoder()).decodeBuffer(key);
    }

    public static String encryptBASE64(byte[] key) throws Exception {
        return (new BASE64Encoder()).encodeBuffer(key);
    }

    public static byte[] encryptMD5(byte[] data) throws Exception {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.update(data);
        return md5.digest();
    }

    public static byte[] encryptSHA(byte[] data) throws Exception {
        MessageDigest sha = MessageDigest.getInstance("SHA");
        sha.update(data);
        return sha.digest();
    }

    public static String initMacKey() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("HmacMD5");
        SecretKey secretKey = keyGenerator.generateKey();
        return encryptBASE64(secretKey.getEncoded());
    }

    public static byte[] encryptHMAC(byte[] data, String key) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(decryptBASE64(key), "HmacMD5");
        Mac mac = Mac.getInstance(secretKey.getAlgorithm());
        mac.init(secretKey);
        return mac.doFinal(data);
    }

    private static Key toKey(byte[] key) throws Exception {
        DESKeySpec dks = new DESKeySpec(key);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey secretKey = keyFactory.generateSecret(dks);
        return secretKey;
    }

    public static byte[] decrypt(byte[] data, String key) throws Exception {
        Key k = toKey(decryptBASE64(key));
        Cipher cipher = Cipher.getInstance("DES");
        cipher.init(2, k);
        return cipher.doFinal(data);
    }

    public static byte[] encrypt(byte[] data, String key) throws Exception {
        Key k = toKey(decryptBASE64(key));
        Cipher cipher = Cipher.getInstance("DES");
        cipher.init(1, k);
        return cipher.doFinal(data);
    }

    public static String initKey() throws Exception {
        return initKey((String) null);
    }

    public static String initKey(String seed) throws Exception {
        SecureRandom secureRandom = null;
        if (seed != null) {
            secureRandom = new SecureRandom(decryptBASE64(seed));
        } else {
            secureRandom = new SecureRandom();
        }

        KeyGenerator kg = KeyGenerator.getInstance("DES");
        kg.init(secureRandom);
        SecretKey secretKey = kg.generateKey();
        return encryptBASE64(secretKey.getEncoded());
    }
}
