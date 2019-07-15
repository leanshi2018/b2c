package com.framework.loippi.dao.product;

import com.framework.loippi.entity.product.ShopGoodsBrand;
import com.framework.loippi.entity.product.ShopGoodsType;
import com.framework.loippi.mybatis.dao.GenericDao;
import com.framework.loippi.mybatis.paginator.domain.PageBounds;
import com.framework.loippi.mybatis.paginator.domain.PageList;
import com.framework.loippi.support.Page;
import com.framework.loippi.support.Pageable;
import com.framework.loippi.vo.goods.BrandListVo;

import java.util.List;
import java.util.Map;

/**
 * DAO - ShopGoodsType(商品类型表)
 *
 * @author zijing
 * @version 2.0
 */
public interface ShopGoodsTypeDao extends GenericDao<ShopGoodsType, Long> {

    ShopGoodsType selectTypeFetchOther(Long typeId);

    //查询分类列表
    List<ShopGoodsBrand> findBrandList(Map<String, Object> params);

    //仅仅查询简单类型信息
    List<ShopGoodsType> findAllSimple();

    //簡單列表页
    PageList<ShopGoodsType> findSimpleByPage(Object var1, PageBounds var2);

}
