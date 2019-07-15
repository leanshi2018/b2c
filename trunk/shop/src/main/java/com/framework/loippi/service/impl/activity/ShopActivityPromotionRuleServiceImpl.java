package com.framework.loippi.service.impl.activity;

import com.framework.loippi.dao.activity.ShopActivityDao;
import com.framework.loippi.dao.activity.ShopActivityPromotionRuleDao;
import com.framework.loippi.entity.activity.ShopActivity;
import com.framework.loippi.entity.activity.ShopActivityPromotionRule;
import com.framework.loippi.mybatis.paginator.domain.PageList;
import com.framework.loippi.service.TwiterIdService;
import com.framework.loippi.service.activity.ShopActivityPromotionRuleService;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.support.Page;
import com.framework.loippi.support.Pageable;
import com.framework.loippi.utils.Paramap;
import com.framework.loippi.vo.activity.CartCouponVo;
import com.framework.loippi.vo.activity.ShopActivityPromotionRuleVO;
import com.framework.loippi.vo.activity.ShopStoreCouponVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 促销活动规则表ServiceImpl
 *
 * @author kwg
 * @version 2016-09-01
 */
@Service
public class ShopActivityPromotionRuleServiceImpl extends GenericServiceImpl<ShopActivityPromotionRule, Long>
        implements ShopActivityPromotionRuleService {

    //促销规则
    @Resource
    private ShopActivityPromotionRuleDao shopActivityPromotionRuleDao;
    @Resource
    private TwiterIdService twiterIdService;

    /**
     * 查询分页促销活动规则表数据
     *
     * @param pager 分页对象
     */
    @Override
    public Page<ShopActivityPromotionRule> findShopActivityPromotionRulePagerList(Pageable pager) {
        PageList<ShopActivityPromotionRule> result = shopActivityPromotionRuleDao.findShopActivityPromotionRulePagerList(pager.getParameter(), pager.getPageBounds());
        return new Page<ShopActivityPromotionRule>(result, result.getPaginator().getTotalCount(), pager);
    }

    /**
     * 通过id获取单条促销活动规则表数据
     */
    @Override
    public ShopStoreCouponVo findShopActivityPromotionRuleById(Long id) {
        return shopActivityPromotionRuleDao.findShopActivityPromotionRuleById(id);
    }

    /**
     * 根据店铺id 获取当前可以领取的优惠券
     */
    @Override
    public List<ShopStoreCouponVo> listCoupon(Pageable pageable) {
        return shopActivityPromotionRuleDao.listShopCouponVo(pageable.getParameter(), pageable.getPageBounds());
    }

    /**
     * 通过id删除促销活动规则表数据
     */
    @Override
    public void deleteShopActivityPromotionRuleById(Long id) {
        shopActivityPromotionRuleDao.deleteShopActivityPromotionRuleById(id);
    }

    /**
     * 修改促销活动规则表数据
     */
    @Override
    public void updateShopActivityPromotionRule(ShopActivityPromotionRule shopActivityPromotionRule) {
        shopActivityPromotionRuleDao.updateShopActivityPromotionRule(shopActivityPromotionRule);
    }


    /**
     * 保存促销活动规则表数据
     */
    @Override
    public void saveShopActivityPromotionRule(List<ShopActivityPromotionRule> ShopActivityPromotionRuleList,
                                              Long activityId) {
        shopActivityPromotionRuleDao.deleteShopActivityPromotionRuleByActivityId(activityId);
        for (ShopActivityPromotionRule rules : ShopActivityPromotionRuleList) {
            rules.setId(twiterIdService.getTwiterId());
            shopActivityPromotionRuleDao.saveShopActivityPromotionRule(rules);
        }
    }

    /**
     * 保存促销活动规则表数据
     */
    @Override
    public void saveShopActivityPromotionRule(ShopActivityPromotionRule shopActivityPromotionRule) {
        shopActivityPromotionRuleDao.saveShopActivityPromotionRule(shopActivityPromotionRule);
    }

    /**
     * 获取所有促销活动规则表数据
     */
    @Override
    public List<ShopActivityPromotionRule> findShopActivityPromotionRuleAllList() {
        return shopActivityPromotionRuleDao.findShopActivityPromotionRuleAllList();
    }


    /**
     * 根据活动id 获取促销规则列表
     */
    @Override
    public List<ShopActivityPromotionRule> findShopActivityPromotionRuleByActivityId(Long activityId) {
        return shopActivityPromotionRuleDao.findShopActivityPromotionRuleByActivityId(activityId);
    }

    /**
     * 通过活动id删除促销活动规则表数据
     */
    @Override
    public void deleteShopActivityPromotionRuleByActivityId(Long activityId) {
        shopActivityPromotionRuleDao.deleteShopActivityPromotionRuleByActivityId(activityId);
    }

    @Override
    public List<ShopActivityPromotionRuleVO> findShopPromotionRuleVOList(Long activityId) {
        return shopActivityPromotionRuleDao.findShopPromotionRuleVOList(activityId);
    }


    /**
     * 根据店铺id 和活动类型获取店铺活动
     */
    @Override
    public List<ShopActivityPromotionRule> findActivityByStoreIdAndActivityType(Long storeId, int activityType,
                                                                                Long ruleId) {
        return shopActivityPromotionRuleDao.findActivityByStoreIdAndActivityType(storeId, activityType, ruleId);
    }

    /**
     * 根据商品id 店铺id 商品类型 商品分类 商品品牌 查询优惠活动
     */
    @Override
    public List<ShopActivityPromotionRule> findCouponListByGoods(Map map) {
        map.put("dbName", "mysql");
        return shopActivityPromotionRuleDao.findCouponListByGoods(map);
    }

    /**
     * 获取用户该订单该商家的参加活动列表 并取得最大的优惠信息
     */
    @Override
    public List<ShopActivityPromotionRule> findActivityRuleByStoreIdAndLimitWhere(CartCouponVo cartCouponVo) {
        return shopActivityPromotionRuleDao.findActivityRuleByStoreIdAndLimitWhere(cartCouponVo);
    }

    /**
     * 根据店铺id 获取优惠券数量
     */
    @Override
    public int findCouponNum(Long storeId) {
        return shopActivityPromotionRuleDao.findCouponNum(storeId);
    }

    /**
     * 查找符合条件的规则
     */
    public List<ShopActivityPromotionRule> findShopActivityPromotionRuleList(
            ShopActivityPromotionRule shopActivityPromotionRule) {
        return shopActivityPromotionRuleDao.findShopActivityPromotionRuleList(shopActivityPromotionRule);
    }

    /**
     * 按条件查询规则列表
     *
     * @param map
     * @return
     */
    public List<ShopActivityPromotionRule> listActivityRules(Map<String, Object> map) {
        if (map == null || map.size() == 0) return null;
        return shopActivityPromotionRuleDao.listActivityRules(map);
    }

    public ShopActivityPromotionRule findById(Long id) {
        if (id == null) {
            return null;
        }
        return shopActivityPromotionRuleDao.findById(id);
    }

    //批量查询
    @Override
    public Map<Long, ShopActivityPromotionRule> findByIdsMap(List<Long> idList) {
        if (idList.size() == 0) {
            return new HashMap<>();
        }
        List<ShopActivityPromotionRule> rulesList = shopActivityPromotionRuleDao.listActivityRules(Paramap.create().put("idList", idList));
        Map<Long, ShopActivityPromotionRule> maps = new HashMap<>();
        for (ShopActivityPromotionRule shopActivityPromotionRule : rulesList) {
            maps.put(shopActivityPromotionRule.getId(), shopActivityPromotionRule);
        }
        return maps;
    }

    @Override
    public Map<Long, ShopActivityPromotionRule> findByAtiIdsMap(List<Long> atiIdList) {
        if (atiIdList.size() == 0) {
            return new HashMap<>();
        }

        List<ShopActivityPromotionRule> rulesList = shopActivityPromotionRuleDao.findAtiIdsList(atiIdList);
        Map<Long, ShopActivityPromotionRule> maps = new HashMap<>();
        for (ShopActivityPromotionRule shopActivityPromotionRule : rulesList) {
            maps.put(shopActivityPromotionRule.getActivityId(), shopActivityPromotionRule);
        }
        return maps;
    }
}