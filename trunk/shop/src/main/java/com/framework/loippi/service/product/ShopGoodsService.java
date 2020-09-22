package com.framework.loippi.service.product;

import java.util.List;
import java.util.Map;

import com.framework.loippi.entity.product.ShopGoods;
import com.framework.loippi.result.common.goods.IdNameDto;
import com.framework.loippi.service.GenericService;
import com.framework.loippi.vo.goods.BrandListVo;
import com.framework.loippi.vo.stats.StatsCountVo;

/**
 * 功能：商品
 * 类名：ShopGoodsService
 * 日期：2017/7/19  11:02
 * 作者：czl
 * 详细说明：
 * 修改备注:
 */
public interface ShopGoodsService extends GenericService<ShopGoods, Long> {

    //新增
    Long saveGoods(ShopGoods goods, String goodsSpecJson, String goodsJson);

    //编辑商品在库房信息
    Integer updateGoods(ShopGoods goods, String goodsSpecJson,String specNameJson);

    //获取关联商品
    List<ShopGoods> findByPageCombination(Long GooodsId);

    Long countPageCombination(Long GooodsId);

    //更新全部
    void updateAll(Map<String, Object> var1);

    //更新销售量
    boolean updateSaleNum(Long GooodsId, Integer saleSum);

    List<BrandListVo> countOnSaleByBrand(Map<String, Object> map);

    // 昨日 前日 上周 上上周, 上月, 上上月商品销售数量统计
    List<StatsCountVo> listStatsCountVo();

    //根据多个商品id查询商品信息
    Map<Long, ShopGoods> findGoodsMap(List<Long> goodsList);

    //批量查询
    Map<Long, List<IdNameDto>> findGoodsBySpecMap(List<Long> goodIds,Integer type);

	List<ShopGoods> findOweGoods();
}
