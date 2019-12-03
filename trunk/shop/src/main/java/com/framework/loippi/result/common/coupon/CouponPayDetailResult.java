package com.framework.loippi.result.common.coupon;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import com.framework.loippi.entity.coupon.CouponDetail;
import com.framework.loippi.entity.coupon.CouponPayDetail;

/**
 * @author :ldq
 * @date:2019/12/3
 * @description:dubbo com.framework.loippi.result.common.coupon
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CouponPayDetailResult {

	private CouponPayDetail couponPayDetail;

	private List<CouponDetail> couponDetailList;

	public static CouponPayDetailResult build(CouponPayDetail couponPayDetail, List<CouponDetail> couponDetailList) {
		CouponPayDetailResult couponPayDetailResult = new CouponPayDetailResult();

		couponPayDetailResult.setCouponPayDetail(couponPayDetail);

		if (couponDetailList.size()!=0){
			couponPayDetailResult.setCouponDetailList(couponDetailList);
		}
		return couponPayDetailResult;
	}

}
