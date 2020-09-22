package com.framework.loippi.dao.cart;

import com.framework.loippi.entity.cart.ShopCartExchange;
import com.framework.loippi.mybatis.dao.GenericDao;
import com.framework.loippi.mybatis.paginator.domain.PageBounds;
import com.framework.loippi.vo.cart.ShopCartExchangeVo;

import java.util.List;

public interface ShopCartExchangeDao extends GenericDao<ShopCartExchange, Long> {
    List<ShopCartExchangeVo> listShopCartExchangeVoWithGoodsAndSpec(Object parameter, PageBounds pageBounds);

    List<ShopCartExchangeVo> listShopCartExchangeVoWithGoodsAndSpecByIds(Long[] toArray);
}
