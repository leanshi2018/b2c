package com.framework.loippi.template.method;

import com.framework.loippi.utils.web.SpringUtils;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateModelException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 模板方法 - 多语言
 * 
 * @author Mounate Yan。
 * @version 1.0
 */
@Component("messageMethod")
public class MessageMethod implements TemplateMethodModel {

	@SuppressWarnings("rawtypes")
	public Object exec(List arguments) throws TemplateModelException {
		if (arguments != null && !arguments.isEmpty() && arguments.get(0) != null && StringUtils.isNotEmpty(arguments.get(0).toString())) {
			String message = null;
			String code = arguments.get(0).toString();
			if (arguments.size() > 1) {
				Object[] args = arguments.subList(1, arguments.size()).toArray();
				message = SpringUtils.getMessage(code, args);
			} else {
				message = SpringUtils.getMessage(code);
			}
			return new SimpleScalar(message);
		}
		return null;
	}

}