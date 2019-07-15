package com.framework.loippi.service.impl.activity;

import com.framework.loippi.consts.ActivityStatus;
import com.framework.loippi.consts.Constants;
import com.framework.loippi.dao.activity.ShopActivityDao;
import com.framework.loippi.dao.activity.ShopActivityGoodsDao;
import com.framework.loippi.dao.activity.ShopActivityGoodsSpecDao;
import com.framework.loippi.entity.activity.ShopActivityGoods;
import com.framework.loippi.entity.activity.ShopActivity;
import com.framework.loippi.entity.activity.ShopActivityGoodsSpec;
import com.framework.loippi.entity.product.ShopGoods;
import com.framework.loippi.enus.ActivityTypeEnus;
import com.framework.loippi.pojo.activity.ShopGoodSpec;
import com.framework.loippi.result.common.goods.GoodsListResult;
import com.framework.loippi.service.TwiterIdService;
import com.framework.loippi.service.activity.ShopActivityGoodsService;
import com.framework.loippi.service.activity.ShopActivityService;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.product.ShopGoodsService;
import com.framework.loippi.support.Pageable;
import com.framework.loippi.utils.JacksonUtil;
import com.framework.loippi.utils.Paramap;
import com.framework.loippi.utils.StringUtil;
import com.framework.loippi.vo.ObjectUnionVO;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * SERVICE - ShopActivityGoods(活动商品规格)
 *
 * @author longbh
 * @version 2.0
 */
@Service
public class ShopActivityGoodsServiceImpl extends GenericServiceImpl<ShopActivityGoods, Long> implements ShopActivityGoodsService {

    @Autowired
    private ShopActivityGoodsDao ShopActivityGoodsDao;
    @Autowired
    private ShopActivityGoodsSpecDao shopActivityGoodsSpecDao;
    @Autowired
    private ShopGoodsService shopGoodsService;
    @Autowired
    private TwiterIdService twiterIdService;
    @Autowired
    private ShopActivityDao shopActivityDao;

    @Autowired
    public void setGenericDao() {
        super.setGenericDao(ShopActivityGoodsDao);
    }

    @Override
    public List<GoodsListResult> findAndAddAtiInfo(List<ShopGoods> shopGoodsList, String prefix) {
        List<Long> goodsList = new ArrayList<>();
        for (ShopGoods itemGoods : shopGoodsList) {
            goodsList.add(itemGoods.getId());
        }

        if (goodsList.size() == 0) {
            return new ArrayList<>();
        }

        List<ShopActivityGoods> shopActivityGoodses = ShopActivityGoodsDao.findByParams(Paramap.create().put("goodIdList", goodsList).put("status", 2));
        goodsList.clear();
        for (ShopActivityGoods shopActivityGoods : shopActivityGoodses) {
            goodsList.add(shopActivityGoods.getActivityId());
        }
        Map<Long, ShopActivity> shopActivityMap = new HashMap<>();
        if (goodsList.size()>0){
            List<ShopActivity> shopActivityList=shopActivityDao.findByParams(Paramap.create().put("ids",goodsList));

            for (ShopActivity item:shopActivityList) {
                if (item.getStartTime().getTime()>new Date().getTime()){
                    shopActivityMap.put(item.getId(),item);
                }else{
                    if (item.getEndTime().getTime()<new Date().getTime()){
                        shopActivityMap.put(item.getId(),item);
                    }
                }
            }
        }
        Map<Long, ShopActivityGoods> shopActivityGoodsMap = new HashMap<>();
        for (ShopActivityGoods shopActivityGoods : shopActivityGoodses) {
            if (shopActivityMap.get(shopActivityGoods.getActivityId())==null)
            {
                shopActivityGoodsMap.put(shopActivityGoods.getObjectId(), shopActivityGoods);
            }
        }

        return GoodsListResult.buildGoodsVoList(shopGoodsList, shopActivityGoodsMap, prefix);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        ShopActivityGoods ShopActivityGoods = new ShopActivityGoods();
        ShopActivityGoods.setId(id);
        ShopActivityGoods.setStatus(status);
        ShopActivityGoods.setUpdateTime(new Date());
        ShopActivityGoodsDao.update(ShopActivityGoods);

        //TODo 审核记录
    }

    @Override
    public void saveGoodSpec(Long storeId, ShopActivity shopActivity, Long goodsId, Long id, String specJson, Integer maxOrderBuy, Integer maxUserBuy, String activityPicture, Long screeningsId) {
        List<ShopGoodSpec> specList = JacksonUtil.convertList(specJson, ShopGoodSpec.class);
        //保存商品
        if (id == null) {
            id = twiterIdService.getTwiterId();
            Long stock = 0l;
            for (ShopGoodSpec shopGoodSpec : specList) {
                ShopActivityGoodsSpec shopActivityGoodsSpec = new ShopActivityGoodsSpec();
                shopActivityGoodsSpec.setActivityId(shopActivity.getId());
                shopActivityGoodsSpec.setGoodsId(goodsId);
                shopActivityGoodsSpec.setActivityGoodsId(id);
                shopActivityGoodsSpec.setActivityPrice(new BigDecimal(shopGoodSpec.getPrice()));
                shopActivityGoodsSpec.setActivityStock(StringUtil.toInt(shopGoodSpec.getStorage()));
                shopActivityGoodsSpec.setCreateTime(new Date());
                shopActivityGoodsSpec.setSpecId(Long.valueOf(shopGoodSpec.getId()));
                shopActivityGoodsSpecDao.insert(shopActivityGoodsSpec);
                stock = stock + shopActivityGoodsSpec.getActivityStock();
            }

            ShopActivityGoods ShopActivityGoods = new ShopActivityGoods();
            ShopActivityGoods.setId(id);
            ShopActivityGoods.setActivityId(shopActivity.getId());
            ShopActivityGoods.setActivityType(shopActivity.getActivityType());
            ShopActivityGoods.setObjectId(goodsId);
            ShopActivityGoods.setObjectType(1);
            ShopActivityGoods.setStatus(2);
            ShopActivityGoods.setCreateTime(new Date());
            ShopActivityGoods.setUpdateTime(new Date());
            ShopActivityGoods.setPrice(new BigDecimal(specList.get(0).getPrice()));
            ShopActivityGoods.setMainPicture(activityPicture);
            ShopActivityGoods.setStockNumber(stock);
            ShopActivityGoods.setSaleNumber(0);
            ShopActivityGoodsDao.insert(ShopActivityGoods);
            //保存规格
        } else {
            //删除旧的记录
            shopActivityGoodsSpecDao.deleteByGoodsId(id);
            if (specList.size() > 0) {
                Long stock = 0l;
                for (ShopGoodSpec shopGoodSpec : specList) {
                    ShopActivityGoodsSpec shopActivityGoodsSpec = new ShopActivityGoodsSpec();
                    shopActivityGoodsSpec.setActivityId(shopActivity.getId());
                    shopActivityGoodsSpec.setGoodsId(goodsId);
                    shopActivityGoodsSpec.setActivityGoodsId(id);
                    shopActivityGoodsSpec.setActivityPrice(new BigDecimal(shopGoodSpec.getPrice()));
                    shopActivityGoodsSpec.setActivityStock(StringUtil.toInt(shopGoodSpec.getStorage()));
                    shopActivityGoodsSpec.setCreateTime(new Date());
                    shopActivityGoodsSpec.setSpecId(Long.valueOf(shopGoodSpec.getId()));
                    shopActivityGoodsSpecDao.insert(shopActivityGoodsSpec);
                    stock = stock + shopActivityGoodsSpec.getActivityStock();
                }
                //更新规格信息
                ShopActivityGoods ShopActivityGoods = new ShopActivityGoods();
                ShopActivityGoods.setId(id);
                ShopActivityGoods.setStockNumber(stock);
                ShopActivityGoods.setPrice(new BigDecimal(specList.get(0).getPrice()));
                ShopActivityGoodsDao.update(ShopActivityGoods);
            }
        }
    }

    //查询活动热门商品列表
    @Override
    public Map<Long, List<ShopActivityGoods>> findGoodsByActivityMap(List<Long> activityIds) {
        List<ShopActivityGoods> shopActivityGoodses = ShopActivityGoodsDao.findByParams(Paramap.create().put("activityIds", activityIds).put("status", 2));
        List<Long> goodList = new ArrayList<>();
        for (ShopActivityGoods ShopActivityGoods : shopActivityGoodses) {
            goodList.add(ShopActivityGoods.getObjectId());
        }
        Map<Long, ShopGoods> shopGoodsMap = shopGoodsService.findGoodsMap(goodList);
        Map<Long, List<ShopActivityGoods>> longListMap = new HashMap<>();
        for (ShopActivityGoods ShopActivityGoods : shopActivityGoodses) {
            List<ShopActivityGoods> ShopActivityGoodses = longListMap.get(ShopActivityGoods.getActivityId());
            if (ShopActivityGoodses == null) {
                ShopActivityGoodses = new ArrayList<>();
                longListMap.put(ShopActivityGoods.getActivityId(), ShopActivityGoodses);
            }
            ShopActivityGoods.addShopGoodProperty(shopGoodsMap.get(ShopActivityGoods.getObjectId()));
            ShopActivityGoodses.add(ShopActivityGoods);
        }
        return longListMap;
    }

    @Override
    public List<Long> getSpecIds(String goodSpecIds) {
        List<ShopActivityGoods> speclist = new ArrayList<ShopActivityGoods>();
        //获取店铺下所有商品资源
        speclist=ShopActivityGoodsDao.findActivityGoods(Paramap.create().put("notActivityStatus",ActivityStatus.ACTIVITY_STATUS_PAST));
        List<Long> sgsIds = Lists.newArrayList();
        speclist.forEach(objectUnionVO -> {
            sgsIds.add(objectUnionVO.getObjectId());
        });
        //页面上已有的商品规格
        if(!StringUtil.isEmpty(goodSpecIds)){
            String[] specIds = goodSpecIds.split(",");
            if(specIds!=null&&specIds.length>0){
                for(String item:specIds){
                    if(StringUtil.isEmpty(item))continue;
                    sgsIds.add(Long.parseLong(item));
                }
            }
        }
        if(sgsIds.size()>0){
            return sgsIds;
        }else{
            return null;
        }
    }

    @Override
    public void deleteActivityGoods(Long[] ids) {
        ShopActivityGoodsDao.deleteAll(Paramap.create().put("ids",ids));
        shopActivityGoodsSpecDao.deleteAll(Paramap.create().put("activityGoodsIds",ids));
    }
}
