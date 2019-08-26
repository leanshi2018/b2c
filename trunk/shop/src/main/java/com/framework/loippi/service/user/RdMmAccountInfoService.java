package com.framework.loippi.service.user;


import com.framework.loippi.entity.user.RdMmAccountInfo;
import com.framework.loippi.entity.user.RdMmAccountLog;
import com.framework.loippi.entity.user.ShopMemberFavorites;
import com.framework.loippi.service.GenericService;

import java.util.List;

/**
 * SERVICE - RdMmAccountInfo(会员账户信息)
 * 
 * @author zijing
 * @version 2.0
 */
public interface RdMmAccountInfoService  extends GenericService<RdMmAccountInfo, Long> {
    /**
     * 保存积分并生成记录日志
     * @param rdMmAccountInfo
     * @param integration
     */
    Integer saveAccountInfo(RdMmAccountInfo rdMmAccountInfo,Integer integration,Integer type,List<RdMmAccountLog> rdMmAccountLogList,RdMmAccountInfo accentMmAccountInfo);

    Integer saveAccountInfoNew(RdMmAccountInfo rdMmAccountInfo, Double integration, int bop, List<RdMmAccountLog> rdMmAccountLogList, RdMmAccountInfo accentMmAccountInfo);
}
