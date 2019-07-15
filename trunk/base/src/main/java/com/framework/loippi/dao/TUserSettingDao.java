package com.framework.loippi.dao;

import com.framework.loippi.entity.TUserSetting;
import com.framework.loippi.mybatis.dao.GenericDao;

import java.util.List;
import java.util.Set;

/**
 * DAO - TUserSetting(用户设置数据)
 *
 * @author longbh
 * @version 2.0
 */
public interface TUserSettingDao extends GenericDao<TUserSetting, Long> {

    List<TUserSetting> findList(Set<String> keys);

}
