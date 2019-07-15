package com.framework.loippi.entity.order;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.mybatis.ext.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * Entity - 订单商品表
 *
 * @author zijing
 * @version 2.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "SHOP_ORDER_GOODS")
public class ShopOrderGoods implements GenericEntity {

    private static final long serialVersionUID = 5081846432919091193L;

    /** 订单商品表索引id */
    @Column(id = true, name = "id", updatable = false)
    private Long id;

    /** 订单id */
    @Column(name = "order_id" )
    private Long orderId;

    /** 商品id */
    @Column(name = "goods_id" )
    private Long goodsId;

    /** 商品名称 */
    @Column(name = "goods_name" )
    private String goodsName;

    /** 规格id */
    @Column(name = "spec_id" )
    private Long specId;

    /** 规格描述 */
    @Column(name = "spec_info" )
    private String specInfo;

    /** pv价 */
    @Column(name = "goods_price" )
    private java.math.BigDecimal goodsPrice;

    /** 商品数量 */
    @Column(name = "goods_num" )
    private Integer goodsNum;

    /** 商品图片 */
    @Column(name = "goods_image" )
    private String goodsImage;

    /** 退货数量 */
    @Column(name = "goods_returnnum" )
    private Integer goodsReturnnum;

    /** 退款金额 */
    @Column(name = "refund_amount" )
    private java.math.BigDecimal refundAmount;

    /** 店铺ID */
    @Column(name = "stores_id" )
    private Long storesId;

    /** 评价状态 0为评价，1已评价 */
    @Column(name = "evaluation_status" )
    private Integer evaluationStatus;

    /** 评价时间 */
    @Column(name = "evaluation_time" )
    private Long evaluationTime;

    /** 商品现金支付价(实际支付商品价格) */
    @Column(name = "goods_pay_price" )
    private java.math.BigDecimal goodsPayPrice;

    /** 买家ID */
    @Column(name = "buyer_id" )
    private String buyerId;

    /** 商品最底级分类ID */
    @Column(name = "gc_id" )
    private String gcId;

    /** 换货数量 */
    @Column(name = "goods_barternum" )
    private Integer goodsBarternum;

    /** 是否赠送商品 */
    @Column(name = "is_presentation" )
    private Integer isPresentation;

    /** 发货地址id */
    @Column(name = "daddress_id" )
    private String daddressId;

    /** 活动类型 */
    @Column(name = "activity_type" )
    private Integer activityType;

    /** 活动id */
    @Column(name = "activity_id" )
    private Long activityId;

    /** 商品编码 */
    @Column(name = "spec_goods_serial" )
    private String specGoodsSerial;

    /** 商品运费 */
    @Column(name = "goods_shipping_fee" )
    private java.math.BigDecimal goodsShippingFee;

    /** 积分抵扣金额 */
    @Column(name = "reward_point_price" )
    private java.math.BigDecimal rewardPointPrice;

    /** 零售价 */
    @Column(name = "market_price" )
    private java.math.BigDecimal marketPrice;

    /** 商品副标题 */
    @Column(name = "goods_subname" )
    private String goodsSubname;

    /** 商品pv值 */
    @Column(name = "ppv" )
    private BigDecimal ppv;

    /** 大单pv值 */
    @Column(name = "big_ppv" )
    private BigDecimal bigPpv;

    /** 配送公司ID */
    @Column(name = "shipping_express_id" )
    private Long shippingExpressId;

    /** 物流单号 */
    @Column(name = "shipping_code" )
    private String shippingCode;

    /** 配送公司编号 */
    @Column(name = "shipping_express_code" )
    private String shippingExpressCode;

    /** 配送公司名称 */
    @Column(name = "shipping_express_name" )
    private String shippingExpressName;
    /** 会员价 */
    @Column(name = "vip_price" )
    private java.math.BigDecimal vipPrice;

    /*********************
     * 添加
     *********************/
    @Column(name = "goods_subname")
    private String goodsSubName;
    /**
     * 商品重量
     */
    @Column(name = "weight")
    private Double weight;

    /**
     * 商品类型1-普通2-换购3-组合
     */
    @Column(name = "goods_type")
    private Integer goodsType;

    /**
     * 已发货数量
     */
    @Column(name = "shipping_goods_num")
    private Integer shippingGoodsNum;

    //售后状态 默认0 0-正常  1-申请售后  2-售后拒绝  3-售后成功
    @Column(name = "refund_state")
    private Integer refundState;
    //仓库code
    private String wareCode;
    //仓库名称
    private String wareName;


    private Integer evaluateCount;

    /**
     * 是否已追评  0|null未追评 1已追评
     */
    private Integer additionalEvalStatus;
    private Long evaluationId;

    //售后申请  0:未申请   1:申请
    private Integer afterSale = 0;

    /**
     * 规格商品库存
     */
    private Integer specGoodsStorage;
    private String[] Ids;
    private String[] goodIds;
    private List<Long> specIds;




}
