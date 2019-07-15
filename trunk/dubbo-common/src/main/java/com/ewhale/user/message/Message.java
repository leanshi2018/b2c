package com.ewhale.user.message;

import java.io.Serializable;

/**
 * 消息传输类
 * @author 
 *<p>
 *	该类封装了service返回的状态，通过调用isSuccess方法判断是否成功，成功：true	失败false
 *	getMessage方法返回失败的提示信息
 *	getEneity 返回对应的实体对象，如果为null，表示没有返回实体对象
 *</p>
 */
public class Message<T> implements Serializable {
	
	/**
	 * 用于判断返回的消息是否成功执行
	 */
	private boolean success;
	
	/**
	 * 返回携带的参数
	 */
	private String message;
	
	/**
	 * 返回携带的实体
	 */
	private T eneity;

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public T getEneity() {
		return eneity;
	}

	public void setEneity(T eneity) {
		this.eneity = eneity;
	}
	
}
