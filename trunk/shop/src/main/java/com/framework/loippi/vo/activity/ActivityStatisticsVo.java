package com.framework.loippi.vo.activity;

import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 促销统计
 * Created by zhuosr on 2017/12/4.
 */
@Data
@ToString
public class ActivityStatisticsVo {
    /**
     * 日
     */
    private Date createDate;

    /**
     * 周
     */
    private Integer weekDate;

    /**
     * 月
     */
    private String monthDate;

    /**
     * 全部人数
     */
    private Integer totalNum;

    /**
     * 购买总额
     */
    private BigDecimal totalPrice;

    /**
     * 开始时间
     */
    private Date beforeDate;

    /**
     * 结束时间
     */
    private Date afterDate;

    /**
     * 订单类型   0.普通 1.团购 3.秒杀 4促销 5混合
     */
    private Integer orderType;

    /**
     * 支付状态  0未付款 1已付款
     */
    private Integer paymentState;

    /**
     * 红包的类型  1注册红包  2活动红包(第一批)  3活动红包（第二批）
     */
    private Integer redPacketType;

    /**
     * 有值为分享注册
     */
    private Integer recommended;

    /**
     * 1:日报   2:周报   3:月报
     */
    private String state;

    /**
     * 优惠劵状态  1:已领取   2:已使用
     */
    private String status;

    /**
     * red_packet  红包,
     * red_packet_register  注册红包,
     * recommend_rebate  推荐返佣,
     * goods_evaluate 评价返佣,
     * order_cancel 取消订单,
     * refund  退货退款
     */
    private String lgType;
    //是否需要模糊查询 1:需要  2:不需要
    private Integer like;

    private Long storeId;
}
