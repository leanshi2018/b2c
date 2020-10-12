package com.framework.loippi.dao.product;

import java.util.List;
import java.util.Map;

import com.framework.loippi.entity.product.ShopGoodsSpec;
import com.framework.loippi.mybatis.dao.GenericDao;
import com.framework.loippi.mybatis.paginator.domain.PageBounds;
import com.framework.loippi.mybatis.paginator.domain.PageList;
import com.framework.loippi.result.sys.GoodsStocksListView;

/**
 * DAO - ShopGoodsSpec(商品规格表)
 *
 * @author zijing
 * @version 2.0
 */
public interface ShopGoodsSpecDao extends GenericDao<ShopGoodsSpec, Long> {

    //根据商品id查询规格
    List<ShopGoodsSpec> findListByGoodsId(Long goodsId);

    //查询规格信息
    ShopGoodsSpec findByCondition(ShopGoodsSpec condition);

    // 修改规格
    void updateGoodsSpec(ShopGoodsSpec goodsSpec);

    //删除商品规格信息
    void deleteGoodsSpecByGoodsId(Map<String, Object> params);

    void updateGoodsSpecStorage(ShopGoodsSpec goodsSpec);

    ShopGoodsSpec findByGoodsId(Long goodsId);

    PageList<GoodsStocksListView> findGoodsStocksInfo(Object var1, PageBounds var2);

	ShopGoodsSpec findByspecGoodsSerial(String specGoodsSerial);

	void updateSpecSaleNum(ShopGoodsSpec goodsSpec);

	List<ShopGoodsSpec> findListBySpecGoodsSerial(String specGoodsSerial);
}
