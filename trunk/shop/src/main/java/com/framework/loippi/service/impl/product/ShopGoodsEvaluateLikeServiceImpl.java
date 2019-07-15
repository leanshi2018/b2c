package com.framework.loippi.service.impl.product;

import com.framework.loippi.service.impl.GenericServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.framework.loippi.dao.product.ShopGoodsEvaluateLikeDao;
import com.framework.loippi.entity.product.ShopGoodsEvaluateLike;
import com.framework.loippi.service.product.ShopGoodsEvaluateLikeService;

/**
 * SERVICE - ShopGoodsLike(商品评价点赞表)
 *
 * @author zijing
 * @version 2.0
 */
@Service
public class ShopGoodsEvaluateLikeServiceImpl extends GenericServiceImpl <ShopGoodsEvaluateLike, Long> implements ShopGoodsEvaluateLikeService {

    @Autowired
    private ShopGoodsEvaluateLikeDao shopGoodsLikeDao;


    @Autowired
    public void setGenericDao() {
        super.setGenericDao(shopGoodsLikeDao);
    }

}
