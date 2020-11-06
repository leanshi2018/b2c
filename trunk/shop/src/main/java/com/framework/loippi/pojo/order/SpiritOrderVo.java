package com.framework.loippi.pojo.order;

import lombok.Data;

/**
 * @author :ldq
 * @date:2020/9/22
 * @description:leanshi_member cn.leanshi.model.common
 */
@Data
public class SpiritOrderVo {

	private String orderSn;//订单编号
	private String specName;//商品规格名称
	private String specGoodsSerial;//规格商品编号
	private Integer goodsNum;//商品数量
	private String buyerName;//买家名称
	private String province;//收件人省份
	private String city;//收件人城市
	private String area;//收件人区
	private String address;//收件人地址
	private String buyerPhone;//买家手机号码
	private String desc;//备注
}
