package com.framework.loippi.service.activity;


import java.util.List;
import java.util.Map;

import com.framework.loippi.entity.activity.ShopActivity;
import com.framework.loippi.service.GenericService;


/**
 * 促销活动基本信息Service接口
 *
 * @author kwg
 * @version 2016-09-01
 */
public interface ShopActivityService extends GenericService<ShopActivity, Long> {


    /**
     * -更新活动开始状态
     *
     * @param currentTime 当前时间戳
     */
    void updateShopActivityStartStatus(Long currentTime);

    /**
     * -更新活动结束状态
     *
     * @param currentTime 当前时间戳
     */
    void updateShopActivityEndStatus(Long currentTime);

    /**
     * 通过id提交审核
     */
    void submitAudit(Long id);

    /**
     * 通过id提交发布信息
     */
    void submitAuditByAdminFaBu(Long id);

    /**
     * 审核活动
     */
    ShopActivity audit(Long id, Integer auditStatus);

    /**
     * 获取店铺下面指定条件满即送条数
     */
    int findByEndTimeCount(Map paramMap);

    void stopShopActivityById(Long id);

    /**
     * 批量更新活动信息
     */
    public void updateActivityBatch(Map<String, Object> map);


    //保存
    public Map<String, String> handleSaveActivity(ShopActivity shopActivity, String activityType, Long storeId, String storeName, boolean isSeller);


    List<ShopActivity> findErrorStatusData();

    ShopActivity findInstanceActivity(Integer type, Long classId);

    //活动删除
    void deleteShopActivityById(Long id);

    Map<Long, ShopActivity> findMapActivity(List<Long> idList);

	Map<String, String> handleSaveCouponActivity(ShopActivity shopActivity, Long platformStoreId, String 平台自营, boolean b);
}