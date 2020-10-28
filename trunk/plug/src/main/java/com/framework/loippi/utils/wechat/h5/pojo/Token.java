package com.framework.loippi.utils.wechat.h5.pojo;

import net.sf.json.JSONObject;

import com.framework.loippi.utils.wechat.h5.config.WachatContent;
import com.framework.loippi.utils.wechat.h5.service.SendHttpRequest;

public class Token {
	private static String access_token="";

	private static String jsapi_ticket = "";

	public static int time = 0;

	private static int expires_in = 7200;

	static{
		Thread t = new Thread(new Runnable(){
			public void run(){
				do{
					 time++;
					 try {
							 Thread.sleep(1000);
						 } catch (InterruptedException e) {
							 e.printStackTrace();
						 }
					 }while(true);
			}});
		 t.start();
	}

	 public static String getToken(){
		 if("".equals(access_token)||access_token==null){
				 send();
			 }else if(time>expires_in){
				 //当前token已经失效，从新获取信息
				 send();
			}
		 return access_token;
	 }
     public static String getTicket(){
		         if("".equals(jsapi_ticket)||jsapi_ticket==null){
			             send();
			         }else if(time>expires_in){
			             //当前token已经失效，从新获取信息
			             send();
			         }
		         return jsapi_ticket;
		     }
     private static void send(){
		         String url = WachatContent.server_token_url+"&appid="+WachatContent.appid+"&secret="+WachatContent.appsecret;
		         JSONObject json = SendHttpRequest.sendGet(url);
		         access_token = json.getString("access_token");
		         String ticket_url = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token="+access_token+"&type=jsapi";
		         jsapi_ticket = SendHttpRequest.sendGet(ticket_url).getString("ticket");
		         time = 0;

		     }

}
