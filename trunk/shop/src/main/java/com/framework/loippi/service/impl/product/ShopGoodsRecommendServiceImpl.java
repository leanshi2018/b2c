package com.framework.loippi.service.impl.product;

import com.framework.loippi.service.impl.GenericServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.framework.loippi.dao.product.ShopGoodsRecommendDao;
import com.framework.loippi.entity.product.ShopGoodsRecommend;
import com.framework.loippi.service.product.ShopGoodsRecommendService;

/**
 * SERVICE - ShopGoodsRecommend(商品推荐)
 *
 * @author longbh
 * @version 2.0
 */
@Service
public class ShopGoodsRecommendServiceImpl extends GenericServiceImpl<ShopGoodsRecommend, Long> implements ShopGoodsRecommendService {

    @Autowired
    private ShopGoodsRecommendDao shopGoodsRecommendDao;


    @Autowired
    public void setGenericDao() {
        super.setGenericDao(shopGoodsRecommendDao);
    }
}
