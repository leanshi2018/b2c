package com.framework.loippi.result.common.coupon;

import com.framework.loippi.entity.activity.ShopActivity;
import com.framework.loippi.entity.coupon.Coupon;
import com.framework.loippi.entity.coupon.CouponUser;
import com.framework.loippi.result.activity.ActivityDetailResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CouponTransInfoResult {
    //优惠券id
    private Long couponId;
    //优惠券名称
    private String couponName;
    //当前登录用户拥有指定优惠券个数
    private Integer couponNum;
    //优惠券图片
    private String image;

    public static CouponTransInfoResult build(Coupon coupon, CouponUser couponUser) {
        CouponTransInfoResult result = new CouponTransInfoResult();
        result.setCouponId(coupon.getId());
        result.setCouponName(Optional.ofNullable(coupon.getCouponName()).orElse(""));
        result.setImage(Optional.ofNullable(coupon.getImage()).orElse(""));
        result.setCouponNum(Optional.ofNullable(couponUser.getOwnNum()).orElse(0));
        return result;
    }
}
