package com.framework.loippi.result.sys;

import lombok.Data;
import lombok.ToString;

/**
 * @author :ldq
 * @date:2020/12/17
 * @description:dubbo com.framework.loippi.result.sys
 */
@Data
@ToString
public class SelectGoodsSpec {

	private Long goodsId;
	private Long specId;
	private String goodsName;
	private String specInfo;
	private Integer num;

}
