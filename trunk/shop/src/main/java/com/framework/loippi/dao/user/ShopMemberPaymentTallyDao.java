package com.framework.loippi.dao.user;

import com.framework.loippi.entity.user.ShopMemberPaymentTally;
import com.framework.loippi.mybatis.dao.GenericDao;
import com.framework.loippi.mybatis.paginator.domain.PageBounds;
import com.framework.loippi.mybatis.paginator.domain.PageList;
import com.framework.loippi.vo.activity.ActivityStatisticsVo;
import com.framework.loippi.vo.user.ShopMemberPaymentTallyVo;

import java.math.BigDecimal;
import java.util.List;

/**
 * DAO - ShopMemberPaymentTally(支付流水表)
 *
 * @author zijing
 * @version 2.0
 */
public interface ShopMemberPaymentTallyDao extends GenericDao<ShopMemberPaymentTally, Long> {

    BigDecimal countAmount( );
    BigDecimal countAmountByStore(Long storeId);
    PageList<ShopMemberPaymentTallyVo> findByPageStore(Object parameter, PageBounds pageBounds );
    PageList<ShopMemberPaymentTallyVo> findByPageStoreIncome(Object parameter, PageBounds pageBounds );


    List<ActivityStatisticsVo> statisticsIncomesBystate(ActivityStatisticsVo param);
}
