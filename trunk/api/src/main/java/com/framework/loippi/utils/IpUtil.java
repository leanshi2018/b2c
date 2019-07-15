package com.framework.loippi.utils;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.StringTokenizer;

public class IpUtil {
	
	public static String getIpAddr(HttpServletRequest request) { 
		if(request != null){			
	       String ip = request.getHeader("x-forwarded-for"); 
	       if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
	            ip = request.getHeader("Proxy-Client-IP"); 
	       } 
	       if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
	            ip = request.getHeader("WL-Proxy-Client-IP"); 
	       } 
	       if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("X-Forwarded-For");
		   }
			if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("HTTP_CLIENT_IP");
		   }
			if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		   }
			if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getRemoteAddr();
		   }
	       return ip; 
	   }
		return null;
	}
	
	
	/** 
     * 从ip的字符串形式得到字节数组形式 
     * @param ip 字符串形式的ip 
     * @return 字节数组形式的ip 
     */  
    public static byte[] getIpByteArrayFromString(String ip) {  
        byte[] ret = new byte[4];  
        StringTokenizer st = new StringTokenizer(ip, ".");  
        try {  
            ret[0] = (byte)(Integer.parseInt(st.nextToken()) & 0xFF);  
            ret[1] = (byte)(Integer.parseInt(st.nextToken()) & 0xFF);  
            ret[2] = (byte)(Integer.parseInt(st.nextToken()) & 0xFF);  
            ret[3] = (byte)(Integer.parseInt(st.nextToken()) & 0xFF);  
        } catch (Exception e) {
        	e.printStackTrace();
            // LogFactory.log("从ip的字符串形式得到字节数组形式报错", Level.ERROR, e);  
        }  
        return ret;  
    }  
    /** 
     * @param ip ip的字节数组形式 
     * @return 字符串形式的ip 
     */  
    public static String getIpStringFromBytes(byte[] ip) {  
    	StringBuilder sb = new StringBuilder();  
        sb.append(ip[0] & 0xFF);  
        sb.append('.');       
        sb.append(ip[1] & 0xFF);  
        sb.append('.');       
        sb.append(ip[2] & 0xFF);  
        sb.append('.');       
        sb.append(ip[3] & 0xFF);  
        return sb.toString();  
    }  
      
    /** 
     * 根据某种编码方式将字节数组转换成字符串 
     * @param b 字节数组 
     * @param offset 要转换的起始位置 
     * @param len 要转换的长度 
     * @param encoding 编码方式 
     * @return 如果encoding不支持，返回一个缺省编码的字符串 
     */  
    public static String getString(byte[] b, int offset, int len, String encoding) {  
        try {  
            return new String(b, offset, len, encoding);  
        } catch (UnsupportedEncodingException e) {  
            return new String(b, offset, len);  
        }  
    }  
}
