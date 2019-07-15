package com.framework.loippi.service.impl;

import com.framework.loippi.dao.TUserSettingDao;
import com.framework.loippi.entity.TUserSetting;
import com.framework.loippi.service.TUserSettingService;
import com.framework.loippi.support.Setting;
import com.framework.loippi.utils.CacheUtils;
import com.framework.loippi.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SERVICE - TUserSetting(用户设置数据)
 *
 * @author longbh
 * @version 2.0
 */
@Service
public class TUserSettingServiceImpl extends GenericServiceImpl<TUserSetting, Long> implements TUserSettingService {

    @Autowired
    private TUserSettingDao tUserSettingDao;

    @Autowired
    public void setGenericDao() {
        super.setGenericDao(tUserSettingDao);
    }

    @Override
    public Setting get() {
        Setting setting = (Setting) CacheUtils.get().get("setting");
        if (setting == null) {
            setting = new Setting();

            List<TUserSetting> userSettings = findAll();
            Map<String, String> maps = new HashMap<>();
            for (TUserSetting userSetting : userSettings) {
                maps.put(userSetting.getCKey(), userSetting.getCValue());
            }

            transMap2Bean(maps, setting);
            CacheUtils.get().add("setting", setting, 60);
        }
        return setting;
    }

    @Override
    public void set(Setting setting) {
        List<TUserSetting> userSettings = findAll();
        Map<String, TUserSetting> maps = new HashMap<>();
        for (TUserSetting userSetting : userSettings) {
            maps.put(userSetting.getCKey(), userSetting);
        }

        BeanInfo beanInfo;
        try {
            beanInfo = Introspector.getBeanInfo(setting.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor property : propertyDescriptors) {
                String key = property.getName();
                // 得到property对应的getter方法
                Method getter = property.getReadMethod();
                Object value = getter.invoke(setting);
                TUserSetting siteName = maps.get(key);
                if (siteName == null) {
                    siteName = new TUserSetting();
                    siteName.setCKey(key);
                    siteName.setCValue(value + "");
                    save(siteName);
                } else {
                    if (!StringUtil.isEmpty(value + "")) {
                        siteName.setCValue(value + "");
                        update(siteName);
                    }
                }
            }
        } catch (IntrospectionException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        CacheUtils.get().delete("setting");
    }

    @Override
    public Object read(String key) {
        Map<String, Object> datas = (Map<String, Object>) CacheUtils.get().get("properties");
        if (datas == null) {
            List<TUserSetting> settings = tUserSettingDao.findAll();
            datas = new HashMap<>();
            for (TUserSetting setting : settings) {
                datas.put(setting.getCKey(), setting.getCValue());
            }
        }
        return datas.get(key);
    }

    public void save(String key, String value) {
        Map<String, Object> datas = (Map<String, Object>) CacheUtils.get().get("properties");
        if (datas == null) {
            List<TUserSetting> settings = tUserSettingDao.findAll();
            datas = new HashMap<>();
            for (TUserSetting setting : settings) {
                datas.put(setting.getCKey(), setting.getCValue());
            }
        }
        //更新数据
        datas.put(key, value);
        TUserSetting setting = find("cKey", key);
        if (setting == null) {
            setting = new TUserSetting();
            setting.setCKey(key);
            setting.setCValue(value);
            save(setting);
        } else {
            setting.setCValue(value);
            update(setting);
        }
        CacheUtils.get().add("properties", datas, 300);
    }

    // Map --> Bean 1: 利用Introspector,PropertyDescriptor实现 Map --> Bean
    private void transMap2Bean(Map<String, String> map, Setting obj) {
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor property : propertyDescriptors) {
                String key = property.getName();
                System.out.println(key + "-----------");
                if("class".equals(key)){
                    continue;
                }
                if (map.containsKey(key)) {
                    Object value = map.get(key);
                    // 得到property对应的setter方法
                    Method setter = property.getWriteMethod();
                    Class data = property.getPropertyType();
                    if (data == Integer.class) {
                        if (value == null || "null".equals(value)) {
                            value = 0;
                        }
                        setter.invoke(obj, Integer.valueOf(value.toString()));
                    } else if (data == Boolean.class) {
                        if (value == null || "null".equals(value)) {
                            value = false;
                        }
                        setter.invoke(obj, Boolean.parseBoolean(value.toString()));
                    } else if (data == Double.class) {
                        if (value == null || "null".equals(value)) {
                            value = 0d;
                        }
                        setter.invoke(obj, Double.parseDouble(value.toString()));
                    } else if(data == BigDecimal.class){
                        if (value == null || "null".equals(value)) {
                            value = 0d;
                        }
                        setter.invoke(obj, new BigDecimal(Double.parseDouble(value.toString())));
                    } else {
                        if (value == null || "null".equals(value)) {
                            value = "";
                        }
                        setter.invoke(obj, value.toString());
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("transMap2Bean Error " + e);
        }
        return;
    }

}
