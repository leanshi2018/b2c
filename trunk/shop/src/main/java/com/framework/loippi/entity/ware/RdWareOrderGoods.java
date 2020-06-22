package com.framework.loippi.entity.ware;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.framework.loippi.mybatis.eitity.GenericEntity;

/**
 * @author :ldq
 * @date:2020/6/3
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RdWareOrderGoods implements GenericEntity {
	private static final long serialVersionUID = 5081846432919091193L;

	@JsonSerialize(using = ToStringSerializer.class)
	private Long id;//id
	@JsonSerialize(using = ToStringSerializer.class)
	private Long orderId;//订单id
	@JsonSerialize(using = ToStringSerializer.class)
	private Long goodsId;//商品id
	@JsonSerialize(using = ToStringSerializer.class)
	private Long specId;//规格id
	private Integer goodsNum;//单品商品数量


}
