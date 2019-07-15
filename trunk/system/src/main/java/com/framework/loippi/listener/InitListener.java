package com.framework.loippi.listener;

import javax.annotation.Resource;
import javax.servlet.ServletContext;

import com.framework.loippi.service.TUserSettingService;
import com.framework.loippi.utils.SettingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletContextAware;

import com.framework.loippi.service.StaticService;

/**
 * Listener - 初始化
 *
 * @author Mounate Yan。
 * @version 1.0
 */
@Component("initListener")
public class InitListener implements ServletContextAware, ApplicationListener<ContextRefreshedEvent> {


    /**
     * logger
     */
    private static Logger logger = LoggerFactory.getLogger(InitListener.class);

    /**
     * servletContext
     */
    private ServletContext servletContext;

    @Value("${system.version}")
    private String systemVersion;
    @Resource(name = "staticServiceImpl")
    private StaticService staticService;
    @Resource
    private TUserSettingService tUserSettingService;

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        if (servletContext != null && contextRefreshedEvent.getApplicationContext().getParent() == null) {

            String info = "I|n|i|t|i|a|l|i|z|i|n|g| |L|o|i|p|p|i |F|r|a|m|e|w|o|r|k|!" + systemVersion;
            logger.info(info.replace("|", ""));
            SettingUtils.set(tUserSettingService.get());
            //生成后台JS文件
            staticService.buildOther();

        }
    }

}