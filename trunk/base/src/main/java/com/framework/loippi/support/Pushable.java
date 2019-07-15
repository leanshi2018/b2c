package com.framework.loippi.support;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 推送
 * 
 * @author Loippi Team
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pushable {

	/**
	 * 推送类型
	 */
	private int type;

	/**
	 * 推送内容
	 */
	private Object content;
}
