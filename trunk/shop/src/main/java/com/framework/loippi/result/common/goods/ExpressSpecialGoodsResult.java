package com.framework.loippi.result.common.goods;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author :ldq
 * @date:2019/12/16
 * @description:dubbo com.framework.loippi.result.common.goods
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpressSpecialGoodsResult {

	private Long id;//id
	private String goodsName;//商品名称
	private String specGoodsSerial;//规格商品编号（sku）
	private Long expressId;//快递公司Id
	private String expressName;//快递公司名称
	private String eExpressCode;//第三方物流单号(物流代码)
	private String creationBy;//创建人
	private Date creationTime;//创建时间
	private String updateBy;//更新人
	private Date updateTime;//更新时间

}
