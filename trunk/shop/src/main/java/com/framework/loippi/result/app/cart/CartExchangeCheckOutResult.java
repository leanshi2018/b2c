package com.framework.loippi.result.app.cart;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.framework.loippi.entity.cart.ShopCartExchange;
import com.framework.loippi.entity.user.RdMmAccountInfo;
import com.framework.loippi.entity.user.RdMmAddInfo;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 换购商品购物车结算
 */
@Data
@Accessors(chain = true)
public class CartExchangeCheckOutResult {
    /**
     * 收货地址id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Integer addressId;
    /**
     * 收货名
     */
    private String receiveName;

    /**
     * 收货手机
     */
    private String receivePhone;

    /**
     * 收货地址【广东省广州市天河区棠安路188号乐天大厦8楼808-812】
     */
    private String receiveAddrInfo;
    /**
     * 商品件数
     */
    private Integer totalQuantity;
    /**
     * 有没有收货地址
     */
    private Integer hadReceiveAddr;
    /**
     * 商品金额
     */
    private BigDecimal goodsTotalAmount;
    /**
     * 运费
     */
    public BigDecimal freightAmount;
    /**
     * 优惠运费
     */
    public BigDecimal preferentialFreightAmount;
    /**
     * 总金额
     */
    private BigDecimal totalAmount;
    /**
     * 用户换购积分余额
     */
    private BigDecimal redemptionBlance;
    /**
     * 换购商品购物车id
     */
    public List<Long> cartIds;

    /**
     * 换购商品购物车id json字符串
     */
    @JsonSerialize(using = ToStringSerializer.class)
    public List<Long> cartIdsStr;

    public static CartExchangeCheckOutResult build(Map<String, Object> moneyMap,
                                                   List<ShopCartExchange> cartList, RdMmAddInfo address, RdMmAccountInfo accountInfo) {
        CartExchangeCheckOutResult result = new CartExchangeCheckOutResult().setHadReceiveAddr(address == null ? 0 : 1);
        Optional<RdMmAddInfo> optAddress = Optional.ofNullable(address);
        // 个人收货信息信息
        result
                .setAddressId(optAddress.map(RdMmAddInfo::getAid).orElse(-1))
                .setReceiveName(optAddress.map(RdMmAddInfo::getConsigneeName).orElse(""))
                .setReceivePhone(optAddress.map(RdMmAddInfo::getMobile).orElse(""))
                .setReceiveAddrInfo(optAddress.map(RdMmAddInfo::getAddProvinceCode).orElse("") +
                        optAddress.map(RdMmAddInfo::getAddCityCode).orElse("")+
                        optAddress.map(RdMmAddInfo::getAddCountryCode).orElse("")+
                        optAddress.map(RdMmAddInfo::getAddDetial).orElse(""));
        // 购物车总数量
        int totalNum = 0;
        for (ShopCartExchange cart : cartList) {
            totalNum += cart.getGoodsNum();
            if (result.getCartIds() == null) {
                result.setCartIds(new ArrayList<Long>());
                result.setCartIdsStr(new ArrayList<Long>());
            }
            result.getCartIds().add(cart.getId());
            result.getCartIdsStr().add(cart.getId());
        }
        result
                .setTotalQuantity(totalNum)
                //商品金额
                .setGoodsTotalAmount(Optional.ofNullable((BigDecimal) moneyMap.get("totalGoodsPrice")).orElse(new BigDecimal("0")))
                //运费
                .setFreightAmount(Optional.ofNullable((BigDecimal) moneyMap.get("freightAmount")).orElse(new BigDecimal("0")))
                //运费优惠
                .setPreferentialFreightAmount(Optional.ofNullable((BigDecimal) moneyMap.get("preferentialFreightAmount")).orElse(new BigDecimal("0")))
                //总金额
                .setTotalAmount(Optional.ofNullable((BigDecimal) moneyMap.get("totalAmount")).orElse(new BigDecimal("0")));
        result.setRedemptionBlance(Optional.ofNullable(accountInfo.getRedemptionBlance()).orElse(BigDecimal.ZERO));
        return result;
    }
}
