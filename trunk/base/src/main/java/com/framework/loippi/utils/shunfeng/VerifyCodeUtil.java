package com.framework.loippi.utils.shunfeng;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import org.springframework.util.Base64Utils;

public class VerifyCodeUtil {
    /**
     * md5加密后转成base64返回
     *
     */
    public static String md5EncryptAndBase64(String str) {
        return Base64.getEncoder().encodeToString(md5Encrypt(str));
    }

    private static byte[] md5Encrypt(String encryptStr) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(encryptStr.getBytes(StandardCharsets.UTF_8));
            return md5.digest();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String getDigest(String str) throws UnsupportedEncodingException {
        return md5EncryptAndBase64(URLEncoder.encode(str, "UTF-8"));
    }

    public static String encryptToMd5(String data){
        MessageDigest md;
        try {
            //利用哈希算法，MD5
            md = MessageDigest.getInstance("MD5");
            //面向字节处理
            byte[] input = data.getBytes("UTF-8"); byte[] output = md.digest(input);
            //将 md5 处理后的 output 结果利用 Base64 转成原有的字符串,不会乱码
            String str = Base64Utils.encodeToString(output);
            return str;
        } catch (NoSuchAlgorithmException e) {
            return "";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }

    }

}