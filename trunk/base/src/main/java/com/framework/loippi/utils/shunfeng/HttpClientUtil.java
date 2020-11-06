package com.framework.loippi.utils.shunfeng;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author :ldq
 * @date:2020/11/3
 * @description:dubbo com.framework.loippi.utils.sfwuliu
 */
public class HttpClientUtil {


	private static Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);

	public static String postSFAPI(String url, String xml, String verifyCode) {
		CloseableHttpClient httpClient = HttpClients.createDefault();

		List<NameValuePair> parameters = new ArrayList<>();
		parameters.add(new BasicNameValuePair("xml", xml));
		parameters.add(new BasicNameValuePair("verifyCode", verifyCode));
		HttpPost post = postForm(url, new UrlEncodedFormEntity(parameters, StandardCharsets.UTF_8));
		String body = "";
		body = invoke(httpClient, post);

		try {
			httpClient.close();
		} catch (IOException var9) {
			logger.error("HttpClientService post error", var9);
		}

		return body;
	}

	private static String invoke(CloseableHttpClient httpclient, HttpUriRequest httpost) {
		HttpResponse response = sendRequest(httpclient, httpost);
		String body = "";
		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode == 200) {
			body = parseResponse(response);
		}

		return body;
	}

	private static String parseResponse(HttpResponse response) {
		HttpEntity entity = response.getEntity();
		String body = "";

		try {
			if (entity != null) {
				body = EntityUtils.toString(entity);
			}
		} catch (ParseException | IOException var4) {
			logger.error("HttpClientService paseResponse error", var4);
		}

		return body;
	}

	private static HttpResponse sendRequest(CloseableHttpClient httpclient, HttpUriRequest httpost) {
		CloseableHttpResponse response = null;

		try {
			response = httpclient.execute(httpost);
		} catch (IOException var4) {
			logger.error("HttpClientService sendRequest error", var4);
		}

		return response;
	}

	private static HttpPost postForm(String url, StringEntity entity) {
		HttpPost httpPost = new HttpPost(url);
		httpPost.setEntity(entity);
		return httpPost;
	}

	public static String post(String url,String methodPath, Map<String, String> params,String accessToken) throws UnsupportedEncodingException {
		url = ""+url+methodPath;
		CloseableHttpClient httpClient = HttpClients.createDefault();
		RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(5000).setConnectTimeout(5000).setSocketTimeout(60000).build();
		HttpPost httpPost = new HttpPost(url);
		httpPost.setConfig(requestConfig);
		httpPost.addHeader("accessToken",accessToken);
		httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
		List<NameValuePair> paramsList = new ArrayList<>();
		Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();

		for (Map.Entry<String, String> entry : params.entrySet()) {
			paramsList.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
		}

		UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(paramsList, "UTF-8");
		httpPost.setEntity(urlEncodedFormEntity);
		String body = invoke(httpClient, httpPost);

		try {
			httpClient.close();
		} catch (IOException var9) {
			logger.error("HttpClientService post error", var9);
		}

		return body;
	}

	public static String postLogin(String url,String methodPath, Map<String, String> params) throws UnsupportedEncodingException {
		url = ""+url+methodPath;
		CloseableHttpClient httpClient = HttpClients.createDefault();
		RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(5000).setConnectTimeout(5000).setSocketTimeout(60000).build();
		HttpPost httpPost = new HttpPost(url);
		httpPost.setConfig(requestConfig);
		httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
		List<NameValuePair> paramsList = new ArrayList<>();
		Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();

		for (Map.Entry<String, String> entry : params.entrySet()) {
			paramsList.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
		}

		UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(paramsList, "UTF-8");
		httpPost.setEntity(urlEncodedFormEntity);
		String body = invoke(httpClient, httpPost);

		try {
			httpClient.close();
		} catch (IOException var9) {
			logger.error("HttpClientService post error", var9);
		}

		return body;
	}

	public static String acquireDataLogin(String url,String methodPath,String accessToken,String requestBody){
		String result = "";
		url = ""+url+methodPath;
		/*if("".equals(url) ||accessToken==null|| "".equals(accessToken)
				||requestBody==null|| "".equals(requestBody)){
			return result;
		}*/
		if("".equals(url) ||requestBody==null|| "".equals(requestBody)){
			return result;
		}
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpPost post = new HttpPost(url);
		//post.addHeader("accessToken",accessToken);
		post.setHeader("Content-Type", "application/json;charset=UTF-8");
		CloseableHttpResponse httpResponse=null;
		try {
			post.setEntity(new StringEntity(requestBody));
			httpResponse = httpClient.execute(post);
			HttpEntity entity = httpResponse.getEntity();       //获取response的body部分
			result = EntityUtils.toString(entity);
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			closeResource(httpResponse,httpClient);
		}
		return result;
	}

	public static String acquireData(String url,String methodPath,String accessToken,String requestBody){
		String result = "";
		url = ""+url+methodPath;
		if("".equals(url) ||accessToken==null|| "".equals(accessToken)
				||requestBody==null|| "".equals(requestBody)){
			return result;
		}
		if("".equals(url) ||requestBody==null|| "".equals(requestBody)){
			return result;
		}
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpPost post = new HttpPost(url);
		post.addHeader("accessToken",accessToken);
		post.setHeader("Content-Type", "application/json;charset=UTF-8");
		CloseableHttpResponse httpResponse=null;
		try {
			StringEntity stringEntity = new StringEntity(requestBody, Charset.forName("UTF-8"));
			stringEntity.setContentEncoding("UTF-8");
			post.setEntity(stringEntity);
			httpResponse = httpClient.execute(post);
			HttpEntity entity = httpResponse.getEntity();       //获取response的body部分
			result = EntityUtils.toString(entity);
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			closeResource(httpResponse,httpClient);
		}
		return result;
	}

	//释放资源
	private static void closeResource(CloseableHttpResponse httpResponse, CloseableHttpClient httpClient){
		try {
			if(httpResponse!=null){
				httpResponse.close();
			}
			httpClient.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
