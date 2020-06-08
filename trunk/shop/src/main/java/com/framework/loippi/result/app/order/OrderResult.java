package com.framework.loippi.result.app.order;

import com.cloopen.rest.sdk.utils.DateUtil;
import com.framework.loippi.consts.ShopOrderDiscountTypeConsts;
import com.framework.loippi.entity.order.ShopOrderDiscountType;
import com.framework.loippi.entity.order.ShopOrderGoods;
import com.framework.loippi.entity.trade.ShopRefundReturn;
import com.framework.loippi.entity.trade.ShopReturnOrderGoods;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.vo.order.ShopOrderVo;
import com.framework.loippi.vo.refund.ReturnGoodsVo;
import com.google.common.collect.Lists;
import java.math.BigDecimal;
import java.util.*;

import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.collections.CollectionUtils;

/**
 * 订单列表返回APP数据
 */
@Data
@Accessors(chain = true)
public class OrderResult {

    /**
     * 订单id
     */
    public Long orderId;

    /**
     * 订单编号
     */
    public String orderSn;

    /**
     * 订单状态：0:已取消;10:待付款;20:待发货;21部分发货;30:待收货;40:交易完成;
     */
    public Integer state;
    /**
     * 商品数量
     */
    public Integer totalQuantity;
    /**
     * 需要支付金额
     */
    public BigDecimal payment;

    /**
     * 支付id
     */
    public String paySn;
    /**
     * 配送公司
     */
    private String expressCode;
    /**
     * 物流单号
     */
    private String shippingCode;
    /**
     * 是否可以进行提醒发货 0不可以 1可以
     */
    private Integer isRemind;
    /**
     * 是否评价 0未 1已经
     */
    private Integer evaluateState;

    /**
     * 后台是否修改过订单  0 没修改 1修改过
     */
    private Integer isModify;
    /**
     * 订单类型  todo待定类型
     */
    private Integer orderType;
    /**
     * 品牌名称
     */
    private String brandName;

    /**
     * 联系客服
     */
    private String qcloudImportAccount;

    /**  1快递 2自提 */
    private Integer logisticType;

    /**  订单mi值 */
    private BigDecimal ppv;

    /**
     * 售后订单类型  1退款 2退款退货 3换货  0无售后
     */
    private Integer refundOrderType;

    /**
     * 商品信息列表
     */
    private List<goodsInfo> goodsInfoList;

    @Data
    static class goodsInfo {
        /**
         * 商品名称
         */
        private String goodsName;
        /**
         * 商品图片
         */
        public String goodsImg;
        /**
         * 规格信息
         */
        private String specInfo;
        /**
         * 商品数量
         */
        public Integer quantity;
        /**
         * 商品pv值
         */
        public BigDecimal ppv;
        /**
         * 商品id
         */
        private Long goodsId;
        /**
         * 规格id
         */
        private Long specId;
        /**
         * 商品价格()
         */
        public BigDecimal price;

    }

    public static List<OrderResult> buildList(List<ShopOrderVo> shopOrderVos) {
        if (CollectionUtils.isEmpty(shopOrderVos)) {
            return Lists.newArrayList();
        }

        List<OrderResult> orderResults = Lists.newArrayList();
        for (ShopOrderVo shopOrderVo : shopOrderVos) {
            Integer evaluateState = 1;//0 未 1 已
            List<goodsInfo> goodsInfoList = new ArrayList<>();
            int quantity = 0;
            for (ShopOrderGoods orderGoods : shopOrderVo.getShopOrderGoods()) {
                quantity += Optional.of(orderGoods.getGoodsNum()).orElse(0);
                goodsInfo goodsInfo = new goodsInfo();
                goodsInfo.setGoodsImg(Optional.ofNullable(orderGoods.getGoodsImage()).orElse(""));
                goodsInfo.setGoodsName(Optional.ofNullable(orderGoods.getGoodsName()).orElse(""));
                goodsInfo.setSpecInfo(Optional.ofNullable(orderGoods.getSpecInfo()).orElse(""));
                goodsInfo.setGoodsId(Optional.ofNullable(orderGoods.getGoodsId()).orElse(-1L));
                goodsInfo.setSpecId(Optional.ofNullable(orderGoods.getSpecId()).orElse(-1L));
                goodsInfo.setPpv(Optional.ofNullable(orderGoods.getPpv()).orElse(BigDecimal.ZERO));
                // TODO: 2018/12/6   等待判断登录用户是否是vip 显示不同价格
                goodsInfo.setPrice(Optional.ofNullable(orderGoods.getGoodsPayPrice()).orElse(new BigDecimal(0)));
                goodsInfo.setQuantity(Optional.ofNullable(orderGoods.getGoodsNum()).orElse(0));
                if (orderGoods.getOrderId() == ShopOrderDiscountTypeConsts.DISCOUNT_TYPE_PPV) {
                    goodsInfo.setPpv(Optional.ofNullable(orderGoods.getBigPpv()).orElse(BigDecimal.ZERO));
                }
                goodsInfoList.add(goodsInfo);
                if(orderGoods.getIsPresentation()!=null&&orderGoods.getIsPresentation()==1){
                    continue;
                }else {
                    //判断商品是否评价
                    if(orderGoods.getEvaluationStatus()==0){
                        evaluateState=0;
                    }
                }
            }

            Optional<ShopOrderVo> optShopOrder = Optional.ofNullable(shopOrderVo);
            OrderResult result = new OrderResult()
                    .setOrderId(optShopOrder.map(ShopOrderVo::getId).orElse(-1L))
                    .setEvaluateState(optShopOrder.map(ShopOrderVo::getEvaluationStatus).orElse(0))
                    .setOrderSn(optShopOrder.map(ShopOrderVo::getOrderSn).orElse(""))
                    .setState(optShopOrder.map(ShopOrderVo::getOrderState).orElse(-1))
                    //.setPayment(optShopOrder.map(ShopOrderVo::getOrderAmount).orElse(BigDecimal.ZERO).add(optShopOrder.map(ShopOrderVo::getPointRmbNum).orElse(BigDecimal.ZERO)))
                    .setPaySn(optShopOrder.map(ShopOrderVo::getPaySn).orElse(""))
                    .setExpressCode(optShopOrder.map(ShopOrderVo::getShippingExpressCode).orElse(""))
                    .setShippingCode(optShopOrder.map(ShopOrderVo::getShippingCode).orElse(""))
                    .setIsRemind(optShopOrder.map(ShopOrderVo::getIsRemind).orElse(0))
                    .setQcloudImportAccount("qqcloud_" + optShopOrder.map(ShopOrderVo::getStoreId).orElse(0L))
                    .setTotalQuantity(quantity)
                    .setBrandName(optShopOrder.map(ShopOrderVo::getBrandName).orElse(""))
                    .setOrderType(optShopOrder.map(ShopOrderVo::getOrderType).orElse(0))
                    .setIsModify(optShopOrder.map(ShopOrderVo::getIsModify).orElse(0))
                    .setLogisticType(optShopOrder.map(ShopOrderVo::getLogisticType).orElse(1));
            result.setGoodsInfoList(goodsInfoList);
            if(shopOrderVo.getPpv()!=null){
                result.setPpv(shopOrderVo.getPpv());
            }else {
                result.setPpv(BigDecimal.ZERO);
            }
              if (shopOrderVo.getOrderType()!=5){
                  result.setPayment(optShopOrder.map(ShopOrderVo::getOrderAmount).orElse(BigDecimal.ZERO).add(optShopOrder.map(ShopOrderVo::getPointRmbNum).orElse(BigDecimal.ZERO)));
              }else{
                  result.setPayment(optShopOrder.map(ShopOrderVo::getOrderAmount).orElse(BigDecimal.ZERO));
              }
            if (result.getState() == 20 && !"".equals(Optional.ofNullable(DateUtil.dateToStr(shopOrderVo.getShippingTime(), "yyyy-MM-dd HH:mm:ss")).orElse(""))) {
                //订单处于待发货状态 但已有发货时间 表示是部分发货
                result.setState(21);
            }
            result.setIsRemind(1);
            if (shopOrderVo.getRemindTime() != null) {
                Long hour = ((new Date().getTime() - shopOrderVo.getRemindTime().getTime()) % 86400000) / 3600000;
                //提醒时间小于4小时不可以进行提醒
                if (hour < 4) {
                    result.setIsRemind(0);
                }
            }
            result.setEvaluateState(evaluateState);
            orderResults.add(result);

        }
        return orderResults;
    }

    public static List<OrderResult> buildList2(List<ReturnGoodsVo> shopRefundReturnVoList) {
        if (CollectionUtils.isEmpty(shopRefundReturnVoList)) {
            return Lists.newArrayList();
        }
        List<OrderResult> orderResults = Lists.newArrayList();
        for (ReturnGoodsVo returnGoodsVo : shopRefundReturnVoList) {
            List<goodsInfo> goodsInfoList = new ArrayList<>();
            int quantity = 0;
            BigDecimal ppvTotal=BigDecimal.ZERO;
            for (ShopReturnOrderGoods orderGoods : returnGoodsVo.getShopReturnOrderGoodsList()) {
                quantity += Optional.of(orderGoods.getGoodsNum()).orElse(0);
                Integer num = Optional.of(orderGoods.getGoodsNum()).orElse(0);
                BigDecimal pv = Optional.ofNullable(orderGoods.getPpv()).orElse(BigDecimal.ZERO);
                BigDecimal multiply = pv.multiply(new BigDecimal(Integer.toString(num)));
                ppvTotal=ppvTotal.add(multiply);
                goodsInfo goodsInfo = new goodsInfo();
                goodsInfo.setGoodsImg(Optional.ofNullable(orderGoods.getGoodsImage()).orElse(""));
                goodsInfo.setGoodsName(Optional.ofNullable(orderGoods.getGoodsName()).orElse(""));
                goodsInfo.setSpecInfo(Optional.ofNullable(orderGoods.getSpecInfo()).orElse(""));
                goodsInfo.setGoodsId(Optional.ofNullable(orderGoods.getGoodsId()).orElse(-1L));
                goodsInfo.setSpecId(Optional.ofNullable(orderGoods.getSpecId()).orElse(-1L));
                goodsInfo.setPpv(Optional.ofNullable(orderGoods.getPpv()).orElse(BigDecimal.ZERO));
                // TODO: 2018/12/6   等待判断登录用户是否是vip 显示不同价格
                goodsInfo.setPrice(Optional.ofNullable(orderGoods.getPrice()).orElse(BigDecimal.ZERO));
                goodsInfo.setQuantity(Optional.ofNullable(orderGoods.getGoodsNum()).orElse(0));
                goodsInfoList.add(goodsInfo);
            }
            Optional<ReturnGoodsVo> optionalReturnGoodsVo = Optional.ofNullable(returnGoodsVo);
            OrderResult result = new OrderResult()
                    .setOrderId(optionalReturnGoodsVo.map(ReturnGoodsVo::getId).orElse(-1L))
                    .setEvaluateState(0)
                    .setOrderSn(optionalReturnGoodsVo.map(ReturnGoodsVo::getOrderSn).orElse(""))
                    .setPayment(optionalReturnGoodsVo.map(ReturnGoodsVo::getRefundAmount).orElse(BigDecimal.ZERO))
                    .setPaySn("")
                    .setExpressCode("")
                    .setShippingCode("")
                    .setIsRemind(0)
                    .setQcloudImportAccount("qqcloud_" + optionalReturnGoodsVo.map(ReturnGoodsVo::getStoreId).orElse(0L))
                    .setTotalQuantity(quantity)
                    .setBrandName(optionalReturnGoodsVo.map(ReturnGoodsVo::getBrandName).orElse(""))
                    .setOrderType(-1)
                    .setIsModify(0);
            result.setPpv(ppvTotal);
            result.setRefundOrderType(optionalReturnGoodsVo.map(ReturnGoodsVo::getRefundType).orElse(0));
//        卖家处理状态:0为待审核,1审核确认,2为同意,3为不同意,默认为0
            if (returnGoodsVo.getSellerState() == 0 || returnGoodsVo.getSellerState() == 1 || returnGoodsVo.getSellerState() == 3 || returnGoodsVo.getSellerState() == 4) {
                result.setState(returnGoodsVo.getSellerState() + 80);
            }
            if (returnGoodsVo.getSellerState() == 4 && returnGoodsVo.getRefundType() != 3){
                result.setState(82);
            }
            if (returnGoodsVo.getSellerState() == 4 && returnGoodsVo.getRefundType() == 3){
                result.setState(84);
            }
            if (returnGoodsVo.getSellerState() == 5){
                result.setState(82);
            }
            if (returnGoodsVo.getSellerState() == 2 && returnGoodsVo.getRefundType() != 3) {
                //已通过
                result.setState(82);
            }
            if (returnGoodsVo.getSellerState() == 2 && returnGoodsVo.getRefundType() == 3) {
                //换货中
                result.setState(85);
            }
            if (returnGoodsVo.getSellerState() == 6 && returnGoodsVo.getRefundType() == 3) {
                //换货完成
                result.setState(84);
            }
            result.setGoodsInfoList(goodsInfoList);
            orderResults.add(result);
        }
        return orderResults;
    }
}

