package com.framework.loippi.entity.coupon;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

import com.framework.loippi.mybatis.eitity.GenericEntity;
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
    private Long id;
    /**
     * 店家id
     */
    private Long storeId;
    /**
     * 卖家店铺名称
     */
    private String storeName;
    /**
     * 优惠券名称
     */
    private String couponName;
    /**
     * 优惠券售价
     */
    private BigDecimal couponPrice;
    /**
     * 优惠券面值  针对于折扣卷为折扣大小
     */
    private BigDecimal couponValue;
    /**
     * 折扣类型  1：满减卷 2：立减卷 3：满金额折扣  4：无金额限制折扣
     */
    private Integer reduceType;
    /**
     * 最小使用金额要求
     */
    private BigDecimal minAmount;
    /**
     * 最小mi值使用要求  （优先级高于最小金额使用要求）
     */
    private BigDecimal minMi;
    /**
     * 品牌id
     */
    private Long brandId;
    /**
     * 品牌名称
     */
    private String brandName;
    /**
     * 优惠券图片
     */
    private String image;
    /**
     * 使用范围 0：不限  1：适用于品类 2：适用于组合商品 3：使用于单品 4：适用于多种商品 9：组合条件使用
     */
    private Integer useScope;
    /**
     * 使用限制描述
     */
    private String scopeRemark;
    /**
     * 是否属于付费购买优惠券  0：免费 1：付费
     */
    private Integer useMoneyFlag;
    /**
     * 获得方式 1：手动领取  2：自动发放
     */
    private Integer receiveType;
    /**
     * 优惠券发放开始时间
     */
    private Date sendStartTime;
    /**
     * 优惠券发放结束时间
     */
    private Date sendEndTime;
    /**
     * 优惠券使用开始时间
     */
    private Date useStartTime;
    /**
     * 优惠券使用结束时间
     */
    private Date useEndTime;
    /**
     * 每个会员限制领取的张数，0为不限
     */
    private Integer personLimitNum;
    /**
     * 优惠券总发行数量 -1代表不限制
     */
    private Long totalLimitNum;
    /**
     * 已发放优惠券数量
     */
    private Long receivedNum;
    /**
     * 领取级别限制 多种级别已逗号分隔
     */
    private String rankLimit;
    /**
     * 会员使用数量限制 0表示不限制
     */
    private Integer useNumLimit;
    /**
     * 活动应用渠道1、通用；2、PC；3、Mobile
     */
    private Integer channel;
    /**
     * 1、新建；2、提交审核；3、审核通过；4、审核失败；5、上架；6、下架
     */
    private Integer status;
    /**
     * 审核意见
     */
    private String auditOpinion;
    /**
     * 优惠券描述（使用说明）
     */
    private String remark;
    /**
     * 创建人id
     */
    private Long createId;
    /**
     * 创建人姓名
     */
    private String createName;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 审核人id
     */
    private Long auditId;
    /**
     * 审核人姓名
     */
    private String auditName;
    /**
     * 审核时间
     */
    private Date auditTime;
    /**
     * 修改人id
     */
    private Long updateId;
    /**
     * 修改人姓名
     */
    private String updateName;
    /**
     * 修改时间
     */
    private Date updateTime;
}
