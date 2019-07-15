package com.framework.loippi.dao.product;

import com.framework.loippi.entity.product.ShopGoodsClass;
import com.framework.loippi.mybatis.dao.GenericDao;
import com.framework.loippi.mybatis.paginator.domain.PageBounds;
import com.framework.loippi.mybatis.paginator.domain.PageList;
import com.framework.loippi.vo.goods.ShopGoodsClassVO;

import java.util.List;
import java.util.Map;

/**
 * DAO - ShopGoodsClass(商品分类表)
 *
 * @author zijing
 * @version 2.0
 */
public interface ShopGoodsClassDao extends GenericDao<ShopGoodsClass, Long> {
    /**
     * 根据父id查询分类列表
     */
    List<ShopGoodsClass> findList(Long parentid);

    /**
     * 根据不同条件查询条数，页面验证用
     */
    int findCount(ShopGoodsClass goodsClass);


    List<ShopGoodsClass> findChildClass(Map<String, Object> map);


    public List<ShopGoodsClass> newFindByParams(Map<String, Object> params);


    List<ShopGoodsClass> findChildByParentIds(Map<String, Object> map);

}
