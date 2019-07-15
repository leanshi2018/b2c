package com.framework.loippi.dao.activity;


import com.framework.loippi.entity.activity.ShopActivity;
import com.framework.loippi.mybatis.dao.GenericDao;
import com.framework.loippi.vo.activity.ActivityStatisticsVo;
import com.framework.loippi.vo.activity.CartCouponVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * longbh
 * 促销活动主表
 */
public interface ShopActivityDao extends GenericDao<ShopActivity, Long> {

    /**
     * 限购数量
     */
    Long updateRestrictionNum(@Param("id") Long id, @Param("restrictionNum") Integer restrictionNum);

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
     * 获取店铺下面指定条件满即送条数
     */
    int findByEndTimeCount(Map paramMap);

    /**
     * 根据订单信息获取活动信息
     */
    List<ShopActivity> findActivityByCartCouponVo(CartCouponVo cartCouponVo);

    /**
     * 停止活动
     * qz
     */
    void stopShopActivityById(Long id);


    void updateActivityBatch(Map<String, Object> map);

    List<ActivityStatisticsVo> statisticsGiveOutCouponTotalByState(ActivityStatisticsVo param);

    /**
     * 查找时间与状态不对应的活动
     *
     * @return
     */
    List<ShopActivity> findErrorStatusData();

}