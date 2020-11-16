package com.framework.loippi.service.user;


import java.util.ArrayList;
import java.util.List;

import com.framework.loippi.entity.user.RdMmAccountInfo;
import com.framework.loippi.entity.user.RdMmBasicInfo;
import com.framework.loippi.entity.user.RdMmRelation;
import com.framework.loippi.service.GenericService;
import com.framework.loippi.utils.Paramap;
import com.framework.loippi.vo.user.UserInfoVo;

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


	RdMmBasicInfo findByMCode(String mCode);

    List<RdMmBasicInfo> findByKeyWord(Paramap put);

	void updatePhoneStatusAndPhoneByMCode(String phone, Integer allInPayPhoneStatus, String mmCode);

    void updatePhoneStatusByMCode(Integer allInPayPhoneStatus, String mmCode);

    /**
     * 注册次店会员
     * @param mmBasicInfo 主店会员基础信息
     * @param mNickName 次店会员昵称
     * @param mmAvatar 默认会员头像
     */
    void addSecondaryUser(RdMmBasicInfo mmBasicInfo, String mNickName, String mmAvatar);

    List<RdMmBasicInfo> findBranch(String mmCode);

    Long countSecondShop(String mainCode);

    void storeBinding(RdMmBasicInfo mainBasic, RdMmBasicInfo secondBasic);

    void whetherFreeze();

    Integer findInvitePlusNum(String mmCode);

    ArrayList<UserInfoVo> findMemberOneMobile(Paramap map);
}
