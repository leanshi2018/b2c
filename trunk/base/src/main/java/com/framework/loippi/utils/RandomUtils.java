package com.framework.loippi.utils;



import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;


/**
 * 随机数工具类
 *
 *
 */
public class RandomUtils {
	private static String allChar = "abcdefghigklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ123456789";
	private static String letterChar = "abcdefghigklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static String numberChar = "0123456789";
	private static String allCharNL = "ABCDEFGHJXYZ123456789";
	/**
	 * 获取 时间字符串 + 随机的字符串(包含大小写字母和数字)
	 * @param length 出去时间以外的字符串长度，dateFormat 时间格式(默认为yyyyMMddHHmmssSSSS)
	 * @return
	 */
	public static String getRandomStringWithTime(int length,String dateFormat) {
		if (StringUtil.isEmpty(dateFormat)) {
			dateFormat ="yyyyMMddHHmmssSSSS";
		}
		Date date = new Date();
		DateFormat df = new SimpleDateFormat(dateFormat);
		return df.format(date) + getRandomString(length);
	}
	
	/**
	 * 获取 时间字符串 + 随机的字符串(包含大小写字母和数字)
	 * @param length 出去时间以外的字符串长度，dateFormat 时间格式(默认为yyyyMMddHHmmssSSSS)
	 * @return
	 */
	public static String getRandomNumberStringWithTime(int length,String dateFormat) {
		if (StringUtil.isEmpty(dateFormat)) {
			dateFormat ="yyyyMMddHHmmssSSSS";
		}
		Date date = new Date();
		DateFormat df = new SimpleDateFormat(dateFormat);
		return df.format(date) + getRandomNumberString(length);
	}

	/**
	 * 获取随机的字符串(包含大小写字母和数字)
	 * @param length 字符串长度
	 * @return
	 */
	public static String getRandomString(int length) {
		return getString(length,allChar);
	}
	
	/**
	 * 获取随机的字符串(包含大小写字母和数字)
	 * @param length 字符串长度
	 * @return
	 */
	public static String getRandomStringNL(int length) {
		return getString(length,allCharNL);
	}
	
	/**
	 * 获取随机的字符串(只包含数字)
	 * @param length 字符串长度
	 * @return
	 */
	public static String getRandomNumberString(int length) {
		return getString(length,numberChar);
	}
	
	/**
	 * 获取随机的字符串(只包含大小写字母)
	 * @param length 字符串长度
	 * @return
	 */
	public static String getRandomLetterString(int length) {
		return getString(length,letterChar);
	}
	
	private static String getString(int length,String str) {
		StringBuffer sb = new StringBuffer();
		Random random = new Random();
		for(int i = 0 ; i < length ; i++) {
			sb.append(str.charAt(random.nextInt(str.length())));
		}
		return sb.toString();
	}
	
	public static void main(String[] args) {
	System.err.println(RandomUtils.getRandomString(6).toUpperCase());
	
	}
}
