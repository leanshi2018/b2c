package com.framework.loippi.dao.activity;


import com.framework.loippi.entity.activity.ShopActivityPromotionRule;
import com.framework.loippi.mybatis.dao.GenericDao;
import com.framework.loippi.mybatis.paginator.domain.PageBounds;
import com.framework.loippi.mybatis.paginator.domain.PageList;
import com.framework.loippi.vo.activity.CartCouponVo;
import com.framework.loippi.vo.activity.ShopActivityPromotionRuleVO;
import com.framework.loippi.vo.activity.ShopStoreCouponVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 促销活动规则表DAO接口
 *
 * @author kwg
 * @version 2016-09-01
 */
public interface ShopActivityPromotionRuleDao extends GenericDao<ShopActivityPromotionRule, Long> {

    /**
     * 查询分页促销活动规则表数据
     */
    PageList<ShopActivityPromotionRule> findShopActivityPromotionRulePagerList(Object obj, PageBounds bounds);

    /**
     * 通过id获取单条促销活动规则表数据
     */
    ShopStoreCouponVo findShopActivityPromotionRuleById(Long id);

    /**
     * 根据店铺id 获取当前可以领取的优惠券
     */
    List<ShopStoreCouponVo> listShopCouponVo(Object var1, PageBounds var2);

    /**
     * 通过id删除促销活动规则表数据
     */
    void deleteShopActivityPromotionRuleById(Long id);

    /**
     * 修改促销活动规则表数据
     */
    void updateShopActivityPromotionRule(ShopActivityPromotionRule shopActivityPromotionRule);

    /**
     * 保存促销活动规则表数据
     */
    void saveShopActivityPromotionRule(ShopActivityPromotionRule shopActivityPromotionRule);

    /**
     * 获取所有促销活动规则表数据
     */
    List<ShopActivityPromotionRule> findShopActivityPromotionRuleAllList();


    /**
     * 根据活动id 获取促销规则列表
     */
    List<ShopActivityPromotionRule> findShopActivityPromotionRuleByActivityId(Long activityId);

    /**
     * 通过活动id删除促销活动规则表数据
     */
    void deleteShopActivityPromotionRuleByActivityId(Long activityId);

    /**
     * 获得规则相关的商品
     */
    List<ShopActivityPromotionRuleVO> findShopPromotionRuleVOList(Long activityId);


    /**
     * 根据店铺id 和活动类型获取店铺活动
     */
    List<ShopActivityPromotionRule> findActivityByStoreIdAndActivityType(Long storeId, int activityType, Long ruleId);


    /**
     * 根据商品id 店铺id 商品类型 商品分类 商品品牌 查询优惠活动
     */
    List<ShopActivityPromotionRule> findCouponListByGoods(Map map);


    /**
     * 获取用户该订单该商家的参加活动列表 并取得最大的优惠信息
     */
    List<ShopActivityPromotionRule> findActivityRuleByStoreIdAndLimitWhere(CartCouponVo cartCouponVo);

    /**
     * 根据店铺id 获取优惠券数量
     */
    int findCouponNum(Long storeId);

    List<ShopActivityPromotionRule> findShopActivityPromotionRuleList(
            ShopActivityPromotionRule shopActivityPromotionRule);

    /**
     * 按条件查询规则列表
     */
    List<ShopActivityPromotionRule> listActivityRules(Map map);

    ShopActivityPromotionRule findById(Long id);

    //活动规则查询
    List<ShopActivityPromotionRule> findAtiIdsList(@Param("activityIds") List<Long> activityIds);

}