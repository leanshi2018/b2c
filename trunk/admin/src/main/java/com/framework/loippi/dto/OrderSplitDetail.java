package com.framework.loippi.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 订单分账详情
 * @author :ldq
 * @date:2020/3/27
 * @description:dubbo com.framework.loippi.dto
 */
@Data
public class OrderSplitDetail {

	/** 订单索引id */
	private Long id;
	/** 订单编号 */
	private String orderSn;
	/** 订单实付金额 */
	private BigDecimal orderAmount;
	/** 公司分账后收入 */
	private BigDecimal firmSplitAmount;
	/** 分账受益人会员编号 */
	private String cutGetId;
	/** 分账受益人会员名称 */
	private String cutGetName;
	/** 分账金额 */
	private BigDecimal cutAmount;
	/** 分账扣减积分 */
	private BigDecimal cutAcc;
	/** 不满足分账条件或分账失败 原因备注 */
	private String cutFailInfo;

}
