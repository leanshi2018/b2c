package com.framework.loippi.service.user;

import com.framework.loippi.entity.user.PlusProfit;
import com.framework.loippi.entity.user.RetailProfit;
import com.framework.loippi.service.GenericService;
import com.framework.loippi.utils.Paramap;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

/**
 * SERVICE - PlusProfit(plus会员订单产生奖励积分记录)
 *
 *
 */
public interface PlusProfitService extends GenericService<PlusProfit, Long> {

    void grantPlusProfit();

    List<PlusProfit> findListTimeAsc(Paramap put);

    BigDecimal countProfit(Paramap put);
}
