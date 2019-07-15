package com.framework.loippi.service.impl.user;

import com.alibaba.druid.proxy.jdbc.JdbcParameter;
import com.framework.loippi.dao.cart.ShopCartDao;
import com.framework.loippi.dao.product.ShopGoodsDao;
import com.framework.loippi.entity.product.ShopGoods;
import com.framework.loippi.service.TwiterIdService;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.utils.Paramap;

import java.util.*;

import com.framework.loippi.support.Pageable;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.framework.loippi.dao.user.ShopMemberFavoritesDao;
import com.framework.loippi.entity.user.ShopMemberFavorites;
import com.framework.loippi.service.user.ShopMemberFavoritesService;

/**
 * SERVICE - ShopMemberFavorites(买家收藏表)
 *
 * @author zijing
 * @version 2.0
 */
@Service
public class ShopMemberFavoritesServiceImpl extends GenericServiceImpl<ShopMemberFavorites, Long> implements ShopMemberFavoritesService {

    @Autowired
    private ShopMemberFavoritesDao shopMemberFavoritesDao;
    @Autowired
    private ShopGoodsDao shopGoodsDao;
    @Autowired
    private TwiterIdService twiterIdService;
    @Autowired
    private ShopCartDao shopCartDao;

    @Autowired
    public void setGenericDao() {
        super.setGenericDao(shopMemberFavoritesDao);
    }

    /**
     * 取消收藏商品
     *
     * @param targetId
     * @param favType
     * @param memberId
     */
    public void deleteFavGoods(Long targetId, Long memberId, Integer favType) {
        ShopMemberFavorites favorites = new ShopMemberFavorites();
        favorites.setMemberId(memberId);
        favorites.setFavId(targetId);
        favorites.setFavType(favType);
        shopMemberFavoritesDao.deleteFav(favorites);
        if (favType == 1) {
            // 修改商品的的收藏数量
            ShopGoods shopGoods = shopGoodsDao.find(targetId);
            shopGoods.setId(targetId);//商品id
            shopGoods.setGoodsCollect(shopGoods.getGoodsCollect() - 1);//-1减少收藏
            shopGoodsDao.update(shopGoods);
        }
    }

    /**
     * 收藏商品
     *
     * @param targetId
     * @param memberId
     */
    public void saveFavGoods(Long targetId,Long activityId,Long memberId, Integer favType) {
        ShopMemberFavorites favorites = new ShopMemberFavorites();
        favorites.setId(twiterIdService.getTwiterId());
        favorites.setFavId(Long.valueOf(targetId));
        favorites.setMemberId(memberId);
        favorites.setFavType(1);//收藏商品
        favorites.setFavTime(new Date());//收藏时间
        favorites.setActivityId(activityId);
        shopMemberFavoritesDao.insert(favorites);
        if (favType == 1) {
            // 修改商品的的收藏数量
            ShopGoods shopGoods = shopGoodsDao.find(targetId);
            shopGoods.setId(targetId);//商品id
            if (shopGoods.getGoodsCollect() == null) {
                shopGoods.setGoodsCollect(0);
            }
            shopGoods.setGoodsCollect(shopGoods.getGoodsCollect() + 1);//+1增加收藏
            shopGoodsDao.update(shopGoods);
        }
    }


    @Override
    public void deleteByIds(String mmCode, Long... ids) {
        List<ShopMemberFavorites> favorites = shopMemberFavoritesDao.findByParams(Paramap.create().put("ids", ids).put("memberId", mmCode));
        Long[] favId = new Long[favorites.size()];
        int i = 0;
        for (ShopMemberFavorites item : favorites) {
            if (item.getFavType() == 1) {
                favId[i] = Long.valueOf(item.getFavId());
                i++;
            }
        }
        // 修改商品的的收藏数量
        shopGoodsDao.updateAllGoodsCollect(Paramap.create().put("ids", favId).put("type", 2));
        //删除对应记录
        shopMemberFavoritesDao.deleteAll(Paramap.create().put("ids", ids));
    }

    @Override
    public void batchSaveFavGoods(Long[] idsLong,Long[] activityIdsLong, Long memberId, Integer favType) {
        List<ShopMemberFavorites> shopMemberFavoritesList = shopMemberFavoritesDao.findByParams(Paramap.create().put("favIds", idsLong).put("memberId",memberId));
        Map<String, String> map = new HashMap<>();
        List<ShopMemberFavorites> memberFavoritesList = new ArrayList<>();
        if (shopMemberFavoritesList != null && shopMemberFavoritesList.size() > 0) {
            for (ShopMemberFavorites item : shopMemberFavoritesList) {
                map.put(item.getFavId() + "", "1");
            }
        }
        for (int i = 0; i < idsLong.length; i++) {
            if (!"1".equals(map.get(idsLong[i]+""))) {
                ShopMemberFavorites favorites = new ShopMemberFavorites();
                favorites.setId(twiterIdService.getTwiterId());
                favorites.setFavId(Long.valueOf(idsLong[i]));
                favorites.setMemberId(memberId);
                favorites.setFavType(1);//收藏商品
                favorites.setFavTime(new Date());//收藏时间
                favorites.setActivityId(activityIdsLong[i]);
                memberFavoritesList.add(favorites);
            }
        }
        if (memberFavoritesList != null && memberFavoritesList.size() > 0) {
            shopMemberFavoritesDao.insertBatch(memberFavoritesList);
            // 修改商品的的收藏数量
            shopGoodsDao.updateAllGoodsCollect(Paramap.create().put("ids", idsLong).put("type", 1));
        }
        //删除购物车
        shopCartDao.deleteBatch(Paramap.create().put("ids", idsLong).put("memberId", memberId));

    }

    @Override
    public Map<Long, ShopMemberFavorites> findFavoriteMap(List<Long> goodsIds) {
        Map<Long, ShopMemberFavorites> shopMemberFavoritesMap = new HashMap<>();
        if (goodsIds!=null && goodsIds.size()>0){
            List<ShopMemberFavorites> favorites = shopMemberFavoritesDao.findByParams(Paramap.create().put("favIds", goodsIds));

            for (ShopMemberFavorites item : favorites) {
                shopMemberFavoritesMap.put(item.getFavId(), item);
            }
        }

        return shopMemberFavoritesMap;
    }
}
