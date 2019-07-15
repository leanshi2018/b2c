package com.framework.loippi.dao.user;

import com.framework.loippi.entity.user.ShopMemberFavorites;
import com.framework.loippi.mybatis.dao.GenericDao;
import com.framework.loippi.mybatis.paginator.domain.PageBounds;
import com.framework.loippi.mybatis.paginator.domain.PageList;
import com.framework.loippi.vo.fav.MemberGoodsFavVo;

import java.util.List;

/**
 * DAO - ShopMemberFavorites(买家收藏表)
 *
 * @author zijing
 * @version 2.0
 */
public interface ShopMemberFavoritesDao extends GenericDao<ShopMemberFavorites, Long> {

    void deleteFav(ShopMemberFavorites favorites);

    /**
     * 分页查询收藏商品
     * @param var1
     * @param var2
     * @return
     */
    PageList<MemberGoodsFavVo> findGoodsByPage(Object var1, PageBounds var2);

    void insertBatch(List<ShopMemberFavorites> shopMemberFavoritesList);

}
