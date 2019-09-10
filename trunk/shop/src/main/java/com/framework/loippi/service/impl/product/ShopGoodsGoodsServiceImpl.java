package com.framework.loippi.service.impl.product;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.framework.loippi.dao.product.ShopGoodsDao;
import com.framework.loippi.entity.product.ShopGoods;
import com.framework.loippi.result.common.goods.GoodsListResult;
import com.framework.loippi.utils.Paramap;
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
    private ShopGoodsDao shopGoodsDao;


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

    @Override
    public List<GoodsListResult> addJoinNum(List<GoodsListResult> build, Long goodsId) {
        ShopGoods shopGoods = shopGoodsDao.find(goodsId);
        if(shopGoods!=null&&shopGoods.getGoodsType()==3){
            ArrayList<GoodsListResult> goodsListResults = new ArrayList<>();
            for (GoodsListResult goodsListResult : build) {
                List<ShopGoodsGoods> goodsGoods = shopGoodsGoodsDao.findByParams(Paramap.create().put("goodId", goodsId).put("combineGoodsId", goodsListResult.getItemId()));
                if(goodsGoods!=null&&goodsGoods.size()>0){
                    goodsListResult.setJoinNum(goodsGoods.get(0).getJoinNum());
                }else {
                    goodsListResult.setJoinNum(1);
                }
                goodsListResults.add(goodsListResult);
            }
            return goodsListResults;
        }
        return build;
    }
}
