package com.framework.loippi.service.product;

import com.framework.loippi.entity.product.ShopGoodsEvaluateSensitivity;
import com.framework.loippi.service.GenericService;

/**
 * SERVICE - ShopGoodsEvaluateSensitivity(评价敏感词)
 *
 * @author zijing
 * @version 2.0
 */
public interface ShopGoodsEvaluateSensitivityService extends GenericService<ShopGoodsEvaluateSensitivity, Long> {

    void filterWords(String word);

    boolean hasFilterWords(String word);

}
