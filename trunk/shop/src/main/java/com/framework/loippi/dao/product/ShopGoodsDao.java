package com.framework.loippi.dao.product;

import com.framework.loippi.entity.product.ShopGoods;
import com.framework.loippi.mybatis.dao.GenericDao;
import com.framework.loippi.mybatis.paginator.domain.PageBounds;
import com.framework.loippi.mybatis.paginator.domain.PageList;
import com.framework.loippi.vo.goods.BrandListVo;
import com.framework.loippi.vo.goods.GoodsExcel;
import com.framework.loippi.vo.goods.ShopGoodsVo;
import com.framework.loippi.vo.stats.StatsCountVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;


/**
 * DAO - ShopGoods(商品表)
 *
 * @author zijing
 * @version 2.0
 */
public interface ShopGoodsDao extends GenericDao<ShopGoods, Long> {

    List<ShopGoods> findByPageCombination(Long GooodsId);

    Long countPageCombination(Long GooodsId);

    void updateAll(Map<String, Object> map);

    void updateGoodsCommentNum(ShopGoods goods);

    List<BrandListVo> countOnSaleByBrand(Map<String, Object> map);

    /**
     * 昨日 前日 上周 上上周, 上月, 上上月商品销售数量统计
     */
    List<StatsCountVo> listStatsCountVo();

    /**
     * 批量操作 取消收藏
     */
    void updateAllGoodsCollect(Map<String, Object> map);
}
