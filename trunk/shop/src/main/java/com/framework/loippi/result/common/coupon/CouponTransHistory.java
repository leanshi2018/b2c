package com.framework.loippi.result.common.coupon;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CouponTransHistory {
    private String mCode;//会员编号
    private String nickName;//会员昵称
    private String gradeName;//会员等级名称
    private String image;//会员头像
    private String mobile;//会员手机号
}
