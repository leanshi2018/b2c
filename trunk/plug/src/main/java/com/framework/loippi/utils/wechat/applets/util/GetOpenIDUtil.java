package com.framework.loippi.utils.wechat.applets.util;

import net.sf.json.JSONObject;

import java.security.Security;
import java.security.spec.AlgorithmParameterSpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * @author :ldq
 * @date:2020/4/3
 * @description:dubbo com.framework.loippi.utils.wechat.applets.util
 */
public class GetOpenIDUtil {

	// 网页授权接口
	//public final static String GetPageAccessTokenUrl = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";//
	//public final static String GetPageAccessTokenUrl = "https://api.weixin.qq.com/sns/jscode2session?appid=APPID&secret=SECRET&js_code=CODE&grant_type=authorization_code";
	public final static String GetPageAccessTokenUrl = "https://api.weixin.qq.com/sns/jscode2session?appid=APPID&secret=SECRET&js_code=CODE&grant_type=authorization_code";
	 public  static Map<String,Object> oauth2GetOpenid(String appid,String code,String appsecret) {

		 String requestUrl = GetPageAccessTokenUrl.replace("APPID", appid).replace("SECRET", appsecret).replace("CODE", code);
		 HttpClient client = null;
		 Map<String,Object> result =new HashMap<String,Object>();
		 try {
				 client = new DefaultHttpClient();
				 HttpGet httpget = new HttpGet(requestUrl);
				 ResponseHandler<String> responseHandler = new BasicResponseHandler();
				 String response = client.execute(httpget, responseHandler);
				 JSONObject OpenidJSONO= JSONObject.fromObject(response);
				 String openid =String.valueOf(OpenidJSONO.get("openid"));
				 String session_key=String.valueOf(OpenidJSONO.get("session_key"));
				 String unionid=String.valueOf(OpenidJSONO.get("unionid"));
				 String errcode=String.valueOf(OpenidJSONO.get("errcode"));
				 String errmsg=String.valueOf(OpenidJSONO.get("errmsg"));

				 result.put("openid", openid);
				 result.put("sessionKey", session_key);
				 result.put("unionid", unionid);
				 result.put("errcode", errcode);
				result.put("errmsg", errmsg);
			 } catch (Exception e) {
				 e.printStackTrace();
			 } finally {
				 client.getConnectionManager().shutdown();
			 }
		 return result;
	 }

	public static String wxDecrypt (String encrypted, String sessionKey, String iv)throws Exception {
		byte[] encrypData = Base64.decodeBase64(encrypted);
		byte[] ivData = Base64.decodeBase64(iv);
		byte[] sKey = Base64.decodeBase64(sessionKey);
		String decrypt = decrypt(sKey,ivData,encrypData);
		return decrypt;
	}

	public static String decrypt(byte[] key, byte[] iv, byte[] encData) throws Exception {
		AlgorithmParameterSpec ivSpec = new IvParameterSpec(iv);
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding","BC");
		SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
		cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
		//解析解密后的字符串
		return new String(cipher.doFinal(encData),"UTF-8");
	}

}
