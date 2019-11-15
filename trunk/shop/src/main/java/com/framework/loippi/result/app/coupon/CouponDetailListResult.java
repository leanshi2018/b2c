package com.framework.loippi.result.app.coupon;

import com.framework.loippi.entity.coupon.Coupon;
import com.framework.loippi.entity.coupon.CouponDetail;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 个人优惠券中心优惠券列表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CouponDetailListResult extends CouponDetail implements Serializable {
    /**
     * 是否可以赠送
     */
    private Integer whetherPresent;
    /**
     * 使用描述 eg.满360或25mi可以使用
     */
    private String scopeRemark;
    /**
     * 优惠券面值
     */
    private BigDecimal couponValue;
    /**
     * 折扣类型  1：满减卷 2：立减卷 3：满金额折扣  4：无金额限制折扣
     */
    private Integer reduceType;

    public static ArrayList<CouponDetailListResult> build(List<CouponDetail> couponDetails, HashMap<Long, Coupon> map) {
        ArrayList<CouponDetailListResult> results = new ArrayList<>();
        for (CouponDetail couponDetail : couponDetails) {
            CouponDetailListResult result = new CouponDetailListResult();
            BeanUtils.copyProperties(couponDetail,result);
            result.setWhetherPresent(map.get(couponDetail.getCouponId()).getWhetherPresent());
            result.setScopeRemark(map.get(couponDetail.getCouponId()).getScopeRemark());
            result.setCouponValue(map.get(couponDetail.getCouponId()).getCouponValue());
            result.setReduceType(map.get(couponDetail.getCouponId()).getReduceType());
            results.add(result);
        }
        return results;
    }
}
