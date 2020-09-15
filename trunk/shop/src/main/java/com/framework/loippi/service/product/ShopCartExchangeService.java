package com.framework.loippi.service.product;

import com.framework.loippi.entity.cart.ShopCartExchange;
import com.framework.loippi.entity.user.RdMmAddInfo;
import com.framework.loippi.service.GenericService;
import com.framework.loippi.support.Pageable;
import com.framework.loippi.vo.cart.ShopCartExchangeVo;

import java.util.List;
import java.util.Map;

public interface ShopCartExchangeService extends GenericService<ShopCartExchange, Long> {
    List<ShopCartExchangeVo> listWithGoodsAndSpec(Pageable pageable);

    void saveExchangeCart(Long goodsId, String mmCode, Integer rankId, Integer count, Long specId, int saveTypeAddToCart);

    void updateNum(long cartId, int num, long parseLong);

    void updateNumBatch(Map<Long, Integer> cartIdNumMap, long parseLong);

    Map<String, Object> queryTotalPrice(String cartIds, String mmCode, RdMmAddInfo addr);
}
