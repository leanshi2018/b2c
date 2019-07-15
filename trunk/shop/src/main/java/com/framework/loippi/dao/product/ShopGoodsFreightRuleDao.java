package com.framework.loippi.dao.product;


import com.framework.loippi.entity.product.ShopGoodsFreightRule;
import com.framework.loippi.mybatis.dao.GenericDao;

import java.util.List;
import java.util.Map;

/**
 * DAO - ShopGoodsFreightRule(运费规则表)
 * 
 * @author zijing
 * @version 2.0
 */
public interface ShopGoodsFreightRuleDao  extends GenericDao<ShopGoodsFreightRule, Long> {
    /**
     * 求最小值最大的那条数据
     * @param var1
     * @return
     */
    ShopGoodsFreightRule findMaxMinimumOrderAmount(Map<String, Object> var1);

      void insertBatch(List<ShopGoodsFreightRule> shopGoodsFreightRuleList);

    void updateBatch(List<ShopGoodsFreightRule> shopGoodsFreightRuleList);


}
