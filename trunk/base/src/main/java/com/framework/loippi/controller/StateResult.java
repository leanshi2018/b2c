package com.framework.loippi.controller;

/**
 * 用于service执行过程中立即中断处理
 * 
 * @author fuqi@ewhale.cn
 * @date2016年6月6日
 * 
 */
public class StateResult extends RuntimeException {

	private static final long serialVersionUID = -7228910360393246890L;

	public Integer code;
	public String message;
	public Object data;

	public StateResult(Integer code) {
		this.code = code;
	}

	public StateResult(Integer code, String message) {
		super(message);
		this.code = code;
		this.message = message;
	}

	public StateResult(Integer code, String message, Object data) {
		super(message);
		this.code = code;
		this.message = message;
		this.data = data;
	}

	public static void prompt(Integer code) {
		throw new StateResult(code);
	}

	public static void prompt(Integer code, String message) {
		throw new StateResult(code, message);
	}

	public static void prompt(Integer code, String message, Object data) {
		throw new StateResult(code, message, data);
	}

}
