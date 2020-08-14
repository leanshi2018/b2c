package com.framework.loippi.service.user;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.framework.loippi.entity.ShopCommonMessage;
import com.framework.loippi.entity.ShopMemberMessage;
import com.framework.loippi.entity.order.ShopOrder;
import com.framework.loippi.entity.user.RdMmAccountInfo;
import com.framework.loippi.entity.user.RdMmAccountLog;
import com.framework.loippi.entity.user.RdMmBasicInfo;
import com.framework.loippi.service.GenericService;

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

    Integer saveAccountInfoNew(RdMmAccountInfo rdMmAccountInfo, Double integration, int bop, List<RdMmAccountLog> rdMmAccountLogList, RdMmAccountInfo accentMmAccountInfo, ArrayList<ShopCommonMessage> shopCommonMessages, ArrayList<ShopMemberMessage> shopMemberMessages);

    void saveAccountInfo2(RdMmAccountInfo rdMmAccountInfo, int pui, List<RdMmAccountLog> rdMmAccountLogList, RdMmAccountInfo accentMmAccountInfo, ShopCommonMessage shopCommonMessage, ShopMemberMessage shopMemberMessage);

	void updateAddBonusBlance(String mmCode, BigDecimal bonusBlance);

	List<RdMmAccountInfo> findByMCode(String mCode);

    List<RdMmAccountInfo> findLastWithdrawalOneHundred(BigDecimal acc);

    void reduceAcc(ShopOrder shopOrder, RdMmAccountInfo accountInfo, BigDecimal acc);

	Long updateStatus(RdMmAccountInfo rdMmAccountInfo);

    void updatePayPassword(RdMmBasicInfo mmBasicInfo, String newpassword);
}
