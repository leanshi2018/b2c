package com.framework.loippi.service.product;

import com.framework.loippi.entity.product.ShopGoodsLike;
import com.framework.loippi.service.GenericService;

/**
 * SERVICE - ShopGoodsLike(商品评价点赞表)
 * 
 * @author zijing
 * @version 2.0
 */
public interface ShopGoodsLikeService  extends GenericService<ShopGoodsLike, Long> {

    ShopGoodsLike findById(String evalGoodsId, Long id);
}
