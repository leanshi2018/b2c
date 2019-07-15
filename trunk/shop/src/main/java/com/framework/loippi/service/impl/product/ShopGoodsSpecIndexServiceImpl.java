package com.framework.loippi.service.impl.product;

import com.framework.loippi.service.impl.GenericServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.framework.loippi.dao.product.ShopGoodsSpecIndexDao;
import com.framework.loippi.entity.product.ShopGoodsSpecIndex;
import com.framework.loippi.service.product.ShopGoodsSpecIndexService;

/**
 * SERVICE - ShopGoodsSpecIndex(商品与规格对应表)
 *
 * @author zijing
 * @version 2.0
 */
@Service
public class ShopGoodsSpecIndexServiceImpl extends GenericServiceImpl<ShopGoodsSpecIndex, Long> implements ShopGoodsSpecIndexService {

    @Autowired
    private ShopGoodsSpecIndexDao shopGoodsSpecIndexDao;


    @Autowired
    public void setGenericDao() {
        super.setGenericDao(shopGoodsSpecIndexDao);
    }

    @Override
    public void deleteByTypeId(Long id) {
        shopGoodsSpecIndexDao.deleteByTypeId(id);
    }
}
