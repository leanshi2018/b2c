package com.framework.loippi.vo.coupon;

import com.framework.loippi.entity.coupon.Coupon;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CouponVo extends Coupon {
    private Integer useNum;//使用了几张当前种类优惠券
}
