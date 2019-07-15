package com.framework.loippi.service.product;

import com.framework.loippi.entity.product.ShopGoodsBrand;
import com.framework.loippi.entity.product.ShopGoodsType;
import com.framework.loippi.service.GenericService;
import com.framework.loippi.support.Page;
import com.framework.loippi.support.Pageable;

import java.util.List;
import java.util.Map;

/**
 * SERVICE - ShopGoodsType(商品类型表)
 *
 * @author zijing
 * @version 2.0
 */
public interface ShopGoodsTypeService extends GenericService<ShopGoodsType, Long> {

    //保存商品类型
    void saveGoodsType(ShopGoodsType goodsType);

    //修改商品类型
    void updateGoodsType(ShopGoodsType vo);

    ShopGoodsType selectTypeFetchOther(Long typeId);

    List<ShopGoodsBrand> findBrandList(Map<String, Object> params);

    //仅仅查询简单类型信息
    List<ShopGoodsType> findAllSimple();

    Page<ShopGoodsType> findSimpleByPage(Pageable pageable);

}
