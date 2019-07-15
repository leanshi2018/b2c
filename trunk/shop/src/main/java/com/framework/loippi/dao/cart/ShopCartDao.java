package com.framework.loippi.dao.cart;

import com.framework.loippi.entity.activity.ShopActivityGoodsSpec;
import com.framework.loippi.entity.cart.ShopCart;
import com.framework.loippi.mybatis.dao.GenericDao;
import com.framework.loippi.mybatis.paginator.domain.PageBounds;
import com.framework.loippi.mybatis.paginator.domain.PageList;
import com.framework.loippi.utils.Paramap;

import com.framework.loippi.vo.cart.ShopCartVo;
import org.apache.bcel.verifier.statics.LONG_Upper;

import java.util.List;
import java.util.Map;

/**
 * DAO - ShopCart(购物车数据表)
 *
 * @author zijing
 * @version 2.0
 */
public interface ShopCartDao extends GenericDao<ShopCart, Long> {

    /**
     * 扩张查询商品
     */
    List<ShopCartVo> listShopCartVoWithGoodsAndSpec(Object parameter, PageBounds pageBounds);

    /**
     * 批量查询购物车+商品+商品规格
     */
    List<ShopCartVo> listShopCartVoWithGoodsAndSpecByIds(Long[] carIds);
//
//    List<String> findGoodsCityNameByIds(Long[] carIdLongArr);
//
//    List<Long> findGoodsIds(Long[] idArr);
//
//    List<ShopCart> findShopCartByIds(Map<String,Object> map);

        Long deleteBatch(Map<String, Object> var1);

    void updateBatchSpec(ShopCart shopCart);

}
