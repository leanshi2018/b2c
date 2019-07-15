package com.framework.loippi.utils.codec;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import javax.crypto.Cipher;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.util.Assert;

public final class RSAUtils {
    private static final Provider PROVIDER = new BouncyCastleProvider();
    private static final int KEY_SIZE = 1024;
    public static final String KEY_ALGORITHM = "RSA";
    public static final String SIGNATURE_ALGORITHM = "MD5withRSA";

    private RSAUtils() {
    }

    public static KeyPair generateKeyPair() {
        try {
            KeyPairGenerator e = KeyPairGenerator.getInstance("RSA", PROVIDER);
            e.initialize(1024, new SecureRandom());
            return e.generateKeyPair();
        } catch (NoSuchAlgorithmException var1) {
            var1.printStackTrace();
            return null;
        }
    }

    public static PrivateKey generatePrivateKey(byte[] encodedKey) {
        Assert.notNull(encodedKey);

        try {
            KeyFactory e = KeyFactory.getInstance("RSA", PROVIDER);
            return e.generatePrivate(new PKCS8EncodedKeySpec(encodedKey));
        } catch (NoSuchAlgorithmException var2) {
            throw new RuntimeException(var2.getMessage(), var2);
        } catch (InvalidKeySpecException var3) {
            throw new RuntimeException(var3.getMessage(), var3);
        }
    }

    public static PrivateKey generatePrivateKey(String keyString) {
        Assert.hasText(keyString);
        return generatePrivateKey(Base64.getDecoder().decode(keyString));
    }

    public static PublicKey generatePublicKey(byte[] encodedKey) {
        Assert.notNull(encodedKey);

        try {
            KeyFactory e = KeyFactory.getInstance("RSA", PROVIDER);
            return e.generatePublic(new X509EncodedKeySpec(encodedKey));
        } catch (NoSuchAlgorithmException var2) {
            throw new RuntimeException(var2.getMessage(), var2);
        } catch (InvalidKeySpecException var3) {
            throw new RuntimeException(var3.getMessage(), var3);
        }
    }

    public static PublicKey generatePublicKey(String keyString) {
        Assert.hasText(keyString);
        return generatePublicKey(java.util.Base64.getDecoder().decode(keyString));
    }

    public static String getKeyString(Key key) {
        Assert.notNull(key);
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    public static Key getKey(String type, InputStream inputStream, String password) {
        Assert.hasText(type);
        Assert.notNull(inputStream);

        try {
            KeyStore e = KeyStore.getInstance(type, PROVIDER);
            e.load(inputStream, password != null?password.toCharArray():null);
            String alias = e.aliases().hasMoreElements()?(String)e.aliases().nextElement():null;
            return e.getKey(alias, password != null?password.toCharArray():null);
        } catch (KeyStoreException var5) {
            throw new RuntimeException(var5.getMessage(), var5);
        } catch (NoSuchAlgorithmException var6) {
            throw new RuntimeException(var6.getMessage(), var6);
        } catch (CertificateException var7) {
            throw new RuntimeException(var7.getMessage(), var7);
        } catch (IOException var8) {
            throw new RuntimeException(var8.getMessage(), var8);
        } catch (UnrecoverableKeyException var9) {
            throw new RuntimeException(var9.getMessage(), var9);
        }
    }

    public static byte[] sign(String algorithm, PrivateKey privateKey, byte[] data) {
        Assert.hasText(algorithm);
        Assert.notNull(privateKey);
        Assert.notNull(data);

        try {
            Signature e = Signature.getInstance(algorithm, PROVIDER);
            e.initSign(privateKey);
            e.update(data);
            return e.sign();
        } catch (NoSuchAlgorithmException var4) {
            throw new RuntimeException(var4.getMessage(), var4);
        } catch (InvalidKeyException var5) {
            throw new RuntimeException(var5.getMessage(), var5);
        } catch (SignatureException var6) {
            throw new RuntimeException(var6.getMessage(), var6);
        }
    }

    public static boolean verify(String algorithm, PublicKey publicKey, byte[] sign, byte[] data) {
        Assert.hasText(algorithm);
        Assert.notNull(publicKey);
        Assert.notNull(sign);
        Assert.notNull(data);

        try {
            Signature e = Signature.getInstance(algorithm, PROVIDER);
            e.initVerify(publicKey);
            e.update(data);
            return e.verify(sign);
        } catch (NoSuchAlgorithmException var5) {
            throw new RuntimeException(var5.getMessage(), var5);
        } catch (InvalidKeyException var6) {
            throw new RuntimeException(var6.getMessage(), var6);
        } catch (SignatureException var7) {
            throw new RuntimeException(var7.getMessage(), var7);
        }
    }

    public static boolean verify(String algorithm, Certificate certificate, byte[] sign, byte[] data) {
        Assert.hasText(algorithm);
        Assert.notNull(certificate);
        Assert.notNull(sign);
        Assert.notNull(data);

        try {
            Signature e = Signature.getInstance(algorithm, PROVIDER);
            e.initVerify(certificate);
            e.update(data);
            return e.verify(sign);
        } catch (NoSuchAlgorithmException var5) {
            throw new RuntimeException(var5.getMessage(), var5);
        } catch (InvalidKeyException var6) {
            throw new RuntimeException(var6.getMessage(), var6);
        } catch (SignatureException var7) {
            throw new RuntimeException(var7.getMessage(), var7);
        }
    }

    public static byte[] encrypt(PublicKey publicKey, byte[] data) {
        Assert.notNull(publicKey);
        Assert.notNull(data);

        try {
            Cipher e = Cipher.getInstance("RSA/ECB/PKCS1Padding", PROVIDER);
            e.init(1, publicKey);
            return e.doFinal(data);
        } catch (Exception var3) {
            var3.printStackTrace();
            return null;
        }
    }

    public static String encrypt(PublicKey publicKey, String text) {
        Assert.notNull(publicKey);
        Assert.notNull(text);
        byte[] data = encrypt(publicKey, text.getBytes());
        return data != null?Base64.getEncoder().encodeToString(data):null;
    }

    public static byte[] encrypt(PrivateKey privateKey, byte[] data) {
        Assert.notNull(privateKey);
        Assert.notNull(data);

        try {
            Cipher e = Cipher.getInstance("RSA/ECB/PKCS1Padding", PROVIDER);
            e.init(1, privateKey);
            return e.doFinal(data);
        } catch (Exception var3) {
            var3.printStackTrace();
            return null;
        }
    }

    public static String encrypt(PrivateKey privateKey, String text) {
        Assert.notNull(privateKey);
        Assert.notNull(text);
        byte[] data = encrypt(privateKey, text.getBytes());
        return data != null?Base64.getEncoder().encodeToString(data):null;
    }

    public static byte[] decrypt(PrivateKey privateKey, byte[] data) {
        Assert.notNull(privateKey);
        Assert.notNull(data);

        try {
            Cipher e = Cipher.getInstance("RSA/ECB/PKCS1Padding", PROVIDER);
            e.init(2, privateKey);
            return e.doFinal(data);
        } catch (Exception var3) {
            var3.printStackTrace();
            return null;
        }
    }

    public static String decrypt(PrivateKey privateKey, String text) {
        Assert.notNull(privateKey);
        Assert.notNull(text);
        byte[] data = decrypt(privateKey, Base64.getDecoder().decode(text));
        return data != null?new String(data):null;
    }

    public static byte[] decrypt(PublicKey publicKey, byte[] data) {
        Assert.notNull(publicKey);
        Assert.notNull(data);

        try {
            Cipher e = Cipher.getInstance("RSA/ECB/PKCS1Padding", PROVIDER);
            e.init(2, publicKey);
            return e.doFinal(data);
        } catch (Exception var3) {
            var3.printStackTrace();
            return null;
        }
    }

    public static String decrypt(PublicKey publicKey, String text) throws UnsupportedEncodingException {
        Assert.notNull(publicKey);
        Assert.notNull(text);
        byte[] data = decrypt(publicKey, Base64.getDecoder().decode(text));
        return data != null?new String(data, "UTF8"):null;
    }

    public static String decrypt(String pk, String text) {
        Assert.notNull(pk);
        Assert.notNull(text);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(pk));

        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey e = keyFactory.generatePrivate(spec);
            byte[] data = decrypt(e, Base64.getDecoder().decode(text));
            return data != null?new String(data):null;
        } catch (NoSuchAlgorithmException var6) {
            var6.printStackTrace();
        } catch (InvalidKeySpecException var7) {
            var7.printStackTrace();
        }

        return null;
    }

    public static String sign(byte[] data, PrivateKey privateKey) throws Exception {
        Signature signature = Signature.getInstance("MD5withRSA");
        signature.initSign(privateKey);
        signature.update(data);
        data = Base64.getEncoder().encode(signature.sign());
        return data != null?new String(data):null;
    }

    public static boolean verify(byte[] data, PublicKey publicKey, String sign) throws Exception {
        Signature signature = Signature.getInstance("MD5withRSA");
        signature.initVerify(publicKey);
        signature.update(data);
        return signature.verify(Base64.getDecoder().decode(sign));
    }

    public static Certificate getCertificate(String type, InputStream inputStream) {
        Assert.hasText(type);
        Assert.notNull(inputStream);

        try {
            CertificateFactory e = CertificateFactory.getInstance(type, PROVIDER);
            return e.generateCertificate(inputStream);
        } catch (CertificateException var3) {
            throw new RuntimeException(var3.getMessage(), var3);
        }
    }

    public static void main(String[] args) throws Exception {
        test1();
        test2();
    }

    private static void test1() {
        KeyPair keyPair = generateKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey)keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey)keyPair.getPrivate();
        System.err.println("公钥加密——私钥解密");
        String source = "这是一行要进行RSA加密的原始数据&100";
        System.out.println("\r加密前文字：\r\n" + source);
        byte[] data = source.getBytes();
        byte[] encodedData = encrypt((PublicKey)publicKey, (byte[])data);
        System.out.println("加密后文字：\r\n" + new String(encodedData));
        byte[] decodedData = decrypt((PrivateKey)privateKey, (byte[])encodedData);
        String target = new String(decodedData);
        System.out.println("解密后文字: \r\n" + target);
        System.out.println("----------------------------------------------------");
    }

    private static void test2() throws Exception {
        KeyPair keyPair = generateKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey)keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey)keyPair.getPrivate();
        System.err.println("私钥加密——公钥解密");
        String source = "my Test -----200---测试RSA数字签名的原始数据";
        System.out.println("原文字：\r\n" + source);
        byte[] data = source.getBytes();
        byte[] encodedData = encrypt((PrivateKey)privateKey, (byte[])data);
        System.out.println("加密后：\r\n" + new String(encodedData));
        byte[] decodedData = decrypt((PublicKey)publicKey, (byte[])encodedData);
        String target = new String(decodedData);
        System.out.println("解密后: \r\n" + target);
        System.out.println("----------------------------------------------------");
        System.err.println("私钥签名——公钥验证签名");
        String sign = sign(encodedData, privateKey);
        System.err.println("签名:\r\n" + sign);
        boolean status = verify(encodedData, publicKey, sign);
        System.err.println("验证结果:\r\n" + status);
    }
}
