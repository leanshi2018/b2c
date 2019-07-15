package com.framework.loippi.service.product;

import com.framework.loippi.entity.product.ShopGoodsClass;
import com.framework.loippi.service.GenericService;
import com.framework.loippi.support.Page;
import com.framework.loippi.support.Pageable;

import java.util.List;
import java.util.Map;

/**
 * 功能：商品分类
 * 类名：ShopGoodsClassService
 * 日期：2017/7/19  10:41
 * 作者：czl
 * 详细说明：
 * 修改备注:
 */
public interface ShopGoodsClassService extends GenericService<ShopGoodsClass, Long> {

    /**
     * 根据父id查询分类列表
     */
    List<ShopGoodsClass> findList(Long parentid);

    /**
     * 根据不同条件查询条数，页面验证用
     */
    int findCount(ShopGoodsClass goodsClass);


    List<ShopGoodsClass> findByParams2(Map<String, Object> params);

    //转map
    Map<Long, ShopGoodsClass> findToMap(List<Long> ids);

    List<Long> findIdsByParentIds(List<Long> parentIds);

}
