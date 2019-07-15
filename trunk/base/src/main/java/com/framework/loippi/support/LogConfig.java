package com.framework.loippi.support;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 日志配置
 * 
 * @author Loippi Team
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LogConfig implements Serializable {

	private static final long serialVersionUID = -1108848647938408402L;

	/** 操作名称 */
	private String operation;

	/** 请求URL */
	private String urlPattern;

}