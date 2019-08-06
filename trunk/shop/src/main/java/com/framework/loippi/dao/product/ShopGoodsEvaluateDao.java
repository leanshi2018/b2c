package com.framework.loippi.dao.product;

import com.framework.loippi.entity.product.ShopGoods;
import com.framework.loippi.entity.product.ShopGoodsEvaluate;
import com.framework.loippi.entity.product.ShopGoodsEvaluateKeywords;
import com.framework.loippi.mybatis.dao.GenericDao;
import com.framework.loippi.mybatis.paginator.domain.PageBounds;
import com.framework.loippi.mybatis.paginator.domain.PageList;
import com.framework.loippi.support.Page;
import com.framework.loippi.support.Pageable;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * DAO - ShopGoodsEvaluate(信誉商品评价表)
 *
 * @author zijing
 * @version 2.0
 */
public interface ShopGoodsEvaluateDao extends GenericDao<ShopGoodsEvaluate, Long> {

    ShopGoodsEvaluate findByParentId(Long parentId);

    Long countByGoodsId(Long goodsId);

    PageList<ShopGoodsEvaluate> findWithReplyNumAndLikeCount(Object parameter, PageBounds pageBounds);

    //总分
    Long findScore(Map<String, Object> params);

    void addLikeNum(@Param("evalId") Long evalId, @Param("sub") Integer sub);

    //根据多个商品id查询商品评价比率信息
   List<ShopGoodsEvaluate> countCommentRate(Map<String, Object> var1);
    //根据多个关键字获取关键字评论数量
    List<ShopGoodsEvaluateKeywords> countGevalContent(Map<String, Object> var1);

    PageList<ShopGoodsEvaluate> findByPageTimeDesc(Object parameter, PageBounds pageBounds);
}
