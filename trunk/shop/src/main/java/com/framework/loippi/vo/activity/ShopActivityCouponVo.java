package com.framework.loippi.vo.activity;

import com.framework.loippi.entity.activity.ShopActivity;
import lombok.Data;

/**
 * Entity - 优惠劵  后台列表展示数据
 * 
 * @author zijing
 * @version 2.0
 */
@Data
public class ShopActivityCouponVo extends ShopActivity {


	private String memberIds;//优惠券选择的会员信息，用逗号隔开

	private String goodsIds;//优惠券选择的商品信息，用逗号隔开

	private  Long  activityId;//活动id

	private Integer limitType;//规则类型

	private String limitWhere;//限制金额

	private String couponSource;//金额

	private Integer ruleType;//规则的类型

	private Integer useNum;//使用数

	private Integer getNum;//领取数

	private Long couponActivityId;//优惠券绑定的活动id

	private Long objectId; // 优惠券绑定活动中间表


}
