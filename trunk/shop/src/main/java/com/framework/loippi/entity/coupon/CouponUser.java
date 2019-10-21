package com.framework.loippi.entity.coupon;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户优惠券拥有记录
 * create by zc on 2019/10/18
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "rd_coupon_user")
public class CouponUser implements GenericEntity {
    private static final long serialVersionUID = 5081846432919091193L;
    /**
     * 主键id
     */
    private Long id;
    /**
     * 会员编号
     */
    private String mCode;
    /**
     * 优惠券id
     */
    private Long couponId;
    /**
     * 当前会员拥有当前优惠券数量
     */
    private Integer ownNum;
    /**
     * 当前优惠券可使用数量
     */
    private Integer useAbleNum;
    /**
     * 当前优惠券已使用数量
     */
    private Integer useNum;
}
