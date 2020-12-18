package com.framework.loippi.service.product;

import java.util.List;
import java.util.Map;

import com.framework.loippi.entity.product.ShopGoodsSpec;
import com.framework.loippi.result.sys.GoodsStocksListView;
import com.framework.loippi.result.sys.SelectGoodsSpec;
import com.framework.loippi.service.GenericService;
import com.framework.loippi.support.Page;
import com.framework.loippi.support.Pageable;

/**
 * SERVICE - ShopGoodsSpec(商品规格表)
 *
 * @author zijing
 * @version 2.0
 */
public interface ShopGoodsSpecService extends GenericService<ShopGoodsSpec, Long> {

    List<ShopGoodsSpec> findListByGoodsId(Long goodsId);

    ShopGoodsSpec findByCondition(ShopGoodsSpec condition);

    /**
     * 根据goodsid批量删除
     *
     * @param goodsId
     */
    void deleteGoodsSpecByGoodsId(Long... goodsId);

    void updateGoodsSpecStorage(ShopGoodsSpec goodsSpec);

    Map<Long, ShopGoodsSpec> findMapSpec(List<Long> idList);


    Page<GoodsStocksListView> findGoodsStocksInfo(Pageable pageable);

	ShopGoodsSpec findByspecGoodsSerial(String specGoodsSerial);

	void updateSpecSaleNum(ShopGoodsSpec goodsSpec);

	List<ShopGoodsSpec> findListBySpecGoodsSerial(String specGoodsSerial);

	Page<SelectGoodsSpec> listGoodsView(Pageable pageable);

	//Map<String, Object> getGoodsSpecBySpecId(Long goodsId, Long goodsSpecId);
}
