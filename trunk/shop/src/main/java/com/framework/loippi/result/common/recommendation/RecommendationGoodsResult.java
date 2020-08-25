package com.framework.loippi.result.common.recommendation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.framework.loippi.entity.coupon.CouponDetail;
import com.framework.loippi.entity.coupon.CouponPayDetail;

/**
 * @author :ldq
 * @date:2020/8/25
 * @description:dubbo com.framework.loippi.dto
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationGoodsResult {

	private static final long serialVersionUID = 5081846432919091193L;

	/**
	 * id
	 */
	@JsonSerialize(using = ToStringSerializer.class)
	private Long id;

	/**
	 * 推荐页id
	 */
	@JsonSerialize(using = ToStringSerializer.class)
	private Long rId;

	/**
	 * 商品id
	 */
	@JsonSerialize(using = ToStringSerializer.class)
	private Long goodsId;

	/**
	 * 商品名称
	 */
	private String goodsName;

	/**
	 * 商品分类Id
	 */
	@JsonSerialize(using = ToStringSerializer.class)
	private Long gcId;

	/**
	 * 商品分类
	 */
	private String gcName;


	public static RecommendationGoodsResult build(CouponPayDetail couponPayDetail, List<CouponDetail> couponDetailList) {
		return null;
	}

}
