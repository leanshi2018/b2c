package com.framework.loippi.dao.user;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.framework.loippi.entity.user.RdMmBasicInfo;
import com.framework.loippi.mybatis.dao.GenericDao;
import com.framework.loippi.utils.Paramap;

/**
 * DAO - RdMmBasicInfo(会员基础信息)
 * 
 * @author zijing
 * @version 2.0
 */
public interface RdMmBasicInfoDao  extends GenericDao<RdMmBasicInfo, Long> {

    String getMaxMmCode();

    // 登录时验证用户是否存在
    RdMmBasicInfo findMemberExist(RdMmBasicInfo member);

    //获取用户分享总数
    int sumShare();


	RdMmBasicInfo findByMCode(String mmCode);

    List<RdMmBasicInfo> findByKeyWord(Paramap put);

	void updatePhoneStatusAndPhoneByMCode(Map<String, Object> map);

	void updatePhoneStatusByMCode(Map<String, Object> map);

    Long findNewMem(HashMap<String, Object> map);
}
