package com.framework.loippi.service.product;


import com.framework.loippi.entity.product.ShopGoodsFreightRule;
import com.framework.loippi.service.GenericService;

import java.math.BigDecimal;
import java.util.List;

/**
 * SERVICE - ShopGoodsFreightRule(运费规则表)
 * 
 * @author zijing
 * @version 2.0
 */
public interface ShopGoodsFreightRuleService  extends GenericService<ShopGoodsFreightRule, Long> {
    /**
     * 计算运费优惠
     * @param memberId  用户id
     * @param goodsTotalAmount 订单商品总额
     * @return
     */
    BigDecimal CalculateFreightDiscount(String memberId,BigDecimal goodsTotalAmount);

    void insertBatch(List<ShopGoodsFreightRule> shopGoodsFreightRuleList);

    void updateBatch(List<ShopGoodsFreightRule> shopGoodsFreightRuleList);
}
