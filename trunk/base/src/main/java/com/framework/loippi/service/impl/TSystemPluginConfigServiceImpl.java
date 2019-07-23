package com.framework.loippi.service.impl;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.framework.loippi.dao.TSystemPluginConfigDao;
import com.framework.loippi.entity.TSystemPluginConfig;
import com.framework.loippi.service.RedisService;
import com.framework.loippi.service.TSystemPluginConfigService;
import com.framework.loippi.utils.CacheUtils;
import com.framework.loippi.utils.JacksonUtil;

/**
 * SERVICE - TSystemPluginConfig(插件配置)
 *
 * @author zijing
 * @version 2.0
 */
@Service
public class TSystemPluginConfigServiceImpl extends GenericServiceImpl<TSystemPluginConfig, Long> implements TSystemPluginConfigService {

    @Autowired
    private TSystemPluginConfigDao tSystemPluginConfigDao;

    @Resource
    private RedisService  redisService;

    @Autowired
    public void setGenericDao() {
        super.setGenericDao(tSystemPluginConfigDao);
    }

    @Override
    public Map<String, Object> readPlug(String plugId) {
        Map<String, Object> data = (Map<String, Object>) CacheUtils.get().get("plug-" + plugId);
        if (data == null) {
            TSystemPluginConfig systemPluginConfig = find("pluginId", plugId);
            data = JacksonUtil.convertMap(systemPluginConfig.getAttrs());
            CacheUtils.get().add("plug-" + plugId, data,600);
        }
        return data;
    }


    @Override
    public Map<String, Object> readPlugConfig(String plugId) {

        Map<String, Object> data = null;
        try{
            data = (Map<String, Object>) redisService.get("plug-Config" + plugId,Map.class);
        }catch (Exception e){
            e.printStackTrace();
        }

        if (data == null) {
            TSystemPluginConfig systemPluginConfig = find("pluginId", plugId);
            data = JacksonUtil.convertMap(systemPluginConfig.getAttrs());
            try{
                redisService.save("plug-Config",data);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        return data;
    }
}
