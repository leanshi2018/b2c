package com.framework.loippi.service.impl.product;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.framework.loippi.dao.product.ShopGoodsGoodsDao;
import com.framework.loippi.entity.product.ShopGoodsGoods;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.product.ShopGoodsGoodsService;

/**
 * SERVICE - ShopGoodsGoods(组合商品，商品选择)
 *
 * @author zijing
 * @version 2.0
 */
@Service
public class ShopGoodsGoodsServiceImpl extends GenericServiceImpl<ShopGoodsGoods, Long> implements ShopGoodsGoodsService {

    @Autowired
    private ShopGoodsGoodsDao shopGoodsGoodsDao;


    @Autowired
    public void setGenericDao() {
        super.setGenericDao(shopGoodsGoodsDao);
    }

    @Override
    public List<ShopGoodsGoods> findGoodsGoodsByGoodsId(Long goodsId) {
        return shopGoodsGoodsDao.findGoodsGoodsByGoodsId(goodsId);
    }

    @Override
    public ShopGoodsGoods findGoodsGoods(Map<String, Object> map) {
        return shopGoodsGoodsDao.findGoodsGoods(map);
    }
}
