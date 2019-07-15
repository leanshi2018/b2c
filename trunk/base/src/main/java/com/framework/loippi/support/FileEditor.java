package com.framework.loippi.support;

import java.beans.PropertyEditorSupport;

/**
 * 功能： 解决上传空字符, 赋值CommonsMultipartFile, 出错
 * 类名：FileEditor
 * 日期：2018/3/1  9:10
 * 作者：czl
 * 详细说明：
 * 修改备注:
 */
public class FileEditor extends PropertyEditorSupport {

	/** 是否将空转换为null */
	private boolean emptyAsNull;

	public FileEditor(boolean emptyAsNull) {
		this.emptyAsNull = emptyAsNull;
	}

	/**
	 * 获取内容
	 * 
	 * @return 内容
	 */
	@Override
	public String getAsText() {
		Object value = getValue();
		return value != null ? value.toString() : "";
	}

	/**
	 * 设置内容
	 * 
	 * @param text
	 *            内容
	 */
	@Override
	public void setAsText(String text) {
		if (text != null) {
			if (emptyAsNull && "".equals(text)) {
				text = null;
			}
			setValue(text);
		} else {
			setValue(null);
		}
	}

}