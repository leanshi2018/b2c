package com.framework.loippi.service.activity;


import com.framework.loippi.entity.activity.ShopActivityPromotionRule;
import com.framework.loippi.support.Page;
import com.framework.loippi.support.Pageable;
import com.framework.loippi.vo.activity.CartCouponVo;
import com.framework.loippi.vo.activity.ShopActivityPromotionRuleVO;
import com.framework.loippi.vo.activity.ShopStoreCouponVo;

import java.util.List;
import java.util.Map;

/**
 * 促销活动规则表Service接口
 *
 * @author kwg
 * @version 2016-09-01
 */
public interface ShopActivityPromotionRuleService {

    /**
     * 查询分页促销活动规则表数据
     */
    public Page<ShopActivityPromotionRule> findShopActivityPromotionRulePagerList(Pageable pageable);

    /**
     * 通过id获取单条促销活动规则表数据
     */
    public ShopActivityPromotionRule findShopActivityPromotionRuleById(Long id);


    /**
     * @param pageable  店铺id
     */
    List<ShopStoreCouponVo> listCoupon(Pageable pageable);

    /**
     * 通过id删除促销活动规则表数据
     */
    public void deleteShopActivityPromotionRuleById(Long id);

    /**
     * 修改促销活动规则表数据
     */
    public void updateShopActivityPromotionRule(ShopActivityPromotionRule shopActivityPromotionRule);

    /**
     * 保存促销活动规则表数据
     */
    public void saveShopActivityPromotionRule(ShopActivityPromotionRule shopActivityPromotionRule);

    /**
     * 保存促销活动规则表数据
     */
    void saveShopActivityPromotionRule(List<ShopActivityPromotionRule> ShopActivityPromotionRuleList, Long activityId);

    /**
     * 获取所有促销活动规则表数据
     */
    public List<ShopActivityPromotionRule> findShopActivityPromotionRuleAllList();


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
     * @param storeId 店铺id 根据店铺id 获取优惠券数量
     */
    int findCouponNum(Long storeId);

    /**
     * 查找符合条件的规则
     */
    List<ShopActivityPromotionRule> findShopActivityPromotionRuleList(
            ShopActivityPromotionRule shopActivityPromotionRule);

    /**
     * 按条件查询规则列表
     */
    List<ShopActivityPromotionRule> listActivityRules(Map<String, Object> map);

    public ShopActivityPromotionRule findById(Long id);

    Map<Long, ShopActivityPromotionRule> findByIdsMap(List<Long> idList);

    //批量查询活动规则转map
    Map<Long, ShopActivityPromotionRule> findByAtiIdsMap(List<Long> atiIdList);

}