package com.framework.loippi.entity.coupon;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.mybatis.ext.annotation.Table;

/**
 * 优惠券实体
 * create by zc on 2019/10/18
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "rd_coupon")
public class Coupon implements GenericEntity {
    private static final long serialVersionUID = 5081846432919091193L;
    /**
     * 主键id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @Column(name = "id")
    private Long id;
    /**
     * 店家id
     */
    @Column(name = "store_id")
    private Long storeId;
    /**
     * 卖家店铺名称
     */
    @Column(name = "store_name")
    private String storeName;
    /**
     * 优惠券名称
     */
    @Column(name = "coupon_name")
    private String couponName;
    /**
     * 优惠券售价
     */
    @Column(name = "coupon_price")
    private BigDecimal couponPrice;
    /**
     * 优惠券面值  针对于折扣卷为折扣大小
     */
    @Column(name = "coupon_value")
    private BigDecimal couponValue;
    /**
     * 折扣类型  1：满减卷 2：立减卷 3：满金额折扣  4：无金额限制折扣
     */
    @Column(name = "reduce_type")
    private Integer reduceType;
    /**
     * 最小使用金额要求
     */
    @Column(name = "min_amount")
    private BigDecimal minAmount;
    /**
     * 最小mi值使用要求  （优先级高于最小金额使用要求）
     */
    @Column(name = "min_mi")
    private BigDecimal minMi;
    /**
     * 品牌id
     */
    @Column(name = "brand_id")
    private Long brandId;
    /**
     * 品牌名称
     */
    @Column(name = "brand_name")
    private String brandName;
    /**
     * 优惠券图片
     */
    @Column(name = "image")
    private String image;
    /**
     * 是否可以赠送  0：不可以 1：可以
     */
    @Column(name = "whether_present")
    private Integer whetherPresent;
    /**
     * 使用范围 0：不限  1：适用于品类 2：适用于组合商品 3：使用于单品 4：适用于多种商品 9：组合条件使用
     */
    @Column(name = "use_scope")
    private Integer useScope;
    /**
     * 商品id，适用于使用范围为单品的优惠券
     */
    @Column(name = "goods_id")
    private Long goodsId;
    /**
     * 使用限制描述
     */
    @Column(name = "scope_remark")
    private String scopeRemark;
    /**
     * 是否属于付费购买优惠券  0：免费 1：付费
     */
    @Column(name = "use_money_flag")
    private Integer useMoneyFlag;
    /**
     * 获得方式 1：手动领取  2：自动发放
     */
    @Column(name = "receive_type")
    private Integer receiveType;
    /**
     * 优惠券发放开始时间
     */
    @Column(name = "send_start_time")
    private Date sendStartTime;
    /**
     * 优惠券发放结束时间
     */
    @Column(name = "send_end_time")
    private Date sendEndTime;
    /**
     * 优惠券使用开始时间
     */
    @Column(name = "use_start_time")
    private Date useStartTime;
    /**
     * 优惠券使用结束时间
     */
    @Column(name = "use_end_time")
    private Date useEndTime;
    /**
     * 每个会员限制领取的张数，0为不限
     */
    @Column(name = "person_limit_num")
    private Integer personLimitNum;
    /**
     * 优惠券总发行数量 -1代表不限制
     */
    @Column(name = "total_limit_num")
    private Long totalLimitNum;
    /**
     * 已发放优惠券数量
     */
    @Column(name = "received_num")
    private Long receivedNum;
    /**
     * 领取级别限制 多种级别已逗号分隔
     */
    @Column(name = "rank_limit")
    private String rankLimit;
    /**
     * 会员使用数量限制 0表示不限制
     */
    @Column(name = "use_num_limit")
    private Integer useNumLimit;
    /**
     * 活动应用渠道1、通用；2、PC；3、Mobile
     */
    @Column(name = "channel")
    private Integer channel;
    /**
     * 1、新建（待审核）；2、审核通过(上架)；3、审核失败；4、下架
     */
    @Column(name = "status")
    private Integer status;
    /**
     * 审核意见
     */
    @Column(name = "audit_opinion")
    private String auditOpinion;
    /**
     * 优惠券描述（使用说明）
     */
    @Column(name = "remark")
    private String remark;
    /**
     * 优惠券回收张数
     */
    @Column(name = "refund_num")
    private Integer refundNum;
    /**
     * 创建人id
     */
    @Column(name = "create_id")
    private Long createId;
    /**
     * 创建人姓名
     */
    @Column(name = "create_name")
    private String createName;
    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime;
    /**
     * 审核人id
     */
    @Column(name = "audit_id")
    private Long auditId;
    /**
     * 审核人姓名
     */
    @Column(name = "audit_name")
    private String auditName;
    /**
     * 审核时间
     */
    @Column(name = "audit_time")
    private Date auditTime;
    /**
     * 修改人id
     */
    @Column(name = "update_id")
    private Long updateId;
    /**
     * 修改人姓名
     */
    @Column(name = "update_name")
    private String updateName;
    /**
     * 修改时间
     */
    @Column(name = "update_time")
    private Date updateTime;
    /**
     * 是否动态决定优惠券使用结束时间  0：否 1：是
     */
    @Column(name = "dynamic_state")
    private Integer dynamicState;

    /******************设置条件搜索字段，不参与数据库存储****************************/
    /**
     * 查询发放优惠券时间
     */
    private String searchSendTime;
    /**
     * 查询使用优惠券时间
     */
    private String searchUseTime;
    /**
     * 当前用户当前订单使用张数
     */
    private Integer customerUseNum;
    /**
     * 是否可购买 0可买 1不可
     */
    private Integer canBuy;
    /**
     * 优惠券模糊查询名称
     */
    private String couponLikeName;
    /**
     * 不可使用原因（针对于提交购物车判断用户优惠券使用情况）
     * 0.未达到使用时间 1.已超过使用时间 2.不满足最小mi值要求 3.不满足最小金额要求 4.不满足店铺使用要求
     * 5.不满足品牌使用要求 6.不满足任何使用范围要求 7.使用数量达到当前优惠券使用上限 8.订单内不存在指定优惠商品(针对于单品优惠券)
     */
    private Integer noUseFalg;
    /**
     * 当前查询用户未使用优惠券数量
     */
    private Integer searchUseNum;
    /**
     * 优惠券月份
     */
    private int monthNum;
}
