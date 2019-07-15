package com.framework.loippi.service.user;


import com.framework.loippi.entity.user.RdMmAccountInfo;
import com.framework.loippi.entity.user.RdMmAddInfo;
import com.framework.loippi.entity.user.RdMmBasicInfo;
import com.framework.loippi.entity.user.RdMmRelation;
import com.framework.loippi.service.GenericService;

import java.util.List;

/**
 * SERVICE - RdMmBasicInfo(会员基础信息)
 * 
 * @author zijing
 * @version 2.0
 */
public interface RdMmBasicInfoService  extends GenericService<RdMmBasicInfo, Long> {


    void addUser(RdMmBasicInfo rdMmBasicInfo, RdMmAccountInfo rdMmAccountInfo, RdMmRelation rdMmRelation,Integer registerType);
    // 登录时验证用户是否存在
    RdMmBasicInfo findMemberExist(RdMmBasicInfo member);
    /**
     *  更新用户表同时更新客户提供的用户表等相关信息
     * @param member  用户信息
     * @param type  更新类型
     */
    void updateMember(RdMmBasicInfo member,Integer type);
    //获取用户分享总数
    int sumShare();
    //批量查询
    List<RdMmBasicInfo> findShopMember(List<String> mmCode);


}
