package com.framework.loippi.vo.order;

import com.framework.loippi.entity.order.ShopOrder;
import com.framework.loippi.utils.validator.DateUtils;
import lombok.Data;

import java.sql.Timestamp;

/**
 * 订单统计Vo
 * Created by Administrator on 2017/8/21.
 */
@Data
public class OrderStatisticsVo {

    private Long storeId;
    /** 开始时间-数据库字段 */
    private Long startTime;
    /** 结束时间-数据库字段 */
    private Long endTime;
    /** 开始时间－页面字段 */
    private Timestamp startTimeStr;
    /** 结束时间－页面字段 */
    private Timestamp endTimeStr;

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
        if (null != startTime) {
            this.startTimeStr = DateUtils.getTimestampByLong(startTime);
        }
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
        if (null != endTime) {
            this.endTimeStr = DateUtils.getTimestampByLong(endTime);
        }
    }

    /**
     * 会员号
     */
    private String memberName;

    /**
     * 订单状态：0:已取消;10:待付款;20:待发货;30:待收货;40:交易完成;50:已提交;60:已确认;
     */
    private Integer orderState;

    /**
     * 订单编号
     */
    private String orderSn;

    /**
     * 订单金额
     */
    private String orderTotalPrice;

    /**
     * 订单支付金额
     */
    private String orderPayPrice;
    /**
     * 积分支付金额
     */
    private String pointRmbNum;

    /**
     * 订单类型
     */
    private String orderType;

    /**
     * 下单时间
     */
    private String createDate;

    //订单数量
    private String num;
   //用于分组时间
    private String orderDate;
    private String notOrderType;

}
