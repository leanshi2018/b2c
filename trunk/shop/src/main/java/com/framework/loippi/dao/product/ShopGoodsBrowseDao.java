package com.framework.loippi.dao.product;

import com.framework.loippi.entity.product.ShopGoodsBrowse;
import com.framework.loippi.mybatis.dao.GenericDao;
import com.framework.loippi.support.Page;
import com.framework.loippi.support.Pageable;

import java.util.List;
import java.util.Map;

/**
 * DAO - ShopGoodsBrowse(浏览记录表)
 * 
 * @author zijing
 * @version 2.0
 */
public interface ShopGoodsBrowseDao  extends GenericDao<ShopGoodsBrowse, Long> {
    /***
     * 根据条件删除足迹
     * @param var1
     * @return
     */
    Long deleteMemberFavorites(Map<String, Object> var1);

    Page<ShopGoodsBrowse> findFootById(Pageable pager);

    List<ShopGoodsBrowse> findFootByIdAndTime(Map<String, Object> params);
}
