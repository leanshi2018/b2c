package com.framework.loippi.service.user;

import com.framework.loippi.entity.user.ShopMemberFavorites;
import com.framework.loippi.service.GenericService;
import java.util.List;

import java.util.Map;


/**
 * SERVICE - ShopMemberFavorites(买家收藏表)
 *
 * @author zijing
 * @version 2.0
 */
public interface ShopMemberFavoritesService extends GenericService<ShopMemberFavorites, Long> {

    void deleteFavGoods(Long targetId, Long memberId, Integer favType);

    void saveFavGoods(Long targetId,Long activityId, Long memberId, Integer favType);

    void deleteByIds(String mmCode, Long... ids);

    void batchSaveFavGoods(Long[] idsLong,Long[] activityIdsLong, Long memberId, Integer favType);

    Map<Long,ShopMemberFavorites> findFavoriteMap(List<Long> goodsIds);

}
