package com.framework.loippi.service.user;

import com.framework.loippi.entity.user.RetailProfit;
import com.framework.loippi.service.GenericService;
import com.framework.loippi.utils.Paramap;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

import com.framework.loippi.entity.user.RetailProfit;
import com.framework.loippi.service.GenericService;
import com.framework.loippi.utils.Paramap;

/**
 * SERVICE - RetailProfit(零售利润记录)
 *
 *
 */
public interface RetailProfitService extends GenericService<RetailProfit, Long> {
    /**
     * 查询当前会员延期发放的零售利润记录
     * @param mmCode
     * @return
     */
    List<RetailProfit> findNoGrantByCode(String mmCode);

    BigDecimal findTotalProfit(HashMap<String, Object> map);

	BigDecimal countProfit(Paramap put);
    BigDecimal findPeriodPay(Paramap put);

    BigDecimal findPeriodNoPay(Paramap put);

    void grantRetail();
}
