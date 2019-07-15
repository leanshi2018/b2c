package com.framework.loippi.service;

import java.io.File;
import java.util.Map;

import org.springframework.core.io.ClassPathResource;

/**
 * Service - 邮件
 * 
 * @author Mounate Yan。
 * @version 1.0
 */
public interface MailService {

	/**
	 * 发送邮件
	 * 
	 * @param smtpFromMail
	 *            发件人邮箱
	 * @param smtpHost
	 *            SMTP服务器地址
	 * @param smtpPort
	 *            SMTP服务器端口
	 * @param smtpUsername
	 *            SMTP用户名
	 * @param smtpPassword
	 *            SMTP密码
	 * @param toMail
	 *            收件人邮箱
	 * @param subject
	 *            主题
	 * @param templatePath
	 *            模板路径
	 * @param model
	 *            数据
	 * @param async
	 *            是否异步
	 */
	void send(String smtpFromMail, String smtpHost, Integer smtpPort, String smtpUsername, String smtpPassword, String toMail, String subject, String templatePath, Map<String, Object> model, boolean async);

	/**
	 * 发送邮件
	 * 
	 * @param toMail
	 *            收件人邮箱
	 * @param subject
	 *            主题
	 * @param templatePath
	 *            模板路径
	 * @param model
	 *            数据
	 * @param async
	 *            是否异步
	 */
	void send(String toMail, String subject, String templatePath, Map<String, Object> model, boolean async);

	/**
	 * 发送邮件(异步)
	 * 
	 * @param toMail
	 *            收件人邮箱
	 * @param subject
	 *            主题
	 * @param templatePath
	 *            模板路径
	 * @param model
	 *            数据
	 */
	void send(String toMail, String subject, String templatePath, Map<String, Object> model);

	/**
	 * 发送邮件(异步)
	 * 
	 * @param toMail
	 *            收件人邮箱
	 * @param subject
	 *            主题
	 * @param templatePath
	 *            模板路径
	 */
	void send(String toMail, String subject, String templatePath);

	/**
	 * 发送测试邮件
	 * 
	 * @param smtpFromMail
	 *            发件人邮箱
	 * @param smtpHost
	 *            SMTP服务器地址
	 * @param smtpPort
	 *            SMTP服务器端口
	 * @param smtpUsername
	 *            SMTP用户名
	 * @param smtpPassword
	 *            SMTP密码
	 * @param toMail
	 *            收件人邮箱
	 */
	void sendTestMail(String smtpFromMail, String smtpHost, Integer smtpPort, String smtpUsername, String smtpPassword, String toMail);
	
	
	/**
	 * 发送邮件
	 * @param subject
	 * @param content
	 * @param toMail
	 * @param async
	 */
	void send(String subject, String content, String[] toMail, boolean async );
	
	/**
	 * 发送邮件
	 * @param subject
	 * @param content
	 * @param toMail
	 * @param async
	 */
	void send(String subject, String content, String[] toMail,ClassPathResource attachment,String attachmentName, boolean async);
	
	/**
	 * 发送邮件
	 * @param subject
	 * @param content
	 * @param toMail
	 * @param async
	 */
	void send(String subject, String content, String[] toMail,File attachment,String attachmentName, boolean async);

}