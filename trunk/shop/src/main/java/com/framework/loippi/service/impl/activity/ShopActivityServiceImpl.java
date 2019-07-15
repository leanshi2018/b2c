package com.framework.loippi.service.impl.activity;

import com.framework.loippi.dao.activity.ShopActivityDao;
import com.framework.loippi.dao.activity.ShopActivityGoodsDao;
import com.framework.loippi.dao.activity.ShopActivityGoodsSpecDao;
import com.framework.loippi.dao.activity.ShopActivityPromotionRuleDao;
import com.framework.loippi.entity.activity.ShopActivity;
import com.framework.loippi.entity.activity.ShopActivityPromotionRule;
import com.framework.loippi.enus.ActivityRuleTypeEnus;
import com.framework.loippi.enus.ActivityStatusTypeEnus;
import com.framework.loippi.enus.ActivityTypeEnus;
import com.framework.loippi.service.TwiterIdService;
import com.framework.loippi.service.activity.ShopActivityGoodsSpecService;
import com.framework.loippi.service.activity.ShopActivityService;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.utils.Paramap;
import com.framework.loippi.vo.activity.ActivityStatisticsVo;
import com.framework.loippi.vo.activity.CartCouponVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 促销活动基本信息ServiceImpl
 *
 * @author kwg
 * @version 2016-09-01
 */
@Service
@Slf4j
public class ShopActivityServiceImpl extends GenericServiceImpl<ShopActivity, Long> implements ShopActivityService {

    /**
     * 促销活动基本信息DAO接口
     */
    @Resource
    private ShopActivityDao shopActivityDao;
    @Resource
    private ShopActivityPromotionRuleDao shopActivityPromotionRuleDao;
    @Resource
    private ShopActivityGoodsDao shopActivityGoodsDao;
    @Resource
    private ShopActivityGoodsSpecDao shopActivityGoodsSpecDao;
    @Resource
    private TwiterIdService twiterIdService;

    /**
     * 通过id删除促销活动基本信息数据
     *
     * @param id
     */
    @Override
    public void deleteShopActivityById(Long id) {
        //删除商品相关信息
//        activityObjectDao.deleteShopActivityObjectByActivityId(id);
        //删除活动基础数据
        shopActivityDao.delete(id);
        shopActivityGoodsDao.deleteShopActivityGoodsByActivityId(id);
        shopActivityGoodsSpecDao.deleteShopActivityGoodsSpecByActivityId(id);
        //删除 优惠券与优化规则的绑定关系
//        shopActivityPromotionRuleDao.deleteShopActivityPromotionRuleByActivityId(id);
    }

    //查询活动转map
    @Override
    public Map<Long, ShopActivity> findMapActivity(List<Long> idList) {
        if (idList.size() == 0) {
            return new HashMap<>();
        }
        List<ShopActivity> shopActivities = shopActivityDao.findByParams(Paramap.create().put("ids", idList));
        Map<Long, ShopActivity> shopActivityMap = new HashMap<>();
        for (ShopActivity itemActivity : shopActivities) {
            shopActivityMap.put(itemActivity.getId(), itemActivity);
        }
        return shopActivityMap;
    }

    //更新活动开始状态
    @Override
    public void updateShopActivityStartStatus(Long currentTime) {
        shopActivityDao.updateShopActivityStartStatus(currentTime);
    }

    //更新活动结束状态
    @Override
    public void updateShopActivityEndStatus(Long currentTime) {
        shopActivityDao.updateShopActivityEndStatus(currentTime);
    }

    /**
     * 通过id提交审核
     *
     * @param id
     */
    @Override
    public void submitAudit(Long id) {
        ShopActivity shopActivity = shopActivityDao.find(id);
        List<ShopActivityPromotionRule> shopActivityPromotionRuleList = shopActivityPromotionRuleDao.findShopActivityPromotionRuleByActivityId(id);
        String activityType = ActivityTypeEnus.activitTypeEnumMap.get(shopActivity.getActivityType());
        if (shopActivityPromotionRuleList.size() == 0 && ("manjian".equals(activityType) || "mansong".equals(activityType) || "coupon".equals(activityType) || "manmianyou".equals(activityType) || "tuangou".equals(activityType))) {
            throw new RuntimeException("请先添加活动规则");
        }
        shopActivity.setAuditStatus(10);
        shopActivity.setUpdateTime(new Date());
        shopActivityDao.update(shopActivity);
    }

    /**
     * 审核活动
     *
     * @param id
     */
    @Override
    public ShopActivity audit(Long id, Integer auditStatus) {
        ShopActivity shopActivity = shopActivityDao.find(id);
        if (shopActivity.getAuditStatus() == 10 && (auditStatus == 20 || auditStatus == 40)) {
            shopActivity.setAuditStatus(auditStatus);
            shopActivity.setUpdateTime(new Date());
            shopActivityDao.update(shopActivity);
        } else {
            throw new RuntimeException("审核失败，该活动无法审核");
        }
        return shopActivity;
    }

    /**
     * 获取店铺下面指定条件满即送条数
     *
     * @param paramMap
     * @return
     */
    @Override
    public int findByEndTimeCount(Map paramMap) {
        return shopActivityDao.findByEndTimeCount(paramMap);
    }

    //停止活动
    public void stopShopActivityById(Long id) {
        shopActivityDao.stopShopActivityById(id);
    }

    /**
     * 管理员通过id提交发布信息
     *
     * @param id
     */
    @Override
    public void submitAuditByAdminFaBu(Long id) {
        ShopActivity shopActivity = shopActivityDao.find(id);
        List<ShopActivityPromotionRule> shopActivityPromotionRuleList = shopActivityPromotionRuleDao.findShopActivityPromotionRuleByActivityId(id);
        String activityType = ActivityTypeEnus.activitTypeEnumMap.get(shopActivity.getActivityType());
        if (shopActivityPromotionRuleList.size() == 0 && ("manjian".equals(activityType) || "mansong".equals(activityType) || "coupon".equals(activityType) || "manmianyou".equals(activityType))) {
            throw new RuntimeException("请先添加活动规则");
        }
        shopActivity.setAuditStatus(20);
        shopActivity.setUpdateTime(new Date());
        shopActivityDao.update(shopActivity);
    }

    //批量更新活动审核状态
    public void updateActivityBatch(Map<String, Object> map) {
        shopActivityDao.updateActivityBatch(map);
    }

    /**
     * 处理活动信息的保存
     *
     * @param shopActivity
     * @param storeId
     * @param storeName
     * @param isSeller     识别商家与平台    true  平台    false商家
     */
    public Map<String, String> handleSaveActivity(ShopActivity shopActivity, String activityType,
                                                  Long storeId, String storeName, boolean isSeller) {

        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("code", "0");
        String errorMsg = "";
        resultMap.put("msg", errorMsg);


        //活动状态
        Date today = new Date();
        if (today.before(shopActivity.getStartTime())) {
            shopActivity.setActivityStatus(ActivityStatusTypeEnus.EnumType.unstarted.getValue());
        } else if (today.before(shopActivity.getEndTime())) {
            shopActivity.setActivityStatus(ActivityStatusTypeEnus.EnumType.inactivity.getValue());
        } else {
            shopActivity.setActivityStatus(ActivityStatusTypeEnus.EnumType.ended.getValue());
        }

        //save or update
        if (shopActivity.getId() != null) {
            shopActivity.setUpdateTime(new Date());
            shopActivityDao.update(shopActivity);

            if ("coupon".equals(activityType)) {
                //TODO 更新促销规则
                ShopActivityPromotionRule shopActivityPromotionRule = new ShopActivityPromotionRule();
                shopActivityPromotionRule.setId(shopActivity.getPromotionRuleId());
                shopActivityPromotionRule.setCouponSource(shopActivity.getCouponSource());
                shopActivityPromotionRule.setLimitWhere(shopActivity.getLimitWhere());
                Integer ruleType = shopActivity.getRuleType();
                if (ruleType != null && ruleType == ActivityRuleTypeEnus.EnumType.manjian.getValue()) {//满减
                    shopActivityPromotionRule.setLimitType(10);//限制类型--购买金额10,购买数量20,无限制30
                } else if (shopActivityPromotionRule != null && ruleType.intValue() == ActivityRuleTypeEnus.EnumType.zhekou.getValue()) {
                    shopActivityPromotionRule.setLimitType(30);
                }
                shopActivityPromotionRule.setRuleType(ruleType);
                shopActivityPromotionRuleDao.updateShopActivityPromotionRule(shopActivityPromotionRule);
            }
        } else {
            shopActivity.setId(twiterIdService.getTwiterId());
            shopActivity.setCreateTime(new Date());
            shopActivity.setUpdateTime(new Date());
//            shopActivity.setPromotionRuleId(twiterIdService.getTwiterId());
            shopActivityDao.insert(shopActivity);

            if ("coupon".equals(activityType)) {
                //添加促销规则
                ShopActivityPromotionRule shopActivityPromotionRule = new ShopActivityPromotionRule();
                shopActivityPromotionRule.setId(shopActivity.getPromotionRuleId());
                shopActivityPromotionRule.setCouponSource(shopActivity.getCouponSource());
                shopActivityPromotionRule.setLimitWhere(shopActivity.getLimitWhere());
                Integer ruleType = shopActivity.getRuleType();
                if (ruleType != null && ruleType == ActivityRuleTypeEnus.EnumType.manjian.getValue()) {//满减
                    shopActivityPromotionRule.setLimitType(10);//限制类型--购买金额10,购买数量20,无限制30
                } else if (shopActivityPromotionRule != null && ruleType.intValue() == ActivityRuleTypeEnus.EnumType.zhekou.getValue()) {
                    shopActivityPromotionRule.setLimitType(30);
                }
                shopActivityPromotionRule.setRuleType(ruleType);
                shopActivityPromotionRuleDao.saveShopActivityPromotionRule(shopActivityPromotionRule);
            }
        }

//        //处理秒杀、团购
//        if (activityType.equals("tuangou") || activityType.equals("xianshiqiang")) {
//
//        }
//
//        //处理促销活动
//        if (activityType.equals("zhuanchang")) {
//
//        }
//

        resultMap.put("code", "1");
        return resultMap;

    }

    @Override
    public List<ShopActivity> findErrorStatusData() {
        return shopActivityDao.findErrorStatusData();
    }

    //获取正在进行中的活动
    @Override
    public ShopActivity findInstanceActivity(Integer type, Long classId) {
        Map<String, Object> params = Paramap.create().put("activityClassId", classId)
                .put("activityType", type)
                .put("activityStatus", ActivityStatusTypeEnus.EnumType.inactivity.getValue());
        List<ShopActivity> shopActivities = shopActivityDao.findByParams(params);
        if (shopActivities.size() == 0) {
            return null;
        }
        return shopActivities.get(0);
    }


}