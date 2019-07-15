package com.framework.loippi.service.impl.product;

import com.framework.loippi.dao.product.ShopGoodsGoodsDao;
import com.framework.loippi.entity.product.ShopGoodsGoods;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.product.ShopGoodsGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
