package com.framework.loippi.vo.activity;

import lombok.Data;

import java.util.Date;

/**
 * 用户优惠劵
 * Created by zhuosr on 2018/1/16.
 */
@Data
public class CouponMemberVo {
    //编号(sacm)   sacm:ShopActivityCouponMember  sm:ShopMember   sac:ShopActivityCoupon
    private Long id;

    private String activityName;

    private String activityDescription;

    // 会员名称(sm)
    private String memberName;

    //类型(sac)  1为代金卷，2为专场卷，3为通用卷
    private String type;

    // 促销规则（减)(sac)
    private String minus;

    // 促销规则（折扣率）(sac)
    private String discount;

    // 优惠劵状态（0为未领取，1为已领取，2为已使用，默认为0)(sacm)
    private String status;

    /**
     * 领取时间(sacm)
     */
    private Date createTime;

    /**
     * 使用时间(sacm)
     */
    private Date useTime;

    private Date startTime;

    private Date endTime;

    private String sn;

    private String orderSn;

}
