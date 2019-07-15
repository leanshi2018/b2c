package com.framework.loippi.utils;

import com.google.common.collect.Maps;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

/**
 * 功能： 分摊金额
 * 类名：CalcUtil
 * 日期：2017/12/18  10:52
 * 作者：czl
 * 详细说明：
 * 修改备注:
 */
public class CalcUtil {

    /**
     * 根据金额比例分摊【优惠金额】
     */
    public static Map<Long, BigDecimal> divideByRate(BigDecimal divideAmount, Map<Long, BigDecimal> map) {
        // 总金额
        BigDecimal total = new BigDecimal(0);
        for (BigDecimal price : map.values()) {
            total = total.add(price);
        }

        MathContext mc = new MathContext(4, RoundingMode.DOWN);
        // 实际分摊金额
        BigDecimal totalDividePrice = new BigDecimal(0);
        Map<Long, BigDecimal> resultMap = Maps.newHashMap();
        for (Entry<Long, BigDecimal> entry : map.entrySet()) {
            BigDecimal bigDecimal = entry.getValue().divide(total, mc).multiply(divideAmount)
                .setScale(2, BigDecimal.ROUND_HALF_EVEN);
            totalDividePrice = totalDividePrice.add(bigDecimal);
            resultMap.put(entry.getKey(), bigDecimal);
        }

        // 实际分摊金额小于需要分摊金额
        BigDecimal flagValue = new BigDecimal(0);
        Long flagKey = null;
        for (Entry<Long, BigDecimal> entry : resultMap.entrySet()) {
            if (flagValue.compareTo(entry.getValue()) == -1) {
                flagKey = entry.getKey();
                flagValue = entry.getValue();
            }
        }
        // 将差额给比例最大的
        resultMap.put(flagKey, flagValue.add(divideAmount.subtract(totalDividePrice)));
        return resultMap;
    }

    /**
     * divideByRate改进
     *
     * @param allocationAmount 要分配的金额（如优惠券100块， 三件商品分配）
     * @param list 要分配的对象集合 （如List<ShopOrderGoods> 订单商品集合）
     * @param valuemapper 要常用分配计算的项金额 （如 ShopOrderGoods.goodsPayPrice订单商品支付金额）
     * @param fun 回调set计算好的值 , 如：(v, t) -> t.setCouponPrice(v)
     */
    public static <T> void divideByRate2(BigDecimal allocationAmount, List<T> list,
        Function<? super T, BigDecimal> valuemapper,
        CalcFun<T, BigDecimal> fun) {
        // 总价格
        BigDecimal totalPrice = list.stream().map(valuemapper).reduce(BigDecimal.ZERO, BigDecimal::add);
        MathContext mc = new MathContext(4, RoundingMode.DOWN);
        // 实际分摊金额
        BigDecimal realAllocationAmount = new BigDecimal(0);
        // 单项分配最大的金额
        BigDecimal flagValue = BigDecimal.ZERO;
        // 单项分配最大的下标
        int flagIndex = 0;
        // 按比例均摊
        for (int i = 0; i < list.size(); i++) {
            // 单项价格
            BigDecimal itemPrice = valuemapper.apply(list.get(i));
            // 单项按比例分配到金额
            BigDecimal itemAllocatePrice = itemPrice.divide(totalPrice, mc)
                .multiply(allocationAmount).setScale(2, BigDecimal.ROUND_HALF_EVEN);
            fun.callback(list.get(i), itemAllocatePrice);
            // 实际总分配金额
            realAllocationAmount = realAllocationAmount.add(itemAllocatePrice);
            // 分配到最大金额的值
            if (flagValue.compareTo(itemAllocatePrice) == -1) {
                flagValue = itemAllocatePrice;
                flagIndex = i;
            }
        }
        // 将差额给比例最大的
        fun.callback(list.get(flagIndex), flagValue.add((allocationAmount.subtract(realAllocationAmount))));
    }
}