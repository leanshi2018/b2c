package com.framework.loippi.utils.wechat.h5.service;

import net.sf.json.JSONObject;

import java.io.IOException;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class SendHttpRequest {

	/**
	 32      *
	 33      * @param url
	 34      * @param jsonParam
	 35      * @return
	 36      */
     public static JSONObject sendGet(String url){

		 CloseableHttpClient httpclient = HttpClients.createDefault();
		 JSONObject jsonResult = null;
		 HttpGet method = new HttpGet(url);
		 try {
				 CloseableHttpResponse result = httpclient.execute(method);
				 if (result.getStatusLine().getStatusCode() == 200) {
						 String str = "";
						 try {
								 str = EntityUtils.toString(result.getEntity());
								 jsonResult = JSONObject.fromObject(str);
							 } catch (Exception e) {
								 System.out.println("get请求提交失败:" + url);
							 }
					 }
			 } catch (IOException e) {
				 System.out.println("get请求提交失败:" + url);
			 }
		 return jsonResult;
	 }
}
