package com.framework.loippi.vo.goods;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author linjm 时间: 2015年06月10日16:20:41 获得所有规格所对应的商品
 */
@Data
public class SpecGoodsVo implements GenericEntity {

	/**
	 * 规格ID
	 */
	private String goodsSpecId;

	/**
	 * 规格名
	 */
	private String specName;

	/**
	 * 规格序列化
	 */
	private String specGoodsSpec;

	/**
	 * 价格
	 */
	private BigDecimal specGoodsPrice;

	/**
	 * 库存
	 */
	private int specGoodsStorage;

	/**
	 * 商品ID
	 */
	private String goodsId;

	/**
	 * 商品名称
	 */
	private String goodsName;

	/**
	 * 商品图片
	 */
	private String goodsImage;


	/**
	 * 店铺ID
	 */
	private String storeId;

	/**
	 * 规格描述
	 */
	private String 	specInfo;

}
