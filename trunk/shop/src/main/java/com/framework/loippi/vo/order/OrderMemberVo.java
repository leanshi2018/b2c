package com.framework.loippi.vo.order;

import lombok.Data;

import java.util.Date;

/**
 * Created by Administrator on 2017/8/10.
 */
@Data
public class OrderMemberVo {

    /**
     * 订单id
     */
    private Long orderId;

    /**
     * 完成订单时间
     */
    private Date orderTime;

    /**
     * 会员id
     */
    private Long memberId;

    /**
     *会员名称
     */
    private String memberName;

    /**
     * 会员头像
     */
    private String memberAvatar;

    //id集合
    private Long[] ids;

    /**
     * 成团的倒计时  创建团的时间与12小时之差
     */
    private Long times;

}
