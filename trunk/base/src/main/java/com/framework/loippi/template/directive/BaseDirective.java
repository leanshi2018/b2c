package com.framework.loippi.template.directive;

import com.framework.loippi.utils.FreemarkerUtils;
import freemarker.core.Environment;
import freemarker.template.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 模板指令 - 基类
 * 
 * @author Mounate Yan。
 * @version 1.0
 */
public abstract class BaseDirective implements TemplateDirectiveModel {

	/** "使用缓存"参数名称 */
	private static final String USE_CACHE_PARAMETER_NAME = "useCache";
	/** "缓存区域"参数名称 */
	private static final String CACHE_REGION_PARAMETER_NAME = "cacheRegion";
	/** "ID"参数名称 */
	private static final String ID_PARAMETER_NAME = "id";
	/** "数量"参数名称 */
	private static final String COUNT_PARAMETER_NAME = "count";
	/** "排序"参数名称 */
	private static final String ORDER_BY_PARAMETER_NAME = "orderBy";
	/** 排序项分隔符 */
	private static final String ORDER_BY_ITEM_SEPARATOR = "\\s*,\\s*";
	/** 排序字段分隔符 */
	private static final String ORDER_BY_FIELD_SEPARATOR = "\\s+";

	/**
	 * 使用缓存
	 * 
	 * @param env
	 *            Environment
	 * @param params
	 *            参数
	 * @return 使用缓存
	 */
	protected boolean useCache(Environment env, Map<String, TemplateModel> params) throws TemplateModelException {
		Boolean useCache = FreemarkerUtils.getParameter(USE_CACHE_PARAMETER_NAME, Boolean.class, params);
		return useCache != null ? useCache : true;
	}

	/**
	 * 获取缓存区域
	 * 
	 * @param env
	 *            Environment
	 * @param params
	 *            参数
	 * @return 缓存区域
	 */
	protected String getCacheRegion(Environment env, Map<String, TemplateModel> params) throws TemplateModelException {
		String cacheRegion = FreemarkerUtils.getParameter(CACHE_REGION_PARAMETER_NAME, String.class, params);
		return cacheRegion != null ? cacheRegion : env.getTemplate().getName();
	}

	/**
	 * 获取ID
	 * 
	 * @param params
	 *            参数
	 * @return ID
	 */
	protected Long getId(Map<String, TemplateModel> params) throws TemplateModelException {
		return FreemarkerUtils.getParameter(ID_PARAMETER_NAME, Long.class, params);
	}

	/**
	 * 获取数量
	 * 
	 * @param params
	 *            参数
	 * @return 数量
	 */
	protected Integer getCount(Map<String, TemplateModel> params) throws TemplateModelException {
		return FreemarkerUtils.getParameter(COUNT_PARAMETER_NAME, Integer.class, params);
	}

	/**
	 * 设置局部变量
	 * 
	 * @param name
	 *            名称
	 * @param value
	 *            变量值
	 * @param env
	 *            Environment
	 * @param body
	 *            TemplateDirectiveBody
	 */
	protected void setLocalVariable(String name, Object value, Environment env, TemplateDirectiveBody body) throws TemplateException, IOException {
		TemplateModel sourceVariable = FreemarkerUtils.getVariable(name, env);
		FreemarkerUtils.setVariable(name, value, env);
		body.render(env.getOut());
		FreemarkerUtils.setVariable(name, sourceVariable, env);
	}

	/**
	 * 设置局部变量
	 * 
	 * @param variables
	 *            变量
	 * @param env
	 *            Environment
	 * @param body
	 *            TemplateDirectiveBody
	 */
	protected void setLocalVariables(Map<String, Object> variables, Environment env, TemplateDirectiveBody body) throws TemplateException, IOException {
		Map<String, Object> sourceVariables = new HashMap<String, Object>();
		for (String name : variables.keySet()) {
			TemplateModel sourceVariable = FreemarkerUtils.getVariable(name, env);
			sourceVariables.put(name, sourceVariable);
		}
		FreemarkerUtils.setVariables(variables, env);
		body.render(env.getOut());
		FreemarkerUtils.setVariables(sourceVariables, env);
	}

}