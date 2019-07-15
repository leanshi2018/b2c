package com.framework.loippi.support;

/**
 * 公共参数
 * 
 * @author Mounate Yan。
 * @version 1.0
 */
public final class CommonAttributes {
	
	/** 日期格式配比 */
	public static final String[] DATE_PATTERNS = new String[] { "yyyy",
			"yyyy-MM", "yyyyMM", "yyyy/MM", "yyyy-MM-dd", "yyyyMMdd",
			"yyyy/MM/dd", "yyyy-MM-dd HH:mm:ss", "yyyyMMddHHmmss", 	"yyyy/MM/dd HH:mm:ss" };

	/** loippi.config.xml文件路径 */
	public static final String FRAMEWORK_XML_PATH = "/loippi.config.xml";

	/** loippi.config.properties文件路径 */
	public static final String FRAMEWORK_PROPERTIES_PATH = "/loippi.config.properties";

	/**
	 * 不可实例化
	 */
	private CommonAttributes() {
	}
}