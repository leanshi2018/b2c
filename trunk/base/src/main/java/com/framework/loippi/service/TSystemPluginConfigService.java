package com.framework.loippi.service;

import com.framework.loippi.entity.TSystemPluginConfig;

import java.util.Map;

/**
 * SERVICE - TSystemPluginConfig(插件配置)
 * 
 * @author zijing
 * @version 2.0
 */
public interface TSystemPluginConfigService  extends GenericService<TSystemPluginConfig, Long> {

    Map<String, Object> readPlug(String plugId);

    public Map<String, Object> readPlugConfig(String plugId);

}
