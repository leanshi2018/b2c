package com.framework.loippi.service;

import com.framework.loippi.entity.TUserSetting;
import com.framework.loippi.support.Setting;

/**
 * SERVICE - TUserSetting(用户设置数据)
 *
 * @author longbh
 * @version 2.0
 */
public interface TUserSettingService extends GenericService<TUserSetting, Long> {

    Setting get();

    void set(Setting setting);

    Object read(String key);

    void save(String key, String value);

}
