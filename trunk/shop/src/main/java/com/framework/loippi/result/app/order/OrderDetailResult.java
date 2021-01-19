package com.framework.loippi.result.app.order;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.cloopen.rest.sdk.utils.DateUtil;
import com.framework.loippi.consts.ShopOrderDiscountTypeConsts;
import com.framework.loippi.entity.coupon.Coupon;
import com.framework.loippi.entity.order.ShopOrderAddress;
import com.framework.loippi.entity.order.ShopOrderGoods;
import com.framework.loippi.entity.user.RdMmAddInfo;
import com.framework.loippi.result.app.cart.BaseGoodsResult;
import com.framework.loippi.vo.order.ShopOrderVo;
import com.google.common.collect.Lists;

/**
 * 订单详情--返回app数据
 */
@Data
@Accessors(chain = true)
public class OrderDetailResult {

    /**
     * 订单编号
     */
    private String orderSn;

    /**
     * 订单状态
     */
    private Integer state;

    /**
     * 收货人
     */
    private String receiverName;

    /**
     * 收货人手机
     */
    private String receiverMobile;

    /**
     * 收货人地址(省市区)
     */
    private String areaAddress;
    /**
     * 收货人地址
     */
    private String receiverAddress;
    /**
     * 收货人地址详情
     */
    private String addressDetail;
    /**
     * 订单所用积分数量
     */
    private Integer usePointNum;
    /**
     * 订单所用积分数量
     */
    private BigDecimal usePointNumB;

    /**
     * 积分抵扣金额
     */
    private BigDecimal rewardPointAmount;

    /**
     * 商品金额
     */
    private BigDecimal goodsTotalAmount;

    /**
     * 优惠金额
     */
    public BigDecimal couponAmount;
    /**
     * 优惠卷抵扣金额
     */
    public BigDecimal useCouponAmount;

    /**
     * 实付金额
     */
    public BigDecimal needToPay;
    /**
     *订单PV总值
     */
    public BigDecimal totalPpv;
    /**
     *实际订单PV总值
     */
    public BigDecimal actualTotalPpv;
    /**
     *运费
     */
    public BigDecimal freightAmount;
    /**
     *优惠运费
     */
    public BigDecimal preferentialFreightAmount;
    /**
     * 订单id
     */
    private Long orderId;

    /**
     * 订单商品
     */
    List<OrderGoods> orderGoodsList;
    /**
     * 赠品列表
     */
    List<ShopOrderGoods> giftOrderGoodsList;
    /**
     * 赠品列表
     */
    List<ShopOrderGoods> bundledGoodsList;

    /**
     * 支付表编号
     */
    private String paySn;

    /**
     * 支付方式编号
     */
    public String paymentCode;

    /**
     * 配送公司
     */
    private String expressCode;

    /**
     * 物流单号
     */
    private String shippingCode;


    /**
     * 是否评价 0未 1已经
     */
    private Integer evaluateState;
    /**
     * 后台是否修改过订单  0 没修改 1修改过
     */
    private Integer isModify;

    /**
     * 联系客服用
     */
    private String chatAccount;

    /**
     * 订单留言
     */
    private String orderMessage;

    /**
     * 订单生成时间
     */
    private String createTime;
    /**
     * 支付(付款)时间
     */
    private String paymentTime;
    /**
     * 配送时间
     */
    private String shippingTime;
    /**
     * 订单完成时间
     */
    private String finnshedTime;
    /**
     * 品牌id
     */
    private Long brandId;
    /**
     * 品牌名称
     */
    private String brandName;
    /**
     * 订单类型
     */
    private String orderTypeStr;
    /**
     * 取消订单原因
     */
    private String cancelCause;
    /**
     * 1快递 2自提
     */
    private Integer logisticType;
    /**
     * 1在线支付 2货到付款
     */
    private Integer paymentType;
    /**
     * 是否可以进行提醒发货 0不可以 1可以
     */
    private Integer isRemind;
    /**
     * 是否分单标识 0：未分单 1：分单
     */
    private Integer splitFlag;
    /**
     * 分单会员编号
     */
    private ArrayList<String> splitMmCodes=Lists.newArrayList();
    /**
     * 当前订单使用优惠券集合  olomi商城默认订单只能使用一张优惠券
     */
    private ArrayList<Coupon> coupons= Lists.newArrayList();
    //物流信息
    Map<String,String> LogisticsInformation;
    @Data
    @Accessors(chain = true)
    static class OrderGoods extends BaseGoodsResult {

        private Long activityId;

        private Integer activityType;

        private BigDecimal ppv;
        //是否发货 0没发货 1已发货
        private Integer isShipment;

        //商品类型 1-普通2-换购3-组合
        private Integer goodsType;
        public static List<OrderGoods> buildList(List<ShopOrderGoods> orderGoodsList) throws Exception {
            return BaseGoodsResult.buildList(orderGoodsList,
                    result -> new OrderGoods()
                            .setIsShipment(
                                    (result.getShippingExpressId()!=null && result.getShippingCode()!=null && !"".equals(result.getShippingCode())?1:0)
                            )
                            .setPpv(Optional.ofNullable(result.getPpv()).orElse(BigDecimal.ZERO))
                            .setActivityId(Optional.ofNullable(result.getActivityId()).orElse(0L))
                            .setActivityType(Optional.ofNullable(result.getActivityType()).orElse(0))
                            .setGoodsType(Optional.ofNullable(result.getGoodsType()).orElse(1)));
        }

        public static List<OrderGoods>  buildList1(List<ShopOrderGoods> orderGoodsList,Integer orderType) throws Exception {
            return BaseGoodsResult.buildList1(orderGoodsList,orderType,
                    result -> new OrderGoods()
                            .setIsShipment(
                                    (result.getShippingExpressId()!=null && result.getShippingCode()!=null && !"".equals(result.getShippingCode())?1:0)
                            )
                            .setPpv(Optional.ofNullable(result.getPpv()).orElse(BigDecimal.ZERO))
                            .setActivityId(Optional.ofNullable(result.getActivityId()).orElse(0L))
                            .setActivityType(Optional.ofNullable(result.getActivityType()).orElse(0))
                            .setGoodsType(Optional.ofNullable(result.getGoodsType()).orElse(1)));
        }
    }


    public static OrderDetailResult build(ShopOrderVo order,RdMmAddInfo shopMemberAddress, OrderDetailResult orderDetailResult1) throws Exception {
        Optional<ShopOrderVo> optOrder = Optional.ofNullable(order);
        Optional<ShopOrderAddress> optAddr = optOrder.map(ShopOrderVo::getAddress);

        OrderDetailResult orderDetailResult = new OrderDetailResult()
                .setOrderSn(optOrder.map(ShopOrderVo::getOrderSn).orElse(""))
                .setState(optOrder.map(ShopOrderVo::getOrderState).orElse(-1))
                .setOrderId(optOrder.map(ShopOrderVo::getId).orElse(-1L))
                .setPaySn(optOrder.map(ShopOrderVo::getPaySn).orElse(""))
                .setPaymentCode(optOrder.map(ShopOrderVo::getPaymentCode).orElse(""))
                .setExpressCode(optOrder.map(ShopOrderVo::getShippingExpressCode).orElse(""))
                .setShippingCode(optOrder.map(ShopOrderVo::getShippingCode).orElse(""))
                .setEvaluateState(optOrder.map(ShopOrderVo::getEvaluationStatus).orElse(0).intValue())
                .setChatAccount("qqcloud_"+optOrder.map(ShopOrderVo::getStoreId).orElse(0L).toString())
                //.setOrderGoodsList(OrderGoods.buildList(order.getShopOrderGoods()))
                .setOrderGoodsList(OrderGoods.buildList1(order.getShopOrderGoods(),order.getOrderType()))
                .setBrandId(optOrder.map(ShopOrderVo::getBrandId).orElse(-1L))
                .setBrandName(optOrder.map(ShopOrderVo::getBrandName).orElse(""))
                .setCancelCause(optOrder.map(ShopOrderVo::getCancelCause).orElse(""))
                .setTotalPpv(optOrder.map(ShopOrderVo::getPpv).orElse(BigDecimal.ZERO))
                //订单生成时间
                .setCreateTime(Optional.ofNullable(DateUtil.dateToStr(order.getCreateTime(),"yyyy-MM-dd HH:mm:ss")).orElse(""))
                // 支付(付款)时间
                .setPaymentTime(Optional.ofNullable(DateUtil.dateToStr(order.getPaymentTime(),"yyyy-MM-dd HH:mm:ss")).orElse(""))
                //配送时间
                .setShippingTime(Optional.ofNullable(DateUtil.dateToStr(order.getShippingTime(),"yyyy-MM-dd HH:mm:ss")).orElse(""))
                //订单完成时间
                .setFinnshedTime(Optional.ofNullable(DateUtil.dateToStr(order.getFinnshedTime(),"yyyy-MM-dd HH:mm:ss")).orElse(""))
                //备注
                .setOrderMessage(optOrder.map(ShopOrderVo::getOrderMessage).orElse(""))
                .setUsePointNum(optOrder.map(ShopOrderVo::getUsePointNum).orElse(BigDecimal.ZERO).intValue())
                .setUsePointNumB(optOrder.map(ShopOrderVo::getUsePointNum).orElse(BigDecimal.ZERO))
                //是否被后台修改
                .setIsModify(optOrder.map(ShopOrderVo::getIsModify).orElse(0).intValue())
                // 可用积分抵扣金额
                .setRewardPointAmount(optOrder.map(ShopOrderVo::getPointRmbNum).orElse(new BigDecimal("0")))
                // 商品金额
                .setGoodsTotalAmount(optOrder.map(ShopOrderVo::getGoodsAmount).orElse(new BigDecimal("0")).subtract(optOrder.map(ShopOrderVo::getRankDiscount).orElse(BigDecimal.ZERO)))
                // 实付款
                .setNeedToPay(optOrder.map(ShopOrderVo::getOrderAmount).orElse(new BigDecimal("0")))
                //运费
                .setFreightAmount(optOrder.map(ShopOrderVo::getShippingFee).orElse(new BigDecimal("0")))
                //运费优惠
                .setPreferentialFreightAmount(optOrder.map(ShopOrderVo::getShippingPreferentialFee).orElse(new BigDecimal("0")))
                //优惠金额
                .setCouponAmount(optOrder.map(ShopOrderVo::getDiscount).orElse(new BigDecimal("0")).subtract(optOrder.map(ShopOrderVo::getRankDiscount).orElse(BigDecimal.ZERO)))
                //使用优惠券金额
                .setUseCouponAmount(optOrder.map(ShopOrderVo::getCouponDiscount).orElse(new BigDecimal("0")));
        if (order.getLogisticType()==1){
            orderDetailResult.setLogisticType(1);
            orderDetailResult.setReceiverName(optAddr.map(ShopOrderAddress::getTrueName).orElse(""));
            orderDetailResult.setAreaAddress(optAddr.map(ShopOrderAddress::getAreaInfo).orElse(""));
            orderDetailResult.setReceiverAddress(optAddr.map(ShopOrderAddress::getAreaInfo).orElse("")
                    + optAddr.map(ShopOrderAddress::getAddress).orElse(""));
            /*orderDetailResult.setAddressPCD(optAddr.map(ShopOrderAddress::getAreaInfo).orElse(""));
            orderDetailResult.setAddressDetail(optAddr.map(ShopOrderAddress::getAddress).orElse(""));*/
            orderDetailResult.setReceiverMobile(optAddr.map(ShopOrderAddress::getMobPhone).orElse(""));
            orderDetailResult.setAddressDetail(optAddr.map(ShopOrderAddress::getAddress).orElse(""));
        }else{
            //自提
            orderDetailResult.setLogisticType(2);
            if (shopMemberAddress!=null){
                orderDetailResult.setReceiverName(Optional.ofNullable(shopMemberAddress.getConsigneeName()).orElse("后台还未设置"));
                orderDetailResult.setReceiverMobile(Optional.ofNullable(shopMemberAddress.getMobile()).orElse("后台还未设置"));
                orderDetailResult.setReceiverAddress(Optional.ofNullable(
                        shopMemberAddress.getAddProvinceCode()+shopMemberAddress.getAddCityCode()+shopMemberAddress.getAddCountryCode()
                ).orElse("后台还未设置")+Optional.ofNullable(shopMemberAddress.getAddDetial()).orElse(""));
                orderDetailResult.setAddressDetail(Optional.ofNullable(shopMemberAddress.getAddDetial()).orElse(""));
            }else{
                orderDetailResult.setReceiverName("后台还未设置");
                orderDetailResult.setReceiverMobile("后台还未设置");
                orderDetailResult.setReceiverAddress("后台还未设置");
                orderDetailResult.setAddressDetail("后台还未设置");
            }
        }
        if (orderDetailResult.getState()==20 && !"".equals(Optional.ofNullable(DateUtil.dateToStr(order.getShippingTime(),"yyyy-MM-dd HH:mm:ss")).orElse(""))){
            //订单处于待发货状态 但已有发货时间 表示是部分发货
            orderDetailResult.setState(21);
        }
        orderDetailResult.setIsRemind(1);
        if (order.getRemindTime()!=null){
            Long hour = ((new Date().getTime() - order.getRemindTime().getTime())%86400000)/3600000;
            //提醒时间小于4小时不可以进行提醒
            if (hour<4){
                orderDetailResult.setIsRemind(0);
            }
        }
        orderDetailResult.setPaymentType(1);
        if ("cashOnDeliveryPlugin".equals(order.getPaymentCode()) || "货到付款".equals(order.getPaymentName())){
            orderDetailResult.setPaymentType(2);
        }
        orderDetailResult.setOrderTypeStr(ShopOrderDiscountTypeConsts.convert(order.getOrderType()));
        //********************************************************************
        ArrayList<ShopOrderGoods> giftsGoods = new ArrayList<>();
        ArrayList<ShopOrderGoods> bundledGoods = new ArrayList<>();
        List<ShopOrderGoods> shopOrderGoods = order.getShopOrderGoods();
        for (ShopOrderGoods shopOrderGood : shopOrderGoods) {
            if(shopOrderGood.getIsPresentation()!=null&&shopOrderGood.getIsPresentation()==1){
                shopOrderGood.setIsShipment(order.getShippingExpressId()!=null && order.getShippingCode()!=null && !"".equals(order.getShippingCode())?1:0);
                giftsGoods.add(shopOrderGood);
            }
            if(shopOrderGood.getIsBundled()!=null&&shopOrderGood.getIsBundled()==1){
                shopOrderGood.setIsShipment(order.getShippingExpressId()!=null && order.getShippingCode()!=null && !"".equals(order.getShippingCode())?1:0);
                bundledGoods.add(shopOrderGood);
            }
        }
        orderDetailResult.setGiftOrderGoodsList(giftsGoods);
        orderDetailResult.setBundledGoodsList(bundledGoods);


        orderDetailResult.setCoupons(orderDetailResult1.getCoupons());
        orderDetailResult.setSplitFlag(orderDetailResult1.getSplitFlag());
        orderDetailResult.setSplitMmCodes(orderDetailResult1.getSplitMmCodes());
        return orderDetailResult;
    }
    public static OrderDetailResult build2(ArrayList<Coupon> coupons, OrderDetailResult orderDetailResult) throws Exception {
        orderDetailResult.setCoupons(coupons);
        return orderDetailResult;
    }
}
