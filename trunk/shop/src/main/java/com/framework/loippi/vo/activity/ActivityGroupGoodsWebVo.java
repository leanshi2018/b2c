package com.framework.loippi.vo.activity;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;

/**
 * YMQ
 */
@Data
@ToString
public class ActivityGroupGoodsWebVo implements GenericEntity {

    //分组id
    private Long groupId;

    //活动id
    private Long activityId;

    //订单id
    private Long orderId;

    //订单编号
    private String orderSn;

    //活动开始时间
    private Date startTime;

    //活动结束时间
    private Date endTime;

    //商品名称
    private String goodsName;

    //商品图片
    private String goodsImage;

    //商品规格价格
    private BigDecimal specGoodsPrice;

    // 活动价格
    private BigDecimal price;

    //参与活动的商品数量
    private Integer stockNumber;

    //活动销售数量
    private Integer saleNumber;

    //每人限制数量
    private Integer menNumber;

    //参团人数
    private Integer restrictionNum;

    //目前团购人数
   private Integer orderNumber;

   //参团订单状态：  0参团失败 1正在参团   2 团已满(参团成功)  3参团失败订单已退款
   private Integer orderType;

   //参团人员id 集合 可用于查询
    private String  partnerMemeberId;

    //正在参团的倒计时
    private Long times;

    /**
     * 规格主图
     */
    private String  mainPicture;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 规格列表图
     */
    private String listPicture;

    /**
     * 活动状态
     */
    private Integer activityStatus;

    /**
     * 订单付款时间
     */
    private Date paymentTime;

    /**
     * 订单的商品数
     */
    private Integer goodsNum;

    //订单总金额
    private BigDecimal orderAmount;

    /**
     * 物流单号
     */
    private String shippingCode;

    /**
     * 快递公司
     */
    private String shippingExpressCode;

}
