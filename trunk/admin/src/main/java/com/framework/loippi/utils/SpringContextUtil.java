package com.framework.loippi.utils;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.DefaultResourceLoader;

import java.io.IOException;

/**
 * 让静态变量保存到Spring ApplicationContext
 * 可在任何代码任何地方任何时候中取出ApplicaitonContext
 */
public class SpringContextUtil implements ApplicationContextAware, DisposableBean {


    private static ApplicationContext applicationContext = null;

    private static Logger logger = LoggerFactory.getLogger(SpringContextUtil.class);

    /**
     * 取得存储在静态变量中的ApplicationContext.
     */
    public static ApplicationContext getApplicationContext() {
        assertContextInjected();
        return applicationContext;
    }

    public static String getRootRealPath(){
        String rootRealPath ="";
        try {
            rootRealPath=getApplicationContext().getResource("").getFile().getAbsolutePath();
        } catch (IOException e) {
            logger.warn("获取系统根目录失败");
        }
        return rootRealPath;
    }

    public static String getResourceRootRealPath(){
        String rootRealPath ="";
        try {
            rootRealPath=new DefaultResourceLoader().getResource("").getFile().getAbsolutePath();
        } catch (IOException e) {
            logger.warn("获取资源根目录失败");
        }
        return rootRealPath;
    }


    /**
     * 从静态变量applicationContext中取得Bean, 自动转型为所赋值对象的类型.
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) {
        assertContextInjected();
        return (T) applicationContext.getBean(name);
    }

    /**
     * 从静态变量applicationContext中取得Bean, 自动转型为所赋值对象的类型.
     */
    public static <T> T getBean(Class<T> requiredType) {
        assertContextInjected();
        return applicationContext.getBean(requiredType);
    }

    /**
     * 清除SpringContextUtil中的ApplicationContext为Null.
     */
    public static void clearHolder() {
        if (logger.isDebugEnabled()){
            logger.debug("清除SpringContextUtil中的ApplicationContext:" + applicationContext);
        }
        applicationContext = null;
    }

    /**
     * 实现ApplicationContextAware接口, 注入Context到静态变量中.
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
//		logger.debug("注入ApplicationContext到SpringContextUtil:{}", applicationContext);

        if (SpringContextUtil.applicationContext != null) {
            logger.info("SpringContextUtil中的ApplicationContext被覆盖, 原有ApplicationContext为:" + SpringContextUtil.applicationContext);
        }

        SpringContextUtil.applicationContext = applicationContext; // NOSONAR
    }

    /**
     * 实现DisposableBean接口, 在Context关闭时清理静态变量.
     */
    @Override
    public void destroy() throws Exception {
        SpringContextUtil.clearHolder();
    }

    /**
     * 检查ApplicationContext不为空.
     */
    private static void assertContextInjected() {
        Validate.validState(applicationContext != null, "applicaitonContext属性未注入, 请在applicationContext.xml中定义SpringContextUtil.");
    }

}
