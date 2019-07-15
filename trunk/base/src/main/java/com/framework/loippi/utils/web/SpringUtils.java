//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.framework.loippi.utils.web;

import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.LocaleResolver;

@Component("springUtils")
@Lazy(false)
public final class SpringUtils implements ApplicationContextAware, DisposableBean {
    private static ApplicationContext applicationContext;

    private SpringUtils() {
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void destroy() throws Exception {
        applicationContext = null;
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static Object getBean(String name) {
        Assert.hasText(name);
        return applicationContext.getBean(name);
    }

    public static <T> T getBean(String name, Class<T> type) {
        Assert.hasText(name);
        Assert.notNull(type);
        return applicationContext.getBean(name, type);
    }

    public static String getMessage(String code, Object... args) {
        LocaleResolver localeResolver = (LocaleResolver)getBean("localeResolver", LocaleResolver.class);
        Locale locale = localeResolver.resolveLocale((HttpServletRequest)null);
        return applicationContext.getMessage(code, args, locale);
    }

    public static Object getBean(HttpServletRequest request, String name) throws BeansException {
        if(applicationContext == null) {
            applicationContext = WebApplicationContextUtils.getWebApplicationContext(request.getSession().getServletContext());
        }

        return applicationContext.getBean(name);
    }
}
