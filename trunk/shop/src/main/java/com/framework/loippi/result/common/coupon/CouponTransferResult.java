package com.framework.loippi.result.common.coupon;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CouponTransferResult {
    private String serialNum;//流水号
    private String couponName;//优惠券名称
    private int transNum;//转出数量
    private int residueNum;//当前会员对应优惠券剩余数量
    private String receiveCode;//受赠人会员编号
    private String receiveName;//受赠人姓名
    private String receiveNickName;//受赠人昵称
    private Long couponId;//优惠券id
}
