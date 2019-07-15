package com.framework.loippi.service.impl.activity;

import com.framework.loippi.dao.activity.ShopActivityGoodsSpecDao;
import com.framework.loippi.entity.activity.ShopActivityGoodsSpec;
import com.framework.loippi.entity.product.ShopGoodsSpec;
import com.framework.loippi.service.activity.ShopActivityGoodsSpecService;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.product.ShopGoodsSpecService;
import com.framework.loippi.utils.JacksonUtil;
import com.framework.loippi.utils.Paramap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SERVICE - ShopActivityGoodsSpec(活动规格)
 *
 * @author longbh
 * @version 2.0
 */
@Service
public class ShopActivityGoodsSpecServiceImpl extends GenericServiceImpl<ShopActivityGoodsSpec, Long> implements ShopActivityGoodsSpecService {

    @Autowired
    private ShopActivityGoodsSpecDao shopActivityGoodsSpecDao;
    @Autowired
    private ShopGoodsSpecService shopGoodsSpecService;

    @Autowired
    public void setGenericDao() {
        super.setGenericDao(shopActivityGoodsSpecDao);
    }

    @Override
    public Map<Long, List<ShopActivityGoodsSpec>> findMapSpecList(List<Long> idList) {
        List<ShopActivityGoodsSpec> shopActivityGoodsSpecs = shopActivityGoodsSpecDao.findByParams(Paramap.create().put("goodIdList", idList));
        if (shopActivityGoodsSpecs.size() == 0) {
            return new HashMap<>();
        }
        List<Long> specIdList = new ArrayList<>();
        for (ShopActivityGoodsSpec shopActivityGoodsSpec : shopActivityGoodsSpecs) {
            specIdList.add(shopActivityGoodsSpec.getSpecId());
        }
        Map<Long, ShopGoodsSpec> shopGoodsSpecMap = shopGoodsSpecService.findMapSpec(specIdList);
        Map<Long, List<ShopActivityGoodsSpec>> longListMap = new HashMap<>();
        for (ShopActivityGoodsSpec shopActivityGoodsSpec : shopActivityGoodsSpecs) {
            List<ShopActivityGoodsSpec> itemGoodSpec = longListMap.get(shopActivityGoodsSpec.getActivityGoodsId());
            if (itemGoodSpec == null) {
                itemGoodSpec = new ArrayList<>();
            }
            ShopGoodsSpec shopGoodsSpec = shopGoodsSpecMap.get(shopActivityGoodsSpec.getSpecId());
            if (shopGoodsSpec != null) {
                shopActivityGoodsSpec.setSpecValueMap(JacksonUtil.convertStrMap(shopGoodsSpec.getSpecGoodsSpec()));
                //TODo 添加规格信息进入活动规格
            }
            itemGoodSpec.add(shopActivityGoodsSpec);
            longListMap.put(shopActivityGoodsSpec.getActivityGoodsId(), itemGoodSpec);
        }
        return longListMap;
    }

    @Override
    public Map<Long, ShopActivityGoodsSpec> findByAtiGoodsId(Long goodsId) {
        Map<Long, ShopActivityGoodsSpec> map = new HashMap<>();
        List<ShopActivityGoodsSpec> shopActivityGoodsSpecs = shopActivityGoodsSpecDao.findByParams(Paramap.create().put("activityGoodsId", goodsId));
        for (ShopActivityGoodsSpec item : shopActivityGoodsSpecs) {
            map.put(item.getSpecId(), item);
        }
        return map;
    }

    @Override
    public Map<Long, ShopActivityGoodsSpec> findByIds(List<Long> goodsId) {
        Map<Long, ShopActivityGoodsSpec> map = new HashMap<>();
        if (goodsId.size() == 0) {
            return map;
        }
        List<ShopActivityGoodsSpec> shopActivityGoodsSpecs = shopActivityGoodsSpecDao.findByParams(Paramap.create()
                .put("idList", goodsId));
        for (ShopActivityGoodsSpec item : shopActivityGoodsSpecs) {
            map.put(item.getId(), item);
        }
        return map;
    }
}
