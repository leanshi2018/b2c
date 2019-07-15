package com.framework.loippi.service.impl.product;

import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.utils.Paramap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.framework.loippi.dao.product.ShopGoodsBrandDao;
import com.framework.loippi.entity.product.ShopGoodsBrand;
import com.framework.loippi.service.product.ShopGoodsBrandService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SERVICE - ShopGoodsBrand(品牌表)
 *
 * @author zijing
 * @version 2.0
 */
@Service
public class ShopGoodsBrandServiceImpl extends GenericServiceImpl<ShopGoodsBrand, Long> implements ShopGoodsBrandService {

    @Autowired
    private ShopGoodsBrandDao shopGoodsBrandDao;


    @Autowired
    public void setGenericDao() {
        super.setGenericDao(shopGoodsBrandDao);
    }

    @Override
    public List<ShopGoodsBrand> findByClassId(Long classId) {
        return shopGoodsBrandDao.findByClassId(classId);
    }

    @Override
    public List<ShopGoodsBrand> findByClassKey(String keyword) {
        return shopGoodsBrandDao.findByClassKey(keyword);
    }

    @Override
    public Map<Long, ShopGoodsBrand> findToMap(List<Long> ids) {
        if (ids.size() == 0) {
            return new HashMap<>();
        }
        List<ShopGoodsBrand> goodsClasses = shopGoodsBrandDao.findByParams(Paramap.create().put("brandIds", ids));
        Map<Long, ShopGoodsBrand> mapGoodsClass = new HashMap<>();
        for (ShopGoodsBrand item : goodsClasses) {
            mapGoodsClass.put(item.getId(), item);
        }
        return mapGoodsClass;
    }

}
