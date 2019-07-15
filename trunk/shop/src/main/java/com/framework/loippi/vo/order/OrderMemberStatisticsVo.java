package com.framework.loippi.vo.order;

import java.math.BigDecimal;

import lombok.Data;

/**
 * 功能： 统计销量量排名
 * 类名：OrderMemberStatisticsVo
 * 日期：2018/4/9  16:09
 * 作者：czl
 * 详细说明：
 * 修改备注:
 */
@Data
public class OrderMemberStatisticsVo {

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 姓名
     */
    private String memberName;
    /**
     * 等级名称
     */
    private String gradeName;
    /**
     * 用户id
     */
    private Long memberId;

    /**
     * 订单数
     */
    private Integer orderNum;

    /**
     * 订单总额
     */
    private BigDecimal orderPriceSum;

}
