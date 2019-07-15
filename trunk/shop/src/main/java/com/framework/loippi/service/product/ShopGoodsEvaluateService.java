package com.framework.loippi.service.product;

import com.framework.loippi.entity.product.ShopGoodsEvaluate;
import com.framework.loippi.entity.product.ShopGoodsEvaluateKeywords;
import com.framework.loippi.entity.user.RdMmBasicInfo;

import com.framework.loippi.service.GenericService;
import com.framework.loippi.support.Page;
import com.framework.loippi.support.Pageable;
import com.framework.loippi.utils.Paramap;

import java.util.List;
import java.util.Map;

/**
 * SERVICE - ShopGoodsEvaluate(信誉商品评价表)
 *
 * @author zijing
 * @version 2.0
 */
public interface ShopGoodsEvaluateService extends GenericService<ShopGoodsEvaluate, Long> {

    //保存数据
    String saveEvaluate(ShopGoodsEvaluate evaluateGoods);
    //批量保存数据
    String saveBatchEvaluate(String evaluateJson,RdMmBasicInfo member);

    //额外查询评论数 和 是否点赞
    Page<ShopGoodsEvaluate> findWithExtends(Pageable pager, Long memberId);

    List<ShopGoodsEvaluate> findListWithExtends(Paramap paramap);

    //关联查询评价列表
    Page<ShopGoodsEvaluate> findWithGoodsByPage(Pageable pageable, String prex);

    //统计商品的总积分
    Long findScore(Map<String, Object> params);

    void saveEvaluateLike(Long evalId, Long memberId);

    //根据多个商品id查询商品评价比率信息
    Map<String, String> countCommentRate(List<Long> goodsList,Integer score);
    //根据多个商品id查询商品评价比率信息
    List<ShopGoodsEvaluateKeywords> countGevalContent(List<String> gevalContents,Long goodsId);

}
