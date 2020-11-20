package com.framework.loippi.result.order;

import com.framework.loippi.entity.order.ShopOrderGoods;
import com.framework.loippi.entity.trade.ShopReturnOrderGoods;
import com.framework.loippi.entity.user.RdMmAddInfo;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.result.app.order.OrderResult;
import com.framework.loippi.vo.refund.ReturnGoodsVo;
import com.google.common.collect.Lists;
import javolution.util.internal.table.ReversedTableImpl;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 订单商品分物流--返回app数据
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefundOrderResult {


    /**
     * 订单id
     */
    public Long orderId;

    /**
     * 订单编号
     */
    public String orderSn;
    /**
     * 配送公司
     */
    private String expressName;

    /**
     * 物流单号
     */
    private String shippingCode;

    /**
     * 订单状态：0:已取消;10:待付款;20:待发货;21部分发货;30:待收货;40:交易完成;
     */
    public Integer state;
    /**
     * 品牌名称
     */
    private String brandName;
    /**
     * 售后订单类型  1退款 2退款退货 3换货
     */
    private Integer RefundOrderType;
    /**
     * 申请原因
     */
    private String buyerMessage;
    /**
     * 图片
     */
    private String picInfo;
    /**
     * 原因内容
     */
    private String reasonInfo;
    /**
     * 管理员备注
     */
    private String adminMessage;
    /**
     * 收货人
     */
    private String receiverName;

    /**
     * 收货人手机
     */
    private String receiverMobile;

    /**
     * 收货人地址
     */
    private String receiverAddress;

    /**
     * 退款金额
     */
    private BigDecimal refundAmount;

    /**
     * 退还积分
     */
    private BigDecimal rewardPointAmount;
    /**
     * 退款成功标识 0:未成功 1：已成功
     */
    private Integer refundFlag;
    /**
     * 商品信息列表
     */
    private List<goodsInfo> goodsInfoList;
    @Data
    static  class goodsInfo {

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
        // TODO: 2018/12/6  等待判断登录用户是否是vip 显示不同价格
        public BigDecimal price;

        /**
         * pv
         */
        private BigDecimal ppv;
        /**
         * 商品类型 1-普通2-换购3-组合
         */
        public Integer goodsType;
    }

    public static RefundOrderResult buildList(List<ReturnGoodsVo> returnGoodsVoList, RdMmAddInfo shopMemberAddress) {
        RefundOrderResult result = new RefundOrderResult();
        if (CollectionUtils.isEmpty(returnGoodsVoList)) {
            return result;
        }
        for (ReturnGoodsVo returnGoodsVo : returnGoodsVoList) {
            List<goodsInfo> goodsInfoList = new ArrayList<>();
            int quantity = 0;
            for (ShopReturnOrderGoods orderGoods : returnGoodsVo.getShopReturnOrderGoodsList()) {
                quantity += Optional.of(orderGoods.getGoodsNum()).orElse(0);
                goodsInfo goodsInfo = new goodsInfo();
                goodsInfo.setGoodsImg(Optional.ofNullable(orderGoods.getGoodsImage()).orElse(""));
                goodsInfo.setGoodsName(Optional.ofNullable(orderGoods.getGoodsName()).orElse(""));
                goodsInfo.setSpecInfo(Optional.ofNullable(orderGoods.getSpecInfo()).orElse(""));
                goodsInfo.setGoodsId(Optional.ofNullable(orderGoods.getGoodsId()).orElse(-1L));
                goodsInfo.setSpecId(Optional.ofNullable(orderGoods.getSpecId()).orElse(-1L));
                // TODO: 2018/12/6   等待判断登录用户是否是vip 显示不同价格
                goodsInfo.setPrice(Optional.ofNullable(orderGoods.getPrice()).orElse(new BigDecimal(0)));
                goodsInfo.setQuantity(Optional.ofNullable(orderGoods.getGoodsNum()).orElse(0));
                goodsInfo.setGoodsType(Optional.ofNullable(orderGoods.getGoodsType()).orElse(0));
                goodsInfo.setPpv(Optional.ofNullable(orderGoods.getPpv()).orElse(BigDecimal.ZERO));
                goodsInfoList.add(goodsInfo);
            }
            Optional<ReturnGoodsVo> optionalReturnGoodsVo = Optional.ofNullable(returnGoodsVo);

            result.setOrderId(optionalReturnGoodsVo.map(ReturnGoodsVo::getId).orElse(-1L));
            result.setAdminMessage(optionalReturnGoodsVo.map(ReturnGoodsVo::getAdminMessage).orElse(""));
            result.setBrandName(optionalReturnGoodsVo.map(ReturnGoodsVo::getBrandName).orElse(""));
            result.setBuyerMessage(optionalReturnGoodsVo.map(ReturnGoodsVo::getBuyerMessage).orElse(""));
            result.setExpressName(optionalReturnGoodsVo.map(ReturnGoodsVo::getExpressName).orElse(""));
            result.setOrderSn(optionalReturnGoodsVo.map(ReturnGoodsVo::getOrderSn).orElse(""));
            result.setPicInfo(optionalReturnGoodsVo.map(ReturnGoodsVo::getPicInfo).orElse(""));
            result.setReasonInfo(optionalReturnGoodsVo.map(ReturnGoodsVo::getReasonInfo).orElse(""));
            result.setRefundOrderType(optionalReturnGoodsVo.map(ReturnGoodsVo::getRefundType).orElse(0));
            result.setShippingCode(optionalReturnGoodsVo.map(ReturnGoodsVo::getInvoiceNo).orElse(""));
            result.setRewardPointAmount(optionalReturnGoodsVo.map(ReturnGoodsVo::getRewardPointAmount).orElse(BigDecimal.ZERO));
            result.setRefundAmount(optionalReturnGoodsVo.map(ReturnGoodsVo::getRefundAmount).orElse(BigDecimal.ZERO));
            if(returnGoodsVo.getRefundState()==null||returnGoodsVo.getRefundState()!=3){
                result.setRefundFlag(0);
            }else {
                result.setRefundFlag(1);
            }
            if (shopMemberAddress!=null){
                result.setReceiverName(Optional.ofNullable(shopMemberAddress.getConsigneeName()).orElse("后台还未设置"));
                result.setReceiverMobile(Optional.ofNullable(shopMemberAddress.getMobile()).orElse("后台还未设置"));
                result.setReceiverAddress(Optional.ofNullable(
                        shopMemberAddress.getAddProvinceCode()+shopMemberAddress.getAddCityCode()+shopMemberAddress.getAddCountryCode()
                ).orElse("后台还未设置")+Optional.ofNullable(shopMemberAddress.getAddDetial()).orElse(""));
            }else{
                result.setReceiverName("后台还未设置");
                result.setReceiverMobile("后台还未设置");
                result.setReceiverAddress("后台还未设置");
            }
// 卖家处理状态:0为待审核,1审核确认,2为同意,3为不同意,默认为0
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
        }

        return result;
    }

}
